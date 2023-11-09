package com.ruoyi.quote.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author yrb
 * @Date 2022/6/27 20:58
 * @Version 1.0
 * @Description 公卫--获取检测类型列表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuotePollutantTypeVO implements Serializable {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "所属行业")
     private String industryName;

    @ApiModelProperty(value = "检测性质名称集合")
    private String natureName;

    @ApiModelProperty(value = "检测性质名称id集合")
    private String natureIds;

    @ApiModelProperty(value = "检测类别")
    private String pollutantName;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
