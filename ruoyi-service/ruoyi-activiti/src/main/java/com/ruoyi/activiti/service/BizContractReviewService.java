package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.my_apply.BizContractReview;

/**
 * @Author yrb
 * @Date 2023/5/22 13:46
 * @Version 1.0
 * @Description 合同评审
 */
public interface BizContractReviewService {
    /**
     * 插入合同信息、项目信息
     * @return result
     */
    int insert(BizContractReview bizContractReview);

    /**
     * 查询合同信息、报价信息详情
     * @param id 主键id
     * @return 详情信息
     */
    BizContractReview findDetail(String id);

    /**
     * 流程撤销
     * @param contractId 合同ID
     */
    boolean revokeProcess(Long contractId);

    /**
     * 判断当前审批是否在处理中
     * @param contractId 合同ID
     * @return result
     */
    boolean isProcessing(Long contractId);
}
