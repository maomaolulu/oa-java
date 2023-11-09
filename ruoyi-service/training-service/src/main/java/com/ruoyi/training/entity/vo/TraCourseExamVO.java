package com.ruoyi.training.entity.vo;

import com.ruoyi.training.entity.TraCourseExam;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author yrb
 * @Date 2022/10/21 16:51
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraCourseExamVO extends TraCourseExam {
    @ApiModelProperty(value = "课程所属分类及对应颜色")
    private String categoryList;

    /**
     * 关联数据-是否可以考试  0：不可考试  1：可以考试
     * 注：此属性用于判断是否满足考试的条件
     * hjy
     */
    private Integer checked;
}
