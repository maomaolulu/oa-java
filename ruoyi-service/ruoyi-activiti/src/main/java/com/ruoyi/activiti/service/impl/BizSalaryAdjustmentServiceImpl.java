package com.ruoyi.activiti.service.impl;

import cn.hutool.core.exceptions.StatefulException;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import com.ruoyi.activiti.consts.ActivitiConstant;
import com.ruoyi.activiti.domain.*;
import com.ruoyi.activiti.domain.my_apply.BizSalaryAdjustment;
import com.ruoyi.activiti.domain.proc.ActReProcdef;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.feign.RemoteFileService;
import com.ruoyi.activiti.mapper.ActReProcdefMapper;
import com.ruoyi.activiti.mapper.BizSalaryAdjustmentMapper;
import com.ruoyi.activiti.service.BizSalaryAdjustmentService;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.utils.CollectUtil;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.feign.RemoteUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 薪资调整
 * @author zx
 * @date 2022/3/8 21:24
 */
@Service
@Slf4j
public class BizSalaryAdjustmentServiceImpl implements BizSalaryAdjustmentService {
    private final RemoteUserService remoteUserService;
    private final ActReProcdefMapper actReProcdefMapper;
    private final IBizBusinessService bizBusinessService;
    private final RemoteDeptService remoteDeptService;
    private final RemoteFileService remoteFileService;
    private final BizSalaryAdjustmentMapper salaryAdjustmentMapper;

    public BizSalaryAdjustmentServiceImpl(RemoteUserService remoteUserService, ActReProcdefMapper actReProcdefMapper, IBizBusinessService bizBusinessService, RemoteDeptService remoteDeptService, RemoteFileService remoteFileService, BizSalaryAdjustmentMapper salaryAdjustmentMapper) {
        this.remoteUserService = remoteUserService;
        this.actReProcdefMapper = actReProcdefMapper;
        this.bizBusinessService = bizBusinessService;
        this.remoteDeptService = remoteDeptService;
        this.remoteFileService = remoteFileService;
        this.salaryAdjustmentMapper = salaryAdjustmentMapper;
    }

    /**
     * 新增申请
     *
     * @param salaryAdjustment
     * @return
     */
    @Override
    public int insert(BizSalaryAdjustment salaryAdjustment) {
        try {
            // 抄送人去重
            String cc = salaryAdjustment.getCc();
            if (StrUtil.isNotBlank(cc)) {
                cc = String.join(",", CollectUtil.twoClear(cc.split(",")));
                salaryAdjustment.setCc(cc);
            }
            String userName = SystemUtil.getUserName();
            SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
            Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(sysUser.getDeptId());
            String companyId1 = belongCompany2.get("companyId").toString();
            String companyName = belongCompany2.get("companyName").toString();
            salaryAdjustment.setCreateTime(new Date());
            salaryAdjustment.setUpdateTime(new Date());
            salaryAdjustment.setCreateBy(userName);
            salaryAdjustment.setUpdateBy(userName);
            salaryAdjustment.setName(remoteUserService.selectSysUserByUserId(salaryAdjustment.getUserId()).getUserName());
            //申请人部门
            salaryAdjustment.setDeptId(sysUser.getDeptId());
            salaryAdjustment.setTitle(sysUser.getUserName() + "提交的薪资调整申请");
            int insert = salaryAdjustmentMapper.insert(salaryAdjustment);

            // 初始化流程
            BizBusiness business = initBusiness(salaryAdjustment);
            business.setCompanyId(SystemUtil.getCompanyId());
            business.setCompanyName(SystemUtil.getCompanyName());
            business.setDeptId(SystemUtil.getDeptId());
            business.setDeptName(sysUser.getDept().getDeptName());
            business.setApplyCode(salaryAdjustment.getApplyCode());
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
            variables.put("cc", salaryAdjustment.getCc());
            bizBusinessService.startProcess(business, variables);

            // 将上传的临时文件转为有效文件
            int reimburse = remoteFileService.update(salaryAdjustment.getId(), salaryAdjustment.getApplyCode());
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
    public BizSalaryAdjustment selectById(Long id) {
        BizSalaryAdjustment salaryAdjustment = salaryAdjustmentMapper.selectById(id);

        // 查询附件信息
        List<SysAttachment> appendix = remoteFileService.getList(salaryAdjustment.getId(), "xztz-appendix");
        if(appendix==null){
            appendix = new ArrayList<>();
        }

        salaryAdjustment.setAttachment(appendix);

//        Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(salaryAdjustment.getDeptId());
//        //申请公司名称
//        salaryAdjustment.setCompanyName(belongCompany2.get("companyName").toString());
//        //申请部门名称
        salaryAdjustment.setDeptName(remoteDeptService.selectSysDeptByDeptId(salaryAdjustment.getDeptId()).getDeptName());
        salaryAdjustment.setUserDeptName(remoteDeptService.selectSysDeptByDeptId(salaryAdjustment.getUserDept()).getDeptName());
//        if(salaryAdjustment.getUserDept()!=null){
//            Map<String, Object> belongCompany = remoteDeptService.getBelongCompany2(salaryAdjustment.getUserDept());
//            //用印公司名称
//            salaryAdjustment.setSubjectionCompanyName(belongCompany.get("companyName").toString());
//            // 用印部门名称
//            salaryAdjustment.setSubjectionDeptName(remoteDeptService.selectSysDeptByDeptId(salaryAdjustment.getUserDept()).getDeptName());
//        }
//        //申请人赋值
//        SysUser sysUser2 = remoteUserService.selectSysUserByUsername(salaryAdjustment.getCreateBy());
//        salaryAdjustment.setCreateByName(sysUser2.getUserName());
//        //用印人人赋值
//        SysUser sysUser3 = remoteUserService.selectSysUserByUserId(salaryAdjustment.getSealUser());
//        salaryAdjustment.setSealUserName(sysUser3.getUserName());

//        String ccStr = salaryAdjustment.getCc();
//        if(StrUtil.isNotBlank(ccStr)){
//            List<String> ccList = new ArrayList<>();
//            for (String cc : ccStr.split(",")) {
//                SysUser ccUser = remoteUserService.selectSysUserByUserId(Long.valueOf(cc));
//                ccList.add(ccUser.getUserName());
//            }
//            salaryAdjustment.setCcName(String.join("、",ccList));
//        }

//        String seal_type = remoteDictService.getLabel("seal_type", salaryAdjustment.getSealType());
//        salaryAdjustment.setSealTypeName(seal_type);
//        String seal_document = remoteDictService.getLabel("seal_document", salaryAdjustment.getDocumentType());
//        salaryAdjustment.setDocumentTypeName(seal_document);
        return salaryAdjustment;
    }
    /**
     * 初始化业务流程
     *
     * @param salaryAdjustment
     * @return
     */
    private BizBusiness initBusiness(BizSalaryAdjustment salaryAdjustment) {
        // 查出最新发布的流程定义信息
        Example example = new Example(ActReProcdef.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("key", "xz_adjustment");
        example.setOrderByClause("VERSION_ DESC");
        ActReProcdef actReProcdef = actReProcdefMapper.selectByExample(example).get(0);

        BizBusiness business = new BizBusiness();
        business.setTableId(salaryAdjustment.getId().toString());
        business.setProcDefId(actReProcdef.getId());
        business.setTitle(salaryAdjustment.getTitle());
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
