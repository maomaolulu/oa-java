package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.fiance.BizReimburseApply;
import com.ruoyi.common.core.domain.R;

import java.util.List;
import java.util.Map;

/**
 * 报销申请
 * @author zx
 * @date 2021/12/16 11:08
 */
public interface BizReimburseApplyService {
    /**
     * 新增申请
     * @param reimburseApply
     * @return
     */
    R insert(BizReimburseApply reimburseApply);

    /**
     * 获取详情
     * @param id
     * @return
     */
    BizReimburseApply selectBizReimburseApplyById(Long id);

    /**
     * 获取关联审批单下拉列表
     * @return
     */
    List<Map<String, Object>> getAuditList(String source,String type);
}
