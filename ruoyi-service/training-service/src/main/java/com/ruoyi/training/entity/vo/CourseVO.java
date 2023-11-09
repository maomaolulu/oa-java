package com.ruoyi.training.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: zx
 * @CreateTime: 2022-06-01  21:06
 * @Description: 课程信息
 */
@Data
@Accessors(chain = true)
public class CourseVO {
    /**
     * 课程名称
     */
    private String courseName;
    /**
     * 课程类别
     */
    private String categoryName;
}
