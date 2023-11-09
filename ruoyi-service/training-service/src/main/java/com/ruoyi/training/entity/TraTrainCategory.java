package com.ruoyi.training.entity;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 培训类型对象 tra_train_category
 *
 * @author yrb
 * @date 2022-06-06
 */
public class TraTrainCategory extends BaseEntity {
    /**
     * 自增主键-培训类型id
     */
    private Long id;

    /**
     * 培训类型名称
     */
    @Excel(name = "培训类型名称")
    private String categoryName;

    /**
     * 创建者
     */
    @Excel(name = "创建者")
    private String creator;

    /**
     * 最后操作者
     */
    @Excel(name = "最后操作者")
    private String lastModifer;

    /**
     * 封面图片链接-新增
     */
    private String coverUrl;
    /**
     * 封面真实路径
     */
    private String tempUrl;
    /**
     * 样式属性-新增
     */
    private String cssClass;
    /**
     * 公司id-新增
     */
    private Long companyId;
    /**
     * 图片临时id
     */
    private String tempId;
    /**
     * 图片文件夹类型
     */
    private String types;

    public String getTempUrl() {
        return tempUrl;
    }

    public void setTempUrl(String tempUrl) {
        this.tempUrl = tempUrl;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public String getTempId() {
        return tempId;
    }

    public void setTempId(String tempId) {
        this.tempId = tempId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreator() {
        return creator;
    }

    public void setLastModifer(String lastModifer) {
        this.lastModifer = lastModifer;
    }

    public String getLastModifer() {
        return lastModifer;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("categoryName", getCategoryName())
                .append("creator", getCreator())
                .append("createTime", getCreateTime())
                .append("lastModifer", getLastModifer())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
