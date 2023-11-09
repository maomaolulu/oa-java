package com.ruoyi.training.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author yrb
 * @Date 2022/6/2 9:56
 * @Version 1.0
 * @Description 培训管理Dto
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingManageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("部门id")
    private Long deptId;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("公司id")
    private Integer companyId;

    @ApiModelProperty("考核年度")
    private Integer trainYear;

    @ApiModelProperty("注册时间")
    private String createTime;
}
