package com.ruoyi.quote.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author yrb
 * @Date 2022/5/22 18:07
 * @Version 1.0
 * @Description 公卫gw
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelationInfoGwDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "行业类别")
    private String industryName;

    @ApiModelProperty(value = "检测类别")
    private String pollutantTypeName;

    @ApiModelProperty(value = "收费标准名称")
    private String chargeCategory;

    @ApiModelProperty(value = "污染物名称")
    private String pollutantName;

    @ApiModelProperty(value = "检测标准")
    private String standInfo;

    @ApiModelProperty(value = "限制范围")
    private String limitRange;
}
