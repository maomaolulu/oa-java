package com.ruoyi.activiti.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.ruoyi.activiti.consts.ActivitiConstant;
import com.ruoyi.activiti.domain.dto.BizQuotationApplyDTO;
import com.ruoyi.activiti.domain.my_apply.BizQuotationApply;
import com.ruoyi.activiti.domain.proc.ActReProcdef;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.mapper.ActReProcdefMapper;
import com.ruoyi.activiti.mapper.BizQuotationApplyMapper;
import com.ruoyi.activiti.service.BizQuotationApplyService;
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
 * @Date 2023/5/4 17:50
 * @Version 1.0
 * @Description 报价申请
 */
@Service
@Slf4j
public class BizQuotationApplyServiceImpl implements BizQuotationApplyService {
    private final RemoteUserService remoteUserService;
    private final BizQuotationApplyMapper bizQuotationApplyMapper;
    private final ActReProcdefMapper actReProcdefMapper;
    private final IBizBusinessService bizBusinessService;
    private final ProcessService processService;

    public BizQuotationApplyServiceImpl(RemoteUserService remoteUserService,
                                        BizQuotationApplyMapper bizQuotationApplyMapper,
                                        ActReProcdefMapper actReProcdefMapper,
                                        IBizBusinessService bizBusinessService,
                                        ProcessService processService) {
        this.remoteUserService = remoteUserService;
        this.bizQuotationApplyMapper = bizQuotationApplyMapper;
        this.actReProcdefMapper = actReProcdefMapper;
        this.bizBusinessService = bizBusinessService;
        this.processService = processService;
    }

    /**
     * 操作报价申请信息
     *
     * @return result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(BizQuotationApplyDTO bizQuotationApplyDTO) {
        BizQuotationApply bizQuotationApply = bizQuotationApplyDTO.getBizQuotationApply();
        // 初始化报价申请信息
        initBizQuotationApply(bizQuotationApply);
        int insert = bizQuotationApplyMapper.insert(bizQuotationApply);
        if (insert != 1) {
            return -2;
        }
        // 初始化流程信息
        BizBusiness bizBusiness = initBusiness(bizQuotationApply);
        int i = bizBusinessService.insertBizBusiness(bizBusiness);
        if (i != 1) {
            throw new RuntimeException("流程信息插入失败");
        }
        // 开启流程
        Map<String, Object> variables = Maps.newHashMap();
        bizBusinessService.startProcess(bizBusiness, variables);
        return 1;
    }

    /**
     * 查询报价审批详情
     *
     * @param id 主键id
     * @return 详情信息
     */
    @Override
    public BizQuotationApply findDetail(String id) {
        return bizQuotationApplyMapper.selectById(id);
    }

    /**
     * 判断当前报价审批是否在处理中
     *
     * @param code 报价单
     * @return result
     */
    @Override
    public boolean isProcessing(String code) {
        BizBusiness bizBusiness = getBizBusinessInfo(code);
        return bizBusiness != null && StrUtil.isNotBlank(bizBusiness.getProcInstId());
    }

    /**
     * 流程撤销
     *
     * @param code 报价单编号
     */
    @Override
    public boolean revokeProcess(String code) {
        BizBusiness bizBusiness = getBizBusinessInfo(code);
        if (bizBusiness != null && StrUtil.isNotBlank(bizBusiness.getProcInstId())) {
            processService.revokeProcess(bizBusiness.getProcInstId(), "");
            return true;
        }else{
            return false;
        }
    }

    /**
     * 获取BizBusiness
     *
     * @param code 报价单
     * @return result
     */
    private BizBusiness getBizBusinessInfo(String code) {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("t1.code", code);
        queryWrapper.eq("t2.`status`", 1);
        queryWrapper.eq("t2.proc_def_key", "quotation");
        queryWrapper.last("limit 1");
        return bizQuotationApplyMapper.selectOneRecord(queryWrapper);
    }

    private void initBizQuotationApply(BizQuotationApply bizQuotationApply) {
        String salesmen = bizQuotationApply.getSalesmen();
        bizQuotationApply.setApplicant("上海量远/市场部/" + salesmen);
        bizQuotationApply.setTitle(salesmen + "提交的报价申请");
        bizQuotationApply.setApplyCode(CodeUtil.getCode("BJ"));
        bizQuotationApply.setCreateTime(new Date());
    }

    /**
     * 初始化业务流程
     *
     * @param bizQuotationApply 报价申请信息
     * @return result
     */
    private BizBusiness initBusiness(BizQuotationApply bizQuotationApply) {
        // 查出最新发布的流程定义信息
        Example example = new Example(ActReProcdef.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("key", "quotation");
        example.setOrderByClause("VERSION_ DESC");
        ActReProcdef actReProcdef = actReProcdefMapper.selectByExample(example).get(0);
        SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
        BizBusiness business = new BizBusiness();
        business.setTableId(bizQuotationApply.getId().toString());
        business.setProcDefId(actReProcdef.getId());
        business.setTitle(bizQuotationApply.getTitle());
        business.setProcName(actReProcdef.getName());
        business.setStatus(ActivitiConstant.STATUS_DEALING);
        business.setResult(ActivitiConstant.RESULT_DEALING);
        business.setApplyTime(new Date());
        business.setApplyCode(bizQuotationApply.getApplyCode());

        business.setUserId(SystemUtil.getUserId());
        business.setApplyer(SystemUtil.getUserName());
        business.setCompanyId(SystemUtil.getCompanyId());
        business.setCompanyName(SystemUtil.getCompanyName());
        business.setDeptId(SystemUtil.getDeptId());
        business.setDeptName(sysUser.getDept().getDeptName());

        return business;
    }

}
