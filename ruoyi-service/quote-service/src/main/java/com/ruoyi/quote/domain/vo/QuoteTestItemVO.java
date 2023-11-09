package com.ruoyi.quote.domain.vo;

import com.ruoyi.quote.domain.entity.QuoteTestItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author yrb
 * @Date 2022/7/8 10:45
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteTestItemVO  extends QuoteTestItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "公卫行业id")
    private Long masterCategoryId;
}
