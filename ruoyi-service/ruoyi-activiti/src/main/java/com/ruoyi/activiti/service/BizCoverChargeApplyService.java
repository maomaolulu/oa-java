package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.fiance.BizCoverChargeApply;
import com.ruoyi.activiti.domain.fiance.BizCoverChargeInfo;
import com.ruoyi.activiti.domain.dto.BizCoverChargeApplyDto;
import com.ruoyi.common.core.domain.R;

import java.util.List;

/**
 * @author zx
 * @date 2022-3-16 22:01:29
 * @desc 服务费申请
 */
public interface BizCoverChargeApplyService {

    /**
     * 查询评审服务申请
     *
     * @param id 评审服务申请ID
     * @return 评审服务申请
     */
    public BizCoverChargeApplyDto selectBizCoverChargeApplyById(Long id) ;
    /**
     * 查询评审服务申请列表
     *
     * @param dto 评审服务申请
     * @return 评审服务申请
     */

    public List<BizCoverChargeApplyDto> selectBizCoverChargeApply(BizCoverChargeApplyDto dto);

    /**
     * 评审服务费申请新增
     *
     * @param coverChargeApply 评审、服务费新增
     * @return 结果
     */

    public R insertBizCoverChargeApply(BizCoverChargeApply coverChargeApply);
    /**
     * 评审服务费信息
     *
     * @param applyId 评审服务费ID
     * @return 评审服务费信息
     */

    public List<BizCoverChargeInfo> selectBizCoverChargeInfoList(Long applyId);

    /**
     * 查询详情
     * @param coverChargeApplyDto
     * @return
     */
    public BizCoverChargeApplyDto getPurchase(BizCoverChargeApplyDto coverChargeApplyDto) ;
    /**
     * 查询详情
     */
    public BizCoverChargeApplyDto selectOne( Long tableId);
}
