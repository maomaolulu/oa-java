package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.asset.BizScrappedApply;
import com.ruoyi.common.core.domain.R;

/**
 * @description: 报废出库申请
 * @author: zx
 * @date: 2021/11/21 19:09
 */
public interface BizScrappedApplyService {
    /**
     * 新增报废出库申请
     * @param scrappedApply
     * @return
     */
    R insert(BizScrappedApply scrappedApply);

    /**
     * 查询详情
     * @param tableId
     * @return
     */
    BizScrappedApply selectBizScrappedApplyById(String tableId);

    void test(Long deptId);
}
