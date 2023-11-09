package com.ruoyi.activiti.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wuYang
 * @date 2022/9/6 19:07
 */
@Data
public class BizOrSignDTO {

    @ApiModelProperty("流程类型")
    private String procDefKey;

    @ApiModelProperty("公司id")
    private Long companyId;

    @ApiModelProperty("部门id")
    private Long deptId;

    @ApiModelProperty("申请编号")
    private String applyCode;

    @ApiModelProperty("流程名称")
    private String procName;

    @ApiModelProperty("审批状态")
    private String result;

    private String sortField ="";

    private String sortOrder="";


}
