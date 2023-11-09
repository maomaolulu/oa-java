package com.ruoyi.quote.domain.vo;

import com.ruoyi.quote.domain.entity.QuotePollutantType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author yrb
 * @Date 2022/9/19 10:19
 * @Version 1.0
 * @Description 检测类别
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuotePollutantTestTypeVO extends QuotePollutantType implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "是否选中：1已选中，0未选中")
    private Integer checked;
}
