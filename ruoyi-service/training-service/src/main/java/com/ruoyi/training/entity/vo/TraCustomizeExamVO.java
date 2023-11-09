package com.ruoyi.training.entity.vo;

import com.ruoyi.training.entity.TraCustomizeExam;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author yrb
 * @Date 2022/10/21 14:02
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraCustomizeExamVO extends TraCustomizeExam {
    @ApiModelProperty(value = "课程名称: 中间用”、“隔开")
    private String courseName;

    @ApiModelProperty(value = "课程名称: 中间用”、“隔开")
    private String userIds;


    private Long companyId;
}
