package com.ruoyi.quote.domain.vo;

import com.ruoyi.quote.domain.entity.QuotePollutantType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author yrb
 * @Date 2022/7/11 10:12
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteTestItemEditVO implements Serializable {

    @ApiModelProperty(value = "报价信息")
    private List<QuoteTestItemDetailsVO> quoteTestItemDetailsVOList;

    @ApiModelProperty(value = "检测类别信息")
    private QuotePollutantType quotePollutantType;
}
