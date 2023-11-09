package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.fiance.BizOtherChargeApply;
import com.ruoyi.activiti.domain.fiance.BizOtherChargeInfo;
import com.ruoyi.activiti.domain.dto.BizOtherChargeApplyDto;
import com.ruoyi.common.core.domain.R;

import java.util.List;

/**
 * @author zx
 * @date 2022-3-17 21:27:43
 * @desc 其他费用申请
 */
public interface BizOtherChargeApplyService {

    /**
     * 查询其他费用申请
     *
     * @param id 其他费用申请ID
     * @return 其他费用申请
     */
    public BizOtherChargeApplyDto selectBizOtherChargeApplyById(Long id) ;
    /**
     * 查询其他费用申请列表
     *
     * @param dto 其他费用申请
     * @return 其他费用申请
     */

    public List<BizOtherChargeApplyDto> selectBizOtherChargeApply(BizOtherChargeApplyDto dto);

    /**
     * 其他费用费申请新增
     *
     * @param otherChargeApply 其他费用新增
     * @return 结果
     */

    public R insertBizOtherChargeApply(BizOtherChargeApply otherChargeApply);
    /**
     * 其他费用费信息
     *
     * @param applyId 其他费用费ID
     * @return 其他费用费信息
     */

    public List<BizOtherChargeInfo> selectBizOtherChargeInfoList(Long applyId);

    /**
     * 查询详情
     * @param otherChargeApplyDto
     * @return
     */
    public BizOtherChargeApplyDto getPurchase(BizOtherChargeApplyDto otherChargeApplyDto) ;
    /**
     * 查询详情
     */
    public BizOtherChargeApplyDto selectOne( Long tableId);
}
