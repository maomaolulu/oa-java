package com.ruoyi.quote.domain.dto;

import com.ruoyi.quote.domain.entity.QuoteCategoryPollutant;
import com.ruoyi.quote.domain.vo.QuoteBaseFactorVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author yrb
 * @Date 2022/6/23 11:21
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteBaseFactorDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "污染物对应关系")
    private QuoteCategoryPollutant quoteCategoryPollutant;

    @ApiModelProperty(value = "污染物基础信息")
    private QuoteBaseFactorVO quoteBaseFactorVO;

    @ApiModelProperty(value = "表单id")
    private String sheetId;

    @ApiModelProperty(value = "子类id")
    private Long subId;

    @ApiModelProperty(value = "岗位id")
    private Long postId;

    @ApiModelProperty(value = "危害因素列表区分是否已关联岗位")
    private List<QuoteBaseFactorVO> quoteBaseFactorVOList;
}
