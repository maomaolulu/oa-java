package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.dto.BizApplyLimitDTO;
import com.ruoyi.activiti.domain.vo.BizApplyLimitVO;

/**
 * @Author yrb
 * @Date 2023/4/19 15:03
 * @Version 1.0
 * @Description
 */
public interface BizApplyLimitService {
    /**
     * 插入限制信息
     * @param bizApplyLimitDTO 限制信息
     * @return result
     * @throws Exception 异常
     */
    void insertLimitInfo(BizApplyLimitDTO bizApplyLimitDTO) throws Exception;

    /**
     * 通过processKey获取限制信息
     * @param processKey 费用类型唯一标识
     * @return result
     */
    BizApplyLimitVO findLimitInfo(String processKey);
}
