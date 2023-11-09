package com.ruoyi.activiti.domain.dto;

import lombok.Data;

/**
 * 转交参数
 * @author zx
 * @date 2022/1/15 10:54
 */
@Data
public class ReassignmentDto {
    /**
     * 任务id
     */
    private String taskId;
    /**
     * 转交发起人
     */
    private Long userId;
    /**
     * 转交目标
     */
    private Long newUserId;
    /**
     * 流程业务id
     */
    private String businessKey;
    /**
     * 任务节点定义
     */
    private String taskDefKey;
    /**
     * 转交理由
     */
    private String reason;
}
