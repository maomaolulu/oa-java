package com.ruoyi.activiti.utils;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;
import org.activiti.engine.impl.persistence.entity.TaskEntity;

import java.io.Serializable;

/**
 * 减签业务实现类
 * @author zx
 * @date 2022-4-18 16:13:27
 */
public class MultiDeleteCmd implements Command<Void>, Serializable {

    /**
     * 变量表中和实例数量相关的参数
     */
    protected final String NUMBER_OF_INSTANCES = "nrOfInstances";
    protected final String NUMBER_OF_ACTIVE_INSTANCES = "nrOfActiveInstances";

    private String taskId;

    public MultiDeleteCmd( String taskId) {
        this.taskId = taskId;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        ProcessEngineConfigurationImpl pec = commandContext.getProcessEngineConfiguration();
        TaskService taskService = pec.getTaskService();
        RuntimeService runtimeService = pec.getRuntimeService();
        TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(taskId).singleResult();
        // 获取executionId
        String executionId = task.getExecutionId();
        ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery().executionId(executionId).singleResult();
        Integer loopCounter = LoopVariableUtils.getLoopVariable(execution, NUMBER_OF_INSTANCES);
        Integer nrOfActiveInstances = LoopVariableUtils.getLoopVariable(execution, NUMBER_OF_ACTIVE_INSTANCES);
        System.out.println("loopCounter:" + loopCounter);
        System.out.println("nrOfActiveInstances:" + nrOfActiveInstances);
        LoopVariableUtils.setLoopVariable(execution, NUMBER_OF_INSTANCES, loopCounter - 1);
        LoopVariableUtils.setLoopVariable(execution, NUMBER_OF_ACTIVE_INSTANCES, nrOfActiveInstances - 1);
        task.setProcessInstanceId(null);
        task.setExecutionId(null);
        taskService.saveTask(task);
        ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();
        executionEntityManager.deleteProcessInstance(executionId, "会签减签", false);
        taskService.deleteTask(taskId, false);
        return null;
    }
    
}
