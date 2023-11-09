package com.ruoyi.quote.domain.dto;

import com.ruoyi.quote.domain.entity.QuoteSheetInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author yrb
 * @Date 2022/7/29 10:25
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteSheetInfoDTO extends QuoteSheetInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "报价单id")
    private String id;

    @ApiModelProperty(value = "python请求地址")
    private String requestUrl;

    @ApiModelProperty(value = "python下载地址")
    private String downloadUrl;

    @ApiModelProperty(value = "下载类型")
    private Integer type;
}
