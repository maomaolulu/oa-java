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
@Document("budget_statistics")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetExcelTotalEntity {
    @ExcelProperty(value = "付款类别级次")
    private String paymentNumber;

    @ExcelProperty(value = "付款类别明细")
    private String paymentDetail;

    @ExcelProperty(value = "公司/部门代号")
    private String companyDeptNameNumber;

    @ExcelProperty(value = "预算公司/部门")
    private String companyDeptName;

    @ExcelProperty(value = "1月份---占用金额")
    private String month1_amount_occupied;
    @ExcelProperty(value = "1月份---剩余金额")
    private String month1_remaining_quota;
    @ExcelProperty(value = "1月份---确认金额")
    private String month1_confirmed_quota;
    @ExcelProperty(value = "1月份---预算金额")
    private String month1_budget_quota;
    @ExcelProperty(value = "2月份---占用金额")
    private String month2_amount_occupied;
    @ExcelProperty(value = "2月份---剩余金额")
    private String month2_remaining_quota;
    @ExcelProperty(value = "2月份---确认金额")
    private String month2_confirmed_quota;
    @ExcelProperty(value = "2月份---预算金额")
    private String month2_budget_quota;
    @ExcelProperty(value = "3月份---占用金额")
    private String month3_amount_occupied;
    @ExcelProperty(value = "3月份---剩余金额")
    private String month3_remaining_quota;
    @ExcelProperty(value = "3月份---确认金额")
    private String month3_confirmed_quota;
    @ExcelProperty(value = "3月份---预算金额")
    private String month3_budget_quota;
    @ExcelProperty(value = "4月份---占用金额")
    private String month4_amount_occupied;
    @ExcelProperty(value = "4月份---剩余金额")
    private String month4_remaining_quota;
    @ExcelProperty(value = "4月份---确认金额")
    private String month4_confirmed_quota;
    @ExcelProperty(value = "4月份---预算金额")
    private String month4_budget_quota;
    @ExcelProperty(value = "5月份---占用金额")
    private String month5_amount_occupied;
    @ExcelProperty(value = "5月份---剩余金额")
    private String month5_remaining_quota;
    @ExcelProperty(value = "5月份---确认金额")
    private String month5_confirmed_quota;
    @ExcelProperty(value = "5月份---预算金额")
    private String month5_budget_quota;
    @ExcelProperty(value = "6月份---占用金额")
    private String month6_amount_occupied;
    @ExcelProperty(value = "6月份---剩余金额")
    private String month6_remaining_quota;
    @ExcelProperty(value = "6月份---确认金额")
    private String month6_confirmed_quota;
    @ExcelProperty(value = "6月份---预算金额")
    private String month6_budget_quota;
    @ExcelProperty(value = "7月份---占用金额")
    private String month7_amount_occupied;
    @ExcelProperty(value = "7月份---剩余金额")
    private String month7_remaining_quota;
    @ExcelProperty(value = "7月份---确认金额")
    private String month7_confirmed_quota;
    @ExcelProperty(value = "7月份---预算金额")
    private String month7_budget_quota;
    @ExcelProperty(value = "8月份---占用金额")
    private String month8_amount_occupied;
    @ExcelProperty(value = "8月份---剩余金额")
    private String month8_remaining_quota;
    @ExcelProperty(value = "8月份---确认金额")
    private String month8_confirmed_quota;
    @ExcelProperty(value = "8月份---预算金额")
    private String month8_budget_quota;
    @ExcelProperty(value = "9月份---占用金额")
    private String month9_amount_occupied;
    @ExcelProperty(value = "9月份---剩余金额")
    private String month9_remaining_quota;
    @ExcelProperty(value = "9月份---确认金额")
    private String month9_confirmed_quota;
    @ExcelProperty(value = "9月份---预算金额")
    private String month9_budget_quota;
    @ExcelProperty(value = "10月份---占用金额")
    private String month10_amount_occupied;
    @ExcelProperty(value = "10月份---剩余金额")
    private String month10_remaining_quota;
    @ExcelProperty(value = "10月份---确认金额")
    private String month10_confirmed_quota;
    @ExcelProperty(value = "10月份---预算金额")
    private String month10_budget_quota;
    @ExcelProperty(value = "11月份---占用金额")
    private String month11_amount_occupied;
    @ExcelProperty(value = "11月份---剩余金额")
    private String month11_remaining_quota;
    @ExcelProperty(value = "11月份---确认金额")
    private String month11_confirmed_quota;
    @ExcelProperty(value = "11月份---预算金额")
    private String month11_budget_quota;
    @ExcelProperty(value = "12月份---占用金额")
    private String month12_amount_occupied;
    @ExcelProperty(value = "12月份---剩余金额")
    private String month12_remaining_quota;
    @ExcelProperty(value = "12月份---确认金额")
    private String month12_confirmed_quota;
    @ExcelProperty(value = "12月份---预算金额")
    private String month12_budget_quota;


}
