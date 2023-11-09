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
 * @Description 职卫zw
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelationInfoZwDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "行业编号")
    private String code;

    @ApiModelProperty(value = "行业名称")
    private String industryName;

    @ApiModelProperty(value = "岗位名称")
    private String postName;

    @ApiModelProperty(value = "危害因素")
    private String factorName;
}
