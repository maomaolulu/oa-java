package com.ruoyi.quote.domain.dto;

import com.ruoyi.quote.domain.entity.QuoteCategoryPollutant;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author yrb
 * @Date 2022/6/23 21:28
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteCategoryPollutantDTO implements Serializable {
    @ApiModelProperty("检测类别集合")
    private List<QuoteCategoryPollutant> quoteCategoryPollutantList;
}
