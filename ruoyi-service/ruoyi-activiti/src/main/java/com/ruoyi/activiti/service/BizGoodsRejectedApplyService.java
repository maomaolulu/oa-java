package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.purchase.BizGoodsRejectedApply;
import com.ruoyi.activiti.domain.dto.CapitalGoodsDto;
import com.ruoyi.activiti.domain.dto.GoodsRejectedApplyDto;

import java.util.List;

/**
 * lx
 * 22/01/14
 */
public interface BizGoodsRejectedApplyService {
    /**
     * 点击退货申请获取物品信息--固定资产asset
     * @param idList
     * @return
     */
    List<GoodsRejectedApplyDto> getAsset(List<Long> idList);

    /**
     * 点击退货申请获取物品信息--流动资产sku
     * @param idList
     * @return
     */
    List<GoodsRejectedApplyDto> getSpu(List<Long> idList);

    /**
     * 点击提交退货申请
     * @param goodsRejectedApply
     * @return
     */
    int insert(BizGoodsRejectedApply goodsRejectedApply);

    /**
     * 通过流程ID获取详情
     * @param id
     * @return
     */
    BizGoodsRejectedApply getInfo(Long id);

    /**
     * 按条件查询流动资产退货列表
     * @param capitalGoodsDto
     * @return
     */
    List<CapitalGoodsDto> getSpuList(CapitalGoodsDto capitalGoodsDto);
    /**
     * 按条件查询固定资产退货列表
     * @param capitalGoodsDto
     * @return
     */
    List<CapitalGoodsDto> getAssetList(CapitalGoodsDto capitalGoodsDto);
}
