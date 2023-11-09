package com.ruoyi.training.entity.vo;

import com.ruoyi.training.entity.TraCourseInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author yrb
 * @Date 2022/6/3 3:42
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseInfoVO extends TraCourseInfo implements Serializable {
    @ApiModelProperty("我的课程")
    private Integer myCourse;
    @ApiModelProperty("学习进度")
    private String schedule;
    @ApiModelProperty("课程类型：1必修 0选修")
    private Integer courseType;
    @ApiModelProperty("课程类型名称")
    private String trainType;
}
