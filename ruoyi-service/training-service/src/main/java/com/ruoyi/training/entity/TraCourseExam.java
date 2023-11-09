package com.ruoyi.training.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 我的考试对象 tra_course_exam
 * 
 * @author yrb
 * @date 2022-10-21
 */
@TableName("tra_course_exam")
@Accessors(chain = true)
@Data
public class TraCourseExam implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 主键，自增 */
    private Long id;

    /** 用户id */
    @Excel(name = "用户id")
    private Long userId;

    /** 类别id (废除) */
    @Excel(name = "类别id (废除)")
    private Long trainId;

    /** 年份：eg： 2022 */
    @Excel(name = "年份：eg： 2022")
    private String trainYear;

    /** 状态 0：未开始  1：未通过； 2：已通过 */
    @Excel(name = "状态 0：未开始  1：未通过； 2：已通过")
    private Long status;

    /** 分数 */
    @Excel(name = "分数")
    private BigDecimal score;

    /** 考试类型：0：每年默认考试；1自定义考试 */
    @Excel(name = "考试类型：0：每年默认考试；1自定义考试")
    private Long examType;

    /** 考试名称 */
    @Excel(name = "考试名称")
    private String examName;

    /** 考试信息id */
    @Excel(name = "考试信息id")
    private Long examId;


}
