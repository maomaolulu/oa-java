package com.ruoyi.quote.domain.vo;

import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author yrb
 * @Date 2022/6/20 10:59
 * @Version 1.0
 * @Description 参数配置 获取大类、子类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteIndustryInfoVO extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "大类id")
    private Long id;

    @ApiModelProperty(value = "子类id")
    private Long subId;

    @ApiModelProperty(value = "行业大类")
    private String industryName;

    @ApiModelProperty(value = "行业编号")
    private String industryCode;

    @ApiModelProperty(value = "行业子类")
    private String subName;

    @ApiModelProperty(value = "所属项目")
    private String categoryName;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "父类id")
    private Long parentId;

    @ApiModelProperty(value = "创建者")
    private String creator;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}
