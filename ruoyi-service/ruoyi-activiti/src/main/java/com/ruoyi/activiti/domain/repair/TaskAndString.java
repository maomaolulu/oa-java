package com.ruoyi.activiti.domain.repair;

import lombok.Data;
import org.activiti.engine.task.Task;

/**
 * @author wuYang
 * @date 2022/10/8 18:51
 */
@Data
public class TaskAndString {

    Task task;

    String ans;

    /**
     * 下个节点的名称
     */
    String nextNodeName;
}
