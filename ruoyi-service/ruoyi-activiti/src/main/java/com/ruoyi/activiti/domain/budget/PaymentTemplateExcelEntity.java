package com.ruoyi.activiti.domain.budget;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author yrb
 * @Date 2023/3/9 14:22
 * @Version 1.0
 * @Description  导出模板申请金额
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentTemplateExcelEntity {
    @ExcelProperty(value = "付款申请模板")
    private String name;

    @ExcelProperty(value = "金额")
    private BigDecimal projectMoney;
}
