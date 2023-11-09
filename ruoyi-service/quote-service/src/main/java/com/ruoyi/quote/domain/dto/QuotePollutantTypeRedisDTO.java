package com.ruoyi.quote.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author yrb
 * @Date 2022/7/15 12:50
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuotePollutantTypeRedisDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "已有检测类别id")
    private Long oldId;

    @ApiModelProperty(value = "检测类别id")
    private Long id;

    @ApiModelProperty(value = "子类id")
    private Long subId;

    @ApiModelProperty(value = "表单id")
    private String sheetId;
}
