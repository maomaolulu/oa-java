package com.ruoyi.quote.domain.dto;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.quote.domain.entity.QuoteHarmFactor;
import com.ruoyi.quote.domain.entity.QuotePostInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author yrb
 * @Date 2022/4/27 15:26
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostHarmFactorDTO extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "岗位信息")
    private QuotePostInfo quotePostInfo;

    @ApiModelProperty(value = "危害因素列表")
    private List<QuoteHarmFactor> quoteHarmFactorList;
}
