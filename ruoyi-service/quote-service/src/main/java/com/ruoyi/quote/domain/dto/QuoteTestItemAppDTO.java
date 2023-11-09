package com.ruoyi.quote.domain.dto;

import com.ruoyi.quote.domain.entity.QuoteTestItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author yrb
 * @Date 2022/6/22 18:02
 * @Version 1.0
 * @Description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuoteTestItemAppDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "检测条目集合")
    private List<QuoteTestItem> quoteTestItemList;
}
