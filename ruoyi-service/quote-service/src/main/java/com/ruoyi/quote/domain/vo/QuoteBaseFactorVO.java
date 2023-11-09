package com.ruoyi.quote.domain.vo;

import com.ruoyi.quote.domain.entity.QuoteBaseFactor;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author yrb
 * @Date 2022/6/22 20:39
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteBaseFactorVO extends QuoteBaseFactor implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "类别名称")
    private String category;

    @ApiModelProperty(value = "子类名称")
    private String subcategory;

    @ApiModelProperty(value = "污染物名称 + 检测标准")
    private String fullName;

    @ApiModelProperty(value = "已关联污染物id")
    private List<Long> list;

    @ApiModelProperty(value = "区分是否已关联岗位：1已关联  其他值未关联")
    private Integer relationFlag;

    @ApiModelProperty(value = "表单id")
    private String sheetId;

    @ApiModelProperty(value = "子类id")
    private Long subId;

    @ApiModelProperty(value = "岗位id")
    private Long postId;

    @ApiModelProperty(value = "点位数")
    private BigDecimal pointNumber;

    @ApiModelProperty(value = "小计")
    private BigDecimal totalPrice;
}
