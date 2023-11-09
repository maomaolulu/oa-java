package com.ruoyi.activiti.service.impl;

import cn.hutool.core.exceptions.StatefulException;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import com.ruoyi.activiti.consts.ActivitiConstant;
import com.ruoyi.activiti.domain.*;
import com.ruoyi.activiti.domain.my_apply.BizSalaryApproval;
import com.ruoyi.activiti.domain.proc.ActReProcdef;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.feign.RemoteFileService;
import com.ruoyi.activiti.mapper.ActReProcdefMapper;
import com.ruoyi.activiti.mapper.BizSalaryApprovalMapper;
import com.ruoyi.activiti.service.BizSalaryApprovalService;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.utils.CollectUtil;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.feign.RemoteUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 薪资核准
 * @author zx
 * @date 2022/3/8 21:25
 */
@Service
@Slf4j
public class BizSalaryApprovalServiceImpl implements BizSalaryApprovalService {
    private final RemoteUserService remoteUserService;
    private final ActReProcdefMapper actReProcdefMapper;
    private final IBizBusinessService bizBusinessService;
    private final RemoteDeptService remoteDeptService;
    private final RemoteFileService remoteFileService;
    private final BizSalaryApprovalMapper salaryApprovalMapper;
    @Autowired
    public BizSalaryApprovalServiceImpl(RemoteUserService remoteUserService, ActReProcdefMapper actReProcdefMapper, IBizBusinessService bizBusinessService, RemoteDeptService remoteDeptService, RemoteFileService remoteFileService, BizSalaryApprovalMapper salaryApprovalMapper) {
        this.remoteUserService = remoteUserService;
        this.actReProcdefMapper = actReProcdefMapper;
        this.bizBusinessService = bizBusinessService;
        this.remoteDeptService = remoteDeptService;
        this.remoteFileService = remoteFileService;
        this.salaryApprovalMapper = salaryApprovalMapper;
    }

    /**
     * 新增申请
     *
     * @param salaryApproval
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(BizSalaryApproval salaryApproval) {
        try {
            // 抄送人去重
            String cc = salaryApproval.getCc();
            if (StrUtil.isNotBlank(cc)) {
                cc = String.join(",", CollectUtil.twoClear(cc.split(",")));
                salaryApproval.setCc(cc);
            }
            String userName = SystemUtil.getUserName();
            SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
            Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(sysUser.getDeptId());
            String companyId1 = belongCompany2.get("companyId").toString();
            String companyName = belongCompany2.get("companyName").toString();
            salaryApproval.setCreateTime(new Date());
            salaryApproval.setUpdateTime(new Date());
            salaryApproval.setCreateBy(userName);
            salaryApproval.setUpdateBy(userName);

            salaryApproval.setName(remoteUserService.selectSysUserByUserId(salaryApproval.getUserId()).getUserName());
            //申请人部门
            salaryApproval.setDeptId(sysUser.getDeptId());
            salaryApproval.setTitle(sysUser.getUserName() + "提交的录用审批申请");
            int insert = salaryApprovalMapper.insert(salaryApproval);

            // 初始化流程
            BizBusiness business = initBusiness(salaryApproval);
            business.setCompanyId(SystemUtil.getCompanyId());
            business.setCompanyName(SystemUtil.getCompanyName());
            business.setDeptId(SystemUtil.getDeptId());
            business.setDeptName(sysUser.getDept().getDeptName());
            business.setApplyCode(salaryApproval.getApplyCode());
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
            variables.put("cc", salaryApproval.getCc());
            bizBusinessService.startProcess(business, variables);

            // 将上传的临时文件转为有效文件
            int reimburse = remoteFileService.update(salaryApproval.getId(), salaryApproval.getApplyCode());
            log.error(salaryApproval.getId()+"--------------"+salaryApproval.getApplyCode());
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

    /**
     * 获取详情
     *
     * @param id
     * @return
     */
    @Override
    public BizSalaryApproval selectById(Long id) {
        BizSalaryApproval salaryApproval = salaryApprovalMapper.selectById(id);

        // 查询附件信息
        // 图片
        List<SysAttachment> certificate = remoteFileService.getList(salaryApproval.getId(), "xzhz-certificate");
        if(certificate==null){
            certificate = new ArrayList<>();
        }
        // 附件
        List<SysAttachment> appendix = remoteFileService.getList(salaryApproval.getId(), "xzhz-appendix");
        if(appendix==null){
            appendix = new ArrayList<>();
        }

        salaryApproval.setVouchers(certificate);
        salaryApproval.setAttachment(appendix);

//        Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(salaryApproval.getDeptId());
//        //申请公司名称
//        salaryApproval.setCompanyName(belongCompany2.get("companyName").toString());
//        //申请部门名称
        salaryApproval.setDeptName(remoteDeptService.selectSysDeptByDeptId(salaryApproval.getDeptId()).getDeptName());
        salaryApproval.setUserDeptName(remoteDeptService.selectSysDeptByDeptId(salaryApproval.getUserDept()).getDeptName());
//        if(salaryApproval.getUserDept()!=null){
//            Map<String, Object> belongCompany = remoteDeptService.getBelongCompany2(salaryApproval.getUserDept());
//            //用印公司名称
//            salaryApproval.setSubjectionCompanyName(belongCompany.get("companyName").toString());
//            // 用印部门名称
//            salaryApproval.setSubjectionDeptName(remoteDeptService.selectSysDeptByDeptId(salaryApproval.getUserDept()).getDeptName());
//        }
//        //申请人赋值
//        SysUser sysUser2 = remoteUserService.selectSysUserByUsername(salaryApproval.getCreateBy());
//        salaryApproval.setCreateByName(sysUser2.getUserName());
//        //用印人人赋值
//        SysUser sysUser3 = remoteUserService.selectSysUserByUserId(salaryApproval.getSealUser());
//        salaryApproval.setSealUserName(sysUser3.getUserName());

        String ccStr = salaryApproval.getCc();
        if(StrUtil.isNotBlank(ccStr)){
            List<String> ccList = new ArrayList<>();
            for (String cc : ccStr.split(",")) {
                SysUser ccUser = remoteUserService.selectSysUserByUserId(Long.valueOf(cc));
                ccList.add(ccUser.getUserName());
            }
            salaryApproval.setCcName(String.join("、",ccList));
        }

//        String seal_type = remoteDictService.getLabel("seal_type", salaryApproval.getSealType());
//        salaryApproval.setSealTypeName(seal_type);
//        String seal_document = remoteDictService.getLabel("seal_document", salaryApproval.getDocumentType());
//        salaryApproval.setDocumentTypeName(seal_document);
        return salaryApproval;
    }
    /**
     * 初始化业务流程
     *
     * @param salaryApproval
     * @return
     */
    private BizBusiness initBusiness(BizSalaryApproval salaryApproval) {
        // 查出最新发布的流程定义信息
        Example example = new Example(ActReProcdef.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("key", "xz_approval");
        example.setOrderByClause("VERSION_ DESC");
        ActReProcdef actReProcdef = actReProcdefMapper.selectByExample(example).get(0);

        BizBusiness business = new BizBusiness();
        business.setTableId(salaryApproval.getId().toString());
        business.setProcDefId(actReProcdef.getId());
        business.setTitle(salaryApproval.getTitle());
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
