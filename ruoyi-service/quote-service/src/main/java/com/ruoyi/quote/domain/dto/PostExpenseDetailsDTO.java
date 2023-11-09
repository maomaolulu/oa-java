package com.ruoyi.quote.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author yrb
 * @Date 2022/7/21 14:07
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostExpenseDetailsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "表单id")
    private String sheetId;

    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "子类id")
    private Long subId;

    @ApiModelProperty(value = "岗位id")
    private Long postId;

    @ApiModelProperty(value = "所选岗位主键id集合")
    private List<Long> list;
}
