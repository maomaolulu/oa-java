package com.ruoyi.activiti.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @description:
 * @author: zx
 * @date: 2021/11/15 16:57
 */
@Data
@AllArgsConstructor
public class ProcessesAllFlowVo {
    /**
     * 线路id
     */
    private String id;
    /**
     * 节点名称
     */
    private String name;
    /**
     * 起始
     */
    private String source;
    /**
     * 目标
     */
    private String target;

    /**
     * 起点是否是网关
     */
    private boolean isSourceGateway;

}
