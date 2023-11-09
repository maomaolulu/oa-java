package com.ruoyi.quote.domain.dto;

import com.ruoyi.quote.domain.entity.QuoteTestNature;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author yrb
 * @Date 2022/6/27 20:58
 * @Version 1.0
 * @Description 公卫--新增检测类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuotePollutantTypeAddDTO implements Serializable {

    @ApiModelProperty(value = "所属项目id")
    private Long projectId;

    @ApiModelProperty(value = "所属行业id")
     private Long industryId;

    @ApiModelProperty(value = "检测类别名称")
    private String pollutantName;

    @ApiModelProperty(value = "污染物id集合")
    private List<Long> ids;

    @ApiModelProperty(value = "检测性质列表")
    private List<QuoteTestNature> quoteTestNatureList;
}
