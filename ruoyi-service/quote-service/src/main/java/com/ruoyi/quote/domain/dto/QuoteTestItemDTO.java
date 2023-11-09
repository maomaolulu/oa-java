package com.ruoyi.quote.domain.dto;

import com.ruoyi.quote.domain.entity.QuoteTestItem;
import com.ruoyi.quote.domain.vo.QuoteTestItemDetailsVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author yrb
 * @Date 2022/6/24 11:42
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteTestItemDTO extends QuoteTestItem implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "污染物名称")
    private String pollutantName;

    @ApiModelProperty(value = "检测性质id")
    private Long testNatureId;

    @ApiModelProperty(value = "检测项集合")
    private List<QuoteTestItemDetailsVO> quoteTestItemDetailsVOList;
}
