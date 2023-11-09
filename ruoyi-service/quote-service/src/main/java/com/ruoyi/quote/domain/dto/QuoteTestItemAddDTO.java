package com.ruoyi.quote.domain.dto;

import com.ruoyi.quote.domain.entity.QuoteTestItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author yrb
 * @Date 2022/6/20 21:15
 * @Version 1.0
 * @Description 批量添加检测项
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuoteTestItemAddDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "污染物id集合")
    private List<Long> pollutantIdList;

    @ApiModelProperty(value = "检测条目")
    private QuoteTestItem quoteTestItem;
}
