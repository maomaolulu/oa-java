package com.ruoyi.quote.domain.vo;

import com.ruoyi.quote.domain.entity.QuotePollutantType;
import com.ruoyi.quote.domain.entity.QuoteTestNature;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author yrb
 * @Date 2022/9/19 10:14
 * @Version 1.0
 * @Description 公卫重构 获取已报价的相关信息列表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteTestItemInfoVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "检测性质列表")
    private List<QuoteTestNature> quoteTestNatureList;

    @ApiModelProperty(value = "检测类别列表")
    private List<QuotePollutantTestTypeVO> quotePollutantTestTypeVOList;

    @ApiModelProperty(value = "检测信息列表")
    private List<QuoteTestItemDetailsVO> quoteTestItemDetailsVOList;
}
