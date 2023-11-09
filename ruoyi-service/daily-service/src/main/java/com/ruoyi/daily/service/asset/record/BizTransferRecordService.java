package com.ruoyi.daily.service.asset.record;

import com.ruoyi.daily.domain.asset.Asset;
import com.ruoyi.daily.domain.asset.record.dto.AssetDTO;
import com.ruoyi.daily.domain.asset.record.dto.AssetSelectDTO;
import com.ruoyi.daily.domain.asset.record.dto.AssetStockDTO;
import com.ruoyi.daily.domain.asset.record.vo.BizTransferRecordVO;

import java.util.List;

/**
 * 资产移交记录服务
 * Created by WuYang on 2022/8/22 17:36
 */
public interface BizTransferRecordService {
    /**
     * 资产转移
     */
    void assetTransfer(AssetDTO assetDTO);

    /**
     * 设为库存
     */
    void toStock(AssetStockDTO assetStockDTO);

    /**
     * 查询记录
     */
    List<BizTransferRecordVO> getList(AssetSelectDTO d);

    /**
     * 修改assert companyid
     */
    void  repairAsset();
}
