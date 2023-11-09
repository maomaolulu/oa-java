package com.ruoyi.activiti.service.my_apply;

import com.ruoyi.activiti.domain.my_apply.BizDemandFeedback;

/**
 * 需求反馈
 * @author zx
 * @date 2022/3/21 11:04
 */
public interface BizDemandFeedbackService {
    /**
     * 新增申请
     * @param demandFeedback
     * @return
     */
    int insert(BizDemandFeedback demandFeedback);

    /**
     * 获取详情
     * @param id
     * @return
     */
    BizDemandFeedback selectById(Long id);
}
