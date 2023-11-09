package com.ruoyi.quote.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author yrb
 * @Date 2022/8/12 11:09
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteBaseFactorFilterDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("行业主分类id")
    private Long masterCategoryId;

    @ApiModelProperty("行业子类id")
    private Long subCategoryId;

    @ApiModelProperty("污染物类别id")
    private Long pollutantTypeId;

    @ApiModelProperty("类别id")
    private Long categoryId;

    @ApiModelProperty("因素名称")
    private String factorName;
}
