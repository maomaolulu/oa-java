package com.ruoyi.activiti.service.impl;

import cn.hutool.core.exceptions.StatefulException;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import com.ruoyi.activiti.consts.ActivitiConstant;
import com.ruoyi.activiti.domain.SysAttachment;
import com.ruoyi.activiti.domain.my_apply.BizUniversalApply;
import com.ruoyi.activiti.domain.proc.ActReProcdef;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.feign.RemoteFileService;
import com.ruoyi.activiti.mapper.ActReProcdefMapper;
import com.ruoyi.activiti.mapper.BizUniversalApplyMapper;
import com.ruoyi.activiti.service.BizUniversalApplyService;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.utils.CollectUtil;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zx
 * @date 2022/1/14 17:19
 */
@Service
@Slf4j
public class BizUniversalApplyServiceImpl implements BizUniversalApplyService {
    private final RemoteUserService remoteUserService;
    private final ActReProcdefMapper actReProcdefMapper;
    private final IBizBusinessService bizBusinessService;
    private final RemoteDeptService remoteDeptService;
    private final RemoteFileService remoteFileService;
    private final BizUniversalApplyMapper universalApplyMapper;

    @Autowired
    public BizUniversalApplyServiceImpl(RemoteUserService remoteUserService, ActReProcdefMapper actReProcdefMapper, IBizBusinessService bizBusinessService,  RemoteDeptService remoteDeptService, RemoteFileService remoteFileService, BizUniversalApplyMapper universalApplyMapper) {
        this.remoteUserService = remoteUserService;
        this.actReProcdefMapper = actReProcdefMapper;
        this.bizBusinessService = bizBusinessService;
        this.remoteDeptService = remoteDeptService;
        this.remoteFileService = remoteFileService;
        this.universalApplyMapper = universalApplyMapper;
    }

    /**
     * 新增申请
     *
     * @param universalApply
     * @return
     */
    @Override
    public int insert(BizUniversalApply universalApply) {
        try {
            // 抄送人去重
            String cc = universalApply.getCc();
            if (StrUtil.isNotBlank(cc)) {
                cc = String.join(",", CollectUtil.twoClear(cc.split(",")));
                universalApply.setCc(cc);
            }
            String userName = SystemUtil.getUserName();
            SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
            Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(sysUser.getDeptId());
            String companyId1 = belongCompany2.get("companyId").toString();
            String companyName = belongCompany2.get("companyName").toString();
            universalApply.setCreateTime(new Date());
            universalApply.setUpdateTime(new Date());
            universalApply.setCreateBy(userName);
            universalApply.setUpdateBy(userName);
            //申请人部门
            universalApply.setDeptId(sysUser.getDeptId());
            universalApply.setTitle(sysUser.getUserName() + "提交的通用审批");
            int insert = universalApplyMapper.insert(universalApply);

            // 初始化流程
            BizBusiness business = initBusiness(universalApply);
            business.setCompanyId(SystemUtil.getCompanyId());
            business.setCompanyName(SystemUtil.getCompanyName());
            business.setDeptId(SystemUtil.getDeptId());
            business.setDeptName(sysUser.getDept().getDeptName());
            business.setApplyCode(universalApply.getApplyCode());
            bizBusinessService.insertBizBusiness(business);

            Map<String, Object> variables = Maps.newHashMap();
            // 这里可以设置各个负责人，key跟模型的代理变量一致
            variables.put("cc", universalApply.getCc());
            bizBusinessService.startProcess(business, variables);

            // 将上传的临时文件转为有效文件
            int reimburse = remoteFileService.update(universalApply.getId(), universalApply.getApplyCode());
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
    public BizUniversalApply selectById(Long id) {
        BizUniversalApply universalApply = universalApplyMapper.selectById(id);

        // 查询附件信息
        // 通用申请凭证
        List<SysAttachment> certificate = remoteFileService.getList(universalApply.getId(), "universal-certificate");
        if (certificate == null) {
            certificate = new ArrayList<>();
        }
        // 通用申请附件
        List<SysAttachment> appendix = remoteFileService.getList(universalApply.getId(), "universal-appendix");
        if (appendix == null) {
            appendix = new ArrayList<>();
        }

        universalApply.setVouchers(certificate);
        universalApply.setAttachment(appendix);

        Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(universalApply.getDeptId());
        if (belongCompany2.get("code") != null && "500".equals(belongCompany2.get("code").toString())) {
            //申请公司名称
            universalApply.setCompanyName("");
            log.error("此处数据异常，获取不到部门名称");
        } else {
            //申请公司名称
            universalApply.setCompanyName(belongCompany2.get("companyName").toString());
        }

        //申请部门名称
        universalApply.setDeptName(remoteDeptService.selectSysDeptByDeptId(universalApply.getDeptId()).getDeptName());
        //申请人赋值
        SysUser sysUser2 = remoteUserService.selectSysUserByUsername(universalApply.getCreateBy());
        universalApply.setCreateByName(sysUser2.getUserName());

        String ccStr = universalApply.getCc();
        if (StrUtil.isNotBlank(ccStr)) {
            List<String> ccList = new ArrayList<>();
            for (String cc : ccStr.split(",")) {
                SysUser ccUser = remoteUserService.selectSysUserByUserId(Long.valueOf(cc));
                ccList.add(ccUser.getUserName());
            }
            universalApply.setCcName(String.join("、", ccList));
        }
        return universalApply;
    }

    /**
     * 初始化业务流程
     *
     * @param universalApply
     * @return
     */
    private BizBusiness initBusiness(BizUniversalApply universalApply) {
        // 查出最新发布的流程定义信息
        Example example = new Example(ActReProcdef.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("key", "universal");
        example.setOrderByClause("VERSION_ DESC");
        ActReProcdef actReProcdef = actReProcdefMapper.selectByExample(example).get(0);

        BizBusiness business = new BizBusiness();
        business.setTableId(universalApply.getId().toString());
        business.setProcDefId(actReProcdef.getId());
        business.setTitle(universalApply.getTitle());
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
