package com.ruoyi.activiti.domain.dto;

import com.ruoyi.activiti.domain.purchase.BizSupplierQuote;
import lombok.Data;

import java.util.List;

/**
 *  供应商报价DTO
 * @author zx
 * @date 2022/4/24 9:51
 */
@Data
public class BizSupplierQuoteDto {
    private List<BizSupplierQuote> map;
    private String procInstId;
}
