package com.ruoyi.quote.domain.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 危害因素对象 quote_harm_factor
 * 
 * @author yrb
 * @date 2022-07-19
 */
public class QuoteHarmFactor extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 自增主键 */
    private Long id;

    /** 岗位id */
    @Excel(name = "岗位id")
    private Long postId;

    /** 危害因素id */
    @Excel(name = "危害因素id")
    private Long baseId;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setPostId(Long postId) 
    {
        this.postId = postId;
    }

    public Long getPostId() 
    {
        return postId;
    }
    public void setBaseId(Long baseId) 
    {
        this.baseId = baseId;
    }

    public Long getBaseId() 
    {
        return baseId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("postId", getPostId())
            .append("baseId", getBaseId())
            .append("createTime", getCreateTime())
            .toString();
    }
}
