package com.ruoyi.activiti.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.ruoyi.activiti.consts.ActivitiConstant;
import com.ruoyi.activiti.domain.my_apply.BizContractInfo;
import com.ruoyi.activiti.domain.my_apply.BizContractProjectInfo;
import com.ruoyi.activiti.domain.my_apply.BizContractReview;
import com.ruoyi.activiti.domain.proc.ActReProcdef;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.mapper.ActReProcdefMapper;
import com.ruoyi.activiti.mapper.BizContractInfoMapper;
import com.ruoyi.activiti.mapper.BizContractProjectInfoMapper;
import com.ruoyi.activiti.service.BizContractReviewService;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.service.ProcessService;
import com.ruoyi.activiti.utils.CodeUtil;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.Map;

/**
 * @Author yrb
 * @Date 2023/5/22 13:48
 * @Version 1.0
 * @Description 合同评审
 */
@Service
@Slf4j
public class BizContractReviewServiceImpl implements BizContractReviewService {
    private final RemoteUserService remoteUserService;
    private final BizContractInfoMapper bizContractInfoMapper;
    private final ActReProcdefMapper actReProcdefMapper;
    private final IBizBusinessService bizBusinessService;
    private final BizContractProjectInfoMapper bizContractProjectInfoMapper;
    private final ProcessService processService;

    public BizContractReviewServiceImpl(RemoteUserService remoteUserService,
                                        BizContractInfoMapper bizContractInfoMapper,
                                        ActReProcdefMapper actReProcdefMapper,
                                        IBizBusinessService bizBusinessService,
                                        BizContractProjectInfoMapper bizContractProjectInfoMapper,
                                        ProcessService processService) {
        this.remoteUserService = remoteUserService;
        this.bizContractInfoMapper = bizContractInfoMapper;
        this.actReProcdefMapper = actReProcdefMapper;
        this.bizBusinessService = bizBusinessService;
        this.bizContractProjectInfoMapper = bizContractProjectInfoMapper;
        this.processService = processService;
    }

    /**
     * 插入合同信息、项目信息
     *
     * @param bizContractReview 合同、项目信息
     * @return result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(BizContractReview bizContractReview) {
        BizContractInfo bizContractInfo = bizContractReview.getBizContractInfo();
        // 合同ID是通过ID传过来的
        bizContractInfo.setContractId(bizContractInfo.getId());
        bizContractInfo.setId(null);
        // 初始化合同信息
        initBizContractInfo(bizContractInfo);
        int i = bizContractInfoMapper.insert(bizContractInfo);
        if (i == 0) {
            return -2;
        }
        // 插入项目信息
        int j = bizContractProjectInfoMapper.insertList(bizContractReview.getBizContractProjectInfoList());
        if (j == 0) {
            throw new RuntimeException("合同关联的项目信息插入失败");
        }
        // 初始化流程信息
        BizBusiness bizBusiness = initBusiness(bizContractInfo);
        int k = bizBusinessService.insertBizBusiness(bizBusiness);
        if (k != 1) {
            throw new RuntimeException("流程信息插入失败");
        }
        // 开启流程
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("cc", bizContractInfo.getCc());
        bizBusinessService.startProcess(bizBusiness, variables);
        return 1;
    }

    /**
     * 查询合同信息、报价信息详情
     *
     * @param id 主键id
     * @return 详情信息
     */
    @Override
    public BizContractReview findDetail(String id) {
        BizContractReview bizContractReview = new BizContractReview();
        // 获取合同信息
        BizContractInfo bizContractInfo = bizContractInfoMapper.selectById(id);
        bizContractReview.setBizContractInfo(bizContractInfo);
        // 获取项目信息
        BizContractProjectInfo projectInfo = new BizContractProjectInfo();
        projectInfo.setContractId(bizContractInfo.getContractId());
        bizContractReview.setBizContractProjectInfoList(bizContractProjectInfoMapper.select(projectInfo));
        return bizContractReview;
    }

    /**
     * 流程撤销
     *
     * @param contractId 合同ID
     */
    @Override
    public boolean revokeProcess(Long contractId) {
        BizBusiness bizBusiness = getBizBusinessInfo(contractId);
        if (bizBusiness != null && StrUtil.isNotBlank(bizBusiness.getProcInstId())) {
            processService.revokeProcess(bizBusiness.getProcInstId(), "");
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断当前审批是否在处理中
     *
     * @param contractId 合同ID
     * @return result
     */
    @Override
    public boolean isProcessing(Long contractId) {
        BizBusiness bizBusiness = getBizBusinessInfo(contractId);
        return bizBusiness != null && StrUtil.isNotBlank(bizBusiness.getProcInstId());
    }

    /**
     * 获取BizBusiness
     *
     * @param contractId 合同ID
     * @return result
     */
    private BizBusiness getBizBusinessInfo(Long contractId) {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("t1.contract_id", contractId);
        queryWrapper.eq("t2.`status`", 1);
        queryWrapper.eq("t2.proc_def_key", "contract-review");
        queryWrapper.last("limit 1");
        return bizContractInfoMapper.selectOneRecord(queryWrapper);
    }

    /**
     * 初始化合同信息
     *
     * @param bizContractInfo 合同基本信息
     */
    private void initBizContractInfo(BizContractInfo bizContractInfo) {
        String userNameCn = SystemUtil.getUserNameCn();
        bizContractInfo.setApplicant("上海量远/市场部/" + userNameCn);
        bizContractInfo.setTitle(userNameCn + "提交的合同审批申请");
        bizContractInfo.setApplyCode(CodeUtil.getCode("HTPS"));
        bizContractInfo.setCreateTime(new Date());
        // 抄送人
        bizContractInfo.setCc("222,221,958");
        bizContractInfo.setCcCn("刘俊美；陈惠青；刘倩");
        // 拼接完整的地址
        initAddress(bizContractInfo);
    }

    /**
     * 拼接完整的地址
     *
     * @param bizContractInfo 合同信息
     */
    private void initAddress(BizContractInfo bizContractInfo) {
        String province = bizContractInfo.getProvince();
        if (StrUtil.isBlank(province)) {
            province = "";
        }
        String city = bizContractInfo.getCity();
        if (StrUtil.isBlank(city)) {
            city = "";
        }
        String area = bizContractInfo.getArea();
        if (StrUtil.isBlank(area)) {
            area = "";
        }
        String officeAddress = bizContractInfo.getOfficeAddress();
        if (StrUtil.isBlank(officeAddress)) {
            officeAddress = "";
        }
        bizContractInfo.setOfficeAddress(province + city + area + officeAddress);
    }

    /**
     * 初始化业务流程
     *
     * @param bizContractInfo 合同信息
     * @return result
     */
    private BizBusiness initBusiness(BizContractInfo bizContractInfo) {
        // 查出最新发布的流程定义信息
        Example example = new Example(ActReProcdef.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("key", "contract-review");
        example.setOrderByClause("VERSION_ DESC");
        ActReProcdef actReProcdef = actReProcdefMapper.selectByExample(example).get(0);
        SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
        BizBusiness business = new BizBusiness();
        business.setTableId(bizContractInfo.getId().toString());
        business.setProcDefId(actReProcdef.getId());
        business.setTitle(bizContractInfo.getTitle());
        business.setProcName(actReProcdef.getName());
        business.setStatus(ActivitiConstant.STATUS_DEALING);
        business.setResult(ActivitiConstant.RESULT_DEALING);
        business.setApplyTime(new Date());
        business.setApplyCode(bizContractInfo.getApplyCode());

        business.setUserId(SystemUtil.getUserId());
        business.setApplyer(SystemUtil.getUserName());
        business.setCompanyId(SystemUtil.getCompanyId());
        business.setCompanyName(SystemUtil.getCompanyName());
        business.setDeptId(SystemUtil.getDeptId());
        business.setDeptName(sysUser.getDept().getDeptName());

        return business;
    }
}
