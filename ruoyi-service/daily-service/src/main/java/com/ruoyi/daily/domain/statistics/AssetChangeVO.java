package com.ruoyi.daily.domain.statistics;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 后端返回VO ----> 固定资产变化
 * @author wuYang
 * @date 2022/8/30 9:12
 */
@Data
public class AssetChangeVO {
    /**
     * 返回的月份
     */
    @ApiModelProperty("返回的月份")
    private String month;
    /**
     * 返回的总共金额
     */
    @ApiModelProperty("返回的总共金额")
    private BigDecimal total;
//    /**
//     * 真实价格
//     */
//    @ApiModelProperty("真实价格")
//    private BigDecimal totalPrice;
}
