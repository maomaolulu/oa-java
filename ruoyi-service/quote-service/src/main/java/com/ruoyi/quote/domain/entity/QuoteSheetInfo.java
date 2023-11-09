package com.ruoyi.quote.domain.entity;

import java.math.BigDecimal;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 报价单信息对象 quote_sheet_info
 *
 * @author yrb
 * @date 2022-07-29
 */
public class QuoteSheetInfo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 报价单id(主键)
     */
    private String id;

    /**
     * 客户id
     */
    @Excel(name = "客户id")
    private Long customerId;

    /**
     * 记录编号
     */
    @Excel(name = "记录编号")
    private String recordCode;

    /**
     * 项目名称
     */
    @Excel(name = "项目名称")
    private String projectName;

    /**
     * 客户名称
     */
    @Excel(name = "客户名称")
    private String customerName;

    /**
     * 公司名称
     */
    @Excel(name = "公司名称")
    private String companyName;

    /**
     * 主分类
     */
    @Excel(name = "主分类")
    private String masterCategory;

    /**
     * 主类简称
     */
    @Excel(name = "主类简称")
    private String masterAbbreviation;

    /**
     * 总价
     */
    @Excel(name = "总价")
    private BigDecimal totalPrice;

    /**
     * 专家评审费
     */
    @Excel(name = "专家评审费")
    private BigDecimal expertEvaluation;

    /**
     * 折扣
     */
    @Excel(name = "折扣")
    private BigDecimal discount;

    /**
     * 优惠后价格（含税）
     */
    @Excel(name = "优惠后价格", readConverterExp = "含=税")
    private BigDecimal discountPrice;

    /**
     * 优惠后价格（不含税）
     */
    @Excel(name = "优惠后价格", readConverterExp = "不=含税")
    private BigDecimal excludeTaxesPrice;

    /**
     * 税金
     */
    @Excel(name = "税金")
    private BigDecimal taxes;

    /**
     * 商务联系人
     */
    @Excel(name = "商务联系人")
    private String salesman;

    /**
     * 联系电话
     */
    @Excel(name = "联系电话")
    private String telephone;

    /**
     * 电子邮箱
     */
    @Excel(name = "电子邮箱")
    private String email;

    /**
     * 客服姓名
     */
    @Excel(name = "客服姓名")
    private String serviceName;

    /**
     * 客服电话
     */
    @Excel(name = "客服电话")
    private String serviceTelephone;

    /**
     * 客服邮箱
     */
    @Excel(name = "客服邮箱")
    private String serviceEmail;

    /**
     * 报价项目
     */
    @Excel(name = "报价项目")
    private String quoteProject;

    /**
     * 报价人
     */
    @Excel(name = "报价人")
    private String salesInfo;

    /**
     * 备注
     */
    @Excel(name = "备注")
    private String remarks;

    /**
     * 主类id
     */
    @Excel(name = "主类id")
    private Long masterCategoryId;

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

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setRecordCode(String recordCode) {
        this.recordCode = recordCode;
    }

    public String getRecordCode() {
        return recordCode;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setMasterCategory(String masterCategory) {
        this.masterCategory = masterCategory;
    }

    public String getMasterCategory() {
        return masterCategory;
    }

    public void setMasterAbbreviation(String masterAbbreviation) {
        this.masterAbbreviation = masterAbbreviation;
    }

    public String getMasterAbbreviation() {
        return masterAbbreviation;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setExpertEvaluation(BigDecimal expertEvaluation) {
        this.expertEvaluation = expertEvaluation;
    }

    public BigDecimal getExpertEvaluation() {
        return expertEvaluation;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
    }

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public void setExcludeTaxesPrice(BigDecimal excludeTaxesPrice) {
        this.excludeTaxesPrice = excludeTaxesPrice;
    }

    public BigDecimal getExcludeTaxesPrice() {
        return excludeTaxesPrice;
    }

    public void setTaxes(BigDecimal taxes) {
        this.taxes = taxes;
    }

    public BigDecimal getTaxes() {
        return taxes;
    }

    public void setSalesman(String salesman) {
        this.salesman = salesman;
    }

    public String getSalesman() {
        return salesman;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceTelephone(String serviceTelephone) {
        this.serviceTelephone = serviceTelephone;
    }

    public String getServiceTelephone() {
        return serviceTelephone;
    }

    public void setServiceEmail(String serviceEmail) {
        this.serviceEmail = serviceEmail;
    }

    public String getServiceEmail() {
        return serviceEmail;
    }

    public void setQuoteProject(String quoteProject) {
        this.quoteProject = quoteProject;
    }

    public String getQuoteProject() {
        return quoteProject;
    }

    public void setSalesInfo(String salesInfo) {
        this.salesInfo = salesInfo;
    }

    public String getSalesInfo() {
        return salesInfo;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setMasterCategoryId(Long masterCategoryId) {
        this.masterCategoryId = masterCategoryId;
    }

    public Long getMasterCategoryId() {
        return masterCategoryId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("customerId", getCustomerId())
                .append("recordCode", getRecordCode())
                .append("projectName", getProjectName())
                .append("customerName", getCustomerName())
                .append("companyName", getCompanyName())
                .append("masterCategory", getMasterCategory())
                .append("masterAbbreviation", getMasterAbbreviation())
                .append("totalPrice", getTotalPrice())
                .append("expertEvaluation", getExpertEvaluation())
                .append("discount", getDiscount())
                .append("discountPrice", getDiscountPrice())
                .append("excludeTaxesPrice", getExcludeTaxesPrice())
                .append("taxes", getTaxes())
                .append("salesman", getSalesman())
                .append("telephone", getTelephone())
                .append("email", getEmail())
                .append("serviceName", getServiceName())
                .append("serviceTelephone", getServiceTelephone())
                .append("serviceEmail", getServiceEmail())
                .append("quoteProject", getQuoteProject())
                .append("salesInfo", getSalesInfo())
                .append("createTime", getCreateTime())
                .append("remarks", getRemarks())
                .append("masterCategoryId", getMasterCategoryId())
                .append("urgentExpense", getUrgentExpense())
                .toString();
    }
}
