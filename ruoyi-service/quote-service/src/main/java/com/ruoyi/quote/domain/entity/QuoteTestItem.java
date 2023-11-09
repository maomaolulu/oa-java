package com.ruoyi.quote.domain.entity;

import java.math.BigDecimal;
import java.io.Serializable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * (环境)子类检测项目对象 quote_test_item
 * 
 * @author yrb
 * @date 2022-09-20
 */
public class QuoteTestItem extends BaseEntity implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 自增主键 */
    private Long id;

    /** 报价单id */
    @Excel(name = "报价单id")
    private String sheetId;

    /** 子类id */
    @Excel(name = "子类id")
    private Long subId;

    /** 污染物类别id */
    @Excel(name = "污染物类别id")
    private Long pollutantTypeId;

    /** 检测点位id */
    @Excel(name = "检测点位id")
    private Long pointId;

    /** 点位名称 */
    @Excel(name = "点位名称")
    private String pointName;

    /** 频次 */
    @Excel(name = "频次")
    private String frequence;

    /** 污染物id */
    @Excel(name = "污染物id")
    private Long pollutantId;

    /** 因素名称 */
    @Excel(name = "因素名称")
    private String factorName;

    /** 检测标准及编号 */
    @Excel(name = "检测标准及编号")
    private String standardInfo;

    /** 限制范围 */
    @Excel(name = "限制范围")
    private String limitRange;

    /** 单价 */
    @Excel(name = "单价")
    private BigDecimal price;

    /** 总价 */
    @Excel(name = "总价")
    private BigDecimal totalPrice;

    /** 点位数 */
    @Excel(name = "点位数")
    private BigDecimal pointNumber;

    /** 样品数 */
    @Excel(name = "样品数")
    private BigDecimal sampleNumber;

    /** 是否是临时文件：1是，0否 */
    @Excel(name = "是否是临时文件：1是，0否")
    private Long tempFlag;

    /** 检测性质id */
    @Excel(name = "检测性质id")
    private Long natureId;

    /** 其他检测项目：1其他，2非其他 */
    @Excel(name = "其他检测项目：1其他，2非其他")
    private Long otherType;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setSheetId(String sheetId) 
    {
        this.sheetId = sheetId;
    }

    public String getSheetId() 
    {
        return sheetId;
    }
    public void setSubId(Long subId) 
    {
        this.subId = subId;
    }

    public Long getSubId() 
    {
        return subId;
    }
    public void setPollutantTypeId(Long pollutantTypeId) 
    {
        this.pollutantTypeId = pollutantTypeId;
    }

    public Long getPollutantTypeId() 
    {
        return pollutantTypeId;
    }
    public void setPointId(Long pointId) 
    {
        this.pointId = pointId;
    }

    public Long getPointId() 
    {
        return pointId;
    }
    public void setPointName(String pointName) 
    {
        this.pointName = pointName;
    }

    public String getPointName() 
    {
        return pointName;
    }
    public void setFrequence(String frequence) 
    {
        this.frequence = frequence;
    }

    public String getFrequence() 
    {
        return frequence;
    }
    public void setPollutantId(Long pollutantId) 
    {
        this.pollutantId = pollutantId;
    }

    public Long getPollutantId() 
    {
        return pollutantId;
    }
    public void setFactorName(String factorName) 
    {
        this.factorName = factorName;
    }

    public String getFactorName() 
    {
        return factorName;
    }
    public void setStandardInfo(String standardInfo) 
    {
        this.standardInfo = standardInfo;
    }

    public String getStandardInfo() 
    {
        return standardInfo;
    }
    public void setLimitRange(String limitRange) 
    {
        this.limitRange = limitRange;
    }

    public String getLimitRange() 
    {
        return limitRange;
    }
    public void setPrice(BigDecimal price) 
    {
        this.price = price;
    }

    public BigDecimal getPrice() 
    {
        return price;
    }
    public void setTotalPrice(BigDecimal totalPrice) 
    {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getTotalPrice() 
    {
        return totalPrice;
    }
    public void setPointNumber(BigDecimal pointNumber) 
    {
        this.pointNumber = pointNumber;
    }

    public BigDecimal getPointNumber() 
    {
        return pointNumber;
    }
    public void setSampleNumber(BigDecimal sampleNumber) 
    {
        this.sampleNumber = sampleNumber;
    }

    public BigDecimal getSampleNumber() 
    {
        return sampleNumber;
    }
    public void setTempFlag(Long tempFlag) 
    {
        this.tempFlag = tempFlag;
    }

    public Long getTempFlag() 
    {
        return tempFlag;
    }
    public void setNatureId(Long natureId) 
    {
        this.natureId = natureId;
    }

    public Long getNatureId() 
    {
        return natureId;
    }
    public void setOtherType(Long otherType) 
    {
        this.otherType = otherType;
    }

    public Long getOtherType() 
    {
        return otherType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("sheetId", getSheetId())
            .append("subId", getSubId())
            .append("pollutantTypeId", getPollutantTypeId())
            .append("pointId", getPointId())
            .append("pointName", getPointName())
            .append("frequence", getFrequence())
            .append("pollutantId", getPollutantId())
            .append("factorName", getFactorName())
            .append("standardInfo", getStandardInfo())
            .append("limitRange", getLimitRange())
            .append("price", getPrice())
            .append("totalPrice", getTotalPrice())
            .append("pointNumber", getPointNumber())
            .append("sampleNumber", getSampleNumber())
            .append("tempFlag", getTempFlag())
            .append("createTime", getCreateTime())
            .append("natureId", getNatureId())
            .append("otherType", getOtherType())
            .toString();
    }
}
