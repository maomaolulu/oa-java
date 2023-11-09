package com.ruoyi.training.entity.dto;

import com.ruoyi.training.entity.TraCourseInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author yrb
 * @Date 2022/5/31 10:32
 * @Version 1.0
 * @Description 课程信息传输对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraCourseInfoDTO extends TraCourseInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "关联部门id")
    private String ancestors;

    @ApiModelProperty(value = "考核年度")
    private Integer trainYear;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "课程类型：1必修 0选修")
    private Integer courseType;

    @ApiModelProperty(value = "上下架")
    private Integer market;

    @ApiModelProperty(value = "培训id集合（前端传参数）")
    private String trainIds;

    @ApiModelProperty(value = "培训id集合")
    private List<String> trainIdList;
}
