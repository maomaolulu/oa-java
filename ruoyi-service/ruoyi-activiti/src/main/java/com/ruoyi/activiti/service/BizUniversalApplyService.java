package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.my_apply.BizUniversalApply;

/**
 * @author zx
 * @date 2022/1/14 17:19
 */
public interface BizUniversalApplyService {
    /**
     * 新增申请
     * @param universalApply
     * @return
     */
    int insert(BizUniversalApply universalApply);

    /**
     * 获取详情
     * @param id
     * @return
     */
    BizUniversalApply selectById(Long id);
}
