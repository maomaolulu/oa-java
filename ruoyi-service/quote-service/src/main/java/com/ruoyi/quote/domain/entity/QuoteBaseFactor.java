package com.ruoyi.quote.domain.entity;

import java.math.BigDecimal;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 危害因素基础信息对象 quote_base_factor
 *
 * @author yrb
 * @date 2022-07-11
 */
public class QuoteBaseFactor extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    private Long id;

    /**
     * 类别id
     */
    @Excel(name = "类别id")
    private Long categoryId;

    /**
     * 子类id
     */
    @Excel(name = "子类id")
    private Long subcategoryId;

    /**
     * 因素名称
     */
    @Excel(name = "因素名称")
    private String factorName;

    /**
     * 检测标准及编号
     */
    @Excel(name = "检测标准及编号")
    private String standardInfo;

    /**
     * 限制范围
     */
    @Excel(name = "限制范围")
    private String limitRange;

    /**
     * 分析费
     */
    @Excel(name = "分析费")
    private BigDecimal analysePrice;

    /**
     * 采样费
     */
    @Excel(name = "采样费")
    private BigDecimal samplePrice;

    /**
     * 单价
     */
    @Excel(name = "单价")
    private BigDecimal price;

    /**
     * 删除标识：1已删除，0未删除
     */
    private Integer delFlag;

    /**
     * 危害因素类型 1分包 2扩项
     */
    private Integer factorType;

    public Integer getFactorType() {
        return factorType;
    }

    public void setFactorType(Integer factorType) {
        this.factorType = factorType;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setSubcategoryId(Long subcategoryId) {
        this.subcategoryId = subcategoryId;
    }

    public Long getSubcategoryId() {
        return subcategoryId;
    }

    public void setFactorName(String factorName) {
        this.factorName = factorName;
    }

    public String getFactorName() {
        return factorName;
    }

    public void setStandardInfo(String standardInfo) {
        this.standardInfo = standardInfo;
    }

    public String getStandardInfo() {
        return standardInfo;
    }

    public void setLimitRange(String limitRange) {
        this.limitRange = limitRange;
    }

    public String getLimitRange() {
        return limitRange;
    }

    public void setAnalysePrice(BigDecimal analysePrice) {
        this.analysePrice = analysePrice;
    }

    public BigDecimal getAnalysePrice() {
        return analysePrice;
    }

    public void setSamplePrice(BigDecimal samplePrice) {
        this.samplePrice = samplePrice;
    }

    public BigDecimal getSamplePrice() {
        return samplePrice;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("categoryId", getCategoryId())
                .append("subcategoryId", getSubcategoryId())
                .append("factorName", getFactorName())
                .append("standardInfo", getStandardInfo())
                .append("limitRange", getLimitRange())
                .append("analysePrice", getAnalysePrice())
                .append("samplePrice", getSamplePrice())
                .append("price", getPrice())
                .append("delFlag", getDelFlag())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("factorType", getFactorType())
                .toString();
    }
}
