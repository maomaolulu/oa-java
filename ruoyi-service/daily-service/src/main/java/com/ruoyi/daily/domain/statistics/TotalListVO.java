package com.ruoyi.daily.domain.statistics;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 各公司总资产变化趋势VO
 * @author wuYang
 * @date 2022/8/30 16:55
 */
@Data
public class TotalListVO {

    @ApiModelProperty("公司流动资产变化")
    List<AssetChangeVO> flowAssetList;

    @ApiModelProperty("公司固定资产变化")
    List<AssetChangeVO> AssetList;

    @ApiModelProperty("公司总资产变化")
    List<AssetChangeVO> TotalAssetList;
}
