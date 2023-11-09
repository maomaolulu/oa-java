package com.ruoyi.quote.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author yrb
 * @Date 2022/6/29 14:56
 * @Version 1.0
 * @Description 公卫-获取关联污染物信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteRelationFactorDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "行业主分类id")
    private Long masterCategoryId;

    @ApiModelProperty(value = "检测类别id")
    private Long pollutantTypeId;

    @ApiModelProperty(value = "表单id")
    private String sheetId;

    @ApiModelProperty(value = "子类id")
    private Long subId;

    @ApiModelProperty(value = "点位id")
    private Long pointId;
}
