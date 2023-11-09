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
 * @Date 2022/9/21 14:32
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteTestInfoDTO extends QuoteTestItem implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "大类id")
    private Long masterCategoryId;

    @ApiModelProperty(value = "检测类型id集合")
    private List<Long> list;

    @ApiModelProperty(value = "污染物id集合")
    private List<Long> pollutantIdList;

    @ApiModelProperty(value = "检测项集合")
    private List<QuoteTestItemDetailsVO> quoteTestItemDetailsVOList;
}
