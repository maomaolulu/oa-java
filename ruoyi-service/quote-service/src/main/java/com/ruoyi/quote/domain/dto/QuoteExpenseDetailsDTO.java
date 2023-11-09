package com.ruoyi.quote.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * @Author yrb
 * @Date 2022/7/13 14:26
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteExpenseDetailsDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank
    @ApiModelProperty(value = "表单id")
    private String sheetId;

    @NotBlank
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @NotBlank
    @ApiModelProperty(value = "子类全称")
    private String subName;

    @NotBlank
    @ApiModelProperty(value = "子类简称")
    private String subAbb;

    @NotEmpty
    @ApiModelProperty(value = "所选子类主键id集合")
    private List<Long> list;
}
