package com.ruoyi.daily.domain.statistics;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 后端返回VO ----> 固定资产占比分析
 * @author wuYang
 * @date 2022/8/30 9:12
 */
@Data
public class AssetProportionVO {

    @ApiModelProperty("返回的价格")
    private Long purchasePrice;

    @ApiModelProperty("返回的公司名字")
    private String companyName;
}
