package com.ruoyi.quote.domain.vo;

import com.ruoyi.quote.domain.entity.QuotePostInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author yrb
 * @Date 2022/7/26 14:50
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuotePostInfoVO extends QuotePostInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "行业名称")
    private String industryName;

    @ApiModelProperty(value = "1选中,其他未选中")
    private Integer checked;

    @ApiModelProperty(value = "表单id")
    private String sheetId;

    @ApiModelProperty(value = "子类id")
    private Long subId;
}
