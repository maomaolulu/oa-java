package com.ruoyi.quote.domain.vo;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.quote.domain.entity.QuoteExpenseDetails;
import com.ruoyi.quote.domain.entity.QuoteSheetInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author yrb
 * @Date 2022/5/9 15:17
 * @Version 1.0
 * @Description 报价单导出
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteSheetInfoVO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "报价单信息")
    private QuoteSheetInfo quoteSheetInfo;

    @ApiModelProperty(value = "报价客户信息")
    private QuoteCustomerInfoVO quoteCustomerInfoVO;

    @ApiModelProperty(value = "报价项目费用明细")
    private List<QuoteExpenseDetails> quoteExpenseDetailsList;

    @ApiModelProperty(value = "报价检测项目明细")
    private List<QuoteSubDetaillsVO> quoteSubDetaillsVOList;

    @ApiModelProperty(value = "优惠后费用总计（含税）")
    private String discountPriceCN;

    @ApiModelProperty(value = "优惠后费用总计（不含税）")
    private String excludeTaxesPriceCN;
}
