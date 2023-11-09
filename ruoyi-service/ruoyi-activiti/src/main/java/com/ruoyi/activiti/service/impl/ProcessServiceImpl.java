package com.ruoyi.activiti.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.ruoyi.activiti.consts.ActivitiConstant;
import com.ruoyi.activiti.consts.UrlConstants;
import com.ruoyi.activiti.cover.ICustomProcessDiagramGenerator;
import com.ruoyi.activiti.domain.ActComment;
import com.ruoyi.activiti.domain.BizReassignmentRecord;
import com.ruoyi.activiti.domain.car.BizReserveDetail;
import com.ruoyi.activiti.domain.my_apply.BizContractInfo;
import com.ruoyi.activiti.domain.my_apply.BizContractProject;
import com.ruoyi.activiti.domain.my_apply.BizQuotationApply;
import com.ruoyi.activiti.domain.proc.ActReProcdef;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.domain.purchase.BizGoodsInfo;
import com.ruoyi.activiti.feign.MessageEntity;
import com.ruoyi.activiti.feign.RemoteSocketService;
import com.ruoyi.activiti.mapper.*;
import com.ruoyi.activiti.service.*;
import com.ruoyi.activiti.vo.HiTaskVo;
import com.ruoyi.activiti.vo.ProcessesAllFlowVo;
import com.ruoyi.activiti.vo.ProcessesAllTaskVo;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.exception.RuoyiException;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteConfigService;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.*;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.javax.el.ExpressionFactory;
import org.activiti.engine.impl.javax.el.PropertyNotFoundException;
import org.activiti.engine.impl.javax.el.ValueExpression;
import org.activiti.engine.impl.juel.ExpressionFactoryImpl;
import org.activiti.engine.impl.juel.SimpleContext;
import org.activiti.engine.impl.persistence.entity.ByteArrayEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.VariableInstance;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

/**
 * @author zx
 * @date 2022/1/11 16:39
 */
@Service
@Slf4j
public class ProcessServiceImpl implements ProcessService {
    @Resource
    BizBusinessTestMapper bizBusinessTestMapper;
    @Resource
    MongoTemplate mongoTemplate;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private ProcessEngineConfiguration processEngineConfiguration;
    @Autowired
    private IActReProcdefService procdefService;
    @Autowired
    private IBizBusinessService bizBusinessService;
    @Autowired
    private RemoteSocketService remoteSocketService;
    @Resource
    BizGoodsInfoMapper bizGoodsInfoMapper;

    private final BizAssociateGoodMapper associateGoodMapper;
    private final BizReserveDetailMapper reserveDetailMapper;
    private final IBizAuditService auditService;
    private final IBizNodeService nodeService;
    private final RemoteUserService remoteUserService;
    private final RemoteDeptService remoteDeptService;
    private final RemoteConfigService remoteConfigService;
    private final BizReassignmentRecordMapper reassignmentRecordMapper;
    private final ActCommentService actCommentService;
    private final BizQuotationApplyMapper bizQuotationApplyMapper;
    private final BizContractInfoMapper bizContractInfoMapper;
    private final BizContractProjectMapper bizContractProjectMapper;
    private final YunYingCommonService yunYingCommonService;

    @Autowired
    public ProcessServiceImpl(IBizAuditService auditService,
                              IBizNodeService nodeService,
                              RemoteUserService remoteUserService,
                              RemoteDeptService remoteDeptService,
                              RemoteConfigService remoteConfigService,
                              BizReassignmentRecordMapper reassignmentRecordMapper,
                              ActCommentService actCommentService,
                              BizReserveDetailMapper reserveDetailMapper,
                              BizAssociateGoodMapper associateGoodMapper,
                              BizQuotationApplyMapper bizQuotationApplyMapper,
                              BizContractInfoMapper bizContractInfoMapper,
                              BizContractProjectMapper bizContractProjectMapper,
                              YunYingCommonService yunYingCommonService) {
        this.auditService = auditService;
        this.nodeService = nodeService;
        this.remoteUserService = remoteUserService;
        this.remoteDeptService = remoteDeptService;
        this.remoteConfigService = remoteConfigService;
        this.reassignmentRecordMapper = reassignmentRecordMapper;
        this.actCommentService = actCommentService;
        this.reserveDetailMapper = reserveDetailMapper;
        this.associateGoodMapper = associateGoodMapper;
        this.bizQuotationApplyMapper = bizQuotationApplyMapper;
        this.bizContractInfoMapper = bizContractInfoMapper;
        this.bizContractProjectMapper = bizContractProjectMapper;
        this.yunYingCommonService = yunYingCommonService;
    }

    /**
     * 获取完整流程列表
     *
     * @param name
     * @param tableId
     * @param userId
     * @param money
     * @return
     */
    @Override
    public Map<String, Object> getProcessAll(String name, String tableId, Long userId, BigDecimal money, String variable, String procInstIdNew) {
        Map<String, Object> map = new HashMap<>();
        String applyer2 = "";
        try {
            String procDefId = "";
            String procInstId = "";
            String applyer = "";
            Date applyTime = null;
            Integer procResult = 0;
            // 流程发起后要根据原有流程走
            BizBusiness bizBusiness = new BizBusiness();


            if (StrUtil.isNotEmpty(tableId)) {
                bizBusiness.setTableId(tableId);
                bizBusiness.setProcDefKey(name);
                List<BizBusiness> bizBusinesses = bizBusinessService.selectBizBusinessListAll(bizBusiness);
                if (!bizBusinesses.isEmpty()) {
                    bizBusiness = bizBusinesses.get(0);
                    procDefId = bizBusinesses.get(0).getProcDefId();
                    procInstId = bizBusinesses.get(0).getProcInstId();
                    applyer = bizBusinesses.get(0).getApplyer();
                    applyTime = bizBusinesses.get(0).getApplyTime();
                    procResult = bizBusinesses.get(0).getResult();
                    procInstIdNew = bizBusiness.getProcInstId();
                } else {
                    log.error("流程不存在1");
                    map.put("tasks", new ArrayList<>());
                    return map;
                }
            } else if (StrUtil.isNotBlank(procInstIdNew) && StrUtil.isBlank(tableId)) {
                bizBusiness.setProcInstId(procInstIdNew);
                bizBusiness.setProcDefKey(name);
                List<BizBusiness> bizBusinesses = bizBusinessService.selectBizBusinessListAll(bizBusiness);
                if (!bizBusinesses.isEmpty()) {
                    bizBusiness = bizBusinesses.get(0);
                    procDefId = bizBusinesses.get(0).getProcDefId();
                    procInstId = bizBusinesses.get(0).getProcInstId();
                    applyer = bizBusinesses.get(0).getApplyer();
                    applyTime = bizBusinesses.get(0).getApplyTime();
                    procResult = bizBusinesses.get(0).getResult();
                } else {
                    log.error("流程不存在2");
                    map.put("tasks", new ArrayList<>());
                    return map;
                }
            } else {
                bizBusiness.setUserId(userId);
                bizBusiness.setResult(1);
                bizBusiness.setStatus(1);
                ActReProcdef process = procdefService.getActReProcdefs(name);
                procDefId = process.getId();
            }
            BpmnModel bpmnModel = new BpmnModel();
            try {
                bpmnModel = repositoryService.getBpmnModel(procDefId);
            } catch (Exception e) {
                log.error("流程定义不存在");
                map.put("tasks", new ArrayList<>());
                return map;
            }
            // 流程是否包含会签
            boolean isMultiInstance = false;
            List<String> multiInstanceList = new ArrayList<>();
            Collection<FlowElement> flowElements = bpmnModel.getMainProcess().getFlowElements();
            for (FlowElement flowElement : flowElements) {
                if (flowElement instanceof UserTask) {
                    UserTask userTask = (UserTask) flowElement;
                    if (userTask.getLoopCharacteristics() != null) {
                        isMultiInstance = true;
                        System.out.println("流程包含会签" + userTask.getId());
                        multiInstanceList.add(userTask.getId());
                    }
                }
            }
            //获得流程模型的所有节点
            List<Process> processes = bpmnModel.getProcesses();
            if (processes.isEmpty()) {
                log.error("获得流程模型的所有节点失败");
                map.put("tasks", new ArrayList<>());
                return map;
            }
            processes.get(0).getFlowElements();
            List<StartEvent> startEventList = processes.get(0).findFlowElementsOfType(StartEvent.class);
            List<EndEvent> endEventList = processes.get(0).findFlowElementsOfType(EndEvent.class);
            List<UserTask> userTaskList = processes.get(0).findFlowElementsOfType(UserTask.class);
            List<SequenceFlow> sequenceFlowList = processes.get(0).findFlowElementsOfType(SequenceFlow.class);
            List<ExclusiveGateway> exclusiveGatewayList = processes.get(0).findFlowElementsOfType(ExclusiveGateway.class);

            List<ProcessesAllTaskVo> result = new ArrayList<>();
            List<ProcessesAllFlowVo> resultFlow = new ArrayList<>();
            List<String> target = new ArrayList<>();
            // 高亮
            List<String> highLightedFlows = new ArrayList<>();
            List<String> highLightedActivitis = new ArrayList<>();

            // 开始节点
            if (!startEventList.isEmpty()) {
                result.add(new ProcessesAllTaskVo(startEventList.get(0).getId(), startEventList.get(0).getName(), "start", false, applyer));
//            highLightedActivitis.add(startEventList.get(0).getId());
            }
            // 任务节点
            // 网关
            exclusiveGatewayList.stream().forEach(exclusiveGateway -> {
                result.add(new ProcessesAllTaskVo(exclusiveGateway.getId(), exclusiveGateway.getName(), "gateway", false, null));
            });

            Long userIdNow = SystemUtil.getUserId();
            if (StrUtil.isNotBlank(tableId) || StrUtil.isNotBlank(procInstIdNew)) {
                // 如果流程已发起，按发起人查，否则按当前登录人查
                userIdNow = bizBusiness.getUserId();

                HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(procInstId).singleResult();
                // 已结束的流程被删除
                if (processInstance == null) {
                    log.error("已结束的流程被删除");
                    map.put("tasks", new ArrayList<>());
                    return map;
                }
                ProcessDefinitionEntity definitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processInstance.getProcessDefinitionId());
                List<HistoricActivityInstance> highLightedActivitList = historyService.createHistoricActivityInstanceQuery().processInstanceId(procInstId).orderByHistoricActivityInstanceId().asc().list();
                // 高亮节点id集合
                for (HistoricActivityInstance tempActivity : highLightedActivitList) {
                    String activityId = tempActivity.getActivityId();
                    highLightedActivitis.add(activityId);
                }
                // 获取高亮线路id集合
                highLightedFlows = getHighLightedFlows(definitionEntity, highLightedActivitList);

            }
            // 找到当前任务的流程变量

            SysUser sysUserNow = remoteUserService.selectSysUserByUserId(userIdNow);
            log.error(sysUserNow.getUserName() + "---isGroup:{}", sysUserNow.getIsGroup());
            VariableInstance historicVariableInstance = getHistoricVariableInstance("isGroup", sysUserNow.getIsGroup());
            VariableInstance isLeader = getHistoricVariableInstance("isLeader", sysUserNow.getIsLeader());

            Set<String> set = new HashSet<>();
            set.add(procInstId);
            List<VariableInstance> listVar = runtimeService.getVariableInstancesByExecutionIds(set);
            //companyId
            //deptId
            //g1
            //g2
            //g3
            //isCancel
            //isGroup
            //money
            //params200
            //params2000
            //purchase200
            //purchase2000
            // 实时变量为空时取历史变量
            if (procInstIdNew != null && listVar.isEmpty()) {
                List<HistoricVariableInstance> moneyVar = historyService.createHistoricVariableInstanceQuery().processInstanceId(procInstId).variableName("money").list();
                for (HistoricVariableInstance variableInstance : moneyVar) {
                    listVar.add(getHistoricVariableInstance(variableInstance.getVariableName(), variableInstance.getValue()));
                }
                List<HistoricVariableInstance> g1Var = historyService.createHistoricVariableInstanceQuery().processInstanceId(procInstId).variableName("g1").list();
                for (HistoricVariableInstance variableInstance : g1Var) {
                    listVar.add(getHistoricVariableInstance(variableInstance.getVariableName(), variableInstance.getValue()));
                }
                List<HistoricVariableInstance> g2Var = historyService.createHistoricVariableInstanceQuery().processInstanceId(procInstId).variableName("g2").list();
                for (HistoricVariableInstance variableInstance : g2Var) {
                    listVar.add(getHistoricVariableInstance(variableInstance.getVariableName(), variableInstance.getValue()));
                }
                List<HistoricVariableInstance> g3Var = historyService.createHistoricVariableInstanceQuery().processInstanceId(procInstId).variableName("g3").list();
                for (HistoricVariableInstance variableInstance : g3Var) {
                    listVar.add(getHistoricVariableInstance(variableInstance.getVariableName(), variableInstance.getValue()));
                }
                List<HistoricVariableInstance> companyIdVar = historyService.createHistoricVariableInstanceQuery().processInstanceId(procInstId).variableName("companyId").list();
                for (HistoricVariableInstance variableInstance : companyIdVar) {
                    listVar.add(getHistoricVariableInstance(variableInstance.getVariableName(), variableInstance.getValue()));
                }
                List<HistoricVariableInstance> deptIdVar = historyService.createHistoricVariableInstanceQuery().processInstanceId(procInstId).variableName("deptId").list();
                for (HistoricVariableInstance variableInstance : deptIdVar) {
                    listVar.add(getHistoricVariableInstance(variableInstance.getVariableName(), variableInstance.getValue()));
                }
                List<HistoricVariableInstance> isCancelVar = historyService.createHistoricVariableInstanceQuery().processInstanceId(procInstId).variableName("isCancel").list();
                for (HistoricVariableInstance variableInstance : isCancelVar) {
                    listVar.add(getHistoricVariableInstance(variableInstance.getVariableName(), variableInstance.getValue()));
                }
                List<HistoricVariableInstance> isGroupVar = historyService.createHistoricVariableInstanceQuery().processInstanceId(procInstId).variableName("isGroup").list();
                for (HistoricVariableInstance variableInstance : isGroupVar) {
                    listVar.add(getHistoricVariableInstance(variableInstance.getVariableName(), variableInstance.getValue()));
                }
                List<HistoricVariableInstance> isLeaderVar = historyService.createHistoricVariableInstanceQuery().processInstanceId(procInstId).variableName("isLeader").list();
                for (HistoricVariableInstance variableInstance : isLeaderVar) {
                    listVar.add(getHistoricVariableInstance(variableInstance.getVariableName(), variableInstance.getValue()));
                }
                List<HistoricVariableInstance> params200Var = historyService.createHistoricVariableInstanceQuery().processInstanceId(procInstId).variableName("params200").list();
                for (HistoricVariableInstance variableInstance : params200Var) {
                    listVar.add(getHistoricVariableInstance(variableInstance.getVariableName(), variableInstance.getValue()));
                }
                List<HistoricVariableInstance> params2000Var = historyService.createHistoricVariableInstanceQuery().processInstanceId(procInstId).variableName("params2000").list();
                for (HistoricVariableInstance variableInstance : params2000Var) {
                    listVar.add(getHistoricVariableInstance(variableInstance.getVariableName(), variableInstance.getValue()));
                }
                List<HistoricVariableInstance> purchase200Var = historyService.createHistoricVariableInstanceQuery().processInstanceId(procInstId).variableName("purchase200").list();
                for (HistoricVariableInstance variableInstance : purchase200Var) {
                    listVar.add(getHistoricVariableInstance(variableInstance.getVariableName(), variableInstance.getValue()));
                }
                List<HistoricVariableInstance> purchase2000Var = historyService.createHistoricVariableInstanceQuery().processInstanceId(procInstId).variableName("purchase2000").list();
                for (HistoricVariableInstance variableInstance : purchase2000Var) {
                    listVar.add(getHistoricVariableInstance(variableInstance.getVariableName(), variableInstance.getValue()));
                }
                List<HistoricVariableInstance> isLyVar = historyService.createHistoricVariableInstanceQuery().processInstanceId(procInstId).variableName("isLy").list();
                for (HistoricVariableInstance variableInstance : isLyVar) {
                    listVar.add(getHistoricVariableInstance(variableInstance.getVariableName(), variableInstance.getValue()));
                }
                List<HistoricVariableInstance> isCFOVar = historyService.createHistoricVariableInstanceQuery().processInstanceId(procInstId).variableName("isCFO").list();
                for (HistoricVariableInstance variableInstance : isCFOVar) {
                    listVar.add(getHistoricVariableInstance(variableInstance.getVariableName(), variableInstance.getValue()));
                }
            }
            if (listVar.isEmpty() && procInstIdNew == null) {
                listVar.add(historicVariableInstance);
                listVar.add(isLeader);
                if (name.equals("seal")) {
                    if (StrUtil.isBlank(variable)) {
                        variable = "1";
                    }
                    VariableInstance g1 = getHistoricVariableInstance("g1", Integer.valueOf(variable));
                    listVar.add(g1);

                }
                if (money == null) {
                    money = new BigDecimal(9999999);
                }
//                SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
                Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(sysUserNow.getDeptId());
                String companyId1 = belongCompany2.get("companyId").toString();
                String companyName = belongCompany2.get("companyName").toString();
                VariableInstance moneyVariable = getHistoricVariableInstance("money", money);
                listVar.add(moneyVariable);
                VariableInstance deptVariable = getHistoricVariableInstance("deptId", companyId1);
                listVar.add(deptVariable);
                VariableInstance companyIdVariable = getHistoricVariableInstance("companyId", companyId1);
                listVar.add(companyIdVariable);
                // 获取经营参数
                if ("purchase".equals(name)) {

                    SysConfig config1 = new SysConfig();
                    config1.setConfigKey("purchase200");
                    List<SysConfig> list = remoteConfigService.listOperating(config1);
                    if (list.isEmpty()) {
                        return R.error(companyName + "缺少采购申请经营参数1");
                    }
                    VariableInstance paymentVariable = getHistoricVariableInstance("purchase200", list.get(0).getConfigValue());
                    listVar.add(paymentVariable);


                    SysConfig config2 = new SysConfig();
                    config2.setConfigKey("purchase2000");
                    List<SysConfig> list2 = remoteConfigService.listOperating(config2);
                    if (list2.isEmpty()) {
                        return R.error(companyName + "缺少采购申请经营参数2");
                    }
                    VariableInstance paymentVariable2 = getHistoricVariableInstance("purchase2000", list2.get(0).getConfigValue());
                    listVar.add(paymentVariable2);
                }
                if ("payment".equals(name)) {

                    SysConfig config1 = new SysConfig();
                    config1.setConfigKey(companyId1 + "g1fk");
                    List<SysConfig> list = remoteConfigService.listOperating(config1);
                    if (list.isEmpty()) {
                        return R.error(companyName + "缺少付款申请经营参数");
                    }
                    VariableInstance paymentVariable = getHistoricVariableInstance("g1", list.get(0).getConfigValue());
                    listVar.add(paymentVariable);
                }
                if (name.contains("payment-")) {

                    SysConfig config1 = new SysConfig();
                    SysConfig config2 = new SysConfig();
                    SysConfig config3 = new SysConfig();
                    config1.setConfigKey(companyId1 + "g1" + name);
                    config2.setConfigKey(companyId1 + "g2" + name);
                    config3.setConfigKey(companyId1 + "g3" + name);
                    List<SysConfig> list = remoteConfigService.listOperating(config1);
                    List<SysConfig> list2 = remoteConfigService.listOperating(config2);
                    List<SysConfig> list3 = remoteConfigService.listOperating(config3);
                    if (list.isEmpty() || list2.isEmpty() || list3.isEmpty()) {
                        List<String> paps = new ArrayList<>();
                        // 合同款项-认证分包费、业务分包费
                        paps.add("payment-renzhengfenbao");
                        // 日常费用-总分公司管理费
                        paps.add("payment-maincompany");
                        // 保证金
                        paps.add("payment-baozhengjin");
                        // 合同款项-渠道服务费
                        paps.add("payment-qudaofuwu");
                        if (paps.contains(name)) {
//                            VariableInstance paymentVariable1 = getHistoricVariableInstance("g1", 3000);
//                            listVar.add(paymentVariable1);
//                            VariableInstance paymentVariable2 = getHistoricVariableInstance("g2", 3000);
//                            listVar.add(paymentVariable2);
//                            VariableInstance paymentVariable3 = getHistoricVariableInstance("g3", 3000);
//                            listVar.add(paymentVariable3);
                        } else {
                            return R.error(companyName + "缺少付款申请经营参数");
                        }
                    } else {
                        VariableInstance paymentVariable1 = getHistoricVariableInstance("g1", list.get(0).getConfigValue());
                        listVar.add(paymentVariable1);
                        VariableInstance paymentVariable2 = getHistoricVariableInstance("g2", list2.get(0).getConfigValue());
                        listVar.add(paymentVariable2);
                        VariableInstance paymentVariable3 = getHistoricVariableInstance("g3", list3.get(0).getConfigValue());
                        listVar.add(paymentVariable3);
                    }
//                    variables.put("g1", Double.valueOf(list.get(0).getConfigValue()));
//                    variables.put("g2", Double.valueOf(list2.get(0).getConfigValue()));
//                    variables.put("g3", Double.valueOf(list3.get(0).getConfigValue()));
                }
                if ("reimburse".equals(name)) {

                    SysConfig config1 = new SysConfig();
                    config1.setConfigKey(companyId1 + "g1bx");
                    List<SysConfig> list = remoteConfigService.listOperating(config1);
                    if (list.isEmpty()) {
                        return R.error(companyName + "缺少报销申请经营参数");
                    }
                    VariableInstance reimburseVariable = getHistoricVariableInstance("g1", list.get(0).getConfigValue());
                    listVar.add(reimburseVariable);
                }
                if ("scrapped".equals(name)) {

                    SysConfig config1 = new SysConfig();
                    config1.setConfigKey("params200");
                    List<SysConfig> list = remoteConfigService.listOperating(config1);
                    SysConfig config2 = new SysConfig();
                    config2.setConfigKey("params2000");
                    List<SysConfig> list2 = remoteConfigService.listOperating(config2);
                    if (list.isEmpty() || list2.isEmpty()) {
                        return R.error("请先配置经营参数");
                    }
                    VariableInstance scrappedVariable1 = getHistoricVariableInstance("params200", list.get(0).getConfigValue());
                    listVar.add(scrappedVariable1);
                    VariableInstance scrappedVariable2 = getHistoricVariableInstance("params2000", list2.get(0).getConfigValue());
                    listVar.add(scrappedVariable2);
                }


            }
            // 去除不符合条件的线
            Set<SequenceFlow> sequenceFlowList2 = new HashSet<>();
            for (SequenceFlow sequenceFlow : sequenceFlowList) {
                String conditionExpression = sequenceFlow.getConditionExpression();
                if (conditionExpression != null && StringUtils.isNotEmpty(conditionExpression)) {
                    ExpressionFactory factory = new ExpressionFactoryImpl();
                    SimpleContext context = new SimpleContext();
                    for (VariableInstance var : listVar) {
                        context.setVariable(var.getName(), factory.createValueExpression(var.getValue(), var.getValue().getClass()));
                    }
                    try {
                        ValueExpression e = factory.createValueExpression(context, conditionExpression, boolean.class);
                        if ((Boolean) e.getValue(context)) {
                            sequenceFlowList2.add(sequenceFlow);
                        }
                    } catch (PropertyNotFoundException e) {
                        log.error("流程变量不存在：{}，表达式：{}", e.getMessage(), conditionExpression);
                        continue;
                    }
                } else {
                    sequenceFlowList2.add(sequenceFlow);
                }
            }
            sequenceFlowList = new ArrayList<>(sequenceFlowList2);
            sequenceFlowList.stream().forEach(sequenceFlow -> {
                boolean flag = false;
                for (ExclusiveGateway exclusiveGateway : exclusiveGatewayList) {
                    if (exclusiveGateway.getId().equals(sequenceFlow.getSourceRef())) {
                        flag = true;
                    }
                }
                resultFlow.add(new ProcessesAllFlowVo(sequenceFlow.getId(), sequenceFlow.getName(), sequenceFlow.getSourceRef(), sequenceFlow.getTargetRef(), flag));
            });

            userTaskList.stream().forEach(userTask -> {
                result.add(new ProcessesAllTaskVo(userTask.getId(), userTask.getName(), "userTask", false, null));
            });
            // 结束节点
            if (!endEventList.isEmpty()) {
                result.add(new ProcessesAllTaskVo(endEventList.get(0).getId(), endEventList.get(0).getName(), "end", false, null));
            }
            /**
             * 按顺序连线，遇到分支随机，若已高亮则走高亮的线，直到结束
             */
            List<ProcessesAllTaskVo> all = new ArrayList<>();

            // 发起人是自己，显示 我
            Long userId1 = SystemUtil.getUserId();
            applyer2 = applyer;
            if (userId1.equals(bizBusiness.getUserId())) {
                applyer = "我";
            }
            if (applyTime != null) {
                applyer = applyer + "      " + DateUtil.format(applyTime, "yyyy-MM-dd HH:mm");
            }
            all.add(new ProcessesAllTaskVo(startEventList.get(0).getId(), startEventList.get(0).getName(), "start", true, applyer, bizBusiness.getUserId(), "", 0, new ArrayList<>(), "", new ArrayList<>()));
            //
            if (!exclusiveGatewayList.isEmpty()) {


                // 根据高亮线获取正确的网关方向
//                List<Map<String, Object>> maps = new ArrayList<>();
//                for (ProcessesAllFlowVo processesAllFlowVo : resultFlow) {
//                    for (String flow : highLightedFlows) {
//                        if (flow.equals(processesAllFlowVo.getId()) && processesAllFlowVo.isSourceGateway()) {
//                            maps.add(new HashMap<String, Object>(2) {{
//                                put("flowId", flow);
//                                put("flowSource", processesAllFlowVo.getSource());
//                            }});
//                        }
//                    }
//                }

                // 预提交选择分支
                SysUser sysUser1 = remoteUserService.selectSysUserByUserId(userId);
                Map<String, Object> variables = new HashMap<>();
                variables.put("deptId", sysUser1.getDeptId());

                if (StrUtil.isNotBlank(variable)) {
                    // 获取经营参数
                    if ("seal".equals(name)) {
                        variables.put("g1", Long.valueOf(variable));
                    }
//                    for (ExclusiveGateway exclusiveGateway : exclusiveGatewayList) {
//                        for (SequenceFlow outgoingFlow : exclusiveGateway.getOutgoingFlows()) {
//                            boolean targetTask = ProcessUtil.isTargetTask(outgoingFlow.getConditionExpression(), variables);
//                            if (targetTask) {
//                                maps.add(new HashMap<String, Object>(2) {{
//                                    put("flowId", outgoingFlow.getId());
//                                    put("flowSource", outgoingFlow.getSourceRef());
//                                }});
//                            }
//                        }
//                    }
                }
                if (money != null) {
                    SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
                    Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(sysUser.getDeptId());
                    String companyId1 = belongCompany2.get("companyId").toString();
                    String companyName = belongCompany2.get("companyName").toString();
                    variables.put("money", money.doubleValue());
                    // 获取经营参数
                    if ("payment".equals(name)) {

                        SysConfig config1 = new SysConfig();
                        config1.setConfigKey(companyId1 + "g1fk");
                        List<SysConfig> list = remoteConfigService.listOperating(config1);
                        if (list.isEmpty()) {
                            return R.error(companyName + "缺少付款申请经营参数");
                        }
                        variables.put("g1", Double.valueOf(list.get(0).getConfigValue()));
                    }
                    if (name.contains("payment-")) {

                        SysConfig config1 = new SysConfig();
                        SysConfig config2 = new SysConfig();
                        SysConfig config3 = new SysConfig();
                        config1.setConfigKey(companyId1 + "g1" + name);
                        config2.setConfigKey(companyId1 + "g2" + name);
                        config3.setConfigKey(companyId1 + "g3" + name);
                        List<SysConfig> list = remoteConfigService.listOperating(config1);
                        List<SysConfig> list2 = remoteConfigService.listOperating(config2);
                        List<SysConfig> list3 = remoteConfigService.listOperating(config3);
                        if (list.isEmpty() || list2.isEmpty() || list3.isEmpty()) {
//                            return R.error(companyName + "缺少付款申请经营参数");
                            List<String> paps = new ArrayList<>();
                            // 合同款项-认证分包费、业务分包费
                            paps.add("payment-renzhengfenbao");
                            // 日常费用-总分公司管理费
                            paps.add("payment-maincompany");
                            // 保证金
                            paps.add("payment-baozhengjin");
                            // 合同款项-渠道服务费
                            paps.add("payment-qudaofuwu");
                            if (paps.contains(name)) {
//                            VariableInstance paymentVariable1 = getHistoricVariableInstance("g1", 3000);
//                            listVar.add(paymentVariable1);
//                            VariableInstance paymentVariable2 = getHistoricVariableInstance("g2", 3000);
//                            listVar.add(paymentVariable2);
//                            VariableInstance paymentVariable3 = getHistoricVariableInstance("g3", 3000);
//                            listVar.add(paymentVariable3);
                            } else {
                                return R.error(companyName + "缺少付款申请经营参数");
                            }
                        }
                        variables.put("g1", Double.valueOf(list.get(0).getConfigValue()));
                        variables.put("g2", Double.valueOf(list2.get(0).getConfigValue()));
                        variables.put("g3", Double.valueOf(list3.get(0).getConfigValue()));
                    }
                    if ("reimburse".equals(name)) {

                        SysConfig config1 = new SysConfig();
                        config1.setConfigKey(companyId1 + "g1bx");
                        List<SysConfig> list = remoteConfigService.listOperating(config1);
                        if (list.isEmpty()) {
                            return R.error(companyName + "缺少报销申请经营参数");
                        }
                        variables.put("g1", Double.valueOf(list.get(0).getConfigValue()));
                    }
                    if ("scrapped".equals(name)) {

                        SysConfig config1 = new SysConfig();
                        config1.setConfigKey("params200");
                        List<SysConfig> list = remoteConfigService.listOperating(config1);
                        SysConfig config2 = new SysConfig();
                        config2.setConfigKey("params2000");
                        List<SysConfig> list2 = remoteConfigService.listOperating(config2);
                        if (list.isEmpty() || list2.isEmpty()) {
                            return R.error("请先配置经营参数");
                        }
                        variables.put("params200", Double.valueOf(list.get(0).getConfigValue()));
                        variables.put("params2000", Double.valueOf(list2.get(0).getConfigValue()));
                    }
//                    for (ExclusiveGateway exclusiveGateway : exclusiveGatewayList) {
//                        for (SequenceFlow outgoingFlow : exclusiveGateway.getOutgoingFlows()) {
//                            boolean targetTask = ProcessUtil.isTargetTask(outgoingFlow.getConditionExpression(), variables);
//                            if (targetTask) {
//                                maps.add(new HashMap<String, Object>(2) {{
//                                    put("flowId", outgoingFlow.getId());
//                                    put("flowSource", outgoingFlow.getSourceRef());
//                                }});
//                            }
//                        }
//                    }
                }
                // 将不走的线移除
//                Iterator<ProcessesAllFlowVo> iterator = resultFlow.iterator();
//                while (iterator.hasNext()) {
//                    ProcessesAllFlowVo next = iterator.next();
//                    for (Map<String, Object> reMap : maps) {
//
//                        if (next.getSource().equals(reMap.get("flowSource").toString()) && !next.getId().equals(reMap.get("flowId").toString())) {
//                            iterator.remove();
//                        }
//                    }
//                }
                //流程未走到互斥网关时随机去重选一条分支
                ArrayList<ProcessesAllFlowVo> collect = resultFlow.stream().collect(collectingAndThen(toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getSource()))), ArrayList::new));
                Map<String, String> flowMap = new HashMap<>();
                Map<String, ProcessesAllTaskVo> taskMap = new HashMap<>();
                for (ProcessesAllFlowVo flowVo : collect) {
                    flowMap.put(flowVo.getSource(), flowVo.getTarget());
                }
                result.stream().forEach(task -> taskMap.put(task.getId(), task));
                getTask(startEventList.get(0).getId(), flowMap, taskMap, all);
            } else {
                Map<String, String> flowMap = new HashMap<>();
                Map<String, ProcessesAllTaskVo> taskMap = new HashMap<>();
                for (ProcessesAllFlowVo flowVo : resultFlow) {
                    flowMap.put(flowVo.getSource(), flowVo.getTarget());
                }
                result.stream().forEach(task -> taskMap.put(task.getId(), task));
                getTask(startEventList.get(0).getId(), flowMap, taskMap, all);
            }
            // 移除网关节点11
            Iterator<ProcessesAllTaskVo> iterator = all.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().getType().equals("gateway")) {
                    iterator.remove();
                }
            }
            HiTaskVo hiTaskVo = new HiTaskVo();
            hiTaskVo.setProcInstId(procInstId);
            List<HiTaskVo> historyTaskList = new ArrayList<>();
            if (StrUtil.isNotBlank(procInstId)) {
                historyTaskList = auditService.getHistoryTaskList(hiTaskVo).stream().collect(collectingAndThen(toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getAuditId() + ";" + o.getProcInstId()))), ArrayList::new));
            }
            for (ProcessesAllTaskVo processesAllTaskVo : all) {
                for (String highLightedActiviti : highLightedActivitis) {
                    if (highLightedActiviti.equals(processesAllTaskVo.getId())) {
                        processesAllTaskVo.setFlag(true);
                    }
                }
                for (HiTaskVo taskVo : historyTaskList) {
                    if (taskVo.getTaskDefKey().equals(processesAllTaskVo.getId())) {
                        String str = "";
                        if (StrUtil.isNotBlank(taskVo.getAuditor())) {
                            if (taskVo.getResult() != null) {
                                processesAllTaskVo.setResult(taskVo.getResult());
                                if (taskVo.getResult() == 2) {
                                    str = "（已通过）" + DateUtil.format(taskVo.getEndTime(), "yyyy-MM-dd HH:mm");


                                }
                                if (taskVo.getResult() == 3) {
                                    str = "（已驳回）";
                                }
                            }
                            processesAllTaskVo.setComment(taskVo.getComment());
                            processesAllTaskVo.setAuditorId(taskVo.getAuditorId());
                            processesAllTaskVo.setAuditor((processesAllTaskVo.getAuditor() == null ? "" : processesAllTaskVo.getAuditor() + "、") + taskVo.getAuditor().split("-")[0] + str);
                        } else {
                            if (procResult == 4) {
                                str = "（已撤销）";
                                processesAllTaskVo.setAuditor(str);
                            }
                            if (procResult == 1) {
                                str = "（审批中）";
                                processesAllTaskVo.setAuditor((processesAllTaskVo.getAuditor() == null ? "" : processesAllTaskVo.getAuditor() + "、") + str);
                            }
                            processesAllTaskVo.setResult(procResult);

                        }
//                        if (taskVo.getEndTime() != null) {
//                            processesAllTaskVo.setEndTime(DateUtil.format(taskVo.getEndTime(), "yyyy-MM-dd HH:mm"));
//                        }
                        // 如果未审批记录不显示
                        if (taskVo.getResult() == null) {
                            continue;
                        }
                        List<HiTaskVo> history = processesAllTaskVo.getHistory() == null ? new ArrayList<>() : processesAllTaskVo.getHistory();
                        history.add(taskVo);
                        processesAllTaskVo.setHistory(history);
                    }
                }
            }
            // 若已驳回，则下一节点不高亮
            for (int i = 0; i < all.size(); i++) {
                if (null == all.get(i).getResult()) {
                    break;
                }
                if (all.get(i).getResult().equals(3)) {
                    all.get(i + 1).setFlag(false);
                }

            }
            Set<String> auditors = new HashSet<>();
            // 获取转交记录
            QueryWrapper<BizReassignmentRecord> wrapper = new QueryWrapper<>();
            wrapper.eq("business_key", bizBusiness.getId());
            List<BizReassignmentRecord> bizReassignmentRecords = reassignmentRecordMapper.selectList(wrapper);

            for (int i = 0; i < all.size() - 1; i++) {
                List<Task> taskResultList = taskService.createTaskQuery().taskDefinitionKey(all.get(i + 1).getId()).processInstanceId(procInstId).list();
                // 若下个节点未审批，则查询候选人
                if ((null == all.get(i + 1).getAuditorId() || multiInstanceList.contains(all.get(i + 1).getId()) && !taskResultList.isEmpty())) {
                    System.out.println(all.get(i + 1).getId() + "***********" + bizBusiness.getUserId());
                    // 修复固定流程
                    if (bizBusiness.getProcDefKey() != null && bizBusiness.getProcDefKey().equals("payment-fznbzzdb") && all.get(i + 1).getId().equals("sid-7F321178-AA74-4C49-931D-80413581816B")) {
                        String paymentId = null;
                        for (VariableInstance variableInstance : listVar) {
                            if (variableInstance.getName().equals("id")) {
                                paymentId = variableInstance.getTextValue();
                                break;
                            }
                        }
                        Query query = new Query();
                        query.addCriteria(Criteria.where("_id").is(paymentId));
                        HashMap template = mongoTemplate.findOne(query, HashMap.class, "payment");
                        List<Integer> feeArray = (List<Integer>) template.get("feeArray");
                        if (!feeArray.isEmpty()) {
                            Set<String> temp = new HashSet<>();
                            for (Integer ids : feeArray) {
                                List<Integer> userRole = bizBusinessTestMapper.getUserRole(ids);
                                for (Integer roleId : userRole) {
                                    temp.add(roleId.toString());
                                }
                            }
                            auditors = temp;
                        } else {
                            auditors = nodeService.getAuditors(all.get(i + 1).getId(), bizBusiness.getUserId(), bizBusiness.getProcDefKey());
                        }
                    } else if (bizBusiness.getProcDefKey() != null && bizBusiness.getProcDefKey().equals("payment-jttxnzjdb") && all.get(i + 1).getId().equals("sid-736F5D7A-E811-4156-AABC-DBA501E340BB")) {
                        String paymentId = null;
                        for (VariableInstance variableInstance : listVar) {
                            if (variableInstance.getName().equals("id")) {
                                paymentId = variableInstance.getTextValue();
                                break;
                            }
                        }

                        // 处理资金调拨集团分公司调拨历史数据获取不到paymentId问题
                        if (StrUtil.isBlank(paymentId)) {
                            if (StrUtil.isBlank(procInstId)) throw new RuntimeException("未获取到流程实例编号");
                            log.info("-----------未获取到流程实例编号");
                            BizBusiness business = new BizBusiness();
                            business.setProcInstId(procInstId);
                            List<BizBusiness> bizBusinesses = bizBusinessService.selectBizBusinessList(bizBusiness);
                            if (bizBusinesses.size() != 1)
                                throw new RuntimeException("查询到0条或多条审批信息");
                            log.info("-----------查询到0条或多条审批信息");
                            paymentId = bizBusinesses.get(0).getTableId();
                        }

                        Query query = new Query();
                        query.addCriteria(Criteria.where("_id").is(paymentId));
                        HashMap template = mongoTemplate.findOne(query, HashMap.class, "payment");
                        List<Integer> feeArray = (List<Integer>) template.get("feeArray");
                        if (!feeArray.isEmpty()) {
                            Set<String> temp = new HashSet<>();
                            for (Integer ids : feeArray) {
                                List<Integer> userRole = bizBusinessTestMapper.getUserRole(ids);
                                for (Integer roleId : userRole) {
                                    temp.add(roleId.toString());
                                }
                            }
                            auditors = temp;
                        } else {
                            auditors = nodeService.getAuditors(all.get(i + 1).getId(), bizBusiness.getUserId(), bizBusiness.getProcDefKey());
                        }
                    } else {
                        auditors = nodeService.getAuditors(all.get(i + 1).getId(), bizBusiness.getUserId(), bizBusiness.getProcDefKey());
                    }

                    // 人名
                    List<String> userNames = new ArrayList<>();
                    if (auditors.size() > 0) {
                        boolean flag = false;
                        for (String auditor : auditors) {

                            SysUser sysUser = remoteUserService.selectSysUserByUserId(Long.valueOf(auditor));
                            String status = "1".equals(sysUser.getStatus()) ? "(已离职)" : "";
                            if (all.get(i + 1).getAuditor() != null && all.get(i + 1).getAuditor().contains(sysUser.getUserName() + "（已通过）")) {
                                continue;
                            }
                            if (!all.get(i + 1).getRecords().isEmpty()) {
                                for (Map<String, Object> record : all.get(i + 1).getRecords()) {
                                    if (record.get("source").toString().equals(sysUser.getUserName())) {
                                        flag = true;
                                    }
                                }
                            }
                            if (flag) {
                                continue;
                            }
                            userNames.add(sysUser.getUserName() + status);
                            System.err.println(sysUser.getUserName() + status);
                        }
                    }
                    String s = all.get(i + 1).getAuditor() == null ? "" : all.get(i + 1).getAuditor();
                    // 若已撤销，则当前审批节点人换成申请人
                    if (all.get(i + 1).getResult() == 4) {
                        all.get(i + 1).setAuditor(applyer + s);
                        // 标题变为发起人
                        all.get(i + 1).setName("发起人");
                    } else {
                        String join = String.join("/", userNames);
                        if (s.contains("、（")) {
                            String[] split = s.split("、（");
                            all.get(i + 1).setAuditor(split[0] + "、" + join + "（" + split[1]);
                        } else {
                            all.get(i + 1).setAuditor(join + s);
                        }
                    }
                }

                // 插入转交记录
                List<Map<String, Object>> records = new ArrayList<>();
                for (BizReassignmentRecord bizReassignmentRecord : bizReassignmentRecords) {
                    System.out.println(bizReassignmentRecord.getTaskDefKey() + "---" + all.get(i).getId());
                    if (bizReassignmentRecord.getTaskDefKey().equals(all.get(i).getId())) {
                        SysUser sysUserSource = remoteUserService.selectSysUserByUserId(bizReassignmentRecord.getSourceUser());
                        SysUser sysUserTarget = remoteUserService.selectSysUserByUserId(bizReassignmentRecord.getTargetUser());
                        records.add(new HashMap<String, Object>(3) {{
                            put("source", sysUserSource.getUserName());
                            put("target", sysUserTarget.getUserName());
                            put("createTime", DateUtil.format(bizReassignmentRecord.getCreateTime(), "yyyy-MM-dd HH:mm"));
                            put("reason", bizReassignmentRecord.getReason() == null ? "" : bizReassignmentRecord.getReason());
                        }});
                        all.get(i).setAuditor(all.get(i).getAuditor().replace(sysUserSource.getUserName(), sysUserTarget.getUserName()));
                    }
                }
                all.get(i).setRecords(records);

            }
            // 撤销节点后面的节点不显示
            List<ProcessesAllTaskVo> allResult = new ArrayList<>();


            for (int i = 0; i < all.size(); i++) {
                allResult.add(all.get(i));
                if (null == all.get(i).getResult()) {
                    continue;
                }
                if (all.get(i).getResult() == 4) {
                    break;
                }
            }
            for (ProcessesAllTaskVo processesAllTaskVo : allResult) {
                Long auditorId = processesAllTaskVo.getAuditorId();
                String auditor = processesAllTaskVo.getAuditor();
                if (auditorId == null) {
                    log.error("没有auditorId");
                    continue;
                }
                SysUser sysUser = remoteUserService.selectSysUserByUserId(Long.valueOf(auditorId));
                if (sysUser.getStatus().equals("1")) {
                    int i = auditor.indexOf('2');

                    auditor = auditor.substring(0, i) + "(已离职)  " + auditor.substring(i, auditor.length());
                    processesAllTaskVo.setAuditor(auditor);
                }


            }

            int count = actCommentService.count(procInstId);
            map.put("commentCount", count);
            List<ActComment> list = actCommentService.list(procInstId).stream().filter(actComment -> StrUtil.isBlank(actComment.getProcInstId())).collect(Collectors.toList());
            map.put("commentList", list);
            map.put("tasks", allResult);
            map.put("applyer", applyer2);
            int i = bizBusiness.getResult() == 2 && bizBusiness.getStatus() == 2 ? 2 : 0;
            map.put("procInstId", procInstId);
            map.put("result", i);
            Long duration = 0L;
            if (i == 2) {
                hiTaskVo.setProcInstId(bizBusiness.getProcInstId());
                List<HiTaskVo> hiTaskList = new ArrayList<>();
                if (StrUtil.isNotBlank(bizBusiness.getProcInstId())) {
                    hiTaskList = auditService.getHistoryTaskList(hiTaskVo);
                }
                if (StrUtil.isBlank(bizBusiness.getProcInstId())) {
                    hiTaskList = new ArrayList<>();
                }
                List<HiTaskVo> collect = hiTaskList.stream().collect(collectingAndThen(toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getAuditId() + ";" + o.getProcInstId()))), ArrayList::new));
                duration = collect.stream().collect(Collectors.summarizingLong(value -> value.getDuration())).getSum();
            }
            map.put("duration", duration);
            LinkedList<HiTaskVo> linkedList = new LinkedList<>();
            for (ProcessesAllTaskVo processesAllTaskVo : allResult) {
                if (processesAllTaskVo.getHistory() != null && !processesAllTaskVo.getHistory().isEmpty()) {
                    linkedList.addAll(processesAllTaskVo.getHistory());
                }
                if (processesAllTaskVo.getAuditor() != null && processesAllTaskVo.getAuditor().startsWith("（审批中）") && historyTaskList.size() > 0) {
                    processesAllTaskVo.setAuditor(historyTaskList.get(0).getNowAuditor() + processesAllTaskVo.getAuditor());
                }
            }
            map.put("history", linkedList);
            return map;
        } catch (Exception e) {
            log.error("未知错误", e);
            map.put("applyer", "");
            map.put("tasks", new ArrayList<>());
            map.put("result", 0);
            map.put("duration", 0);

            return map;
        }
    }

    private VariableInstance getHistoricVariableInstance(String variableName, Object value) {
        VariableInstance variableInstance = new VariableInstance() {
            @Override
            public String getId() {
                return null;
            }

            @Override
            public void setId(String s) {

            }

            @Override
            public Object getPersistentState() {
                return null;
            }

            @Override
            public void setRevision(int i) {

            }

            @Override
            public int getRevision() {
                return 0;
            }

            @Override
            public int getRevisionNext() {
                return 0;
            }

            @Override
            public void setName(String s) {

            }

            @Override
            public String getLocalizedName() {
                return null;
            }

            @Override
            public void setLocalizedName(String s) {

            }

            @Override
            public String getLocalizedDescription() {
                return null;
            }

            @Override
            public void setLocalizedDescription(String s) {

            }

            @Override
            public void setProcessInstanceId(String s) {

            }

            @Override
            public void setExecutionId(String s) {

            }

            @Override
            public Object getValue() {
                return value;
            }

            @Override
            public void setValue(Object o) {

            }

            @Override
            public String getTypeName() {
                return null;
            }

            @Override
            public void setTypeName(String s) {

            }

            @Override
            public String getName() {
                return variableName;
            }

            @Override
            public String getProcessInstanceId() {
                return null;
            }

            @Override
            public String getTaskId() {
                return null;
            }

            @Override
            public String getTextValue() {
                return null;
            }

            @Override
            public void setTextValue(String s) {

            }

            @Override
            public String getTextValue2() {
                return null;
            }

            @Override
            public void setTextValue2(String s) {

            }

            @Override
            public Long getLongValue() {
                return Long.valueOf(value.toString());
            }

            @Override
            public void setLongValue(Long aLong) {

            }

            @Override
            public Double getDoubleValue() {
                return null;
            }

            @Override
            public void setDoubleValue(Double aDouble) {

            }

            @Override
            public byte[] getBytes() {
                return new byte[0];
            }

            @Override
            public void setBytes(byte[] bytes) {

            }

            /**
             * @deprecated
             */
            @Override
            public String getByteArrayValueId() {
                return null;
            }

            /**
             * @deprecated
             */
            @Override
            public ByteArrayEntity getByteArrayValue() {
                return null;
            }

            /**
             * @param bytes
             * @deprecated
             */
            @Override
            public void setByteArrayValue(byte[] bytes) {

            }

            @Override
            public Object getCachedValue() {
                return null;
            }

            @Override
            public void setCachedValue(Object o) {

            }

            @Override
            public void setTaskId(String s) {

            }

            @Override
            public String getExecutionId() {
                return null;
            }
        };
        return variableInstance;
    }

    /**
     * 流程部署
     *
     * @return
     */
    @Override
    public R upload(MultipartFile file) {
        try {
            if (!file.isEmpty()) {
                InputStream fileInputStream = file.getInputStream();
                String fileName = file.getOriginalFilename();
                Deployment deployment = null;
                String extension = FilenameUtils.getExtension(fileName);
                String baseName = FilenameUtils.getBaseName(fileName);
                if (fileName.endsWith("bpmn20.xml")) {
                    deployment = repositoryService.createDeployment().name(baseName).addInputStream(fileName, fileInputStream).deploy();
                } else if ("zip".equals(extension) || "bar".equals(extension)) {
                    ZipInputStream zip = new ZipInputStream(fileInputStream);
                    deployment = repositoryService.createDeployment().name(baseName).addZipInputStream(zip).deploy();
                } else {
                    return R.error("不支持的文件格式");
                }
                if (null != deployment) {
                    log.info("流程部署成功,id:{}", deployment.getId());
                }
                return R.ok();
            }
            return R.error("没有流程文件失败");
        } catch (Exception e) {
            return R.error("部署失败");
        }


    }

    /**
     * 查看流程图
     *
     * @param did
     * @param ext
     * @param httpServletResponse
     */
    @Override
    public void show(String did, String ext, HttpServletResponse httpServletResponse) throws IOException {
        if (StringUtils.isEmpty(did) || StringUtils.isEmpty(ext)) {
            return;
        }
        InputStream in = null;
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(did).singleResult();
        if (".png".equalsIgnoreCase(ext)) {
            in = repositoryService.getProcessDiagram(processDefinition.getId());
        } else if (".bpmn".equalsIgnoreCase(ext)) {
            in = repositoryService.getResourceAsStream(did, processDefinition.getResourceName());
        }
        OutputStream out = null;
        byte[] buf = new byte[1024];
        int legth = 0;
        try {
            out = httpServletResponse.getOutputStream();
            while ((legth = in.read(buf)) != -1) {
                out.write(buf, 0, legth);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * 挂起、激活流程实例
     *
     * @param state
     * @param processId
     * @return
     */
    @Override
    public R updateState(String state, String processId) {
        if (state.equals("active")) {
            // 一并激活流程实例
            repositoryService.activateProcessDefinitionById(processId, true, new Date());
            log.info("已激活ID为:{}的流程", processId);
        } else if (state.equals("suspend")) {
            // 一并挂起流程实例
            repositoryService.suspendProcessDefinitionById(processId, true, new Date());
            log.info("已挂起ID为:{}的流程", processId);
        }
        return R.ok();
    }

    /**
     * 实时高亮流程图
     *
     * @param procInstId
     * @param response
     */
    @Override
    public void getHighlightImg(String procInstId, HttpServletResponse response) {
        if (StringUtils.isBlank(procInstId)) {
            log.error("参数为空");
        }
        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(procInstId).singleResult();
        // 完整流程数据
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());

        ProcessDefinitionEntity definitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processInstance.getProcessDefinitionId());

        List<HistoricActivityInstance> highLightedActivitList = historyService.createHistoricActivityInstanceQuery().processInstanceId(procInstId).orderByHistoricActivityInstanceId().asc().list();
        // 高亮节点id集合
        List<String> highLightedActivitis = new ArrayList<String>();
        // 高亮线路id集合
        List<String> highLightedFlows = getHighLightedFlows(definitionEntity, highLightedActivitList);
        for (HistoricActivityInstance tempActivity : highLightedActivitList) {
            String activityId = tempActivity.getActivityId();
            highLightedActivitis.add(activityId);
        }
        Set<String> currIds = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).list().stream().map(e -> e.getActivityId()).collect(Collectors.toSet());
        ICustomProcessDiagramGenerator diagramGenerator = (ICustomProcessDiagramGenerator) processEngineConfiguration.getProcessDiagramGenerator();
        InputStream imageStream = diagramGenerator.generateDiagram(bpmnModel, "png", highLightedActivitis, highLightedFlows, "宋体", "宋体", "宋体", null, 1.0, new Color[]{ActivitiConstant.COLOR_NORMAL, ActivitiConstant.COLOR_CURRENT}, currIds);
        try {
            // 输出资源内容到相应对象
            byte[] b = new byte[1024];
            int len;
            while ((len = imageStream.read(b, 0, 1024)) != -1) {
                response.getOutputStream().write(b, 0, len);
            }
            response.flushBuffer();
        } catch (IOException e) {
            log.error(e.toString());
            throw new RuoyiException("读取流程图片失败");
        }
    }

    /**
     * 获取需要高亮的线
     *
     * @param processDefinitionEntity
     * @param historicActivityInstances
     * @return
     */
    private List<String> getHighLightedFlows(ProcessDefinitionEntity processDefinitionEntity, List<HistoricActivityInstance> historicActivityInstances) {
        List<String> highFlows = new ArrayList<>();// 用以保存高亮的线flowId
        for (int i = 0; i < historicActivityInstances.size() - 1; i++) {
            // 对历史流程节点进行遍历
            // 获取当前历史节点
            HistoricActivityInstance currentActivityInstance = historicActivityInstances.get(i);
            // 得到节点定义的详细信息
            ActivityImpl activityImpl = processDefinitionEntity.findActivity(currentActivityInstance.getActivityId());
            // 用以保存后需开始时间相同的节点
            List<ActivityImpl> sameStartTimeNodes = new ArrayList<>();
            ActivityImpl sameActivityImpl1 = processDefinitionEntity.findActivity(historicActivityInstances.get(i + 1).getActivityId());
            // 将后面第一个节点放在时间相同节点的集合里
            sameStartTimeNodes.add(sameActivityImpl1);
            /**
             * 遍历outgoingFlows并找到已流转的 满足如下条件认为已流转：
             * 1.当前节点是并行网关或兼容网关，则通过outgoingFlows能够在历史活动中找到的全部节点均为已流转
             * 2.当前节点是以上两种类型之外的，通过outgoingFlows查找到的时间最早的流转节点视为有效流转
             * (第2点有问题，有过驳回的，会只绘制驳回的流程线，通过走向下一级的流程线没有高亮显示)
             */
            // if
            // ("parallelGateway".equals(currentActivityInstance.getActivityType())
            // ||
            // "inclusiveGateway".equals(currentActivityInstance.getActivityType()))
            // {
            // }
            for (int j = i + 1; j < historicActivityInstances.size() - 1; j++) {
                HistoricActivityInstance activityImpl1 = historicActivityInstances.get(j);// 后续第一个节点
                HistoricActivityInstance activityImpl2 = historicActivityInstances.get(j + 1);// 后续第二个节点
                if (Math.abs(activityImpl1.getStartTime().getTime() - activityImpl2.getStartTime().getTime()) < 200) {
                    // if
                    // (activityImpl1.getStartTime().equals(activityImpl2.getStartTime()))
                    // {
                    // 如果第一个节点和第二个节点开始时间相同保存
                    ActivityImpl sameActivityImpl2 = processDefinitionEntity.findActivity(activityImpl2.getActivityId());
                    sameStartTimeNodes.add(sameActivityImpl2);
                } else {
                    // 有不相同跳出循环
                    break;
                }
            }
            // 取出节点的所有出去的线
            List<PvmTransition> pvmTransitions = activityImpl.getOutgoingTransitions();
            for (PvmTransition pvmTransition : pvmTransitions) {
                // 对所有的线进行遍历
                ActivityImpl pvmActivityImpl = (ActivityImpl) pvmTransition.getDestination();
                // 如果取出的线的目标节点存在时间相同的节点里，保存该线的id，进行高亮显示
                if (sameStartTimeNodes.contains(pvmActivityImpl)) {
                    highFlows.add(pvmTransition.getId());
                    break;
                }
            }
        }
        return highFlows;
    }

    /**
     * 根据线始末找到下个节点，组装流程图
     *
     * @param source
     * @param flowMap
     * @param taskMap
     * @param all
     */
    private void getTask(String source, Map<String, String> flowMap, Map<String, ProcessesAllTaskVo> taskMap, List<ProcessesAllTaskVo> all) {
        if (!"start".equals(taskMap.get(source).getType())) {
            all.add(taskMap.get(source));
        }
        taskMap.remove(source);
        if (flowMap.get(source) != null) {
            getTask(flowMap.get(source), flowMap, taskMap, all);
        }
    }

    /**
     * pdf导出流程
     *
     * @param business
     * @param tableId
     * @return
     */
    @Override
    public List<Map<String, Object>> getPdfProcessAll(String business, String tableId) {
        Map<String, Object> purchase = this.getProcessAll(business, tableId, SystemUtil.getUserId(), null, null, null);
        List<ProcessesAllTaskVo> allResult = (List<ProcessesAllTaskVo>) purchase.get("tasks");

        List<Map<String, Object>> maps = new ArrayList<>();
        if (allResult != null && allResult.size() > 0) {
            for (int i = 0; i < allResult.size(); i++) {
                Map<String, Object> map = new HashMap<>(2);
                if (i == 0) {
                    map.put("name", purchase.get("applyer") + "发起申请");
                    map.put("date", allResult.get(i).getEndTime());
                    maps.add(map);
                } else {
                    map.put("name", allResult.get(i).getAuditor());
                    map.put("date", allResult.get(i).getEndTime());
                    maps.add(map);
                }

            }
        } else {
            Map<String, Object> map = new HashMap<>(2);
            map.put("name", "-");
            map.put("date", "-");
            maps.add(map);
        }
        // 当不为空时，删除最后一个
        if (maps.size() > 0) {
            maps.remove(maps.size() - 1);
        }
        return maps;
    }

    /**
     * 流程撤回
     *
     * @param id     procInstId
     * @param reason 撤回原因
     */
    @Override
    public void revokeProcess(String id, String reason) {
        // 关联业务状态结束
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(id).singleResult();
        BizBusiness bizBusiness = new BizBusiness();
        bizBusiness.setId(Long.valueOf(pi.getBusinessKey()));
        bizBusiness.setCurrentTask(ActivitiConstant.END_TASK_NAME);
        bizBusiness.setStatus(ActivitiConstant.STATUS_CANCELED);
        bizBusiness.setResult(ActivitiConstant.RESULT_CANCELED);
        bizBusiness.setAuditors("-");
        bizBusinessService.updateBizBusiness(bizBusiness);
        runtimeService.deleteProcessInstance(id, reason);
        BizBusiness business = bizBusinessService.selectBizBusinessById(bizBusiness.getId().toString());
        pushSocket(business.getProcDefKey());

//        business.getAuditors();
//        changeCarStatus(business);
//        String tableId = business.getTableId();
//        // mongodb 里查询实例
//        Query query = new Query();
//        query.addCriteria(Criteria.where("_id").is(tableId));
//        HashMap payment = mongoTemplate.findOne(query, HashMap.class, "payment");
//        if (tableId.length() < 20) {
//            changeGoodFinanceStatus(business);
//        } else {
//            if (payment != null) {
//                removeGoodStatus(payment);
//            }
//        }
//        // 修改mongodb 状态
//        if (payment != null) {
//            backMoney(payment);
//            Update update = new Update();
//            update.set("result", ActivitiConstant.RESULT_CANCELED);
//            update.set("state", ActivitiConstant.STATUS_CANCELED);
//            mongoTemplate.upsert(query, update, "payment");
//        }
    }

    /**
     * 同步其他系统审批状态
     *
     * @param procDefKey 流程默认key
     * @param status     审批状态
     * @param tableId    业务主键ID
     */
    @Override
    public void syncAuditStatus(String procDefKey, Integer status, String tableId) {
        try {
            String quotationKey = "status";
            String contractKey = "reviewStatus";
            Map<String, Object> statusMap = Maps.newHashMap();
            if (status == 1) {
                // 驳回
                statusMap.put(quotationKey, 2);
                statusMap.put(contractKey, 4);
            } else if (status == 2) {
                // 通过
                statusMap.put(quotationKey, 3);
                statusMap.put(contractKey, 3);
            } else if (status == 3) {
                // 撤回
                statusMap.put(quotationKey, 6);
                statusMap.put(contractKey, 5);
            }
            SysConfig sysConfig = remoteConfigService.findConfigUrl();
            if ("quotation".equals(procDefKey)) {
                // 上海量远-报价审批
                BizQuotationApply bizQuotationApply = bizQuotationApplyMapper.selectById(tableId);
                Map<String, Object> paramMap = Maps.newHashMap();
                paramMap.put("code", bizQuotationApply.getCode());
                paramMap.put("status", statusMap.get(quotationKey));
                if ("test".equals(sysConfig.getConfigValue())) {
                    HttpRequest.put(UrlConstants.LY_QUOTATION_TEST).body(JSON.toJSONString(paramMap)).execute().body();
                } else {
                    HttpRequest.put(UrlConstants.LY_QUOTATION_ONLINE).body(JSON.toJSONString(paramMap)).execute().body();
                }
            } else if ("contract-review".equals(procDefKey)) {
                // 上海量远-合同审批
                BizContractInfo bizContractInfo = bizContractInfoMapper.selectById(tableId);
                Map<String, Object> paramMap = Maps.newHashMap();
                paramMap.put("id", bizContractInfo.getContractId());
                paramMap.put("reviewStatus", statusMap.get(contractKey));
                if ("test".equals(sysConfig.getConfigValue())) {
                    HttpRequest.put(UrlConstants.LY_CONTRACT_TEST).body(JSON.toJSONString(paramMap)).execute().body();
                } else {
                    HttpRequest.put(UrlConstants.LY_CONTRACT_ONLINE).body(JSON.toJSONString(paramMap)).execute().body();
                }
            }

            // 审批通过修改合同项目信息
            if ("contract-project".equals(procDefKey) && status == 2) {
                // 运营系统-合同项目信息修改
                BizContractProject bizContractProject = bizContractProjectMapper.selectById(tableId);
                Map<String, Object> paramMap = Maps.newHashMap();
                paramMap.put("oldIdentifier", bizContractProject.getIdentifier());
                paramMap.put("newIdentifier", bizContractProject.getIdentifierNew());
                paramMap.put("newType", bizContractProject.getTypeNew());
                paramMap.put("newContractType", bizContractProject.getContractTypeNew());
                paramMap.put("newContractIdentifier", bizContractProject.getContractIdentifierNew());
                paramMap.put("remarks", bizContractProject.getReason());
                String response;
                String token = yunYingCommonService.getToken(procDefKey);
                if ("test".equals(sysConfig.getConfigValue())) {
                    response = HttpUtil.createPost(UrlConstants.YY_CP_UPDATE_TEST)
                            .header("token", token)
                            .body(JSON.toJSONString(paramMap))
                            .execute().body();
                } else {
                    response = HttpUtil.createPost(UrlConstants.YY_CP_UPDATE_ONLINE)
                            .header("token", token)
                            .body(JSON.toJSONString(paramMap))
                            .execute().body();
                }
                log.error(response);
            }
        } catch (Exception e) {
            log.error("同步其他系统审批状态发生异常====" + e);
        }
    }

    /**
     * 撤销发送socket
     *
     * @param procDefKey
     */
    private void pushSocket(String procDefKey) {
        // 获取socket-io在线用户
        List<String> users = remoteSocketService.getUsers();
        // 实时刷新所有在线用户待办未读消息数量
        users.stream().forEach(user -> {
            TaskQuery query = taskService.createTaskQuery().taskCandidateOrAssigned(user);
//            query = query.processDefinitionKeyIn(Arrays.asList("purchase;reimburse;payment;claim;businessMoney;scrapped;review;universal;seal;contract_ys;carApply;carSubsidyApply;feedback;cover;other-charge;hcpd;xz_approval;xz_adjustment;".split(";")));
            long count = query.count();
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setMessage(count + "");
            messageEntity.setUserId(user);
            messageEntity.setProcDefKey(procDefKey);
            remoteSocketService.ing(messageEntity);
        });
    }

    /**
     * 回滚车辆状态
     *
     * @param business
     */
    private void changeCarStatus(BizBusiness business) {
        if (!business.getProcDefKey().equals("carApply")) {
            return;
        }
        QueryWrapper<BizReserveDetail> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("apply_id", business.getTableId());
        BizReserveDetail reserveDetail = reserveDetailMapper.selectOne(wrapper1);
        reserveDetail.setDelFlag("1");
        reserveDetail.setUpdateTime(new Date());
        reserveDetail.setUpdateBy(SystemUtil.getUserName());
        reserveDetail.setRemark("撤销");
        reserveDetailMapper.updateById(reserveDetail);
    }

    /**
     * 回滚采购物品财务状态
     *
     * @param business
     */
    private void changeGoodFinanceStatus(BizBusiness business) {
        // 付款1报销2
        int types = 1;
        if ("reimburse".equals(business.getProcDefKey())) {
            types = 2;
        }
        associateGoodMapper.changeGoodFinanceStatus(types, business.getTableId());
    }

    private void removeGoodStatus(HashMap payment) {
        HashMap baseInformation = (HashMap) payment.get("BaseInformation");
        List<HashMap> purchaseData = (List<HashMap>) baseInformation.get("purchaseData");
        for (HashMap purchaseDatum : purchaseData) {
            Long id = Long.parseLong(String.valueOf((int) purchaseDatum.get("id")));
            if (id == null) {
                throw new RuntimeException("撤销流程时关联采购id为空");
            }
            BizGoodsInfo bizGoodsInfo = new BizGoodsInfo();
            bizGoodsInfo.setId(id);
            bizGoodsInfo.setFinanceStatus(0);
            bizGoodsInfoMapper.updateByPrimaryKeySelective(bizGoodsInfo);
        }
    }

    private void backMoney(HashMap payment) {
        // 判断是几几年 几月
        // 判断一下模板的状态
        int year = LocalDateTime.now().getYear();
        int monthValue = LocalDateTime.now().getMonthValue();
        log.info("今年是{}---是{}月", year, monthValue);
        // 去寻找今年的计划 然后找到月份
        // 有可能没计划 新找到明细和模板名字
        HashMap baseInformation = (HashMap) payment.get("BaseInformation");
        String name = (String) baseInformation.get("name");
        // 拿到明细扣减
        List<List<HashMap>> template1 = (List<List<HashMap>>) payment.get("detail");
        for (List<HashMap> hashMaps : template1) {
            Integer dept = Integer.MIN_VALUE;
            String detailMoney = "";
            String value = "";
            for (HashMap hashMaps1 : hashMaps) {
                String index_name = (String) hashMaps1.get("index_name");
                if (index_name.equals("fee_dept")) {
                    List<Integer> array = (List<Integer>) hashMaps1.get("array");
                    dept = array.get(array.size() - 1);
                }
                if (index_name.equals("detail_money")) {
                    detailMoney = (String) hashMaps1.get("value");
                }
            }
            Query query = new Query();
            query.addCriteria(Criteria.where("name").is(name).and("dept_id").is(dept));
            List<HashMap> templates = mongoTemplate.find(query, HashMap.class, "budget_statistics");
            int r = Integer.MIN_VALUE;
            for (int i = 0; i < templates.size(); i++) {
                HashMap map = templates.get(i);
                Query query1 = new Query();
                query1.addCriteria(Criteria.where("_id").is(map.get("budgetId")).and("disable").is(false).and("state").is(2));
                HashMap template = mongoTemplate.findOne(query1, HashMap.class, "budget_template");
                if (template == null) {
                    continue;
                }
                String start_year = (String) template.get("start_year");
                if (start_year.equals(year + "")) {
                    // 一年中只能有一个部门有对此模板的预算
                    r = i;
                    break;
                }
            }
            // 如果R还是最小值说明没找到
            if (r != Integer.MIN_VALUE) {
                // 扣减预算 加锁扣减
                // 精确到模板 拿到总金额直接扣减

                HashMap map = templates.get(r);
                payment.put("template_budget_id", map.get("_id").toString());
                // 是哪一个月分的  金额都是字符串然后变成bigDecimal 去减
                HashMap month = (HashMap) map.get("month" + monthValue);
                //         "amount_occupied": 占用的金额 ,
                //        "remaining_quota": 剩余金额 ,
                //        "confirmed_quota": 确认的金额,
                //        "budget_quota": 预算金额
                BigDecimal bigDecimalMoney = new BigDecimal(detailMoney);
                BigDecimal remaining_quota = new BigDecimal((String) month.get("remaining_quota"));

                month.put("remaining_quota", remaining_quota.add(bigDecimalMoney) + "");
                month.put("amount_occupied", new BigDecimal((String) month.get("amount_occupied")).subtract(bigDecimalMoney) + "");
                // 更新
                Update update = new Update();
                update.set("month" + monthValue, month);
                Query query1 = new Query();
                query1.addCriteria(Criteria.where("_id").is(map.get("_id")));
                mongoTemplate.upsert(query1, update, "budget_statistics");
            }


        }

        // 拿到明细扣减
        List<List<HashMap>> detail = (List<List<HashMap>>) payment.get("detail");
        List<HashMap> detailIds = new ArrayList<>();
        // 遍历明细数组 按照类别扣减
        for (List<HashMap> hashMaps : detail) {
            Integer dept = Integer.MIN_VALUE;
            String detailMoney = "";
            String value = "";
            for (HashMap hashMap : hashMaps) {
                String index_name = (String) hashMap.get("index_name");
                if (index_name.equals("fee_dept")) {
                    List<Integer> array = (List<Integer>) hashMap.get("array");
                    dept = array.get(array.size() - 1);
                }
                if (index_name.equals("detail_money")) {
                    detailMoney = (String) hashMap.get("value");
                }
                if (index_name.equals("button")) {
                    value = (String) hashMap.get("value");
                }

            }
            name = value;
            // 扣减金额
            // 查出今年的明细 只有一个
            Query queryDetail = new Query();
            queryDetail.addCriteria(Criteria.where("name").is(name).and("dept_id").is(dept));
            List<HashMap> details = mongoTemplate.find(queryDetail, HashMap.class, "budget_statistics");

            int r1 = Integer.MIN_VALUE;
            for (int i = 0; i < details.size(); i++) {
                HashMap map = details.get(i);
                Query query1 = new Query();
                query1.addCriteria(Criteria.where("_id").is(map.get("budgetId")).and("disable").is(false).and("state").is(2));
                HashMap template = mongoTemplate.findOne(query1, HashMap.class, "budget_template");
                if (template == null) {
                    continue;
                }
                String start_year = (String) template.get("start_year");
                if (start_year.equals(year + "")) {
                    // 一年中只能有一个部门有对此模板的预算
                    r1 = i;
                    break;
                }
            }
            if (r1 != Integer.MIN_VALUE) {

                HashMap map = details.get(r1);
                HashMap<Object, Object> map1 = new HashMap<>();
                map1.put("id", map.get("_id").toString());
                map1.put("money", detailMoney);
                detailIds.add(map1);
                BigDecimal bigDecimal = new BigDecimal(detailMoney);
                // 是哪一个月分的  金额都是字符串然后变成bigDecimal 去减
                HashMap month = (HashMap) map.get("month" + monthValue);
                //         "amount_occupied": 占用的金额 ,
                //        "remaining_quota": 剩余金额 ,
                //        "confirmed_quota": 确认的金额,
                //        "budget_quota": 预算金额
                BigDecimal remaining_quota = new BigDecimal((String) month.get("remaining_quota"));

                month.put("remaining_quota", remaining_quota.add(bigDecimal) + "");
                month.put("amount_occupied", new BigDecimal((String) month.get("amount_occupied")).subtract(bigDecimal) + "");
                Update update = new Update();
                update.set("month" + monthValue, month);
                Query query1 = new Query();
                query1.addCriteria(Criteria.where("_id").is(map.get("_id")));
                mongoTemplate.upsert(query1, update, "budget_statistics");
            }
        }
    }
}
