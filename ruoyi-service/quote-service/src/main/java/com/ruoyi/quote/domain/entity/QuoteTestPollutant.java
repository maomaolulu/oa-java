package com.ruoyi.quote.domain.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 检测类型污染物关联对象 quote_test_pollutant
 * 
 * @author yrb
 * @date 2022-06-28
 */
public class QuoteTestPollutant extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 检测类型关联id */
    private Long id;

    /** 污染物id */
    @Excel(name = "污染物id")
    private Long pollutantId;

    /** 创建者 */
    @Excel(name = "创建者")
    private String creator;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setPollutantId(Long pollutantId) 
    {
        this.pollutantId = pollutantId;
    }

    public Long getPollutantId() 
    {
        return pollutantId;
    }
    public void setCreator(String creator) 
    {
        this.creator = creator;
    }

    public String getCreator() 
    {
        return creator;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("pollutantId", getPollutantId())
            .append("creator", getCreator())
            .append("createTime", getCreateTime())
            .toString();
    }
}
