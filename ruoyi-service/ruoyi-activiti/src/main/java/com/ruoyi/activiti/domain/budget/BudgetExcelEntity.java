package com.ruoyi.activiti.domain.budget;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author wuYang
 * @date 2022/12/13 15:20
 */
@Document("budget_plan")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetExcelEntity {
    @ExcelProperty(value = "付款类别级次")
    private String paymentNumber;

    @ExcelProperty(value = "付款类别明细")
    private String paymentDetail;

    @ExcelProperty(value = "公司/部门代号")
    private String companyDeptNameNumber;

    @ExcelProperty(value = "预算公司/部门")
    private String companyDeptName;

    @ExcelProperty(value = "1月份")
    private String month1;
    @ExcelProperty(value = "2月份")
    private String month2;
    @ExcelProperty(value = "3月份")
    private String month3;
    @ExcelProperty(value = "4月份")
    private String month4;
    @ExcelProperty(value = "5月份")
    private String month5;
    @ExcelProperty(value = "6月份")
    private String month6;
    @ExcelProperty(value = "7月份")
    private String month7;
    @ExcelProperty(value = "8月份")
    private String month8;
    @ExcelProperty(value = "9月份")
    private String month9;
    @ExcelProperty(value = "10月份")
    private String month10;
    @ExcelProperty(value = "11月份")
    private String month11;
    @ExcelProperty(value = "12月份")
    private String month12;


}
