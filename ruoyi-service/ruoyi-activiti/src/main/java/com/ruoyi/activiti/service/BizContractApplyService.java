package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.fiance.BizContractApply;
import com.ruoyi.activiti.domain.dto.BizContractApplyDto;
import com.ruoyi.common.core.domain.R;

import java.util.List;

/**
 * @author zh
 * @date 2021/12/24
 * @desc 合同审批
 */
public interface BizContractApplyService {

    /**
     * 查询合同审批申请列表
     *
     * @param dto 合同审批
     * @return 合同审批
     */

    public List<BizContractApplyDto> selectBizContractApply(BizContractApplyDto dto);

    /**
     * 新增合同审批
     *
     * @param bizContractApply 新增合同审批
     * @return 结果
     */

    public R insertBizContractApply(BizContractApply bizContractApply);

    /**
     * 详情
     * @param dto
     * @return
     */
    BizContractApplyDto getPurchase(BizContractApplyDto dto);

    /**
     * 查询合同审批详情
     * @param
     * @return
     */
    BizContractApplyDto selectOne(Long tableId);
}
