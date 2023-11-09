package com.ruoyi.activiti.service.impl.my_apply;

import cn.hutool.core.exceptions.StatefulException;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import com.ruoyi.activiti.consts.ActivitiConstant;
import com.ruoyi.activiti.domain.proc.ActReProcdef;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.domain.SysAttachment;
import com.ruoyi.activiti.domain.my_apply.BizDemandFeedback;
import com.ruoyi.activiti.feign.RemoteFileService;
import com.ruoyi.activiti.mapper.ActReProcdefMapper;
import com.ruoyi.activiti.mapper.my_apply.BizDemandFeedbackMapper;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.service.my_apply.BizDemandFeedbackService;
import com.ruoyi.activiti.utils.CollectUtil;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.feign.RemoteUserService;
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
 * 需求反馈
 *
 * @author zx
 * @date 2022/3/21 11:04
 */
@Service
@Slf4j
public class BizDemandFeedbackServiceImpl implements BizDemandFeedbackService {

    private final RemoteUserService remoteUserService;
    private final ActReProcdefMapper actReProcdefMapper;
    private final IBizBusinessService bizBusinessService;
    private final RemoteDeptService remoteDeptService;
    private final RemoteFileService remoteFileService;
    private final BizDemandFeedbackMapper demandFeedbackMapper;

    @Autowired
    public BizDemandFeedbackServiceImpl(RemoteUserService remoteUserService, ActReProcdefMapper actReProcdefMapper, IBizBusinessService bizBusinessService, RemoteDeptService remoteDeptService, RemoteFileService remoteFileService, BizDemandFeedbackMapper demandFeedbackMapper) {
        this.remoteUserService = remoteUserService;
        this.actReProcdefMapper = actReProcdefMapper;
        this.bizBusinessService = bizBusinessService;
        this.remoteDeptService = remoteDeptService;
        this.remoteFileService = remoteFileService;
        this.demandFeedbackMapper = demandFeedbackMapper;
    }


    /**
     * 新增申请
     *
     * @param demandFeedback
     * @return
     */
    @Override
    public int insert(BizDemandFeedback demandFeedback) {
        try {
            // 抄送人去重
            String cc = demandFeedback.getCc();
            if (StrUtil.isNotBlank(cc)) {
                cc = String.join(",", CollectUtil.twoClear(cc.split(",")));
                demandFeedback.setCc(cc);
            }
            String userName = SystemUtil.getUserName();
            SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
            Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(sysUser.getDeptId());
            String companyId1 = belongCompany2.get("companyId").toString();
            demandFeedback.setCompanyId(Long.valueOf(companyId1));
            demandFeedback.setCreateTime(new Date());
            demandFeedback.setUpdateTime(new Date());
            demandFeedback.setCreateBy(userName);
            demandFeedback.setUpdateBy(userName);
            //申请人部门
            demandFeedback.setDeptId(sysUser.getDeptId());
            demandFeedback.setTitle(sysUser.getUserName() + "提交的需求反馈");
            int insert = demandFeedbackMapper.insert(demandFeedback);


            // 初始化流程
            BizBusiness business = initBusiness(demandFeedback);
            business.setCompanyId(SystemUtil.getCompanyId());
            business.setCompanyName(SystemUtil.getCompanyName());
            business.setDeptId(SystemUtil.getDeptId());
            business.setDeptName(sysUser.getDept().getDeptName());
            business.setApplyCode(demandFeedback.getApplyCode());
            bizBusinessService.insertBizBusiness(business);

            Map<String, Object> variables = Maps.newHashMap();
            // 这里可以设置各个负责人，key跟模型的代理变量一致
            variables.put("cc", demandFeedback.getCc());
            bizBusinessService.startProcess(business, variables);

            // 将上传的临时文件转为有效文件
            int reimburse = remoteFileService.update(demandFeedback.getId(), demandFeedback.getApplyCode());
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
    public BizDemandFeedback selectById(Long id) {
        BizDemandFeedback demandFeedback = demandFeedbackMapper.selectById(id);

        // 查询附件信息
        // 需求反馈凭证
        List<SysAttachment> certificate = remoteFileService.getList(demandFeedback.getId(), "feedback-certificate");
        if (certificate == null) {
            certificate = new ArrayList<>();
        }
        // 需求反馈附件
        List<SysAttachment> appendix = remoteFileService.getList(demandFeedback.getId(), "feedback-appendix");
        if (appendix == null) {
            appendix = new ArrayList<>();
        }

        demandFeedback.setVouchers(certificate);
        demandFeedback.setAttachment(appendix);

        Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(demandFeedback.getDeptId());
        //申请公司名称
        demandFeedback.setCompanyName(belongCompany2.get("companyName").toString());
        //申请部门名称
        demandFeedback.setDeptName(remoteDeptService.selectSysDeptByDeptId(demandFeedback.getDeptId()).getDeptName());
        //申请人赋值
        SysUser sysUser2 = remoteUserService.selectSysUserByUsername(demandFeedback.getCreateBy());
        demandFeedback.setCreateByName(sysUser2.getUserName());
        // 所属系统名称
        if (demandFeedback.getAffiliatedSystem() == null) {
            demandFeedback.setAffiliatedSystem("");
        }
        switch (demandFeedback.getAffiliatedSystem()) {
            case "1":
                demandFeedback.setAffiliatedSystemName("云管家平台");
                break;
            case "2":
                demandFeedback.setAffiliatedSystemName("报价系统");
                break;
            case "3":
                demandFeedback.setAffiliatedSystemName("环境公卫运营系统");
                break;
            case "4":
                demandFeedback.setAffiliatedSystemName("安联云盘");
                break;
            case "5":
                demandFeedback.setAffiliatedSystemName("检评系统");
                break;
            case "6":
                demandFeedback.setAffiliatedSystemName("评价系统");
                break;
            case "7":
                demandFeedback.setAffiliatedSystemName("UV贴身管家");
                break;
            case "8":
                demandFeedback.setAffiliatedSystemName("信息化运营平台");
                break;
            default:
                demandFeedback.setAffiliatedSystemName("");
        }

        String ccStr = demandFeedback.getCc();
        if (StrUtil.isNotBlank(ccStr)) {
            List<String> ccList = new ArrayList<>();
            for (String cc : ccStr.split(",")) {
                SysUser ccUser = remoteUserService.selectSysUserByUserId(Long.valueOf(cc));
                ccList.add(ccUser.getUserName());
            }
            demandFeedback.setCcName(String.join("、", ccList));
        }
        return demandFeedback;
    }

    /**
     * 初始化业务流程
     *
     * @param demandFeedback
     * @return
     */
    private BizBusiness initBusiness(BizDemandFeedback demandFeedback) {
        // 查出最新发布的流程定义信息
        Example example = new Example(ActReProcdef.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("key", "feedback");
        example.setOrderByClause("VERSION_ DESC");
        ActReProcdef actReProcdef = actReProcdefMapper.selectByExample(example).get(0);

        BizBusiness business = new BizBusiness();
        business.setTableId(demandFeedback.getId().toString());
        business.setProcDefId(actReProcdef.getId());
        business.setTitle(demandFeedback.getTitle());
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
