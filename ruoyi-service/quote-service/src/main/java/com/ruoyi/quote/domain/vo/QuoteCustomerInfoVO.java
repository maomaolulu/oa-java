package com.ruoyi.quote.domain.vo;

import com.ruoyi.quote.domain.entity.QuoteCustomerInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author yrb
 * @Date 2022/7/27 16:53
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteCustomerInfoVO extends QuoteCustomerInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "客户详细地址(包含省市区)")
    private String fullAddress;
}
