package com.ruoyi.quote.domain.vo;

import com.ruoyi.quote.domain.entity.QuoteHarmFactor;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author yrb
 * @Date 2022/7/18 20:10
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteHarmFactorVO extends QuoteHarmFactor implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "污染物名称 + 检测标准")
    private String fullName;

    @ApiModelProperty(value = "污染物名称")
    private String factorName;

    @ApiModelProperty(value = "检测标准")
    private String standardInfo;

    @ApiModelProperty(value = "限制范围")
    private String limitRange;

    @ApiModelProperty(value = "单价")
    private String price;

    @ApiModelProperty(value = "已选择的危害因素")
    private List<Long> list;

    @ApiModelProperty(value = "表单id")
    private String sheetId;

    @ApiModelProperty(value = "子类id")
    private Long subId;
}
