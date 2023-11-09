package com.ruoyi.activiti.service.impl;

import cn.hutool.core.exceptions.StatefulException;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.ruoyi.activiti.consts.ActivitiConstant;
import com.ruoyi.activiti.domain.SysAttachment;
import com.ruoyi.activiti.domain.dto.SealApplyDto;
import com.ruoyi.activiti.domain.my_apply.BizSealApply;
import com.ruoyi.activiti.domain.proc.ActReProcdef;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.domain.vo.SealApplyVo;
import com.ruoyi.activiti.feign.RemoteFileService;
import com.ruoyi.activiti.mapper.ActReProcdefMapper;
import com.ruoyi.activiti.mapper.BizSealApplyMapper;
import com.ruoyi.activiti.service.BizSealApplyService;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.utils.CollectUtil;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.feign.RemoteDictService;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * 用印申请
 *
 * @author zx
 * @date 2022/1/12 20:21
 */
@Service
@Slf4j
public class BizSealApplyServiceImpl implements BizSealApplyService {
    private final RemoteUserService remoteUserService;
    private final ActReProcdefMapper actReProcdefMapper;
    private final IBizBusinessService bizBusinessService;
    private final RemoteDeptService remoteDeptService;
    private final RemoteFileService remoteFileService;
    private final BizSealApplyMapper sealApplyMapper;
    private final RemoteDictService remoteDictService;

    @Autowired
    public BizSealApplyServiceImpl(RemoteUserService remoteUserService, ActReProcdefMapper actReProcdefMapper, IBizBusinessService bizBusinessService, RemoteDeptService remoteDeptService, RemoteFileService remoteFileService, BizSealApplyMapper sealApplyMapper, RemoteDictService remoteDictService) {
        this.remoteUserService = remoteUserService;
        this.actReProcdefMapper = actReProcdefMapper;
        this.bizBusinessService = bizBusinessService;
        this.remoteDeptService = remoteDeptService;
        this.remoteFileService = remoteFileService;
        this.sealApplyMapper = sealApplyMapper;
        this.remoteDictService = remoteDictService;
    }

    private final static String OfficialSeal = "1";
    private final static String alCompanyId = "115";

    /**
     * 新增申请
     *
     * @param sealApply
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insert(BizSealApply sealApply) {
        try {
            String userName = SystemUtil.getUserName();
            SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
            Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(sysUser.getDeptId());
            String companyId1 = belongCompany2.get("companyId").toString();
            String companyName = belongCompany2.get("companyName").toString();
            // 抄送人去重
            String cc = sealApply.getCc();
            LinkedHashSet<String> strings = CollectUtil.twoClear(cc.split(","));
            // 杭州安联公章不抄送综合行政 2022-5-30
//            if (!strings.isEmpty()&&OfficialSeal.equals(sealApply.getSealType()) && alCompanyId.equals(companyId1)) {
//                Iterator<String> iterator = strings.iterator();
//                while (iterator.hasNext()) {
//                    Set<Long> unUsedCc = getUnUsedCc();
//                    for (Long aLong : unUsedCc) {
//
//                        if (iterator.next().equals(aLong.toString())) {
//                            iterator.remove();
//                        }
//                    }
//                }
//
//            }

            if (StrUtil.isNotBlank(cc)) {
                cc = String.join(",", strings);
                sealApply.setCc(cc);
            }

            sealApply.setCreateTime(new Date());
            sealApply.setUpdateTime(new Date());
            sealApply.setCreateBy(userName);
            sealApply.setUpdateBy(userName);
            //申请人部门
            sealApply.setDeptId(sysUser.getDeptId());
            sealApply.setTitle(sysUser.getUserName() + "提交的用印申请");
            int insert = sealApplyMapper.insert(sealApply);

            // 初始化流程
            BizBusiness business = initBusiness(sealApply);
            business.setCompanyId(SystemUtil.getCompanyId());
            business.setCompanyName(SystemUtil.getCompanyName());
            business.setDeptId(SystemUtil.getDeptId());
            business.setDeptName(sysUser.getDept().getDeptName());
            business.setApplyCode(sealApply.getApplyCode());
            bizBusinessService.insertBizBusiness(business);

            // 获取经营参数
//            SysConfig config1 = new SysConfig();
//            config1.setConfigKey(companyId1 + "g1fk");
//            List<SysConfig> list = remoteConfigService.listOperating(config1);
//            if (list.isEmpty()) {
//                return 2;
//            }

            Map<String, Object> variables = Maps.newHashMap();
            // 这里可以设置各个负责人，key跟模型的代理变量一致
            variables.put("cc", sealApply.getCc());
            variables.put("g1", sealApply.getSealType());
            bizBusinessService.startProcess(business, variables);

            // 将上传的临时文件转为有效文件
            int reimburse = remoteFileService.update(sealApply.getId(), sealApply.getApplyCode());
            if (reimburse == 0) {
                throw new StatefulException("将上传的文件转为有效文件失败");
            }
            return 1;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }

    private Set<Long> getUnUsedCc() {
        // 获取综合行政人员
        Set<Long> longs = remoteUserService.selectUserIdsHasRoles("82");
        return longs;
    }

    /**
     * 获取详情
     *
     * @param id
     * @return
     */
    @Override
    public BizSealApply selectById(Long id) {
        BizSealApply sealApply = sealApplyMapper.selectById(id);

        // 查询附件信息
        // 付款凭证
        List<SysAttachment> certificate = remoteFileService.getList(sealApply.getId(), "seal-certificate");
        if (certificate == null) {
            certificate = new ArrayList<>();
        }
        // 付款附件
        List<SysAttachment> appendix = remoteFileService.getList(sealApply.getId(), "seal-appendix");
        if (appendix == null) {
            appendix = new ArrayList<>();
        }

        sealApply.setVouchers(certificate);
        sealApply.setAttachment(appendix);

        Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(sealApply.getDeptId());
        //申请公司名称
        sealApply.setCompanyName(belongCompany2.get("companyName").toString());
        //申请部门名称
        sealApply.setDeptName(remoteDeptService.selectSysDeptByDeptId(sealApply.getDeptId()).getDeptName());
        if (sealApply.getUserDept() != null) {
            Map<String, Object> belongCompany = remoteDeptService.getBelongCompany2(sealApply.getUserDept());
            log.error("测试*************" + sealApply.getUserDept());
            //用印公司名称
            sealApply.setSubjectionCompanyName(belongCompany.get("companyName").toString());
            // 用印部门名称
            sealApply.setSubjectionDeptName(remoteDeptService.selectSysDeptByDeptId(sealApply.getUserDept()).getDeptName());
        }
        //申请人赋值
        SysUser sysUser2 = remoteUserService.selectSysUserByUsername(sealApply.getCreateBy());
        sealApply.setCreateByName(sysUser2.getUserName());
        //用印人人赋值
        SysUser sysUser3 = remoteUserService.selectSysUserByUserId(sealApply.getSealUser());
        sealApply.setSealUserName(sysUser3.getUserName());

        String ccStr = sealApply.getCc();
        if (StrUtil.isNotBlank(ccStr)) {
            List<String> ccList = new ArrayList<>();
            for (String cc : ccStr.split(",")) {
                SysUser ccUser = remoteUserService.selectSysUserByUserId(Long.valueOf(cc));
                ccList.add(ccUser.getUserName());
            }
            sealApply.setCcName(String.join("、", ccList));
        }

        String seal_type = remoteDictService.getLabel("seal_type", sealApply.getSealType());
        sealApply.setSealTypeName(seal_type);
        String seal_document = remoteDictService.getLabel("seal_document", sealApply.getDocumentType());
        sealApply.setDocumentTypeName(seal_document);
        return sealApply;
    }

    /**
     * 查询列表
     *
     * @param sealApplyDto
     * @return
     */
    @Override
    public List<SealApplyVo> selectList(SealApplyDto sealApplyDto) {
        QueryWrapper<SealApplyVo> wrapper = new QueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(sealApplyDto.getDocument()), "sa.document", sealApplyDto.getDocument());
        wrapper.like(StrUtil.isNotBlank(sealApplyDto.getSealUser()), "sa.seal_user", sealApplyDto.getSealUser());
        wrapper.eq(null != sealApplyDto.getUserDept(), "sa.user_dept", sealApplyDto.getUserDept());
        wrapper.eq("bu.del_flag", 0);
        wrapper.eq("bu.proc_def_key", "seal");
        wrapper.eq("bu.user_id", SystemUtil.getUserId());
        wrapper.between(null != sealApplyDto.getStartTime() && null != sealApplyDto.getEndTime(), "sa.create_time", sealApplyDto.getStartTime(), sealApplyDto.getEndTime());
        List<SealApplyVo> sealApplyVos = sealApplyMapper.selectVo(wrapper);
        sealApplyVos.stream().forEach(sealApplyVo -> {
            String seal_type = remoteDictService.getLabel("seal_type", sealApplyVo.getSealType());
            sealApplyVo.setSealTypeName(seal_type);
            String seal_document = remoteDictService.getLabel("seal_document", sealApplyVo.getDocumentType());
            sealApplyVo.setDocumentTypeName(seal_document);
        });
        return sealApplyVos;
    }

    /**
     * 初始化业务流程
     *
     * @param sealApply
     * @return
     */
    private BizBusiness initBusiness(BizSealApply sealApply) {
        // 查出最新发布的流程定义信息
        Example example = new Example(ActReProcdef.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("key", "seal");
        example.setOrderByClause("VERSION_ DESC");
        ActReProcdef actReProcdef = actReProcdefMapper.selectByExample(example).get(0);

        BizBusiness business = new BizBusiness();
        business.setTableId(sealApply.getId().toString());
        business.setProcDefId(actReProcdef.getId());
        business.setTitle(sealApply.getTitle());
        business.setProcName(actReProcdef.getName());
        long userId = SystemUtil.getUserId();
        business.setUserId(userId);

        SysUser user = remoteUserService.selectSysUserByUserId(userId);
        business.setApplyer(user.getUserName());
        business.setStatus(ActivitiConstant.STATUS_DEALING);
        business.setResult(ActivitiConstant.RESULT_DEALING);
        business.setApplyTime(new Date());
        return business;
    }
}
