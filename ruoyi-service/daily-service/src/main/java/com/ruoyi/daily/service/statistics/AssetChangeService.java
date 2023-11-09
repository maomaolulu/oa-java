package com.ruoyi.daily.service.statistics;

import com.ruoyi.daily.domain.statistics.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 资产变化分析
 * @author wuYang
 * @date 2022/8/30 9:09
 */
public interface AssetChangeService {
    /**
     * 各分公司固定资产变化趋势
     */
    List<AssetChangeVO> getAssetChange(AssetChangeDTO dto, LocalDate startTime, LocalDate endTime);
    /**
     * 各分公司固定资产占比
     */
    List<AssetProportionVO> getAssetProportion(AssetProportionDTO dto, LocalDate startTime, LocalDate endTime);
    /**
     * 流动资产和固定资产对比
     */
    AssetContrastVO getContrast(AssetContrastDTO dto,LocalDate startTime, LocalDate endTime);

    /**
     * 各种资产变化图
     */
    TotalListVO getChangeList(AssetContrastDTO dto, LocalDate startTime, LocalDate endTime);
}
