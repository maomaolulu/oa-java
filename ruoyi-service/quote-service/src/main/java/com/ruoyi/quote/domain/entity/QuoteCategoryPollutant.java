package com.ruoyi.quote.domain.entity;

import java.io.Serializable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 行业类别污染物类别关联对象 quote_category_pollutant
 * 
 * @author yrb
 * @date 2022-09-13
 */
public class QuoteCategoryPollutant extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 行业主分类id */
    @Excel(name = "行业主分类id")
    private Long masterCategoryId;

    /** 行业子类id */
    @Excel(name = "行业子类id")
    private Long subCategoryId;

    /** 污染物类别id */
    @Excel(name = "污染物类别id")
    private Long pollutantTypeId;

    /** 污染物id */
    @Excel(name = "污染物id")
    private Long pollutantId;

    /** 所属项目id */
    @Excel(name = "所属项目id")
    private Long projectId;

    /** 创建人 */
    @Excel(name = "创建人")
    private String creator;

    /** 检测性质名称 */
    @Excel(name = "检测性质名称")
    private String natureName;

    /** 检测性质id */
    @Excel(name = "检测性质id")
    private String natureIds;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setMasterCategoryId(Long masterCategoryId) 
    {
        this.masterCategoryId = masterCategoryId;
    }

    public Long getMasterCategoryId() 
    {
        return masterCategoryId;
    }
    public void setSubCategoryId(Long subCategoryId) 
    {
        this.subCategoryId = subCategoryId;
    }

    public Long getSubCategoryId() 
    {
        return subCategoryId;
    }
    public void setPollutantTypeId(Long pollutantTypeId) 
    {
        this.pollutantTypeId = pollutantTypeId;
    }

    public Long getPollutantTypeId() 
    {
        return pollutantTypeId;
    }
    public void setPollutantId(Long pollutantId) 
    {
        this.pollutantId = pollutantId;
    }

    public Long getPollutantId() 
    {
        return pollutantId;
    }
    public void setProjectId(Long projectId) 
    {
        this.projectId = projectId;
    }

    public Long getProjectId() 
    {
        return projectId;
    }
    public void setCreator(String creator) 
    {
        this.creator = creator;
    }

    public String getCreator() 
    {
        return creator;
    }
    public void setNatureName(String natureName) 
    {
        this.natureName = natureName;
    }

    public String getNatureName() 
    {
        return natureName;
    }
    public void setNatureIds(String natureIds) 
    {
        this.natureIds = natureIds;
    }

    public String getNatureIds() 
    {
        return natureIds;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("masterCategoryId", getMasterCategoryId())
            .append("subCategoryId", getSubCategoryId())
            .append("pollutantTypeId", getPollutantTypeId())
            .append("pollutantId", getPollutantId())
            .append("projectId", getProjectId())
            .append("creator", getCreator())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("natureName", getNatureName())
            .append("natureIds", getNatureIds())
            .toString();
    }
}
