package com.ruoyi.quote.domain.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 岗位信息对象 quote_post_info
 * 
 * @author yrb
 * @date 2022-07-19
 */
public class QuotePostInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 自增主键 */
    private Long id;

    /** 岗位名称 */
    @Excel(name = "岗位名称")
    private String postName;

    /** 所属行业id */
    @Excel(name = "所属行业id")
    private Long industryId;

    /** 创建者 */
    @Excel(name = "创建者")
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
    public void setPostName(String postName) 
    {
        this.postName = postName;
    }

    public String getPostName() 
    {
        return postName;
    }
    public void setIndustryId(Long industryId) 
    {
        this.industryId = industryId;
    }

    public Long getIndustryId() 
    {
        return industryId;
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
            .append("postName", getPostName())
            .append("industryId", getIndustryId())
            .append("creator", getCreator())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
