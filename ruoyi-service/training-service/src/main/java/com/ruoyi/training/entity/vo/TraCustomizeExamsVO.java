package com.ruoyi.training.entity.vo;

import com.ruoyi.training.entity.TraCourseInfo;
import com.ruoyi.training.entity.TraCustomizeExam;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author yrb
 * @Date 2022/10/24 20:22
 * @Version 1.0
 * @Description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TraCustomizeExamsVO {
    @ApiModelProperty(value = "考试信息")
    private TraCustomizeExam traCustomizeExam;

    @ApiModelProperty(value = "课程信息集合")
    private List<TraCourseInfo> traCourseInfoList;
}
