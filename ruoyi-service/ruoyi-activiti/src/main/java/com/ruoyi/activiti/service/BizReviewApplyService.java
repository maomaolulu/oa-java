package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.dto.BizReviewApplyDto;
import com.ruoyi.activiti.domain.fiance.BizReviewApply;
import com.ruoyi.activiti.domain.fiance.BizReviewInfo;
import com.ruoyi.common.core.domain.R;

import java.util.List;

/**
 * @author zh
 * @date 2021/12/20
 * @desc 审批服务费申请
 */
public interface BizReviewApplyService {

    /**
     * 查询评审服务申请
     *
     * @param id 评审服务申请ID
     * @return 评审服务申请
     */
    public BizReviewApplyDto selectBizReviewApplyById(Long id) ;
    /**
     * 查询评审服务申请列表
     *
     * @param dto 评审服务申请
     * @return 评审服务申请
     */

    public List<BizReviewApplyDto> selectBizReviewApply(BizReviewApplyDto dto);

    /**
     * 评审服务费申请新增
     *
     * @param bizReviewApply 评审、服务费新增
     * @return 结果
     */

    public R insertBizReviewApply(BizReviewApply bizReviewApply);
    /**
     * 评审服务费信息
     *
     * @param applyId 评审服务费ID
     * @return 评审服务费信息
     */

    public List<BizReviewInfo> selectBizReviewInfoList(Long applyId);

    /**
     * 查询详情
     * @param bizReviewApplyDto
     * @return
     */
    public BizReviewApplyDto getPurchase(BizReviewApplyDto bizReviewApplyDto) ;
    /**
     * 查询详情
     */
    public BizReviewApplyDto selectOne( Long tableId);

    /**
     * 编辑专家人数实际评审日期
     * @param bizReviewInfos
     * @return
     */
    void editInfo(List<BizReviewInfo> bizReviewInfos,String taskId);
}
