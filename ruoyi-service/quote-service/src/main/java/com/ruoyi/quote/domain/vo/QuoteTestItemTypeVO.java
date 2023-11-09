package com.ruoyi.quote.domain.vo;

import com.ruoyi.quote.domain.entity.QuotePointInfo;
import com.ruoyi.quote.domain.entity.QuotePollutantType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author yrb
 * @Date 2022/7/13 22:06
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteTestItemTypeVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "检测类别")
    private QuotePollutantType quotePollutantType;

    @ApiModelProperty(value = "检测点位")
    private QuotePointInfo quotePointInfo;

    @ApiModelProperty(value = "报价项目")
    private List<QuoteTestItemDetailsVO> quoteTestItemDetailsVOList;
}
