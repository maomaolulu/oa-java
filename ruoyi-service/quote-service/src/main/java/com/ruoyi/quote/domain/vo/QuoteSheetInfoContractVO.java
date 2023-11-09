package com.ruoyi.quote.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author yrb
 * @Date 2022/5/25 17:17
 * @Version 1.0
 * @Description 合同导出
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteSheetInfoContractVO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "委托方（甲方）")
    private String entrustingParty;

    @ApiModelProperty(value = "住所地")
    private String customerAddress;

    @ApiModelProperty(value = "优惠后价格（含税）")
    private BigDecimal discountPrice;

    @ApiModelProperty(value = "大写金额")
    private String formatToCN;
}
