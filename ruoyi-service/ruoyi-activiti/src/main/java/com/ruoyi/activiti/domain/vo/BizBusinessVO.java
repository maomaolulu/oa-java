package com.ruoyi.activiti.domain.vo;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author wuYang
 * @date 2022/10/21 11:39
 */
@Data
public class BizBusinessVO {
    // 1. 修复重新提交id数组变文字
    private Integer errorType;

    private String applyCode;

    private String applyer;

    private Integer result;

    private String title;

    private Integer state;

    private LocalDate startTime;
    private LocalDate endTime;
    private String start;
    private String end;

    private String procDefKey;

    private String deptId;

    private String companyId;
}
