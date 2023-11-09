package com.ruoyi.quote.domain.dto;

import com.ruoyi.quote.domain.entity.QuoteCategoryPollutant;
import com.ruoyi.quote.domain.entity.QuoteTestItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author yrb
 * @Date 2022/6/16 19:30
 * @Version 1.0
 * @Description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuoteCategoryItemDTO implements Serializable {
    @ApiModelProperty(value = "行业类别污染物类别关联表")
    private QuoteCategoryPollutant quoteCategoryPollutant;
    @ApiModelProperty(value = "检测项目")
    private QuoteTestItem quoteTestItem;
    @ApiModelProperty(value = "污染物id集合")
    private List<Long> ids;
}
