package com.ruoyi.activiti.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: zx
 * @date: 2021/11/15 16:57
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessesAllTaskVo {
    /**
     * 节点id
     */
    private String id;
    /**
     * 节点名称
     */
    private String name;
    /**
     * 类型
     */
    private String type;
    /**
     * 是否高亮
     */
    private boolean flag;
    /**
     * 审批人
     */
    private String auditor;

    private Long auditorId;
    /**
     * 审批时间
     */
    private String endTime;
    /**
     * 审批结果
     */
    private Integer result;
    /**
     * 转交记录
     */
    private List<Map<String,Object>> records;
    /**
     * 审批意见
     */
    private String comment;
    /**
     * 历史记录
     */
    private List<HiTaskVo> history;

    public ProcessesAllTaskVo(String id, String name, String type, boolean flag, String auditor) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.flag = flag;
        this.auditor = auditor;
        this.result = 0;
        this.records = new ArrayList<>();
        this.comment = "";
    }
}
