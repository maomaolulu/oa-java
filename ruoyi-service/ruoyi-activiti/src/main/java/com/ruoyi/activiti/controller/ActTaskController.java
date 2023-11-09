package com.ruoyi.activiti.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.StatefulException;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import com.ruoyi.activiti.annotation.RetryJpaOptimisticLock;
import com.ruoyi.activiti.consts.ActivitiConstant;
import com.ruoyi.activiti.domain.dto.ReassignmentDto;
import com.ruoyi.activiti.domain.proc.ActRuTask;
import com.ruoyi.activiti.domain.proc.BizAudit;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.domain.repair.TaskAndString;
import com.ruoyi.activiti.feign.MessageEntity;
import com.ruoyi.activiti.feign.RemoteSocketService;
import com.ruoyi.activiti.mapper.*;
import com.ruoyi.activiti.service.*;
import com.ruoyi.activiti.service.nbcb.NbcbSevice;
import com.ruoyi.activiti.test.BizBusinessTest;
import com.ruoyi.activiti.utils.MailService;
import com.ruoyi.activiti.vo.HiTaskVo;
import com.ruoyi.activiti.vo.RuTask;
import com.ruoyi.common.auth.annotation.HasPermissions;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.system.feign.RemoteConfigService;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteUserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.activiti.bpmn.model.*;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.javax.el.ExpressionFactory;
import org.activiti.engine.impl.javax.el.ValueExpression;
import org.activiti.engine.impl.juel.ExpressionFactoryImpl;
import org.activiti.engine.impl.juel.SimpleContext;
import org.activiti.engine.impl.persistence.entity.IdentityLinkEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.NativeTaskQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 流程任务task
 *
 * @author zx
 * @version 1.0
 * @menu 流程任务task
 */
@RestController
@RequestMapping("task")
public class ActTaskController extends BaseController {
    @Autowired
    private IActReProcdefService procdefService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private IBizAuditService bizAuditService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private RemoteUserService remoteUserService;
    @Autowired
    private IBizBusinessService businessService;
    @Autowired
    private RepositoryService repositoryService;
    @Resource
    BizBusinessTest bizBusinessTest;
    @Resource
    MongoTemplate mongoTemplate;

    private final BizBusinessMapper businessMapper;
    private final ActTaskService actTaskService;
    private final RemoteSocketService remoteSocketService;
    private final MapInfoService mapInfoService;
    private final MailService mailService;
    private final IBizNodeService nodeService;
    private final HistoryService historyService;
    private final BizAssociateApplyMapper associateApplyMapper;
    private final RemoteConfigService remoteConfigService;
    @Resource
    ActRuTaskMapper actRuTaskMapper;
    @Autowired
    private ManagementService managementService;
    private final BizQuotationApplyMapper bizQuotationApplyMapper;
    private final BizContractInfoMapper bizContractInfoMapper;
    private final ProcessService processService;
    private final NbcbSevice nbcbSevice;

    @Autowired
    public ActTaskController(BizBusinessMapper businessMapper,
                             ActTaskService actTaskService,
                             RemoteSocketService remoteSocketService,
                             MapInfoService mapInfoService,
                             MailService mailService,
                             IBizNodeService nodeService,
                             HistoryService historyService,
                             BizAssociateApplyMapper associateApplyMapper,
                             BizQuotationApplyMapper bizQuotationApplyMapper,
                             RemoteConfigService remoteConfigService,
                             BizContractInfoMapper bizContractInfoMapper,
                             ProcessService processService,
                             NbcbSevice nbcbSevice) {
        this.businessMapper = businessMapper;
        this.actTaskService = actTaskService;
        this.remoteSocketService = remoteSocketService;
        this.mapInfoService = mapInfoService;
        this.mailService = mailService;
        this.nodeService = nodeService;
        this.historyService = historyService;
        this.associateApplyMapper = associateApplyMapper;
        this.bizQuotationApplyMapper = bizQuotationApplyMapper;
        this.remoteConfigService = remoteConfigService;
        this.bizContractInfoMapper = bizContractInfoMapper;
        this.processService = processService;
        this.nbcbSevice = nbcbSevice;
    }

    @PostMapping("socket")
    public String test(@RequestBody MessageEntity message) {
        System.out.println(remoteSocketService.ing(message));
        return remoteSocketService.ing(message);
    }

    /**
     * task待办数量
     *
     * @return
     * @a /** 当前记录起始索引
     */
    @HasPermissions("settings:tasking:view")
    @RequestMapping(value = "ing_count", method = RequestMethod.GET)
    public R ingCount(RuTask ruTask) {
        try {
//            Long userId = getCurrentUserId();
//            TaskQuery query = taskService.createTaskQuery().taskCandidateOrAssigned(userId + "").orderByTaskCreateTime()
//                    .desc();
//            if (StrUtil.isNotBlank(ruTask.getProcessDefKey())) {
//                query = query.processDefinitionKey(ruTask.getProcessDefKey());
//            }
//            if (StrUtil.isNotBlank(ruTask.getProcessDefKeys())) {
//                query = query.processDefinitionKeyIn(Arrays.asList(ruTask.getProcessDefKeys().split(";")));
//            }
//            if(ruTask.getProcessDefKeys().contains("payment-")){
//                query = query.processDefinitionKeyLike("payment-");
//            }
//
//            long count = query.count();

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
            if (StrUtil.isNotBlank(ruTask.getProcessDefKeys())) {

                String replace = ruTask.getProcessDefKeys().replace(";", "','");
                whereSql.append("and ( b.proc_def_key in ('" + replace + "')  or b.proc_def_key like'" + "%payment-%" + "' ) ");
            }
            NativeTaskQuery query = taskService.createNativeTaskQuery()
                    .sql("select count(*) from " +
                            managementService.getTableName(Task.class) + " t " +
                            "left join " + managementService.getTableName(IdentityLinkEntity.class) + " ari on t.ID_ = ari.TASK_ID_ and t.ASSIGNEE_ is null " +
                            "left join biz_business b on b.proc_inst_id = t.PROC_INST_ID_ " +
                            "where ( ari.USER_ID_ = #{userId} or t.ASSIGNEE_ = #{userId}) " +
                            whereSql)
                    .parameter("userId", userId);

            long count = query.count();

            return R.data(count);
        } catch (Exception e) {
            logger.error("待办数量错误", e);
            return R.data(0);
        }

    }

    /**
     * task待办数量app
     *
     * @return
     * @a /** 当前记录起始索引
     */
    @HasPermissions("settings:tasking:view")
    @RequestMapping(value = "ing_count_app", method = RequestMethod.GET)
    public R ingCountApp() {
        try {
            Long userId = getCurrentUserId();
            TaskQuery query = taskService.createTaskQuery().taskCandidateOrAssigned(userId + "").orderByTaskCreateTime().desc();
            final long count = query.count();
            Map<String, Object> map = new HashMap<>(20);
            map.put("total", count);

            // 采购申请
            query = query.processDefinitionKey("purchase");
            final long purchaseCount = query.count();
            map.put("purchase", purchaseCount);

            // 报销申请
            query = query.processDefinitionKey("reimburse");
            final long reimburseCount = query.count();
            map.put("reimburse", reimburseCount);

            // 付款申请
            query = query.processDefinitionKey("payment");
            final long paymentCount = query.count();
            map.put("payment", paymentCount);

            // 领用申请
            query = query.processDefinitionKey("claim");
            final long claimCount = query.count();
            map.put("claim", claimCount);

            // 业务费
            query = query.processDefinitionKey("businessMoney");
            final long businessMoneyCount = query.count();
            map.put("businessMoney", businessMoneyCount);

            // 报废出库
            query = query.processDefinitionKey("scrapped");
            final long scrappedCount = query.count();
            map.put("scrapped", scrappedCount);

            // 评审费
            query = query.processDefinitionKey("review");
            final long reviewCount = query.count();
            map.put("review", reviewCount);

            // 通用审批
            query = query.processDefinitionKey("universal");
            final long universalCount = query.count();
            map.put("universal", universalCount);

            // 用印申请
            query = query.processDefinitionKey("seal");
            final long sealCount = query.count();
            map.put("seal", sealCount);

            // 应收合同审批
            query = query.processDefinitionKey("contract_ys");
            final long contract_ysCount = query.count();
            map.put("contract_ys", contract_ysCount);

            // 用车申请
            query = query.processDefinitionKey("carApply");
            final long carApplyCount = query.count();
            map.put("carApply", carApplyCount);

            // 还车补贴申请
            query = query.processDefinitionKey("carSubsidyApply");
            final long carSubsidyApplyCount = query.count();
            map.put("carSubsidyApply", carSubsidyApplyCount);

            // 录用审批
            query = query.processDefinitionKey("xz_approval");
            final long xz_approvalCount = query.count();
            map.put("xz_approval", xz_approvalCount);

            // 薪资调整
            query = query.processDefinitionKey("xz_adjustment");
            final long xz_adjustmentCount = query.count();
            map.put("xz_adjustment", xz_adjustmentCount);

            // 需求反馈
            query = query.processDefinitionKey("feedback");
            final long feedbackCount = query.count();
            map.put("feedback", feedbackCount);

            // 服务费申请
            query = query.processDefinitionKey("cover");
            final long coverCount = query.count();
            map.put("cover", coverCount);

            // 其他费用申请
            query = query.processDefinitionKey("other-charge");
            final long otherChargeCount = query.count();
            map.put("other-charge", otherChargeCount);

            // 耗材盘点
            query = query.processDefinitionKey("hcpd");
            final long hcpdCount = query.count();
            map.put("hcpd", hcpdCount);

            // 退货申请
            query = query.processDefinitionKey("goodsRejected");
            final long goodsRejectedCount = query.count();
            map.put("goodsRejected", goodsRejectedCount);

            // 付款申请（新）
            query = query.processDefinitionKeyLike("payment-");
            final long paymentNewCount = query.count();
            map.put("payment-", paymentNewCount);

            // 项目金额调整
            query = query.processDefinitionKey("pr_amount");
            final long prAmount = query.count();
            map.put("pr_amount", prAmount);

            // 投标
            query = query.processDefinitionKey("bid");
            final long bid = query.count();
            map.put("bid", bid);

            // 合同项目修改
            query = query.processDefinitionKey("contract-project");
            final long contractProjectCount = query.count();
            map.put("contract-project", contractProjectCount);

            return R.data(map);
        } catch (Exception e) {
            logger.error("待办数量错误", e);
            return R.data(0);
        }

    }

    /**
     * 抄送未读数量
     *
     * @return
     */
    @RequestMapping(value = "unread_cc_count", method = RequestMethod.GET)
    public R unReadCount() {
        try {
            return R.data(actTaskService.getUnReadCCCount(SystemUtil.getUserId()));
        } catch (Exception e) {
            logger.error("抄送未读数量", e);
            return R.data(0);
        }
    }

    /**
     * 抄送已读
     *
     * @return
     */
    @RequestMapping(value = "read_cc", method = RequestMethod.POST)
    public R readCc(String processInstanceId) {
        try {
            actTaskService.readCc(processInstanceId, SystemUtil.getUserId());
            return R.ok();
        } catch (Exception e) {
            logger.error("抄送已读失败", e);
            return R.error("抄送已读失败");
        }

    }

    /**
     * task待办
     *
     * @return
     * @a /** 当前记录起始索引
     */
    @HasPermissions("settings:tasking:view")
    @ApiOperation(value = "我的待办", notes = "我的待办")
    @ApiImplicitParams({@ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", dataType = "Integer", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", dataType = "Integer", required = true),
            @ApiImplicitParam(name = "sortField", value = "排序列"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向 \"desc\" 或者 \"asc\"")})
    @RequestMapping(value = "ing", method = RequestMethod.GET)
    public R ing(RuTask ruTask, PageDomain page, @RequestParam(required = false, defaultValue = "") String sortField, @RequestParam(required = false, defaultValue = "") String sortOrder) {
        page.setOrderByColumn(sortField);
        page.setIsAsc(sortOrder.equals("asc") ? "asc" : "desc");

        return R.ok(actTaskService.getIng(ruTask, page));
    }

    /**
     * task待办 修补关联
     *
     * @return
     * @a /** 当前记录起始索引
     */
    @HasPermissions("settings:tasking:view")
    @ApiOperation(value = "我的待办", notes = "我的待办")
    @ApiImplicitParams({@ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", dataType = "Integer", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", dataType = "Integer", required = true),
            @ApiImplicitParam(name = "sortField", value = "排序列"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向 \"desc\" 或者 \"asc\"")})
    @RequestMapping(value = "ing/relate", method = RequestMethod.GET)
    public R ingForRelation(RuTask ruTask, PageDomain page, @RequestParam(required = false, defaultValue = "") String sortField, @RequestParam(required = false, defaultValue = "") String sortOrder,
                            @RequestParam(required = false, defaultValue = "") Boolean hasPurchase) {
        page.setOrderByColumn(sortField);
        page.setIsAsc(sortOrder.equals("asc") ? "asc" : "desc");

        try {
            return R.ok(actTaskService.getIngForRelation(ruTask, page, hasPurchase));
        } catch (Exception e) {
            logger.error("报错", e);
            return R.error("报错");
        }
    }

    /**
     * task 已办
     *
     * @param hiTaskVo
     * @return
     * @author zmr
     */
    @RequestMapping(value = "done", method = RequestMethod.GET)
    public R done(HiTaskVo hiTaskVo) {
        startPage();
        hiTaskVo.setAuditorId(getCurrentUserId());
        hiTaskVo.setDeleteReason(ActivitiConstant.REASON_COMPLETED);
        Long userId = SystemUtil.getUserId();
        hiTaskVo.setCcId(userId.toString());
        List<HiTaskVo> historyTaskList = bizAuditService.getHistoryTaskList(hiTaskVo);

        return result(historyTaskList);
    }

    /**
     * task 已办(修复关联)
     *
     * @param hiTaskVo
     * @return
     * @author zmr
     */
    @RequestMapping(value = "done/relate", method = RequestMethod.GET)
    public R doneForRalte(HiTaskVo hiTaskVo) {
        startPage();
        hiTaskVo.setAuditorId(getCurrentUserId());
        hiTaskVo.setDeleteReason(ActivitiConstant.REASON_COMPLETED);
        Long userId = SystemUtil.getUserId();
        hiTaskVo.setCcId(userId.toString());
        List<HiTaskVo> historyTaskList = bizAuditService.getHistoryTaskListForRelate(hiTaskVo);

        try {
            return result(historyTaskList);
        } catch (Exception e) {
            logger.error("报错", e);
            return R.error("报错");
        }
    }


    /**
     * task 抄送我的
     *
     * @param hiTaskVo
     * @return
     * @author zmr
     */
    @RequestMapping(value = "done_cc", method = RequestMethod.GET)
    public R doneCc(HiTaskVo hiTaskVo, @RequestParam(required = false, defaultValue = "desc") String sortOrder) {
        startPage();
        List<HiTaskVo> historyTaskList = bizAuditService.getHistoryTaskListCc(hiTaskVo);

        return result(historyTaskList);
    }

    /**
     * task 抄送我的
     *
     * @param hiTaskVo
     * @return
     * @author zmr
     */
    @RequestMapping(value = "done_cc/relate", method = RequestMethod.GET)
    public R doneCcForRelate(HiTaskVo hiTaskVo, @RequestParam(required = false, defaultValue = "desc") String sortOrder) {
        startPage();
        List<HiTaskVo> historyTaskList = bizAuditService.getHistoryTaskListCcForRelate(hiTaskVo);
        try {
            return result(historyTaskList);
        } catch (Exception e) {
            logger.error("报错", e);
            return R.error("报错");
        }

    }


    /**
     * task 流转历史
     *
     * @param hiTaskVo
     * @return
     * @author zmr
     */
    @RequestMapping(value = "flow", method = RequestMethod.GET)
    public R flow(HiTaskVo hiTaskVo) {
        startPage();
        List<HiTaskVo> historyTaskList = bizAuditService.getHistoryTaskList(hiTaskVo);
        return result(historyTaskList);
    }

    @GetMapping("test")
    public int test(String nodeId, int userId, String key) {
//        TaskAndString purchase = findNextTask("purchase:7:1937552", "2165057", "2165015", "purchase");
//        System.out.println("；啦啦啦啦啦啦"+ purchase.getAns());
        Set<String> instanceUsers = new HashSet<>();
        instanceUsers = nodeService.getAuditors(nodeId, userId, key);
        System.out.println("候选size:{}" + instanceUsers.size());
        for (String instanceUser : instanceUsers) {
            System.out.println("幸运观众：" + instanceUser);
            System.out.println(remoteUserService.selectSysUserByUserId(Long.valueOf(instanceUser)).getUserName());
        }
        return instanceUsers.size();
    }

    @Resource
    BizBusinessTestMapper bizBusinessTestMapper;

    /**
     * 审批
     *
     * @param bizAudit
     * @return
     * @author zmr
     */
    @ApiOperation(value = "推进流程")
    @OperLog(title = "审批", businessType = BusinessType.UPDATE)
    @PostMapping(value = "audit")
    @Transactional(rollbackFor = Exception.class)
    @RetryJpaOptimisticLock(times = 5)
    public R audit(@RequestBody BizAudit bizAudit) {
        if (false) {
            Map<String, Object> variables = Maps.newHashMap();
            variables.put("result", bizAudit.getResult());
            // 审批
            ArrayList<String> objects = new ArrayList<>();
            objects.add("1");
            variables.put("assigneeList", objects);
            taskService.complete(bizAudit.getTaskId(), variables);
            SysUser user = remoteUserService.selectSysUserByUserId(getCurrentUserId());
            bizAudit.setAuditor(user.getUserName() + "-" + user.getLoginName())
                    .setAuditorId(user.getUserId());
            bizAuditService.insertBizAudit(bizAudit);
            BizBusiness bizBusiness = new BizBusiness()
                    .setId(bizAudit.getBusinessKey())
                    .setProcInstId(bizAudit.getProcInstId());
            businessService.setAuditor(bizBusiness, bizAudit.getResult(), getCurrentUserId());
            // 若是驳回则结束本流程，清除会签其他任务
            if (bizAudit.getResult() == 3) {
                // 驳回审批 修改mongodb 审批 state result
                String tableId = bizBusiness.getTableId();
                Query query = new Query();
                query.addCriteria(Criteria.where("_id").is(tableId));
                HashMap payment = mongoTemplate.findOne(query, HashMap.class, "payment");
                if (payment != null) {
                    Update update = new Update();
                    update.set("result", 3);
                    update.set("state", 4);
                    update.set("updateTime", LocalDateTime.now());
                    mongoTemplate.upsert(query, update, "payment");
                }
                // 如果不是会签触发
                runtimeService.deleteProcessInstance(bizAudit.getProcInstId(), "驳回清除会签其他任务");
            }
            return R.ok();
        }
        try {
            BizBusiness bizBusiness = businessService.selectBizBusinessById2(String.valueOf(bizAudit.getBusinessKey()));
            // 采购关联付款报销
            String procDefKey = bizAudit.getProcDefKey();
            String taskId = bizAudit.getTaskId();
            boolean flag_payment = false;
            ActRuTask actRuTask = null;
            if (taskId != null) {
                actRuTask = actRuTaskMapper.selectByPrimaryKey1(taskId);
                if (actRuTask != null && actRuTask.getName().equals("付款确认人2")) {
                    flag_payment = true;
                }
            }
            if (procDefKey.contains("payment-") && flag_payment) {
                String tableId = bizBusiness.getTableId();
                Query query = new Query();
                query.addCriteria(Criteria.where("_id").is(tableId));
                HashMap payment = mongoTemplate.findOne(query, HashMap.class, "payment");
                HashMap baseInformation = (HashMap) payment.get("BaseInformation");
                List<HashMap> purchaseData = (List<HashMap>) baseInformation.get("purchaseData");
                for (HashMap purchaseDatum : purchaseData) {
                    Long id = Long.parseLong(purchaseDatum.get("id").toString());
                    associateApplyMapper.updateFinanceStatusPaymentMongo(id, 3, SystemUtil.getUserNameCn(), new Date());
                }
            }
            System.out.println(procDefKey + "****" + bizAudit.getTaskDefKey());
            if ("payment".equals(procDefKey) && "cashier".equals(bizAudit.getTaskDefKey())) {
                associateApplyMapper.updateFinanceStatusPayment(1, Long.valueOf(bizBusiness.getTableId()), 3, SystemUtil.getUserNameCn(), new Date());
            }
            if ("reimburse".equals(procDefKey) && "cashier_bx".equals(bizAudit.getTaskDefKey())) {
                associateApplyMapper.updateFinanceStatusReimburse(2, Long.valueOf(bizBusiness.getTableId()), 4);
            }
            if (4 == bizBusiness.getResult()) {
                return R.error("该申请已撤销，请刷新列表");
            }
            long currentUserId = getCurrentUserId();
            // 将自己从抄送人中去除
            String procInstId = bizAudit.getProcInstId();
            businessMapper.deleteCC(String.valueOf(currentUserId), procInstId);
            Map<String, Object> variables = Maps.newHashMap();
            variables.put("result", bizAudit.getResult());
            // 审批
            SysUser user = remoteUserService.selectSysUserByUserId(getCurrentUserId());
            SysUser user2 = remoteUserService.selectSysUserByUserId(bizBusiness.getUserId());
            System.err.println("currentUserId:" + currentUserId + "---UserName:" + user.getUserName() + "---审批：" + bizAudit.getResult());
            variables.put("operator", user.getUserName());
            variables.put("currentUserId", user.getUserId());
            //TODO 评审费暂留代码
            if (bizAudit.getProcDefKey() != null && bizAudit.getTaskDefKey() != null) {
                if ("review".equals(bizAudit.getProcDefKey()) && "leader2".equals(bizAudit.getTaskDefKey())) {
                    // 设置流程变量（评审费）
                    int isCancel = bizAudit.getIsCancel();
                    variables.put("isCancel", isCancel);
                }
            }
            bizBusiness.setProcInstId(bizAudit.getProcInstId());
            // 获取候选人，给会签节点预备实例列表
            Set<String> instanceUsers = new HashSet<>();
            TaskAndString nextTask = null;
            StringBuilder sb = null;
            boolean processFlag = false;
            String paymentid = null;
            try {
                // 插入会签人员
                nextTask = findNextTask(bizBusiness.getProcDefId(), bizAudit.getTaskId(), procInstId, bizAudit.getProcDefKey());
                Task task = nextTask.getTask();
                logger.error("task----{}user---{}key---{}", nextTask, bizBusiness.getUserId(), bizBusiness.getProcDefKey());
                instanceUsers = nodeService.getAuditors(nextTask.getAns(), bizBusiness.getUserId(), bizBusiness.getProcDefKey());
                System.out.println(nextTask + "---" + bizBusiness.getUserId() + "候选size:{}" + instanceUsers.size());
                String ans = nextTask.getAns();

                if (instanceUsers != null && instanceUsers.size() > 0 && bizAudit.getResult() == 2) {
                    for (String instanceUser : instanceUsers) {
                        System.out.println("幸运观众：" + instanceUser);
                    }
                    variables.put("assigneeList", new ArrayList<>(instanceUsers));
                }
                if (bizBusiness.getProcDefKey().equals("payment-fznbzzdb") && ans.equals("sid-D70B6B90-1424-48C9-92AA-B516AAF1398A")) {
                    // 如果是这个流程且是改节点的话就塞进去人
                    // 拿到变量
                    paymentid = (String) taskService.getVariable(task.getId(), "id");
                    Query query = new Query();
                    query.addCriteria(Criteria.where("_id").is(paymentid));
                    HashMap template = mongoTemplate.findOne(query, HashMap.class, "payment");
                    List<Integer> feeArray = (List<Integer>) template.get("feeArray");
                    Set<String> set = new HashSet<>();
                    sb = new StringBuilder();
                    for (Integer ids : feeArray) {
                        List<Integer> userRole = bizBusinessTestMapper.getUserRole(ids);
                        for (Integer roleId : userRole) {
                            set.add(roleId.toString());
                            String userName = bizBusinessTestMapper.getUserName(roleId);
                            sb.append(userName);
                            sb.append("/");
                        }
                    }
                    // 更新总表
                    sb.deleteCharAt(sb.length() - 1);
                    variables.put("assigneeList", new ArrayList<>(set));
                    processFlag = true;
                } else if (ans.equals("payment-jttxnzjdb") && task.getTaskDefinitionKey().equals("sid-736F5D7A-E811-4156-AABC-DBA501E340BB")) {

                    paymentid = (String) taskService.getVariable(task.getId(), "id");
                    Query query = new Query();
                    query.addCriteria(Criteria.where("_id").is(paymentid));
                    HashMap template = mongoTemplate.findOne(query, HashMap.class, "payment");
                    List<Integer> feeArray = (List<Integer>) template.get("feeArray");
                    Set<String> set = new HashSet<>();
                    sb = new StringBuilder();
                    for (Integer ids : feeArray) {
                        List<Integer> userRole = bizBusinessTestMapper.getUserRole(ids);
                        for (Integer roleId : userRole) {
                            set.add(roleId.toString());
                            String userName = bizBusinessTestMapper.getUserName(roleId);
                            sb.append(userName);
                            sb.append("/");
                        }
                    }
                    // 更新总表
                    sb.deleteCharAt(sb.length() - 1);
                    variables.put("assigneeList", new ArrayList<>(set));
                    processFlag = true;
                }
            } catch (StatefulException e) {
                if (e.getStatus() == 292) {
                    throw new StatefulException(292, "无审批人");
                }
            } catch (Exception e) {
                logger.error("获取会签人员失败", e);
            }
            taskService.complete(bizAudit.getTaskId(), variables);
            // 若是驳回则结束本流程，清除会签其他任务
            //todo
            if (bizAudit.getResult() == 3) {
                // 同步审批状态 驳回status = 1
                processService.syncAuditStatus(bizAudit.getProcDefKey(), 1, bizBusiness.getTableId());
                // 驳回审批 修改mongodb 审批 state result
                String tableId = bizBusiness.getTableId();
                Query query = new Query();
                query.addCriteria(Criteria.where("_id").is(tableId));
                HashMap payment = mongoTemplate.findOne(query, HashMap.class, "payment");
                if (payment != null) {
                    Update update = new Update();
                    update.set("result", 3);
                    update.set("state", 4);
                    update.set("updateTime", LocalDateTime.now());
                    mongoTemplate.upsert(query, update, "payment");
                    backMoney(payment);
                }
                List<Task> tasks = taskService.createTaskQuery().processInstanceId(bizBusiness.getProcInstId()).list();

                if (tasks.size() == 0) {
                    // 如果是会签触发
                    if (bizBusinessTest.isHuiSign(bizBusiness.getProcDefId(), nextTask.getTask())) {
                        runtimeService.deleteProcessInstance(procInstId, "驳回清除会签其他任务");
                    }
                } else {
                    Task task = tasks.get(0);
                    // 如果是会签触发
                    if (bizBusinessTest.isHuiSign(bizBusiness.getProcDefId(), nextTask.getTask()) ||
                            bizBusinessTest.isHuiSign(bizBusiness.getProcDefId(), task)) {
                        runtimeService.deleteProcessInstance(procInstId, "驳回清除会签其他任务");
                    }
                }
            }
            bizAudit.setAuditor(user.getUserName() + "-" + user.getLoginName());
            bizAudit.setAuditorId(user.getUserId());
            bizAuditService.insertBizAudit(bizAudit);
            bizBusiness.setLastAuditorTime(new Date());
            bizBusiness.setLastAuditor(user.getUserName());
            bizBusiness.setCustomizedUserId(bizAudit.getCustomizedUserId());
            bizBusiness.setTaskDefKey(bizAudit.getTaskDefKey());
            //todo
            Set<String> strings = businessService.setAuditor(bizBusiness, bizAudit.getResult(), bizBusiness.getUserId());
            // 资金调拨的两个流程的总表名字再这里强制修改
            if (sb != null && processFlag) {
                bizBusinessTestMapper.updateAuditors(sb.toString(), paymentid);
            }
            // 发送通知给发起人
            String result = bizAudit.getResult() == 2 ? "<strong style='color:#52C41A;'>通过</strong>" : "<strong style='color:#FF4D4F;'>驳回</strong>";
            String mapInfoMail = mapInfoService.getMapInfoMail(bizBusiness);
            String txt = "<body>" +
                    "<p>" +
                    "您的申请在" + DateUtil.format(new Date(), "yyyy年MM月dd日 HH时mm分") + "被<strong style='color:#2d8ccc;'>" + user.getUserName() + "</strong>审批" + result + "了。" +
                    "</p>" +
                    "<p>" +
                    "<strong>审批意见：<strong>" + bizAudit.getComment() +
                    "</p><br>" + mapInfoMail + "<br><h4 style='color:#aaaaaa;'><strong>提示：更多数据，请前往云管家平台查看</strong></h4></body>";
            if (StrUtil.isNotBlank(user2.getEmail())) {
                mailService.send(txt, bizBusiness.getTitle(), user2.getEmail(), user.getUserName(), user2.getCid());
            }
            // 若下个节点的人还是自己，则自动通过审批,且不等于节点leader2
            boolean flag = true;
            if (bizAudit.getTaskDefKey() != null && "leader2".equals(bizAudit.getTaskDefKey())) {
                flag = false;
            }
            // 若已经指定下个节点审批人，则不自动通过
            if (bizAudit.getCustomizedUserId() != null || nextTask.getAns().equals("rd_principal") || nextTask.getAns().equals("spr3_ty") || nextTask.getAns().equals("spr3_tb")) {
                flag = false;
            }
            // 若下个节点人员未配置，则自动审批
            if (strings != null && !strings.isEmpty() && strings.contains("1") && bizAudit.getResult() == 2) {
                NoConfigAudit(bizAudit);
                return R.ok("ok");
            }

            if (bizAudit.getResult() == 2 && strings.size() == 1 && strings.contains(String.valueOf(currentUserId)) && flag) {
                TaskQuery query = taskService.createTaskQuery().taskCandidateOrAssigned(currentUserId + "").orderByTaskCreateTime()
                        .desc();
                if (StrUtil.isNotBlank(bizAudit.getProcDefKey())) {
                    query.processDefinitionKey(bizAudit.getProcDefKey());
                }
                if (StrUtil.isNotBlank(procInstId)) {
                    query.processInstanceId(procInstId);
                }
                List<Task> taskList = query.list();
                if (taskList.size() == 1) {
                    bizAudit.setId(null);
                    bizAudit.setTaskId(taskList.get(0).getId());
                    audit(bizAudit);
                }
            } else {
                if (bizAudit.getResult() != 2) {
                    return R.ok("ok");
                }
                // 通知待审批人
                for (String string : strings) {
                    if (string.equals(String.valueOf(currentUserId))) {
                        continue;
                    }
                    SysUser sysUser = remoteUserService.selectSysUserByUserId(Long.valueOf(string));
                    String txt2 = "<body>" +
                            "<p>" +
                            "您有一条新的待办事项，请及时处理。" +
                            "</p>" +
                            "<br>" + mapInfoMail + "<br><h4 style='color:#aaaaaa;'><strong>提示：更多数据，请前往云管家平台查看</strong></h4></body>";
                    if (StrUtil.isNotBlank(sysUser.getEmail())) {
                        mailService.send(txt2, bizBusiness.getTitle(), sysUser.getEmail(), user.getUserName(), sysUser.getCid());
                    }
                }
            }
            // 调用宁波银行接口提交付款单
            if (procDefKey.contains("payment-") && bizAudit.getResult() == 2 && "付款确认人1".equals(nextTask.getNextNodeName())) {
                nbcbSevice.paymentOrderApiAdd(bizBusiness);
            }
            return R.ok("ok");
        } catch (StatefulException e) {
            if (e.getStatus() == 292) {
                logger.error(e.getMessage(), e);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return R.error("下个节点无审批人，请联系管理员");
            } else {
                throw e;
            }
        } catch (Exception e) {
            logger.error("审批失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return R.error("审批失败");
        }
    }

    /**
     * 未配置人员自动审批
     *
     * @param bizAudit
     */
    private void NoConfigAudit(BizAudit bizAudit) {
        bizAudit.setResult(2);
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
//        // 采购关联付款报销
//        String procDefKey = bizAudit.getProcDefKey();
//        String taskId = bizAudit.getTaskId();
//        boolean flag_payment = false;
//        if (taskId != null) {
//            ActRuTask actRuTask = actRuTaskMapper.selectByPrimaryKey1(taskId);
//            if (actRuTask.getName().equals("付款确认人2")) {
//                flag_payment = true;
//            }
//        }
//        if (procDefKey.contains("payment-") && flag_payment) {
//            String tableId = bizBusiness.getTableId();
//            Query query = new Query();
//            query.addCriteria(Criteria.where("_id").is(tableId));
//            HashMap payment = mongoTemplatel.findOne(query, HashMap.class, "payment");
//            HashMap baseInformation = (HashMap) payment.get("BaseInformation");
//            List<HashMap> purchaseData = (List<HashMap>) baseInformation.get("purchaseData");
//            for (HashMap purchaseDatum : purchaseData) {
//                Long id = Long.parseLong(purchaseDatum.get("id").toString());
//                associateApplyMapper.updateFinanceStatusPaymentMongo(id,3,SystemUtil.getUserNameCn(),new Date());
//            }
//
//        }
//        System.out.println(procDefKey + "****" + bizAudit.getTaskDefKey());
//        if ("payment".equals(procDefKey) && "cashier".equals(bizAudit.getTaskDefKey())) {
//            associateApplyMapper.updateFinanceStatusPayment(1, Long.valueOf(bizBusiness.getTableId()), 3, SystemUtil.getUserNameCn(), new Date());
//        }
//        if ("reimburse".equals(procDefKey) && "cashier_bx".equals(bizAudit.getTaskDefKey())) {
//            associateApplyMapper.updateFinanceStatusReimburse(2, Long.valueOf(bizBusiness.getTableId()), 4);
//        }

        // 将自己从抄送人中去除
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("result", bizAudit.getResult());
        // 审批
        variables.put("operator", "未配置");
        variables.put("currentUserId", 1L);
        //TODO 评审费暂留代码
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
        TaskAndString nextTask;
        try {
            // 插入会签人员
            nextTask = findNextTask(bizBusiness.getProcDefId(), bizAudit.getTaskId(), procInstId, bizAudit.getProcDefKey());
            logger.error("task----{}user---{}key---{}", nextTask, bizBusiness.getUserId(), bizBusiness.getProcDefKey());
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
            logger.error("获取会签人员失败", e);
        }
        taskService.complete(bizAudit.getTaskId(), variables);

        bizAudit.setAuditor("未配置");
        bizAudit.setAuditorId(1L);
        bizAuditService.insertBizAudit(bizAudit);
        bizBusiness.setLastAuditorTime(new Date());
        bizBusiness.setLastAuditor("未配置");
        bizBusiness.setCustomizedUserId(bizAudit.getCustomizedUserId());
        bizBusiness.setTaskDefKey(bizAudit.getTaskDefKey());
        Set<String> strings = businessService.setAuditor(bizBusiness, bizAudit.getResult(), bizBusiness.getUserId());
        if (CollUtil.isNotEmpty(strings) && strings.contains("1")) {
            NoConfigAudit(bizAudit);
        }
    }

    private void backMoney(HashMap payment) {
        // 判断是几几年 几月
        // 判断一下模板的状态
        int year = LocalDateTime.now().getYear();
        int monthValue = LocalDateTime.now().getMonthValue();
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
                taskAndString.setNextNodeName(node.getName());
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
            logger.error("查询流程当前节点的下一步节点失败", e);
            throw new StatefulException("Cannot find task with id " + taskId);
        }

    }

    /**
     * 查询流程当前节点的下一步节点。用于流程提示。
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
                    if (sourceFlowElement instanceof org.activiti.bpmn.model.UserTask) {
                        node = sourceFlowElement;
                        break;
                    }else if (sourceFlowElement instanceof org.activiti.bpmn.model.ExclusiveGateway) {
                        node = iteratorNextNodes(process, sourceFlowElement, listVar);
                    }
//                    node = sourceFlowElement;
//                    break;
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


    @PostMapping("audit/batch")
    @OperLog(title = "批量审批", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "批量推进流程")
    public R auditBatch(@RequestBody BizAudit bizAudit) {
        SysUser user = remoteUserService.selectSysUserByUserId(getCurrentUserId());
        for (String taskId : bizAudit.getTaskIds()) {
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            ProcessInstance pi = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId()).singleResult();
            BizBusiness bizBusiness = businessService.selectBizBusinessById2(pi.getBusinessKey());
            if (null != bizBusiness) {
                Map<String, Object> variables = Maps.newHashMap();
                variables.put("result", bizAudit.getResult());
                // 审批
                taskService.complete(taskId, variables);
                // 构建插入审批记录
                BizAudit audit = new BizAudit().setTaskId(taskId).setResult(bizAudit.getResult())
                        .setProcName(bizBusiness.getProcName()).setProcDefKey(bizBusiness.getProcDefKey())
                        .setApplyer(bizBusiness.getApplyer()).setAuditor(user.getUserName() + "-" + user.getLoginName())
                        .setAuditorId(user.getUserId());
                bizAuditService.insertBizAudit(audit);
                businessService.setAuditor(bizBusiness, audit.getResult(), bizBusiness.getUserId());
            }
        }
        return R.ok("ok");
    }

    /**
     * remove审批记录 逻辑删除
     */
    @OperLog(title = "审批记录 逻辑删除", businessType = BusinessType.DELETE)
    @PostMapping("remove")
    public R remove(String ids) {
        return toAjax(bizAuditService.deleteBizAuditLogic(ids));
    }

    /**
     * 转交
     *
     * @param reassignmentDto 转交参数
     */
    @OperLog(title = "转交", businessType = BusinessType.UPDATE)
    @PostMapping("reassignment")
    public R reassignment(@RequestBody ReassignmentDto reassignmentDto) {
        try {
            bizAuditService.reassignment(reassignmentDto);
            return R.ok("转交成功");
        } catch (Exception e) {
            logger.error("转交失败", e);
            return R.error("转交失败");
        }
    }


}
