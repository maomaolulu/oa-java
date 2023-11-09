package com.ruoyi.daily.domain.statistics;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wuYang
 * @date 2022/8/30 15:59
 */
@Data
public class AssetContrastVO {

    @ApiModelProperty("固定资产")
    private Long asset;

    @ApiModelProperty("流动资产")
    private Long flowAsset;


}
