package com.ruoyi.quote.domain.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 报价主分类对象 quote_master_category
 * 
 * @author yrb
 * @date 2022-07-11
 */
public class QuoteMasterCategory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 自增主键 */
    private Long id;

    /** 分类名称 */
    @Excel(name = "分类名称")
    private String categoryName;

    /** 简称 */
    @Excel(name = "简称")
    private String abbreviation;

    /** 删除标识：1已删除，0未删除 */
    private Integer delFlag;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setCategoryName(String categoryName) 
    {
        this.categoryName = categoryName;
    }

    public String getCategoryName() 
    {
        return categoryName;
    }
    public void setAbbreviation(String abbreviation) 
    {
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() 
    {
        return abbreviation;
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
            .append("id", getId())
            .append("categoryName", getCategoryName())
            .append("abbreviation", getAbbreviation())
            .append("delFlag", getDelFlag())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
