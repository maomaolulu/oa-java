package com.ruoyi.quote.domain.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 行业信息对象 quote_industry_info
 * 
 * @author yrb
 * @date 2022-07-11
 */
public class QuoteIndustryInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 自增主键 */
    private Long id;

    /** 行业名称 */
    @Excel(name = "行业名称")
    private String industryName;

    /** 行业编号 */
    @Excel(name = "行业编号")
    private String industryCode;

    /** 项目id */
    @Excel(name = "项目id")
    private Long projectId;

    /** 父类id */
    @Excel(name = "父类id")
    private Long parentId;

    /** 创建人 */
    @Excel(name = "创建人")
    private String creator;

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
    public void setIndustryName(String industryName) 
    {
        this.industryName = industryName;
    }

    public String getIndustryName() 
    {
        return industryName;
    }
    public void setIndustryCode(String industryCode) 
    {
        this.industryCode = industryCode;
    }

    public String getIndustryCode() 
    {
        return industryCode;
    }
    public void setProjectId(Long projectId) 
    {
        this.projectId = projectId;
    }

    public Long getProjectId() 
    {
        return projectId;
    }
    public void setParentId(Long parentId) 
    {
        this.parentId = parentId;
    }

    public Long getParentId() 
    {
        return parentId;
    }
    public void setCreator(String creator) 
    {
        this.creator = creator;
    }

    public String getCreator() 
    {
        return creator;
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
            .append("industryName", getIndustryName())
            .append("industryCode", getIndustryCode())
            .append("projectId", getProjectId())
            .append("parentId", getParentId())
            .append("creator", getCreator())
            .append("delFlag", getDelFlag())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
