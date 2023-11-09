package com.ruoyi.quote.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author yrb
 * @Date 2022/6/20 16:45
 * @Version 1.0
 * @Description 检测细项（环境）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuoteTestItemDetailsVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "岗位名称")
    private String postName;

    @ApiModelProperty(value = "检测项目名称")
    private String item;

    @ApiModelProperty(value = "检测点位id")
    private Long pointId;

    @ApiModelProperty(value = "检测点位名称")
    private String pointName;

    @ApiModelProperty(value = "检测项目id")
    private Long pollutantId;

    @ApiModelProperty(value = "单价")
    private BigDecimal price;

    @ApiModelProperty(value = "频次")
    private String frequence;

    @ApiModelProperty(value = "点位数")
    private BigDecimal pointNumber;

    @ApiModelProperty(value = "样品数")
    private BigDecimal sampleNumber;

    @ApiModelProperty(value = "总价（小计）")
    private BigDecimal totalPrice;

    @ApiModelProperty(value = "检测标准")
    private String standardInfo;

    @ApiModelProperty(value = "限制范围")
    private String limitRange;

    @ApiModelProperty(value = "检测类别id")
    private Long pollutantTypeId;

    @ApiModelProperty(value = "危害因素类别：扩项、分包")
    private Long factorType;

    @ApiModelProperty(value = "检测类别名称")
    private String pollutantName;

    @ApiModelProperty(value = "检测性质id")
    private Long natureId;

    @ApiModelProperty(value = "其他检测项目")
    private Long otherType;

    @ApiModelProperty(value = "表单id")
    private String sheetId;

    @ApiModelProperty(value = "子类id")
    private Long subId;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "选中标识")
    private Integer checked;
}
