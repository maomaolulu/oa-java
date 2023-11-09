package com.ruoyi.quote.domain.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 报价分类子类对象 quote_sub_category
 * 
 * @author yrb
 * @date 2022-07-11
 */
public class QuoteSubCategory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 自增主键 */
    private Long id;

    /** 项目分类 */
    @Excel(name = "项目分类")
    private String projectCategory;

    /** 分类简称 */
    @Excel(name = "分类简称")
    private String abbreviationCategory;

    /** 编号 */
    @Excel(name = "编号")
    private String code;

    /** 分类全称 */
    @Excel(name = "分类全称")
    private String fullCategory;

    /** 父类id */
    @Excel(name = "父类id")
    private Long parentId;

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
    public void setProjectCategory(String projectCategory) 
    {
        this.projectCategory = projectCategory;
    }

    public String getProjectCategory() 
    {
        return projectCategory;
    }
    public void setAbbreviationCategory(String abbreviationCategory) 
    {
        this.abbreviationCategory = abbreviationCategory;
    }

    public String getAbbreviationCategory() 
    {
        return abbreviationCategory;
    }
    public void setCode(String code) 
    {
        this.code = code;
    }

    public String getCode() 
    {
        return code;
    }
    public void setFullCategory(String fullCategory) 
    {
        this.fullCategory = fullCategory;
    }

    public String getFullCategory() 
    {
        return fullCategory;
    }
    public void setParentId(Long parentId) 
    {
        this.parentId = parentId;
    }

    public Long getParentId() 
    {
        return parentId;
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
            .append("projectCategory", getProjectCategory())
            .append("abbreviationCategory", getAbbreviationCategory())
            .append("code", getCode())
            .append("fullCategory", getFullCategory())
            .append("parentId", getParentId())
            .append("delFlag", getDelFlag())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
