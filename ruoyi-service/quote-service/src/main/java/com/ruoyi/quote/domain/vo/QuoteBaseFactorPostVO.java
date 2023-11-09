package com.ruoyi.quote.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author yrb
 * @Date 2022/7/20 23:10
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteBaseFactorPostVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "危害因素信息集合")
    private List<QuoteBaseFactorVO> list;

    @ApiModelProperty(value = "点位数")
    private Long pointNumber;
}
