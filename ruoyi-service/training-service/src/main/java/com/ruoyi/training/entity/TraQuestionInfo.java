package com.ruoyi.training.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * 题目表
 *
 * @author : zx
 * @date : 2022-5-30
 */
@ApiModel(value = "题目表")
@TableName("tra_question_info")
@Data
public class TraQuestionInfo implements Serializable {
    static final long serialVersionUID = 1L;
    static private String ss = "";
    /**
     * id
     */
    @ApiModelProperty(name = "id")
    @TableId(type = IdType.AUTO)
    private BigInteger id;
    /**
     * 逻辑删
     */
    @ApiModelProperty(name = "逻辑删")
    private String delFlag;
    /**
     * 创建人
     */
    @ApiModelProperty(name = "创建人")
    private String createBy;
    /**
     * 创建时间
     */
    @ApiModelProperty(name = "创建时间")
    private Date createTime;
    /**
     * 更新人
     */
    @ApiModelProperty(name = "更新人")
    private String updateBy;
    /**
     * 更新时间
     */
    @ApiModelProperty(name = "更新时间")
    private Date updateTime;
    /**
     * 课程id
     */
    @ApiModelProperty(name = "课程id")
    private BigInteger parentId;
    /**
     * 题目编号
     */
    @ApiModelProperty(name = "题目编号")
    private String questionCode;
    /**
     * 题目内容
     */
    @ApiModelProperty(name = "题目内容")
    private String content;
    /**
     * 1单选2多选3判断
     */
    @ApiModelProperty(name = "1单选2多选3判断")
    private Integer type;
    /**
     * 答案(多选;隔开)
     */
    @ApiModelProperty(name = "答案(多选;隔开)")
    private String answer;
    /**
     * 单题分数
     */
    @ApiModelProperty(name = "单题分数")
    private String score;
    /**
     * 排序字段
     */
    @ApiModelProperty(name = "排序字段")
    private String sortField;
    /**
     * 选项
     */
    @TableField(exist = false)
    private List<TraOptionsInfo> optionsInfos;
    /**
     * 课程ids
     */
    @TableField(exist = false)
    private List<BigInteger> courseIds;
    /**
     * UUID图片临时id
     */
    @TableField(exist = false)
    private String uuid;
}