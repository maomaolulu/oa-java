package com.ruoyi.daily.service.asset.transfer;

import com.ruoyi.daily.domain.asset.transfer.PurchaseTransferDTO;
import com.ruoyi.daily.domain.asset.transfer.PurchaseTransferVO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 采购转移服务
 * @author wuYang
 * @date 2022/9/9 15:45
 */
public interface AssetTransferService {

    /**
     * 1. ------------转移采购资产 ----------------
     * 2. 通过assetId 设置责任人和责任部门
     * 3. 生成待确认记录 {@link com.ruoyi.daily.domain.asset.record.BizTransferRecord}
     * 4. 发送到该责任人账号 ---- 通过 BizTransferRecord id 关联查出
     */
    void  purchaseTransfer(PurchaseTransferDTO dto);

    /**
     * -------------- 本用户确认采购移交 ------------
     * 1. 用户确认完 isChecked 为 True 列表只展示未确认的
     */
    void checkPurchase(Long recordId);

    /**
     * ------------- 展示用户为确认的转移待确认记录 -----------
     * 1. 通过userId 联表 展示 isChecked = false 的
     */
    List<PurchaseTransferVO> getUncheckPurchaseListByUser(
            String purchaseCode, String createBy, String assetSn,
            String name, LocalDateTime start,
            LocalDateTime end
    );

    /**
     * ------------- 展示所有未确认的转移待确认记录 -----------
     * 1. 联表 展示 isChecked = false 的
     */
    void  getUncheckPurchaseList();

    /**
     * 驳回请求
     * 直接转入待移交固定资产
     */
    void rejectCheck(Long id);




}
