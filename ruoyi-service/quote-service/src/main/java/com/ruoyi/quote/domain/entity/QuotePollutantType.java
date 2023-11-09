package com.ruoyi.quote.domain.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 污染物类别对象 quote_pollutant_type
 * 
 * @author yrb
 * @date 2022-07-11
 */
public class QuotePollutantType extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 自增主键 */
    private Long id;

    /** 污染物名称 */
    @Excel(name = "污染物名称")
    private String pollutantName;

    /** 所属项目id */
    @Excel(name = "所属项目id")
    private Long projectId;

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
    public void setPollutantName(String pollutantName) 
    {
        this.pollutantName = pollutantName;
    }

    public String getPollutantName() 
    {
        return pollutantName;
    }
    public void setProjectId(Long projectId) 
    {
        this.projectId = projectId;
    }

    public Long getProjectId() 
    {
        return projectId;
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
            .append("pollutantName", getPollutantName())
            .append("projectId", getProjectId())
            .append("delFlag", getDelFlag())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
