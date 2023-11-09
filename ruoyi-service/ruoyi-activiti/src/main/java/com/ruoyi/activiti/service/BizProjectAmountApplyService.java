package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.dto.BizProjectAmountApplyDto;
import com.ruoyi.activiti.domain.my_apply.BizProjectAmountApply;

import java.util.List;

/**
 * 项目金额调整申请
 * @author zh
 * @date 2023/03/23
 */
public interface BizProjectAmountApplyService {
    /**
     * 新增申请
     * @param parameter
     * @return
     */
    int insert(BizProjectAmountApply parameter);

    /**
     * 获取详情
     * @param id
     * @return
     */
    BizProjectAmountApply selectById(Long id);

    /**
     * 判断该项目是否处于审批状态
     * @param identifier 项目编号
     * @return result
     */
    boolean isProcessing(String identifier);

    /**
     * 查询项目修改列表
     * @param bizProjectAmountApplyDto 条件信息
     * @return result
     */
    List<BizProjectAmountApplyDto> findBizProjectAmountApplyList(BizProjectAmountApplyDto bizProjectAmountApplyDto);

    /**
     * 修改状态失败项目重新同步
     * @param bizProjectAmountApplyDto 条件信息
     * @return 更新结果
     */
    String updateAgain(BizProjectAmountApplyDto bizProjectAmountApplyDto);
}
