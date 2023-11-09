package com.ruoyi.training.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

 /**
 * 选择题选项表
 * @author : zx
 * @date : 2022-5-30
 */
@ApiModel(value = "选择题选项表")
@TableName("tra_options_info")
@Data
public class TraOptionsInfo implements Serializable{
    /** id */
    @ApiModelProperty(name = "id")
    @TableId(type = IdType.AUTO)
    private BigInteger id ;
    /** 逻辑删 */
    @ApiModelProperty(name = "逻辑删")
    private String delFlag ;
    /** 创建人 */
    @ApiModelProperty(name = "创建人")
    private String createBy ;
    /** 创建时间 */
    @ApiModelProperty(name = "创建时间")
    private Date createTime ;
    /** 更新人 */
    @ApiModelProperty(name = "更新人")
    private String updateBy ;
    /** 更新时间 */
    @ApiModelProperty(name = "更新时间")
    private Date updateTime ;
    /** 题目id */
    @ApiModelProperty(name = "题目id")
    private BigInteger parentId ;
    /** 选项内容 */
    @ApiModelProperty(name = "选项内容")
    private String content ;
    /** 排序字段 */
    @ApiModelProperty(name = "排序字段")
    private String sortField ;

}