package com.ruoyi.quote.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author yrb
 * @Date 2022/6/29 22:14
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteTestPollutantDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "关联表主键id")
    private Long id;

    @ApiModelProperty(value = "污染物id集合")
    private List<Long> ids;
}
