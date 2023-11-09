package com.ruoyi.quote.domain.entity;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 点位信息对象 quote_point_info
 * 
 * @author yrb
 * @date 2022-07-11
 */
public class QuotePointInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 自增主键 */
    private Long id;

    /** 表单id */
    @Excel(name = "表单id")
    private String sheetId;

    /** 子类id */
    @Excel(name = "子类id")
    private Long subId;

    /** 点位名称 */
    @Excel(name = "点位名称")
    private String pointName;

    /** 污染物名称 */
    @Excel(name = "污染物名称")
    private String pollutantName;

    /** 检测类别id */
    @Excel(name = "检测类别id")
    private Long pollutantTypeId;

    /** 总价 */
    @Excel(name = "总价")
    private BigDecimal totalPrice;

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
    public void setPointName(String pointName) 
    {
        this.pointName = pointName;
    }

    public String getPointName() 
    {
        return pointName;
    }
    public void setPollutantName(String pollutantName) 
    {
        this.pollutantName = pollutantName;
    }

    public String getPollutantName() 
    {
        return pollutantName;
    }
    public void setPollutantTypeId(Long pollutantTypeId) 
    {
        this.pollutantTypeId = pollutantTypeId;
    }

    public Long getPollutantTypeId() 
    {
        return pollutantTypeId;
    }
    public void setTotalPrice(BigDecimal totalPrice) 
    {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getTotalPrice() 
    {
        return totalPrice;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("sheetId", getSheetId())
            .append("subId", getSubId())
            .append("pointName", getPointName())
            .append("pollutantName", getPollutantName())
            .append("pollutantTypeId", getPollutantTypeId())
            .append("totalPrice", getTotalPrice())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
