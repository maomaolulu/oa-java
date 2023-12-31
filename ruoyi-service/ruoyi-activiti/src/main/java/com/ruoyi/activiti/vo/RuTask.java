package com.ruoyi.activiti.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import org.activiti.engine.task.Task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuTask
{
    // 编号
    private String  id;

    // 版本
    private Integer rev;

    // 执行实例ID
    private String  executionId;

    // 流程实例编号
    private String  procInstId;

    // 流程定义编号
    private String  procDefId;

    // 节点定义名称
    private String  name;

    // 父节点实例ID
    private String  parentTaskId;

    // 节点定义描述
    private String  description;

    // 任务定义的ID
    private String  taskDefKey;

    // 委托人（一般情况下为空，只有在委托时才有值）
    private String  owner;

    // 签收人或委托人
    private String  assignee;

    // 发起人
    private String  applyer;

    // 委托类型，DelegationState分为两种：PENDING，RESOLVED。如无委托则为空
    private String  delegation;

    // 优先级别，默认为：50
    private Integer priority;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    private Date    createTime;

    // 执行时间

    private Date    dueDate;

    private String  category;

    /**
     * 是否挂起
     */
    private Boolean suspended;

    private String  tenantId;

    private String  formKey;

    /**
     * 流程名称（请假标题）
     */
    private String  processName;

    /**
     * 流程定义名称
     */
    private String  processDefName;

    /**
     * 流程定义key
     */
    @ApiModelProperty(value = "流程定义key",notes = "唯一值,区分不同业务流程")
    private String  processDefKey;
    private String processDefKeys;

    private String  businessKey;
    @Transient
    private String tableId;
    /**
     * 详情
     */
    @Transient
    private List<Map<String,Object>> detailMap;
    /**
     * 申请编号
     */
    @Transient
    private String applyCode;
    /**
     * 当前审批人
     */
    @Transient
    private String auditors;
    /**
     * 审批结果
     */
    @Transient
    private Integer result;
    /**
     * 申请时间
     */
    @Transient
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    private Date applyerTime;

    /**
     * 隶属公司id
     */
    @Transient
    private Long companyId;
    /**
     * 隶属部门id
     */
    @Transient
    private Long deptId;
/**
     * 隶属公司
     */
    @Transient
    private String companyName;
    /**
     * 隶属部门
     */
    @Transient
    private String deptName;

    /**
     * 采购物品名称
     */
    @Transient
    private String goodsName;

    /**
     * 金额
     */
    @Transient
    private String money;




    public RuTask(Task task)
    {
        setId(task.getId());
        setName(task.getName());
        setAssignee(task.getAssignee());
        setExecutionId(task.getExecutionId());
        setTenantId(task.getTenantId());
        setParentTaskId(task.getParentTaskId());
        setProcDefId(task.getProcessDefinitionId());
        setProcInstId(task.getProcessInstanceId());
        setCategory(task.getCategory());
        setFormKey(task.getFormKey());
        setDescription(task.getDescription());
        setOwner(task.getOwner());
        setTaskDefKey(task.getTaskDefinitionKey());
        setDelegation("");
        setCreateTime(task.getCreateTime());
        setDueDate(task.getDueDate());
        setPriority(task.getPriority());
        setSuspended(task.isSuspended());
    }
}