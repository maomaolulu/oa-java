package com.ruoyi.training.entity.dto;

import com.ruoyi.training.entity.TraCustomizeExam;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author yrb
 * @Date 2022/10/19 17:03
 * @Version 1.0
 * @Description 考试信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraCustomizeExamDTO extends TraCustomizeExam {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "课程ID集合")
    private List<Long>  courseIdList;

    @ApiModelProperty(value = "考试用户ID集合")
    private List<Long>  userIdList;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "考试用户id集合")
    private String userIds;

    @ApiModelProperty(value = "考试id")
    private Long examId;
}
