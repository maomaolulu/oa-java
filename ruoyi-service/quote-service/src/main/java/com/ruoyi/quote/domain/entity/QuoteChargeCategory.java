package com.ruoyi.quote.domain.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 收费标准详细分类对象 quote_charge_category
 * 
 * @author yrb
 * @date 2022-07-11
 */
public class QuoteChargeCategory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 收费分类id */
    private Long categoryId;

    /** 分类名称 */
    @Excel(name = "分类名称")
    private String categoryName;

    /** 父级部门id */
    @Excel(name = "父级部门id")
    private Long parentId;

    /** 祖级列表 */
    @Excel(name = "祖级列表")
    private String ancestors;

    /** 删除标志（0代表存在 1代表删除） */
    private Integer delFlag;

    public void setCategoryId(Long categoryId) 
    {
        this.categoryId = categoryId;
    }

    public Long getCategoryId() 
    {
        return categoryId;
    }
    public void setCategoryName(String categoryName) 
    {
        this.categoryName = categoryName;
    }

    public String getCategoryName() 
    {
        return categoryName;
    }
    public void setParentId(Long parentId) 
    {
        this.parentId = parentId;
    }

    public Long getParentId() 
    {
        return parentId;
    }
    public void setAncestors(String ancestors) 
    {
        this.ancestors = ancestors;
    }

    public String getAncestors() 
    {
        return ancestors;
    }
    public void setDelFlag(Integer delFlag) 
    {
        this.delFlag = delFlag;
    }

    public Integer getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("categoryId", getCategoryId())
            .append("categoryName", getCategoryName())
            .append("parentId", getParentId())
            .append("ancestors", getAncestors())
            .append("delFlag", getDelFlag())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
