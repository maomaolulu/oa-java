package com.ruoyi.quote.domain.entity;

import java.math.BigDecimal;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 检测费用明细对象 quote_expense_details
 *
 * @author yrb
 * @date 2022-07-11
 */
public class QuoteExpenseDetails extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    private Long id;

    /**
     * 公司名称
     */
    @Excel(name = "公司名称")
    private String companyName;

    /**
     * 子类全称
     */
    @Excel(name = "子类全称")
    private String subName;

    /**
     * 子类简称
     */
    @Excel(name = "子类简称")
    private String subAbb;

    /**
     * 子类id
     */
    @Excel(name = "子类id")
    private Long subId;

    /**
     * 报价单id
     */
    @Excel(name = "报价单id")
    private String sheetId;

    /**
     * 检测费
     */
    @Excel(name = "检测费")
    private BigDecimal testExpense;

    /**
     * 报告编制费
     */
    @Excel(name = "报告编制费")
    private BigDecimal reportFabrication;

    /**
     * 专家评审费
     */
    @Excel(name = "专家评审费")
    private BigDecimal expertEvaluation;

    /**
     * 税金
     */
    @Excel(name = "税金")
    private BigDecimal taxes;

    /**
     * 合计
     */
    @Excel(name = "合计")
    private BigDecimal total;

    /**
     * 是否是临时文件：1是，0否
     */
    @Excel(name = "是否是临时文件：1是，0否")
    private Long tempFlag;

    /**
     * 加急费
     */
    @Excel(name = "加急费")
    private BigDecimal urgentExpense;

    public BigDecimal getUrgentExpense() {
        return urgentExpense;
    }

    public void setUrgentExpense(BigDecimal urgentExpense) {
        this.urgentExpense = urgentExpense;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubAbb(String subAbb) {
        this.subAbb = subAbb;
    }

    public String getSubAbb() {
        return subAbb;
    }

    public void setSubId(Long subId) {
        this.subId = subId;
    }

    public Long getSubId() {
        return subId;
    }

    public void setSheetId(String sheetId) {
        this.sheetId = sheetId;
    }

    public String getSheetId() {
        return sheetId;
    }

    public void setTestExpense(BigDecimal testExpense) {
        this.testExpense = testExpense;
    }

    public BigDecimal getTestExpense() {
        return testExpense;
    }

    public void setReportFabrication(BigDecimal reportFabrication) {
        this.reportFabrication = reportFabrication;
    }

    public BigDecimal getReportFabrication() {
        return reportFabrication;
    }

    public void setExpertEvaluation(BigDecimal expertEvaluation) {
        this.expertEvaluation = expertEvaluation;
    }

    public BigDecimal getExpertEvaluation() {
        return expertEvaluation;
    }

    public void setTaxes(BigDecimal taxes) {
        this.taxes = taxes;
    }

    public BigDecimal getTaxes() {
        return taxes;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTempFlag(Long tempFlag) {
        this.tempFlag = tempFlag;
    }

    public Long getTempFlag() {
        return tempFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("companyName", getCompanyName())
                .append("subName", getSubName())
                .append("subAbb", getSubAbb())
                .append("subId", getSubId())
                .append("sheetId", getSheetId())
                .append("testExpense", getTestExpense())
                .append("reportFabrication", getReportFabrication())
                .append("expertEvaluation", getExpertEvaluation())
                .append("taxes", getTaxes())
                .append("total", getTotal())
                .append("tempFlag", getTempFlag())
                .append("createTime", getCreateTime())
                .append("urgentExpense", getUrgentExpense())
                .toString();
    }
}
