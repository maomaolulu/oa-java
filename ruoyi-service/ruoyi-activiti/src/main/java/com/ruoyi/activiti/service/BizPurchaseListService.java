package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.dto.AssetPurchaseDto;
import com.ruoyi.activiti.domain.purchase.BizGoodsInfo;
import com.ruoyi.activiti.domain.purchase.BizGoodsRecord;
import com.ruoyi.activiti.domain.purchase.BizPurchaseApply;
import com.ruoyi.activiti.domain.dto.BizPurchaseListDto;
import com.ruoyi.activiti.domain.purchase.BizTransferRecord;

import java.util.List;
import java.util.Map;

public interface BizPurchaseListService {

    /**
     * 采购订单所有(采购查询页面)
     * @param
     * @return
     */
    List<BizPurchaseListDto> selectGoodsListAll(BizPurchaseListDto dto);
    /**
     * 固定资产采购查询页面
     * @param
     * @return
     */
    List<BizPurchaseListDto> selectGoodsListFixed(BizPurchaseListDto dto);
    /**
     * 采购订货和采购计划
     * @param
     * @return
     */
    List<BizPurchaseListDto> selectBizPurchaseListAll(BizPurchaseListDto dto);
    /**
     * 采购验收页面
     */
     List<BizPurchaseListDto> selectBizPurchaseCheck(BizPurchaseListDto dto);
    /**
     * 采购入库
     * @param
     * @return
     */
    List<BizPurchaseListDto> selectBizPurchaseApplyList(BizPurchaseListDto dto);
    /**
     *  去采购页面 修改isPurchase是否采购为是
     */
    BizGoodsInfo updateBizGoodsInfo(BizGoodsInfo bizGoodsInfo);
    /**
     *  申请验收 修改isReceived是否到货为是
     */
    void updateBizGoodsInfoList(List<BizGoodsInfo> bizGoodsInfoList);
    /**
     *  申请人申请验收
     */
    void updateBizGoodsInfoList2(List<BizGoodsInfo> bizGoodsInfoList);

    /**
     * 查询采购申请信息 用于退货
     * @return
     */
    BizPurchaseApply selectByGoodId(Long goodsId);
    /**
     *  采购计划修改
     */

     void updateBizGoodsInfoPlan(BizGoodsInfo bizGoodsInfo);

    /**
     * 资产入库（未移交）
     * @param id
     */
    void storageNoTransfer(Long id);

    /**
     * 查询采购流程
     * @param id 采购物品id
     * @return
     */
    List<BizGoodsRecord> getGoodsRecord(Long id);

    /**
     * 移交
     * @param transferRecords 移交记录
     */
    void storageTransfer(List<BizTransferRecord> transferRecords);

    /**
     * 查询移交记录
     * @param bizTransferRecord
     * @return
     */
    List<BizTransferRecord> getTransferRecord(BizTransferRecord bizTransferRecord);

    /**
     * 采购物品作废
     * @param id
     */
    void cancelGoods(Long id);

    /**
     * 入库前退货
     * @param ids
     */
    void beforeStorageReturn(List<Long> ids);

    /**
     * 确认移交
     * @param ids
     */
    void checkTransfer(List<Long> ids);

    /**
     * 设为库存
     * @param assetIds
     */
    void setStorage(List<Long> assetIds);

    /**
     * 导出资产采购数据
     * @param assetPurchaseDto
     * @return
     */
    List<Map<String, Object>> exportAssetPurchaseExcel(AssetPurchaseDto assetPurchaseDto);
}
