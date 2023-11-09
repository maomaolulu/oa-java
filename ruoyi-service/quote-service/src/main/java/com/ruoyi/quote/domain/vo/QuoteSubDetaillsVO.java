package com.ruoyi.quote.domain.vo;

import com.ruoyi.quote.domain.entity.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author yrb
 * @Date 2022/7/27 10:03
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteSubDetaillsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "子类全称")
    private String subFullName;

    @ApiModelProperty(value = "职卫-报价检测项目明细(按子类划分)")
    private List<QuoteSheetItems> quoteSheetItemsList;

    @ApiModelProperty(value = "环境、公卫-报价检测项目明细(按子类划分)")
    private List<QuoteTestItem> quoteTestItemList;

    @ApiModelProperty(value = "子类检测费合计")
    private BigDecimal subTestExpense;
}
