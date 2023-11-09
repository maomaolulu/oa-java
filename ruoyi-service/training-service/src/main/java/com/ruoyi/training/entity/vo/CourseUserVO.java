package com.ruoyi.training.entity.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 我的课程载体-用于查询证书时存放证书对应课程的数据载体
 *
 * @author hjy
 * @date 2022/6/22 10:04
 */
@Data
public class CourseUserVO {

    /**
     * 课程id
     */
    private Long courseId;
    /**
     * 课程名称
     */
    private String courseName;
    /**
     * 课程学时
     */
    private BigDecimal courseHour;


}
