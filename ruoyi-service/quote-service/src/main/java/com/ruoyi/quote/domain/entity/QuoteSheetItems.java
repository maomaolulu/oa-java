package com.ruoyi.quote.domain.entity;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 报价单检测项目对象 quote_sheet_items
 * 
 * @author yrb
 * @date 2022-07-21
 */
public class QuoteSheetItems extends BaseEntity
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

    /** 岗位id */
    @Excel(name = "岗位id")
    private Long postId;

    /** 所属岗位名称 */
    @Excel(name = "所属岗位名称")
    private String postName;

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

    /** 点位数 */
    @Excel(name = "点位数")
    private BigDecimal pointNumber;

    /** 总价 */
    @Excel(name = "总价")
    private BigDecimal totalPrice;

    /** 是否是临时文件：1是，0否 */
    @Excel(name = "是否是临时文件：1是，0否")
    private Long tempFlag;

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
    public void setPostId(Long postId) 
    {
        this.postId = postId;
    }

    public Long getPostId() 
    {
        return postId;
    }
    public void setPostName(String postName) 
    {
        this.postName = postName;
    }

    public String getPostName() 
    {
        return postName;
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
    public void setPointNumber(BigDecimal pointNumber) 
    {
        this.pointNumber = pointNumber;
    }

    public BigDecimal getPointNumber() 
    {
        return pointNumber;
    }
    public void setTotalPrice(BigDecimal totalPrice) 
    {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getTotalPrice() 
    {
        return totalPrice;
    }
    public void setTempFlag(Long tempFlag) 
    {
        this.tempFlag = tempFlag;
    }

    public Long getTempFlag() 
    {
        return tempFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("sheetId", getSheetId())
            .append("subId", getSubId())
            .append("postId", getPostId())
            .append("postName", getPostName())
            .append("factorName", getFactorName())
            .append("standardInfo", getStandardInfo())
            .append("limitRange", getLimitRange())
            .append("price", getPrice())
            .append("pointNumber", getPointNumber())
            .append("totalPrice", getTotalPrice())
            .append("tempFlag", getTempFlag())
            .append("createTime", getCreateTime())
            .toString();
    }
}
