package com.ruoyi.training.entity;

import java.io.Serializable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 考试涉及的课程对象 tra_customize_exam_user
 * 
 * @author yrb
 * @date 2022-10-21
 */
public class TraCustomizeExamUser implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 考试id */
    private Long examId;

    /** 用户id */
    private Long userId;

    public void setExamId(Long examId) 
    {
        this.examId = examId;
    }

    public Long getExamId() 
    {
        return examId;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("examId", getExamId())
            .append("userId", getUserId())
            .toString();
    }
}
