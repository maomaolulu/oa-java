package com.ruoyi.quote.domain.dto;

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
 * @Date 2022/6/30 14:33
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuotePollutantTypeDTO extends QuotePollutantType implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "检测类别id集合")
    private List<Long> list;

    @ApiModelProperty(value = "公卫行业id")
    private Long masterCategoryId;

    @ApiModelProperty(value = "检测性质列表")
    private List<QuoteTestNature> quoteTestNatureList;
}
