package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.my_apply.BizSalaryApproval;

/**
 * 薪资核准
 * @author zx
 * @date 2022/3/8 21:23
 */
public interface BizSalaryApprovalService {
    /**
     * 新增申请
     * @param salaryApproval
     * @return
     */
    int insert(BizSalaryApproval salaryApproval);

    /**
     * 获取详情
     * @param id
     * @return
     */
    BizSalaryApproval selectById(Long id);
}
