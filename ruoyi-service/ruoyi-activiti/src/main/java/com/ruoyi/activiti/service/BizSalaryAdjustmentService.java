package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.my_apply.BizSalaryAdjustment;

/**
 * 薪资调整
 * @author zx
 * @date 2022/3/8 21:23
 */
public interface BizSalaryAdjustmentService {
    /**
     * 新增申请
     * @param salaryAdjustment
     * @return
     */
    int insert(BizSalaryAdjustment salaryAdjustment);

    /**
     * 获取详情
     * @param id
     * @return
     */
    BizSalaryAdjustment selectById(Long id);
}
