package com.ruoyi.training.entity.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author yrb
 * @Date 2022/6/2 10:01
 * @Version 1.0
 * @Description 培训管理vo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingManageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("公司+部门")
    private String department;
    @ApiModelProperty("部门名称")
    @JsonIgnore
    private String deptName;
    @ApiModelProperty("用户名")
    private String username;
    @ApiModelProperty("学时要求")
    private BigDecimal limitHours;
    @ApiModelProperty("完成进度")
    private BigDecimal realHours;
    @ApiModelProperty("注册时间")
    @JsonIgnore
    private Date createTime;
}
