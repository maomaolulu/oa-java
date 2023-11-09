package com.ruoyi.activiti.domain.dto;

import lombok.Data;

/**
 * Author 郝佳星
 * Date 2022/6/13 10:31
 **/
@Data
public class CheckDelayCarDTO {
    //申请id
    private Long id;

    //延迟id
    private Long delayId;

    //驳回1 或者 同意 2
    private String state;

    //备注
    private String remark;
}
