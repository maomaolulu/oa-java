package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.fiance.BizPaymentApply;

import java.util.List;
import java.util.Map;

/**
 * 付款申请
 * @author zx
 * @date 2021/12/19 20:07
 */
public interface BizPaymentApplyService {


    /**
     * 新增申请
     * @param paymentApply
     * @return
     */
    int insert( BizPaymentApply paymentApply);

    /**
     * 获取详情
     * @param id
     * @return
     */
    BizPaymentApply selectById(Long id);

    /**
     * 获取关联审批单下拉列表
     * @return
     */
    List<Map<String, Object>> getAuditList(String source);
}
