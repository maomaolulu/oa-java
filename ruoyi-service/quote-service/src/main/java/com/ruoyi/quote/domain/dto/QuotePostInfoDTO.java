package com.ruoyi.quote.domain.dto;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.quote.domain.entity.QuotePostInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author yrb
 * @Date 2022/6/9 10:36
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuotePostInfoDTO extends BaseEntity implements Serializable {
    @ApiModelProperty(value = "表单id")
    private String sheetId;
    @ApiModelProperty(value = "子类id")
    private Long subId;
    @ApiModelProperty(value = "岗位信息")
    private QuotePostInfo quotePostInfo;
}
