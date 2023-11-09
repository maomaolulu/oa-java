package com.ruoyi.quote.domain.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 检测性质对象 quote_test_nature
 *
 * @author yrb
 * @date 2022-09-13
 */
public class QuoteTestNature implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    private Long id;

    /**
     * 检测名称
     */
    @Excel(name = "检测名称")
    private String natureName;

    /**
     * 删除标识：1已删除，0未删除
     */
    private Integer delFlag;

    /**
     * 是否选中：1已选中，0未选中
     */
    @Excel(name = "是否选中：1已选中，0未选中")
    private Integer checked;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setNatureName(String natureName) {
        this.natureName = natureName;
    }

    public String getNatureName() {
        return natureName;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setChecked(Integer checked) {
        this.checked = checked;
    }

    public Integer getChecked() {
        return checked;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("natureName", getNatureName())
                .append("delFlag", getDelFlag())
                .append("checked", getChecked())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
