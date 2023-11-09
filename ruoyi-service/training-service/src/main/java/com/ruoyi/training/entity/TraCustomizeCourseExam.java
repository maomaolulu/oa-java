package com.ruoyi.training.entity;

import java.io.Serializable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 考试涉及的课程对象 tra_customize_course_exam
 * 
 * @author yrb
 * @date 2022-10-21
 */
public class TraCustomizeCourseExam implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 考试id */
    private Long examId;

    /** 课程id */
    private Long courseId;

    public void setExamId(Long examId) 
    {
        this.examId = examId;
    }

    public Long getExamId() 
    {
        return examId;
    }
    public void setCourseId(Long courseId) 
    {
        this.courseId = courseId;
    }

    public Long getCourseId() 
    {
        return courseId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("examId", getExamId())
            .append("courseId", getCourseId())
            .toString();
    }
}
