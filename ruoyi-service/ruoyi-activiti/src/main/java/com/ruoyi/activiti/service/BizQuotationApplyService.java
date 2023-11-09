package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.dto.BizQuotationApplyDTO;
import com.ruoyi.activiti.domain.my_apply.BizQuotationApply;

/**
 * @Author yrb
 * @Date 2023/5/4 17:49
 * @Version 1.0
 * @Description
 */
public interface BizQuotationApplyService {
    /**
     * 插入报价申请信息
     * @return result
     */
    int insert(BizQuotationApplyDTO bizQuotationApplyDTO);

    /**
     * 查询报价审批详情
     * @param id 主键id
     * @return 详情信息
     */
    BizQuotationApply findDetail(String id);

    /**
     * 判断当前报价审批是否在处理中
     * @param code 报价单编号
     * @return result
     */
    boolean isProcessing(String code);

    /**
     * 流程撤销
     * @param code 报价单编号
     */
    boolean revokeProcess(String code);
}
