package com.ruoyi.activiti.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.activiti.consts.ActivitiConstant;
import com.ruoyi.activiti.domain.car.BizReserveDetail;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.domain.purchase.BizGoodsInfo;
import com.ruoyi.activiti.feign.MessageEntity;
import com.ruoyi.activiti.feign.RemoteSocketService;
import com.ruoyi.activiti.mapper.*;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.service.IHistoryInfoService;
import com.ruoyi.activiti.service.ProcessService;
import com.ruoyi.activiti.vo.HiProcInsVo;
import com.ruoyi.activiti.vo.ProcessInsVo;
import com.ruoyi.activiti.vo.RuTask;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.system.feign.RemoteConfigService;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricIdentityLink;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>File：ProcessInsController.java</p>
 * <p>Title: 流程实例</p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2020 2020年1月6日 下午1:27:18</p>
 * <p>Company: zmrit.com </p>
 *
 * @author zmr
 * @version 1.0
 * @menu 流程实例
 */
@Slf4j
@RestController
@RequestMapping("process/ins/")
public class ProcessInsController extends BaseController {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private IHistoryInfoService historyInfoService;
    @Autowired
    private RemoteUserService userService;
    @Autowired
    private IBizBusinessService bizBusinessService;
    @Resource
    MongoTemplate mongoTemplatel;
    @Resource
    BizGoodsInfoMapper bizGoodsInfoMapper;
    @Autowired
    private RemoteSocketService remoteSocketService;
    private final BizReserveDetailMapper reserveDetailMapper;
    private final BizAssociateGoodMapper associateGoodMapper;
    private final RemoteConfigService remoteConfigService;
    private final BizQuotationApplyMapper bizQuotationApplyMapper;
    private final BizContractInfoMapper bizContractInfoMapper;
    private final ProcessService processService;

    @Autowired
    public ProcessInsController(BizReserveDetailMapper reserveDetailMapper,
                                BizAssociateGoodMapper associateGoodMapper,
                                RemoteConfigService remoteConfigService,
                                BizQuotationApplyMapper bizQuotationApplyMapper,
                                BizContractInfoMapper bizContractInfoMapper,
                                ProcessService processService) {
        this.reserveDetailMapper = reserveDetailMapper;
        this.associateGoodMapper = associateGoodMapper;
        this.remoteConfigService = remoteConfigService;
        this.bizQuotationApplyMapper = bizQuotationApplyMapper;
        this.bizContractInfoMapper = bizContractInfoMapper;
        this.processService = processService;
    }

    /**
     * 挂起、激活流程实例
     */
    @OperLog(title = "挂起激活流程实例", businessType = BusinessType.UPDATE)
    @RequestMapping(value = "update/{processInstanceId}/{state}")
    public R updateState(@PathVariable("state") String state,
                         @PathVariable("processInstanceId") String processInstanceId) {
        if (state.equals("active")) {
            runtimeService.activateProcessInstanceById(processInstanceId);
            log.info("已激活ID为:{}的流程实例", processInstanceId);
        } else if (state.equals("suspend")) {
            runtimeService.suspendProcessInstanceById(processInstanceId);
            log.info("已挂起ID为:{}的流程实例", processInstanceId);
        }
        return R.ok();
    }

    /**
     * 获取任务列表
     *
     * @param page
     * @return
     */
    @RequestMapping(value = "tasks")
    public R tasks(PageDomain page) {
        List<Task> tasks = taskService.createTaskQuery().listPage(page.getPageSize() * (page.getPageNum() - 1),
                page.getPageSize());
        long count = runtimeService.createExecutionQuery().count();
        List<RuTask> list = new ArrayList<>();
        for (Task task : tasks) {
            list.add(new RuTask(task));
        }
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("rows", list);
        m.put("pageNum", page.getPageNum());
        m.put("total", count);
        return R.ok(m);
    }

    @RequestMapping(value = "runs")
    public R getList(PageDomain page, String name, String key) {
        List<ProcessInsVo> list = new ArrayList<>();
        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery().orderByProcessInstanceId().desc();
        if (StrUtil.isNotBlank(name)) {
            query.processInstanceNameLike("%" + name + "%");
        }
        if (StrUtil.isNotBlank(key)) {
            query.processDefinitionKey(key);
        }
        long count = query.count();
        int first = (page.getPageNum() - 1) * page.getPageSize();
        List<ProcessInstance> processInstanceList = query.listPage(first, page.getPageSize());
        processInstanceList.forEach(e -> {
            list.add(new ProcessInsVo(e));
        });
        list.forEach(e -> {
            List<HistoricIdentityLink> identityLinks = historyService
                    .getHistoricIdentityLinksForProcessInstance(e.getId());
            for (HistoricIdentityLink hik : identityLinks) {
                // 关联发起人
                if ("starter".equals(hik.getType()) && StrUtil.isNotBlank(hik.getUserId())) {
                    e.setApplyer(userService.selectSysUserByUserId(Long.valueOf(hik.getUserId())).getLoginName());
                }
            }
            // 关联当前任务
            Task task = taskService.createTaskQuery().processInstanceId(e.getId()).singleResult();
            if (task != null) {
                e.setCurrTaskName(task.getName());
            }
        });
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("rows", list);
        m.put("pageNum", page.getPageNum());
        m.put("total", count);
        return R.ok(m);
    }

    @RequestMapping(value = "finished")
    public R finush(HiProcInsVo hiProcInsVo) {
        startPage();
        return result(historyInfoService.getHiProcInsListDone(hiProcInsVo));
    }

    @Resource
    MongoTemplate mongoTemplate;

    /**
     * 删除运行中实例
     *
     * @param ids
     * @param reason
     * @return
     * @author zmr
     */
    @PostMapping(value = "remove")
    @OperLog(title = "流程撤销", businessType = BusinessType.DELETE)
    public R remove(String ids, String reason) {
        try {
            if (StrUtil.isBlank(reason)) {
                reason = "";
            }
            String[] idArr = ids.split(",");
            for (String id : idArr) {
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
                business.getAuditors();
                changeCarStatus(business);
                String tableId = business.getTableId();
                // mongodb 里查询实例
                Query query = new Query();
                query.addCriteria(Criteria.where("_id").is(tableId));
                HashMap payment = mongoTemplatel.findOne(query, HashMap.class, "payment");
                if (tableId.length() < 20) {
                    changeGoodFinanceStatus(business);
                } else {
                    if (payment != null) {
                        removeGoodStatus(payment);
                    }
                }
                // 修改mongodb 状态
                if (payment != null) {
                    backMoney(payment);
                    Update update = new Update();
                    update.set("result", ActivitiConstant.RESULT_CANCELED);
                    update.set("state", ActivitiConstant.STATUS_CANCELED);
                    mongoTemplatel.upsert(query, update, "payment");
                }

                // 同步审批状态 撤回 status=3
                processService.syncAuditStatus(pi.getProcessDefinitionKey(),3,tableId);

//                // 修改量远报价审批状态
//                String processDefinitionKey = pi.getProcessDefinitionKey();
//                SysConfig sysConfig = remoteConfigService.findConfigUrl();
//                if ("quotation".equals(processDefinitionKey)) {
//                    BizQuotationApply bizQuotationApply = bizQuotationApplyMapper.selectById(bizBusiness.getTableId());
//                    Map<String, Object> paramMap = Maps.newHashMap();
//                    paramMap.put("code", bizQuotationApply.getCode());
//                    // 撤回
//                    paramMap.put("status", 6);
//                    if ("test".equals(sysConfig.getConfigValue())) {
//                        HttpRequest.put(UrlConstants.LY_QUOTATION_TEST).body(JSON.toJSONString(paramMap)).execute().body();
//                    } else {
//                        HttpRequest.put(UrlConstants.LY_QUOTATION_ONLINE).body(JSON.toJSONString(paramMap)).execute().body();
//                    }
//                } else if ("contract-review".equals(processDefinitionKey)) {
//                    // 量远合同审批
//                    BizContractInfo bizContractInfo = bizContractInfoMapper.selectById(bizBusiness.getTableId());
//                    Map<String, Object> paramMap = Maps.newHashMap();
//                    paramMap.put("id", bizContractInfo.getContractId());
//                    // 撤回
//                    paramMap.put("reviewStatus", 5);
//                    if ("test".equals(sysConfig.getConfigValue())) {
//                        HttpRequest.put(UrlConstants.LY_CONTRACT_TEST).body(JSON.toJSONString(paramMap)).execute().body();
//                    } else {
//                        HttpRequest.put(UrlConstants.LY_CONTRACT_ONLINE).body(JSON.toJSONString(paramMap)).execute().body();
//                    }
//                }
            }
            return R.ok("ok");
        } catch (Exception e) {
            logger.error("删除运行中的实例", e);
            return R.error("操作失败");
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
     * 删除已经结束的实例
     *
     * @param ids
     * @return
     * @author zmr
     */
    @OperLog(title = "删除已结束的实例", businessType = BusinessType.DELETE)
    @RequestMapping("remove/his")
    public R removeHis(String ids) {
        String[] idArr = ids.split(",");
        for (String id : idArr) {
            historyService.deleteHistoricProcessInstance(id);
        }
        return R.ok();
    }
}
