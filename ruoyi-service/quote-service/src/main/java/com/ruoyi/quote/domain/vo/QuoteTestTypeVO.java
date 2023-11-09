package com.ruoyi.quote.domain.vo;

import com.ruoyi.quote.domain.entity.QuoteCategoryPollutant;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author yrb
 * @Date 2022/6/21 14:29
 * @Version 1.0
 * @Description 行业大类、子类、检测类别对应关系
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteTestTypeVO extends QuoteCategoryPollutant implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "行业大类")
    private String masterCategory;

    @ApiModelProperty(value = "行业子类")
    private String subCategory;

    @ApiModelProperty(value = "检测类别名称")
    private String pollutantName;
}
