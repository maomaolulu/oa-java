package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.my_apply.BizBidApply;

/**
 * @Author yrb
 * @Date 2023/5/31 15:10
 * @Version 1.0
 * @Description 招标审批
 */
public interface BizBidService {
    /**
     * 插入招标信息
     * @param bizBidApply 招标信息
     * @return result
     */
    int insert(BizBidApply bizBidApply);

    /**
     * 查询招标审批详情
     * @param id 主键id
     * @return 详情信息
     */
    BizBidApply findDetail(String id);
}
