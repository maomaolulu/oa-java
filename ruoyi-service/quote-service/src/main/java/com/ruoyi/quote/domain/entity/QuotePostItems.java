package com.ruoyi.quote.domain.entity;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 岗位检测项目对象 quote_post_items
 * 
 * @author yrb
 * @date 2022-07-21
 */
public class QuotePostItems extends BaseEntity
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

    /** 所属岗位id */
    @Excel(name = "所属岗位id")
    private Long postId;

    /** 公司名称 */
    @Excel(name = "公司名称")
    private String companyName;

    /** 检测项目 */
    @Excel(name = "检测项目")
    private String itemsName;

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
    public void setPostId(Long postId) 
    {
        this.postId = postId;
    }

    public Long getPostId() 
    {
        return postId;
    }
    public void setCompanyName(String companyName) 
    {
        this.companyName = companyName;
    }

    public String getCompanyName() 
    {
        return companyName;
    }
    public void setItemsName(String itemsName) 
    {
        this.itemsName = itemsName;
    }

    public String getItemsName() 
    {
        return itemsName;
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
            .append("postId", getPostId())
            .append("companyName", getCompanyName())
            .append("itemsName", getItemsName())
            .append("totalPrice", getTotalPrice())
            .toString();
    }
}
