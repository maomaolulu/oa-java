package com.ruoyi.activiti.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.StatefulException;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import com.ruoyi.activiti.consts.ActivitiConstant;
import com.ruoyi.activiti.domain.proc.ActRuTask;
import com.ruoyi.activiti.domain.proc.BizAudit;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.domain.repair.TaskAndString;
import com.ruoyi.activiti.mapper.ActRuTaskMapper;
import com.ruoyi.activiti.mapper.BizAssociateApplyMapper;
import com.ruoyi.activiti.mapper.BizBusinessMapper;
import com.ruoyi.activiti.service.*;
import com.ruoyi.activiti.vo.RuTask;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.javax.el.ExpressionFactory;
import org.activiti.engine.impl.javax.el.ValueExpression;
import org.activiti.engine.impl.juel.ExpressionFactoryImpl;
import org.activiti.engine.impl.juel.SimpleContext;
import org.activiti.engine.impl.persistence.entity.IdentityLinkEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.NativeTaskQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author zx
 * @date 2022/1/25 11:56
 */
@Service
@Slf4j
public class ActTaskServiceImpl implements ActTaskService {
    @Autowired
    private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private RemoteUserService remoteUserService;
    @Autowired
    private IBizBusinessService businessService;
    @Autowired
    private ManagementService managementService;
    @Resource
    ActRuTaskMapper actRuTaskMapper;
    @Autowired
    private IBizAuditService bizAuditService;
    @Autowired
    private RepositoryService repositoryService;
    private final BizBusinessMapper businessMapper;
    private final MapInfoService mapInfoService;
    private final BizAssociateApplyMapper associateApplyMapper;
    private final IBizNodeService nodeService;
    private final HistoryService historyService;
    private final IBizBusinessService bizBusinessService;

    @Autowired
    public ActTaskServiceImpl(BizBusinessMapper businessMapper,
                              MapInfoService mapInfoService,
                              BizAssociateApplyMapper associateApplyMapper,
                              IBizNodeService nodeService,
                              HistoryService historyService,
                              IBizBusinessService bizBusinessService) {
        this.businessMapper = businessMapper;
        this.mapInfoService = mapInfoService;
        this.associateApplyMapper = associateApplyMapper;
        this.nodeService = nodeService;
        this.historyService = historyService;
        this.bizBusinessService = bizBusinessService;
    }

    /**
     * 获取待办
     *
     * @param ruTask
     * @param page
     * @return
     */
    @Override
    public Map<String, Object> getIng(RuTask ruTask, PageDomain page) {
        String sortCol = "t.CREATE_TIME_";
        switch (page.getOrderByColumn()) {
            case "applyTime":
                sortCol = "b.apply_time";
                break;
            case "createTime":
                sortCol = "t.CREATE_TIME_";
                break;
        }

        List<RuTask> list = new ArrayList<>();
        Long userId = SystemUtil.getUserId();

        StringBuffer whereSql = new StringBuffer();
        if (StrUtil.isNotBlank(ruTask.getApplyCode())) {
            whereSql.append("and b.apply_code = '" + ruTask.getApplyCode() + "' ");
        }
        // 按采购物品查询
        if (StrUtil.isNotBlank(ruTask.getGoodsName())) {
            List<String> strings = businessMapper.selectInstIdByGoodName(ruTask.getGoodsName());
            if (strings.isEmpty()) {
                strings.add("0000000");
            }
            whereSql.append("and t.PROC_INST_ID_ in (" + String.join(",", strings) + ") ");

        }
        if (StrUtil.isNotBlank(ruTask.getProcessName())) {

            whereSql.append("and b.title like '%" + ruTask.getProcessName() + "%' ");
        }
        //todo
        if (StrUtil.isNotBlank(ruTask.getProcessDefKeys())) {
            whereSql.append("and b.proc_def_key like'" + "%" + ruTask.getProcessDefKeys() + "%" + "' ");
        }
        if (StrUtil.isNotBlank(ruTask.getProcessDefKey())) {
            whereSql.append("and b.proc_def_key like'" + "%" + ruTask.getProcessDefKey() + "%" + "' ");
        }
        // todo 去除了多选的
//        if (StrUtil.isNotBlank(ruTask.getProcessDefKeys())) {
//
//            String replace = ruTask.getProcessDefKeys().replace(";", "','");
//            whereSql.append("and b.proc_def_key in ('" + replace + "') ");
//        }
        if (ruTask.getDeptId() != null) {
            whereSql.append("and sd.dept_id = " + ruTask.getDeptId() + " ");
        }
        if (ruTask.getCompanyId() != null) {
            whereSql.append("and sd.ancestors like '%," + ruTask.getCompanyId() + ",%' ");
        }
        NativeTaskQuery query = taskService.createNativeTaskQuery()
                .sql("select t.*,b.apply_time as apply_time from " +
                        managementService.getTableName(Task.class) + " t " +
                        "left join " + managementService.getTableName(IdentityLinkEntity.class) + " ari on t.ID_ = ari.TASK_ID_ and t.ASSIGNEE_ is null " +
                        "left join biz_business b on b.proc_inst_id = t.PROC_INST_ID_ " +
                        "left join sys_user u on u.user_id = b.user_id " +
                        "left join sys_dept sd on u.dept_id = sd.dept_id " +
                        "where ( ari.USER_ID_ = #{userId} or t.ASSIGNEE_ = #{userId}) " +
                        whereSql +
                        "order by " + sortCol + " " + page.getIsAsc() + " ")
                .parameter("userId", userId);

        long count = query.list().size();
        int first = (page.getPageNum() - 1) * page.getPageSize();
        List<Task> taskList = query.listPage(first, page.getPageSize());
        if (taskList.size() > 0) {
            // 转换vo
            taskList.forEach(e -> {
                RuTask rt = new RuTask(e);
                List<IdentityLink> identityLinks = runtimeService
                        .getIdentityLinksForProcessInstance(rt.getProcInstId());
                for (IdentityLink ik : identityLinks) {
                    // 关联发起人
                    if ("starter".equals(ik.getType()) && StrUtil.isNotBlank(ik.getUserId())) {
                        SysUser sysUser = remoteUserService.selectSysUserByUserId(Long.parseLong(ik.getUserId()));
                        rt.setApplyer(sysUser.getUserName());
                        rt.setDeptName(sysUser.getDept().getDeptName());
                        rt.setCompanyName(sysUser.getCompany());
                    }
                }
                // 关联业务key
                ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(rt.getProcInstId())
                        .singleResult();
                rt.setBusinessKey(pi.getBusinessKey());
                BizBusiness bizBusiness = businessService.selectBizBusinessById(pi.getBusinessKey());
                if (bizBusiness != null) {
                    rt.setTableId(bizBusiness.getTableId());
                }
                rt.setProcessName(pi.getName());
                rt.setProcessDefKey(pi.getProcessDefinitionKey());
                rt.setProcessDefName(pi.getProcessDefinitionName());
                list.add(rt);
            });
        }

        for (RuTask task : list) {

            BizBusiness bizBusiness = businessService.selectBizBusinessById(task.getBusinessKey());
            if (bizBusiness == null) continue;
            task.setResult(bizBusiness.getResult());
            task.setApplyerTime(bizBusiness.getApplyTime());
            task.setDetailMap((List<Map<String, Object>>) mapInfoService.getMapInfo(bizBusiness).get("list"));
            task.setApplyCode(mapInfoService.getMapInfo(bizBusiness).get("applyCode").toString());
            task.setAuditors(bizBusiness.getAuditors());
            if (task.getProcessDefKey().startsWith("payment-")) {
                String tableId = bizBusiness.getTableId();
                Query query1 = new Query();
                query1.addCriteria(Criteria.where("_id").is(tableId));
                HashMap payment = mongoTemplate.findOne(query1, HashMap.class, "payment");
                if (payment == null) continue;
                Object projectMoney = payment.get("projectMoney");
                task.setMoney(projectMoney.toString());
            } else {
                task.setMoney(null);
            }
        }
        Map<String, Object> map = new HashMap<>(3);
        map.put("rows", list);
        map.put("pageNum", page.getPageNum());
        map.put("total", count);
        return map;
    }

    @Resource
    MongoTemplate mongoTemplate;

    @Override
    public Map<String, Object> getIngForRelation(RuTask ruTask, PageDomain page, Boolean hasPurchase) {
        String sortCol = "t.CREATE_TIME_";
        switch (page.getOrderByColumn()) {
            case "applyTime":
                sortCol = "b.apply_time";
                break;
            case "createTime":
                sortCol = "t.CREATE_TIME_";
                break;
        }

        List<RuTask> list = new ArrayList<>();
        Long userId = SystemUtil.getUserId();

        StringBuffer whereSql = new StringBuffer();
        // 按采购物品查询
        if (StrUtil.isNotBlank(ruTask.getGoodsName())) {
            List<String> strings = businessMapper.selectInstIdByGoodName(ruTask.getGoodsName());
            if (strings.isEmpty()) {
                strings.add("0000000");
            }
            whereSql.append("and t.PROC_INST_ID_ in (" + String.join(",", strings) + ") ");

        }
        if (StrUtil.isNotBlank(ruTask.getProcessName())) {

            whereSql.append("and b.title like '%" + ruTask.getProcessName() + "%' ");
        }
        //todo
        if (StrUtil.isNotBlank(ruTask.getProcessDefKey())) {
            whereSql.append("and b.proc_def_key like'" + "%" + ruTask.getProcessDefKey() + "%" + "' ");
        }
        if (hasPurchase != null) {
            if (hasPurchase) {
                whereSql.append("and b.proc_def_key = 'purchase' and b.result NOT IN(3,4,6) ");
            } else {
                whereSql.append("and b.proc_def_key != 'purchase' and b.result NOT IN(3,4,6) ");
            }
        }
        if (StrUtil.isNotBlank(ruTask.getProcessDefKeys())) {

            String replace = ruTask.getProcessDefKeys().replace(";", "','");
            whereSql.append("and b.proc_def_key in ('" + replace + "') ");
        }
        if (ruTask.getDeptId() != null) {
            whereSql.append("and sd.dept_id = " + ruTask.getDeptId() + " ");
        }
        if (ruTask.getCompanyId() != null) {
            whereSql.append("and sd.ancestors like '%," + ruTask.getCompanyId() + ",%' ");
        }
        NativeTaskQuery query = taskService.createNativeTaskQuery()
                .sql("select t.*,b.apply_time as apply_time from " +
                        managementService.getTableName(Task.class) + " t " +
                        "left join " + managementService.getTableName(IdentityLinkEntity.class) + " ari on t.ID_ = ari.TASK_ID_ and t.ASSIGNEE_ is null " +
                        "left join biz_business b on b.proc_inst_id = t.PROC_INST_ID_ " +
                        "left join sys_user u on u.user_id = b.user_id " +
                        "left join sys_dept sd on u.dept_id = sd.dept_id " +
                        "where ( ari.USER_ID_ = #{userId} or t.ASSIGNEE_ = #{userId}) " +
                        whereSql +
                        "order by " + sortCol + " " + page.getIsAsc() + " ")
                .parameter("userId", userId);

        long count = query.list().size();
        int first = (page.getPageNum() - 1) * page.getPageSize();
        List<Task> taskList = query.listPage(first, page.getPageSize());
        if (taskList.size() > 0) {
            // 转换vo
            taskList.forEach(e -> {
                RuTask rt = new RuTask(e);
                List<IdentityLink> identityLinks = runtimeService
                        .getIdentityLinksForProcessInstance(rt.getProcInstId());
                for (IdentityLink ik : identityLinks) {
                    // 关联发起人
                    if ("starter".equals(ik.getType()) && StrUtil.isNotBlank(ik.getUserId())) {
                        SysUser sysUser = remoteUserService.selectSysUserByUserId(Long.parseLong(ik.getUserId()));
                        rt.setApplyer(sysUser.getUserName());
                        rt.setDeptName(sysUser.getDept().getDeptName());
                        rt.setCompanyName(sysUser.getCompany());

                    }
                }
                // 关联业务key
                ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(rt.getProcInstId())
                        .singleResult();
                rt.setBusinessKey(pi.getBusinessKey());
                BizBusiness bizBusiness = businessService.selectBizBusinessById(pi.getBusinessKey());
                rt.setTableId(bizBusiness.getTableId());
                rt.setProcessName(pi.getName());
                rt.setProcessDefKey(pi.getProcessDefinitionKey());
                rt.setProcessDefName(pi.getProcessDefinitionName());
                list.add(rt);
            });
        }

        for (RuTask task : list) {
            BizBusiness bizBusiness = businessService.selectBizBusinessById(task.getBusinessKey());
            task.setResult(bizBusiness.getResult());
            task.setApplyerTime(bizBusiness.getApplyTime());
            task.setDetailMap((List<Map<String, Object>>) mapInfoService.getMapInfo(bizBusiness).get("list"));
            task.setApplyCode(mapInfoService.getMapInfo(bizBusiness).get("applyCode").toString());
            task.setAuditors(bizBusiness.getAuditors());
        }
        Map<String, Object> map = new HashMap<>(3);
        map.put("rows", list);
        map.put("pageNum", page.getPageNum());
        map.put("total", count);
        return map;
    }

    /**
     * 获取抄送未读信息数量
     *
     * @param currentUserId
     * @return
     */
    @Override
    public int getUnReadCCCount(Long currentUserId) {
        int unReadCount = businessMapper.getUnReadCount(currentUserId.toString());
        return unReadCount;
    }

    /**
     * 抄送已读
     *
     * @param processInstanceId 流程实例id
     * @param userId            用户id
     */
    @Override
    public void readCc(String processInstanceId, Long userId) {
        businessMapper.readCc(processInstanceId, userId.toString());
    }

    /**
     * 自动审批
     *
     * @param bizAudit 审批参数
     */
    @Override
    public void autoAudit(BizAudit bizAudit) {
        String procInstId = bizAudit.getProcInstId();
        TaskQuery query = taskService.createTaskQuery().orderByTaskCreateTime()
                .desc();
        if (StrUtil.isNotBlank(procInstId)) {
            query.processInstanceId(procInstId);
        }
        List<Task> taskList = query.list();
        if (taskList.size() == 1) {
            bizAudit.setId(null);
            bizAudit.setTaskId(taskList.get(0).getId());
        }
        BizBusiness bizBusiness = businessService.selectBizBusinessById2(String.valueOf(bizAudit.getBusinessKey()));
        // 采购关联付款报销
        String procDefKey = bizAudit.getProcDefKey();
        String taskId = bizAudit.getTaskId();
        boolean flag_payment = false;
        if (taskId != null) {
            ActRuTask actRuTask = actRuTaskMapper.selectByPrimaryKey1(taskId);
            if (actRuTask.getName().equals("付款确认人2")) {
                flag_payment = true;
            }
        }
        if (procDefKey.contains("payment-") && flag_payment) {
            String tableId = bizBusiness.getTableId();
            Query query1 = new Query();
            query1.addCriteria(Criteria.where("_id").is(tableId));
            HashMap payment = mongoTemplate.findOne(query1, HashMap.class, "payment");
            HashMap baseInformation = (HashMap) payment.get("BaseInformation");
            List<HashMap> purchaseData = (List<HashMap>) baseInformation.get("purchaseData");
            for (HashMap purchaseDatum : purchaseData) {
                Long id = Long.parseLong(purchaseDatum.get("id").toString());
                associateApplyMapper.updateFinanceStatusPaymentMongo(id, 3, "自动审批", new Date());
            }
        }
        System.out.println(procDefKey + "****" + bizAudit.getTaskDefKey());
        if ("payment".equals(procDefKey) && "cashier".equals(bizAudit.getTaskDefKey())) {
            associateApplyMapper.updateFinanceStatusPayment(1, Long.valueOf(bizBusiness.getTableId()), 3, "自动审批", new Date());
        }
        if ("reimburse".equals(procDefKey) && "cashier_bx".equals(bizAudit.getTaskDefKey())) {
            associateApplyMapper.updateFinanceStatusReimburse(2, Long.valueOf(bizBusiness.getTableId()), 4);
        }

        // 将自己从抄送人中去除
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("result", bizAudit.getResult());
        // 审批
        variables.put("operator", "自动审批");
        variables.put("currentUserId", 1L);
        if (bizAudit.getProcDefKey() != null && bizAudit.getTaskDefKey() != null) {
            if ("review".equals(bizAudit.getProcDefKey()) && "leader2".equals(bizAudit.getTaskDefKey())) {
                // 设置流程变量（评审费）
                int isCancel = bizAudit.getIsCancel();
                variables.put("isCancel", isCancel);
            }
        }
        bizBusiness.setProcInstId(bizAudit.getProcInstId());
        // 获取候选人，给会签节点预备实例列表
        Set<String> instanceUsers;
        TaskAndString nextTask = null;
        try {
            // 插入会签人员
            nextTask = findNextTask(bizBusiness.getProcDefId(), bizAudit.getTaskId(), procInstId, bizAudit.getProcDefKey());
            log.error("task----{}user---{}key---{}", nextTask, bizBusiness.getUserId(), bizBusiness.getProcDefKey());
            instanceUsers = nodeService.getAuditors(nextTask.getAns(), bizBusiness.getUserId(), bizBusiness.getProcDefKey());
            System.out.println(nextTask + "---" + bizBusiness.getUserId() + "候选size:{}" + instanceUsers.size());
            if (instanceUsers != null && instanceUsers.size() > 0 && bizAudit.getResult() == 2) {
                for (String instanceUser : instanceUsers) {
                    System.out.println("幸运观众2：" + instanceUser);
                }
                variables.put("assigneeList", new ArrayList<>(instanceUsers));
            }
        } catch (StatefulException e) {
            if (e.getStatus() == 292) {
                throw new StatefulException(292, "无审批人");
            }
        } catch (Exception e) {
            log.error("获取会签人员失败", e);
        }
        taskService.complete(bizAudit.getTaskId(), variables);

        bizAudit.setAuditor("自动审批");
        bizAudit.setAuditorId(1L);
        bizAuditService.insertBizAudit(bizAudit);
        bizBusiness.setLastAuditorTime(new Date());
        bizBusiness.setLastAuditor("自动审批");
        bizBusiness.setCustomizedUserId(bizAudit.getCustomizedUserId());
        bizBusiness.setTaskDefKey(bizAudit.getTaskDefKey());
        bizBusiness.setAutoAdit("自动审批");
        Integer result = bizAudit.getResult();
        if (result == 3) {
            bizBusiness.setCurrentTask(ActivitiConstant.END_TASK_NAME)
                    .setStatus(ActivitiConstant.STATUS_FINISH)
                    .setResult(result)
                    .setAuditors("-");
            bizBusinessService.updateBizBusiness(bizBusiness);
            return;
        }
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(bizBusiness.getProcInstId()).list();
        if (CollUtil.isEmpty(tasks)) {
            // 任务结束
            bizBusiness.setCurrentTask(ActivitiConstant.END_TASK_NAME)
                    .setStatus(ActivitiConstant.STATUS_FINISH)
                    .setResult(result)
                    .setAuditors("-");
            businessMapper.updateByPrimaryKey(bizBusiness);
            // 流程通过
            bizBusinessService.getBusiness2(bizBusiness);
            // 临时抄送转正式
            bizBusinessService.changeCC(bizBusiness);
        }
        if (nextTask != null && "付款确认人2".equals(nextTask.getNextNodeName())) {
            autoAudit(bizAudit);
        }
    }

    /**
     * 查询流程当前节点的下一步节点。用于流程提示时的提示。
     *
     * @param taskId
     * @param procInstId
     * @param procDefKey
     * @return
     * @throws Exception
     */
    public TaskAndString findNextTask(String proc_def_id, String taskId, String procInstId, String procDefKey) {
        try {
            // 查询流程定义
            BpmnModel bpmnModel = repositoryService.getBpmnModel(proc_def_id);

            List<org.activiti.bpmn.model.Process> listp = bpmnModel.getProcesses();
            org.activiti.bpmn.model.Process process = listp.get(0);
            // 根据taskId获取taskDefKey
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            FlowNode sourceFlowElement = (FlowNode) process.getFlowElement(task.getTaskDefinitionKey());
            // 找到当前任务的流程变量
            List<HistoricVariableInstance> listVar = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(procInstId).list();
//            String nextTaskDefKey = "";
            FlowNode node = iteratorNextNodes(process, sourceFlowElement, listVar);
            TaskAndString taskAndString = new TaskAndString();
            taskAndString.setTask(task);
            if (node == null) {
                taskAndString.setAns("");
                taskAndString.setNextNodeName("");
            } else {
                taskAndString.setAns(node.getId());
                taskAndString.setNextNodeName(node.getName().trim());
            }
//            boolean isMulti = true;
//            // 流程是否包含会签
//            Collection<FlowElement> flowElements = bpmnModel.getMainProcess().getFlowElements();
//            for (FlowElement flowElement : flowElements) {
//                if (flowElement instanceof UserTask) {
//                    UserTask userTask = (UserTask) flowElement;
//                    logger.error("审批节点id{}",userTask.getId());
//                    if (userTask.getLoopCharacteristics() != null&&userTask.getId().equals(taskAndString)) {
//                        System.out.println("下节点是会签" + userTask.getId());
//                        isMulti = false;
//                    }
//                }
//            }
//            // 不是会签返回空
//            if(isMulti){
//                System.out.println("下节点不是会签" + nextTaskDefKey);
//                taskAndString.setAns("");
//            }
            return taskAndString;

        } catch (Exception e) {
            log.error("查询流程当前节点的下一步节点失败", e);
            throw new StatefulException("Cannot find task with id " + taskId);
        }
    }

    /**
     * 查询流程当前节点的下一步节点。用于流程提示时的提示。
     *
     * @param process           流程信息
     * @param sourceFlowElement
     * @param listVar
     * @throws Exception
     */
    private FlowNode iteratorNextNodes(org.activiti.bpmn.model.Process process, FlowNode sourceFlowElement,
                                       List<HistoricVariableInstance> listVar) throws Exception {
        // 拿到所有输出节点
        List<SequenceFlow> list = sourceFlowElement.getOutgoingFlows();
        FlowNode node = null;
        // 遍历
        for (SequenceFlow sf : list) {
            sourceFlowElement = (FlowNode) process.getFlowElement(sf.getTargetRef());
            if (StringUtils.isNotEmpty(sf.getConditionExpression())) {
                ExpressionFactory factory = new ExpressionFactoryImpl();
                SimpleContext context = new SimpleContext();
                for (HistoricVariableInstance var : listVar) {
                    context.setVariable(var.getVariableName(),
                            factory.createValueExpression(var.getValue(), var.getValue().getClass()));
                }
                ValueExpression e = factory.createValueExpression(context, sf.getConditionExpression(), boolean.class);
                if ((Boolean) e.getValue(context)) {
                    node = sourceFlowElement;
//                    nodeMap = sourceFlowElement.getId();
                    break;
                }
            }
            if (sourceFlowElement instanceof org.activiti.bpmn.model.UserTask) {
                node = sourceFlowElement;
//                nodeMap = sourceFlowElement.getId();
            } else if (sourceFlowElement instanceof org.activiti.bpmn.model.ExclusiveGateway) {
                node = iteratorNextNodes(process, sourceFlowElement, listVar);
            }
        }
        return node;
    }

    private String getType(Integer types) {
        String type = "";
        switch (types) {
            case 1:
                type = "差旅费";
                break;
            case 2:
                type = "招待费";
                break;
            case 3:
                type = "设备采购";
                break;

            case 4:
                type = "耗材采购";
                break;
            case 5:
                type = "办公用品";
                break;
            case 6:
                type = "活动经费";
                break;
            case 7:
                type = "其他费用";
                break;
            case 8:
                type = "房租/水电费";
                break;
            default:
                type = "无";
                break;
        }
        return type;
    }

    private String getTypePayment(Integer types) {
        String type = "";
        switch (types) {
            case 1:
                type = "试剂或耗材";
                break;
            case 2:
                type = "办公用品及办公耗材";
                break;
            case 3:
                type = "警示标牌或广告宣传材料";
                break;

            case 4:
                type = "仪器检定或仪器维修";
                break;
            case 5:
                type = "仪器及办公设备";
                break;
            case 6:
                type = "培训费";
                break;
            case 7:
                type = "标书费或招标代理费";
                break;
            case 8:
                type = "房租或水电费";
                break;
            case 9:
                type = "业务退款";
                break;
            case 10:
                type = "业务分包费";
                break;
            case 11:
                type = "备用金";
                break;
            case 12:
                type = "投资款";
                break;
            case 13:
                type = "其他";
                break;
            default:
                type = "无";
                break;
        }
        return type;
    }
}
