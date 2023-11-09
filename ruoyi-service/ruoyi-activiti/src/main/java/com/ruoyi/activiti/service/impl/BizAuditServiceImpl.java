package com.ruoyi.activiti.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.page.PageMethod;
import com.ruoyi.activiti.domain.BizReassignmentRecord;
import com.ruoyi.activiti.domain.dto.ReassignmentDto;
import com.ruoyi.activiti.domain.proc.ActReProcdef;
import com.ruoyi.activiti.domain.proc.BizAudit;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.feign.MessageEntity;
import com.ruoyi.activiti.feign.RemoteSocketService;
import com.ruoyi.activiti.mapper.BizAuditMapper;
import com.ruoyi.activiti.mapper.BizBusinessMapper;
import com.ruoyi.activiti.mapper.BizReassignmentRecordMapper;
import com.ruoyi.activiti.service.IActReProcdefService;
import com.ruoyi.activiti.service.IBizAuditService;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.service.MapInfoService;
import com.ruoyi.activiti.utils.MailService;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.activiti.vo.HiTaskVo;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteUserService;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * <p>File：BizAuditServiceImpl.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2020 2020年1月6日 下午3:38:49</p>
 * <p>Company: zmrit.com </p>
 *
 * @author zmr
 * @version 1.0
 */
@Service
public class BizAuditServiceImpl implements IBizAuditService {
    @Autowired
    private BizAuditMapper auditMapper;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private IActReProcdefService procdefService;
    private final RemoteUserService remoteUserService;
    private final IBizBusinessService bizBusinessService;
    private final BizReassignmentRecordMapper reassignmentRecordMapper;
    private final RemoteSocketService remoteSocketService;
    private final MapInfoService mapInfoService;
    private final MailService mailService;
    private final BizBusinessMapper businessMapper;

    @Autowired
    public BizAuditServiceImpl(RemoteUserService remoteUserService, IBizBusinessService bizBusinessService, BizReassignmentRecordMapper reassignmentRecordMapper, RemoteSocketService remoteSocketService, MapInfoService mapInfoService, MailService mailService, BizBusinessMapper businessMapper) {
        this.remoteUserService = remoteUserService;
        this.bizBusinessService = bizBusinessService;
        this.reassignmentRecordMapper = reassignmentRecordMapper;
        this.remoteSocketService = remoteSocketService;
        this.mapInfoService = mapInfoService;
        this.mailService = mailService;
        this.businessMapper = businessMapper;
    }

    /**
     * 查询审核记录
     *
     * @param id 审核记录ID
     * @return 审核记录
     */
    @Override
    public BizAudit selectBizAuditById(String id) {
        return auditMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询审核记录列表
     *
     * @param bizAudit 审核记录
     * @return 审核记录
     */
    @Override
    public List<BizAudit> selectBizAuditList(BizAudit bizAudit) {
        return auditMapper.select(bizAudit);
    }

    /**
     * 新增审核记录
     *
     * @param bizAudit 审核记录
     * @return 结果
     */
    @Override
    public int insertBizAudit(BizAudit bizAudit) {
        return auditMapper.insertSelective(bizAudit);
    }

    /**
     * 修改审核记录
     *
     * @param bizAudit 审核记录
     * @return 结果
     */
    @Override
    public int updateBizAudit(BizAudit bizAudit) {
        return auditMapper.updateByPrimaryKeySelective(bizAudit);
    }

    /**
     * 删除审核记录对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteBizAuditByIds(String ids) {
        return auditMapper.deleteByIds(ids);
    }

    /**
     * 删除审核记录对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteBizAuditLogic(String ids) {
        String[] idArr = ids.split(",");
        return auditMapper.deleteLogic(idArr);
    }

    /**
     * 删除审核记录信息
     *
     * @param id 审核记录ID
     * @return 结果
     */
    @Override
    public int deleteBizAuditById(Long id) {
        return auditMapper.deleteByPrimaryKey(id);
    }


    @Override
    public List<HiTaskVo> getHistoryTaskList(HiTaskVo hiTaskVo) {
        List<HiTaskVo> historyTaskList = new ArrayList<>();
        if (hiTaskVo.getCcId() == null) {
            // 历史
            historyTaskList = auditMapper.getHistoryTaskList2(hiTaskVo);
//            addMapInfo(historyTaskList,"2");
        } else {
            // 已办
            if (StrUtil.isNotBlank(hiTaskVo.getGoodsName())) {
                Page<Object> page = PageHelper.getLocalPage();
                PageMethod.clearPage();
                List<String> strings = businessMapper.selectInstIdByGoodName(hiTaskVo.getGoodsName());
                PageHelper.startPage(page.getPageNum(), page.getPageSize(), page.getOrderBy());
                if (strings.isEmpty()) {
                    strings.add("0000000");
                }
                hiTaskVo.setProcInstIdsStr(String.join(",", strings));
            }
            historyTaskList = auditMapper.getHistoryTaskList(hiTaskVo);
            addMapInfo(historyTaskList, "1");
            historyTaskList.stream().forEach(h -> {

                BizBusiness bizBusiness = bizBusinessService.selectBizBusinessById(h.getBusinessKey());
                h.setAuditor(bizBusiness.getLastAuditor() == null ? "" : bizBusiness.getLastAuditor());
                h.setEndTime(bizBusiness.getLastAuditorTime());
                h.setTableId(bizBusiness.getTableId());
                if (h.getProcDefKey().startsWith("payment-")) {
                    String tableId = bizBusiness.getTableId();
                    Query query1 = new Query();
                    query1.addCriteria(Criteria.where("_id").is(tableId));
                    HashMap payment = mongoTemplate.findOne(query1, HashMap.class, "payment");
                    if (Objects.nonNull(payment)) {
                        Object projectMoney = payment.get("projectMoney");
                        h.setMoney(projectMoney.toString());
                    }
                } else {
                    h.setMoney(null);
                }

            });
//            historyTaskList = historyTaskList.stream().distinct().collect(Collectors.toList());
//            historyTaskList =  historyTaskList.stream().filter(distinctByKey(HiTaskVo::getApplyCode)).collect(Collectors.toList());
        }

        return historyTaskList;
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> key) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(key.apply(t));

    }

    @Resource
    MongoTemplate mongoTemplate;

    /**
     * history task 历史任务记录
     *
     * @param hiTaskVo
     * @return
     * @author zmr
     */
    @Override
    public List<HiTaskVo> getHistoryTaskListForRelate(HiTaskVo hiTaskVo) {
        List<HiTaskVo> historyTaskList = new ArrayList<>();
        if (hiTaskVo.getCcId() == null) {
            // 历史
            historyTaskList = auditMapper.getHistoryTaskList2(hiTaskVo);
//            addMapInfo(historyTaskList,"2");
        } else {

            // 已办
            if (StrUtil.isNotBlank(hiTaskVo.getGoodsName())) {
                Page<Object> page = PageHelper.getLocalPage();
                PageMethod.clearPage();
                List<String> strings = businessMapper.selectInstIdByGoodName(hiTaskVo.getGoodsName());
                PageHelper.startPage(page.getPageNum(), page.getPageSize(), page.getOrderBy());
                if (strings.isEmpty()) {
                    strings.add("0000000");
                }
                hiTaskVo.setProcInstIdsStr(String.join(",", strings));
            }
            Page<Object> page = PageHelper.getLocalPage();
            PageMethod.clearPage();
            PageHelper.startPage(page.getPageNum(), page.getPageSize(), null);
            historyTaskList = auditMapper.getHistoryTaskList(hiTaskVo);
            addMapInfo(historyTaskList, "1");
            historyTaskList.stream().forEach(h -> {
                BizBusiness bizBusiness = bizBusinessService.selectBizBusinessById(h.getBusinessKey());
                h.setAuditor(bizBusiness.getLastAuditor() == null ? "" : bizBusiness.getLastAuditor());
                h.setEndTime(bizBusiness.getLastAuditorTime());
            });
//            if (hiTaskVo.getHasPurchase()!=null) {
//
//                if (hiTaskVo.getHasPurchase()) {
//                    historyTaskList = historyTaskList.stream().filter(e -> e.getProcDefKey().equals("purchase") && e.getResult() != 3 && e.getResult() != 4 && e.getResult() != 6).collect(Collectors.toList());
//                }
//                else {
//                    historyTaskList = historyTaskList.stream().filter(e -> !e.getProcDefKey().equals("purchase") && e.getResult() != 3 && e.getResult() != 4 && e.getResult() != 6).collect(Collectors.toList());
//                }
//                PageHelper.startPage(page.getPageNum(),page.getPageSize(),page.getOrderBy());
//                return historyTaskList;
//            }

        }

        return historyTaskList;
    }

    /**
     * 抄送我的
     *
     * @param hiTaskVo
     * @return
     */
    @Override
    public List<HiTaskVo> getHistoryTaskListCc(HiTaskVo hiTaskVo) {

        Long userId = SystemUtil.getUserId();
//        List<String> ccProcInst = auditMapper.getCcProcInst(userId.toString());
//        hiTaskVo.setProcInstIds(ccProcInst);
        hiTaskVo.setCcId(userId.toString());
        if (StrUtil.isNotBlank(hiTaskVo.getGoodsName())) {
            Page<Object> page = PageHelper.getLocalPage();
            PageMethod.clearPage();
            List<String> strings = businessMapper.selectInstIdByGoodName(hiTaskVo.getGoodsName());
            PageHelper.startPage(page.getPageNum(), page.getPageSize(), page.getOrderBy());
            if (strings.isEmpty()) {
                strings.add("0000000");
            }
            hiTaskVo.setProcInstIdsStr(String.join(",", strings));
        }

        List<HiTaskVo> historyTaskListCc = null;


        historyTaskListCc = auditMapper.getHistoryTaskListCc(hiTaskVo);
        historyTaskListCc.stream().forEach(e -> {
            BizBusiness bizBusiness = bizBusinessService.selectBizBusinessById(e.getBusinessKey());
            e.setTableId(bizBusiness.getTableId());

        });


        addMapInfo(historyTaskListCc, "1");
        if (hiTaskVo.getHasPurchase() != null) {
            Page<Object> page = PageHelper.getLocalPage();
            PageMethod.clearPage();
            if (hiTaskVo.getHasPurchase()) {
                historyTaskListCc = historyTaskListCc.stream().filter(e -> e.getTaskDefKey().equals("purchase") && e.getResult() != 3 && e.getResult() != 4 && e.getResult() != 6).collect(Collectors.toList());
            } else {
                historyTaskListCc = historyTaskListCc.stream().filter(e -> !e.getTaskDefKey().equals("purchase") && e.getResult() != 3 && e.getResult() != 4 && e.getResult() != 6).collect(Collectors.toList());
            }
            PageHelper.startPage(page.getPageNum(), page.getPageSize(), page.getOrderBy());
        }


        return historyTaskListCc;
    }

    /**
     * 抄送我的
     *
     * @param hiTaskVo
     * @return
     */
    @Override
    public List<HiTaskVo> getHistoryTaskListCcForRelate(HiTaskVo hiTaskVo) {

        Long userId = SystemUtil.getUserId();
//        List<String> ccProcInst = auditMapper.getCcProcInst(userId.toString());
//        hiTaskVo.setProcInstIds(ccProcInst);
        hiTaskVo.setCcId(userId.toString());
        if (StrUtil.isNotBlank(hiTaskVo.getGoodsName())) {
            Page<Object> page = PageHelper.getLocalPage();
            PageMethod.clearPage();
            List<String> strings = businessMapper.selectInstIdByGoodName(hiTaskVo.getGoodsName());
            PageHelper.startPage(page.getPageNum(), page.getPageSize(), page.getOrderBy());
            if (strings.isEmpty()) {
                strings.add("0000000");
            }
            hiTaskVo.setProcInstIdsStr(String.join(",", strings));
        }

        List<HiTaskVo> historyTaskListCc = null;

        Page<Object> page = PageHelper.getLocalPage();
        PageMethod.clearPage();
        historyTaskListCc = auditMapper.getHistoryTaskListCc(hiTaskVo);
        if (hiTaskVo.getHasPurchase() != null) {

            if (hiTaskVo.getHasPurchase()) {
                historyTaskListCc = historyTaskListCc.stream().filter(e -> e.getTaskDefKey().equals("purchase") && e.getResult() != 3 && e.getResult() != 4 && e.getResult() != 6).collect(Collectors.toList());
            } else {
                historyTaskListCc = historyTaskListCc.stream().filter(e -> !e.getTaskDefKey().equals("purchase") && e.getResult() != 3 && e.getResult() != 4 && e.getResult() != 6).collect(Collectors.toList());
            }
            PageHelper.startPage(page.getPageNum(), page.getPageSize(), page.getOrderBy());
        }


        addMapInfo(historyTaskListCc, "1");

        return historyTaskListCc;
    }

    /**
     * history task 历史任务记录Map 用于导出pdf
     *
     * @param hiTaskVo
     * @return
     * @author zmr
     */
    @Override
    public List<Map<String, Object>> getHistoryTaskMap(HiTaskVo hiTaskVo) {
        List<HiTaskVo> historyTaskList = auditMapper.getHistoryTaskList(hiTaskVo);
        ArrayList<Map<String, Object>> maps = new ArrayList<>();
        if (historyTaskList != null && historyTaskList.size() > 0) {
            for (HiTaskVo h : historyTaskList
            ) {
                Map<String, Object> map = new LinkedHashMap<>();
                String str = "";
                if (h.getResult() != null) {
                    if (h.getResult() == 1) {
                        str = "审批中";
                    } else if (h.getResult() == 2) {
                        str = "通过";
                    } else if (h.getResult() == 3) {
                        str = "驳回";
                    } else if (h.getResult() == 4) {
                        str = "撤销";
                    } else if (h.getResult() == 6) {
                        str = "中止";
                    }
                    map.put("name", h.getAuditor() + " " + str);
                    map.put("date", DateUtil.format(h.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
                    ;
                    maps.add(map);
                } else {
                    map.put("name", "--");
                    map.put("date", "-");
                    maps.add(map);
                }

            }
        } else {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("name", " ");
            map.put("date", "-");
            maps.add(map);
        }

        return maps;
    }

    /**
     * 转交
     *
     * @param reassignmentDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reassignment(ReassignmentDto reassignmentDto) throws MessagingException {
        reassignmentDto.setUserId(SystemUtil.getUserId());
        // 单个人转交
//        taskService.unclaim(taskId);
//        taskService.claim(taskId, userId);
        try {
            BizBusiness business = bizBusinessService.selectBizBusinessById(reassignmentDto.getBusinessKey());
            ActReProcdef process = procdefService.getActReProcdefs(business.getProcDefKey());
            BpmnModel bpmnModel = repositoryService.getBpmnModel(process.getId());
            // 流程是否包含会签
            boolean isMultiInstance = false;
            Collection<FlowElement> flowElements = bpmnModel.getMainProcess().getFlowElements();
            for (FlowElement flowElement : flowElements) {
                if (flowElement instanceof UserTask) {
                    UserTask userTask = (UserTask) flowElement;
                    if (userTask.getLoopCharacteristics() != null && userTask.getId().equals(reassignmentDto.getTaskDefKey())) {
                        isMultiInstance = true;
                    }
                }
            }
            // 多实例转交
            if (isMultiInstance) {
                taskService.unclaim(reassignmentDto.getTaskId());
                taskService.claim(reassignmentDto.getTaskId(), reassignmentDto.getNewUserId().toString());
            }
            // 更换候选人
            taskService.deleteCandidateUser(reassignmentDto.getTaskId(), String.valueOf(reassignmentDto.getUserId()));
            taskService.addCandidateUser(reassignmentDto.getTaskId(), String.valueOf(reassignmentDto.getNewUserId()));

            // 更新当前审批人
            SysUser sysUserNow = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
            SysUser sysUser = remoteUserService.selectSysUserByUserId(Long.valueOf(reassignmentDto.getNewUserId()));
            bizBusinessService.updateBizBusiness(business.setAuditors(sysUser.getUserName()).setLastAuditor(sysUser.getUserName()));
            // 插入转交记录
            BizReassignmentRecord reassignmentRecord = new BizReassignmentRecord();
            reassignmentRecord.setReason(reassignmentDto.getReason());
            reassignmentRecord.setBusinessKey(reassignmentDto.getBusinessKey());
            reassignmentRecord.setCreateTime(new Date());
            reassignmentRecord.setSourceUser(reassignmentDto.getUserId());
            reassignmentRecord.setTargetUser(reassignmentDto.getNewUserId());
            reassignmentRecord.setTaskDefKey(reassignmentDto.getTaskDefKey());
            reassignmentRecordMapper.insert(reassignmentRecord);
            pushSocket(business.getProcDefKey());
            pushGetui(sysUser.getCid(), sysUser.getUserId(), business.getProcDefKey());
            pushGetui(sysUserNow.getCid(), sysUserNow.getUserId(), business.getProcDefKey());
            // 发送通知给发起人
            String mapInfoMail = mapInfoService.getMapInfoMail(business);
            SysUser sysUserOld = remoteUserService.selectSysUserByUserId(reassignmentDto.getUserId());
            String txt = "<body>" +
                    "<p>" +
                    "您的申请在" + DateUtil.format(new Date(), "yyyy年MM月dd日 HH时mm分") + "被<strong style='color:#2d8ccc;'>" + sysUserOld.getUserName() + "</strong>转交给了<strong style='color:#2d8ccc;'>" + sysUser.getUserName() + "</strong>。" +
                    "</p>" +
                    "<p>" +
                    "<strong>转交原因：<strong>" + reassignmentDto.getReason() +
                    "</p><br>" + mapInfoMail + "<br><h4 style='color:#aaaaaa;'><strong>提示：更多数据，请前往云管家平台查看</strong></h4></body>";
            SysUser sysUser1 = remoteUserService.selectSysUserByUserId(business.getUserId());
            if (StrUtil.isNotBlank(sysUser1.getEmail())) {
                mailService.send(txt, business.getTitle(), sysUser1.getEmail(), sysUserNow.getUserName(), sysUser1.getCid());
            }
            if (StrUtil.isNotBlank(sysUser.getEmail())) {
                String txt2 = "<body>" +
                        "<p>" +
                        "您有一条<strong style='color:#2d8ccc;'>" + sysUserOld.getUserName() + "</strong>转交的待办事项，请及时审批。" +
                        "</p>" +
                        "<p>" +
                        "<strong>转交原因：<strong>" + reassignmentDto.getReason() +
                        "</p><br>" + mapInfoMail + "<br><h4 style='color:#aaaaaa;'><strong>提示：更多数据，请前往云管家平台查看</strong></h4></body>";
                mailService.send(txt2, business.getTitle(), sysUser.getEmail(), sysUserNow.getUserName(), sysUser.getCid());
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }

    }

    private void pushGetui(String cid, Long userId, String procDefKey) {
        if (StrUtil.isBlank(cid)) {
            return;
        }
        TaskQuery query = taskService.createTaskQuery().taskCandidateOrAssigned(userId.toString());
//        query = query.processDefinitionKeyIn(Arrays.asList("purchase;reimburse;payment;claim;businessMoney;scrapped;review;universal;seal;contract_ys;carApply;carSubsidyApply;feedback;cover;other-charge;hcpd;xz_approval;xz_adjustment;".split(";")));
        final long count = query.count();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total", count);
        jsonObject.put("procDefKey", procDefKey);
        jsonObject.put("type", "ing");
        query = query.processDefinitionKey(procDefKey);
        final long num = query.count();
        jsonObject.put("num", num);
        remoteSocketService.singlePushTransmission(cid, jsonObject.toJSONString());
    }

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


    private void addMapInfo(List<HiTaskVo> list, String type) {
        for (HiTaskVo hiTask : list) {

            BizBusiness bizBusiness = bizBusinessService.selectBizBusinessById(hiTask.getBusinessKey());
            hiTask.setAuditors(bizBusiness.getAuditors());
            hiTask.setApplyerTime(bizBusiness.getApplyTime());
            if ("1".equals(type)) {
                hiTask.setResult(bizBusiness.getResult());
            }
//            if (hiTask.getIsCc()) {
//                hiTask.setTitle(bizBusiness.getTitle());
//            }
//            hiTask.setApplyerTime(bizBusiness.getApplyTime());
//            hiTask.setResult(bizBusiness.getResult());
//            hiTask.setAuditors(bizBusiness.getAuditors());
//            // 单个节点通过可能对于流程还是没结束
//            hiTask.setResult(bizBusiness.getResult());
//            String result = "";
//            switch (hiTask.getResult()) {
//                case 2:
//                    result = "通过";
//                    break;
//                case 3:
//                    result = "驳回";
//                    break;
//                default:
//                    break;
//            }
//            List<Map<String, Object>> mpList = new ArrayList<>();
//            String applyCode = "";
//            switch (hiTask.getProcDefKey()) {
//                case "claim":
//                    BizClaimApply claimApply = bizClaimApplyService.selectBizClaimApplyById(bizBusiness.getTableId());
//                    applyCode = claimApply.getClaimCode();
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "申领标题");
//                        put("value", hiTask.getTitle());
//                    }});
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "申领日期");
//                        put("value", DateUtil.format(claimApply.getCreateTime(), "yyyy-MM-dd HH:mm"));
//                    }});
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "物品用途");
//                        put("value", claimApply.getReason());
//                    }});
//                    List<String> goodsName = new ArrayList<>();
//                    List<Long> num = new ArrayList<>();
//                    claimApply.getGoods().stream().forEach(goods -> {
//                        AaSpu aaSpu = aaSpuMapper.selectByPrimaryKey(goods.getGoodsId());
//                        if (aaSpu == null) {
//                            goodsName.add("");
//                            num.add(0L);
//                        } else {
//                            goodsName.add(aaSpu.getName());
//                            num.add(goods.getClaimNum() / 100);
//                        }
//                    });
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "物品名称");
//                        put("value", String.join("、", goodsName));
//                    }});
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "申请数量");
//                        put("value", num.stream().collect(Collectors.summarizingLong(value -> value)).getSum());
//                    }});
//                    String finalResult = result;
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "审批结果");
//                        put("value", finalResult);
//                    }});
//                    break;
//                case "purchase":
//                    BizPurchaseApply purchaseApply = purchaseApplyService.selectBizPurchaseApplyById(Long.valueOf(bizBusiness.getTableId()));
//                    applyCode = purchaseApply.getPurchaseCode();
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "申请标题");
//                        put("value", hiTask.getTitle());
//                    }});
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "申请时间");
//                        put("value", DateUtil.format(purchaseApply.getCreateTime(), "yyyy-MM-dd HH:mm"));
//                    }});
//                    List<BizGoodsInfo> bizGoodsInfos = purchaseApply.getBizGoodsInfos();
//                    SysDept sysDeptPurchase2 = remoteDeptService.selectSysDeptByDeptId(Long.valueOf(bizGoodsInfos.get(0).getUsages()));
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "使用部门");
//                        put("value", sysDeptPurchase2.getDeptName() + "...");
//                    }});
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "物品名称");
//                        put("value", bizGoodsInfos.get(0).getName() + "...");
//                    }});
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "申购数量");
//                        put("value", bizGoodsInfos.stream().collect(Collectors.summarizingLong(value -> value.getAmount())).getSum());
//                    }});
//                    String finalResult2 = result;
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "审批结果");
//                        put("value", finalResult2);
//                    }});
//                    break;
//                case "businessMoney":
//                    BizBusinessFeeApply businessFeeApply = businessFeeApplyService.selectBizBusinessFeeApplyById(Integer.valueOf(bizBusiness.getTableId()));
//                    applyCode = businessFeeApply.getApplyCode();
////                    mpList.add(new HashMap<String,Object>(2){{put("label","申请标题");put("value",businessFeeApply.getTitle());}});
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "申请标题");
//                        put("value", hiTask.getTitle());
//                    }});
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "申请时间");
//                        put("value", DateUtil.format(businessFeeApply.getCreateTime(), "yyyy-MM-dd HH:mm"));
//                    }});
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "项目名称");
//                        put("value", businessFeeApply.getProjectName());
//                    }});
//                    Map<String, Object> belongCompanyBusinessMoney = remoteDeptService.getBelongCompany2(businessFeeApply.getDeptId());
//                    SysDept sysDept1 = remoteDeptService.selectSysDeptByDeptId(businessFeeApply.getDeptId());
////                    mpList.add(new HashMap<String, Object>(2) {{
////                        put("label", "隶属公司");
////                        put("value", belongCompanyBusinessMoney.get("companyName").toString());
////                    }});
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "隶属部门");
//                        put("value", belongCompanyBusinessMoney.get("companyName").toString() + "-" + sysDept1.getDeptName());
//                    }});
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "申请人");
//                        put("value", bizBusiness.getApplyer());
//                    }});
//                    String finalResultBusinessMoney = result;
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "审批结果");
//                        put("value", finalResultBusinessMoney);
//                    }});
//                    break;
//                case "leave":
//
//                    break;
//                case "payment":
//                    BizPaymentApply paymentApply = paymentApplyService.selectById(Long.valueOf(bizBusiness.getTableId()));
//                    applyCode = paymentApply.getPaymentCode();
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "申请标题");
//                        put("value", hiTask.getTitle());
//                    }});
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "申请时间");
//                        put("value", DateUtil.format(paymentApply.getCreateTime(), "yyyy-MM-dd HH:mm"));
//                    }});
//                    String projectType = paymentApply.getProjectType();
//                    String typePayment = getTypePayment(Integer.valueOf(projectType));
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "款项类目");
//                        put("value", typePayment);
//                    }});
//                    SysDept sysDeptPayment = remoteDeptService.selectSysDeptByDeptId(paymentApply.getSubjectionDeptId());
//                    Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(paymentApply.getSubjectionDeptId());
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "隶属部门");
//                        put("value", belongCompany2.get("companyName").toString() + "-" + sysDeptPayment.getDeptName());
//                    }});
//                    String finalResultPayment = result;
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "审批结果");
//                        put("value", finalResultPayment);
//                    }});
//                    break;
//                case "reimburse":
//                    BizReimburseApply reimburseApply = reimburseApplyService.selectBizReimburseApplyById(Long.valueOf(bizBusiness.getTableId()));
//                    applyCode = reimburseApply.getReimbursementCode();
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "申请标题");
//                        put("value", hiTask.getTitle());
//                    }});
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "申请时间");
//                        put("value", DateUtil.format(reimburseApply.getCreateTime(), "yyyy-MM-dd HH:mm"));
//                    }});
//                    List<BizReimburseDetail> reimburseDetails = reimburseApply.getReimburseDetails();
//                    Integer types = reimburseDetails.get(0).getTypes();
//                    String type = getType(types);
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "报销类别");
//                        put("value", type);
//                    }});
//                    SysDept sysDeptReimburse = remoteDeptService.selectSysDeptByDeptId(reimburseApply.getSubjectionDeptId());
//                    Map<String, Object> belongCompanyReimburse = remoteDeptService.getBelongCompany2(reimburseApply.getSubjectionDeptId());
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "归属部门");
//                        put("value", belongCompanyReimburse.get("companyName").toString() + "-" + sysDeptReimburse.getDeptName());
//                    }});
//                    String finalResultReimburse = result;
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "审批结果");
//                        put("value", finalResultReimburse);
//                    }});
//                    break;
//                case "review":
//                    BizReviewApplyDto reviewApplyDto = reviewApplyService.selectBizReviewApplyById(Long.valueOf(bizBusiness.getTableId()));
//                    applyCode = reviewApplyDto.getReviewCode();
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "申请标题");
//                        put("value", hiTask.getTitle());
//                    }});
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "申请时间");
//                        put("value", DateUtil.format(reviewApplyDto.getCreateTime(), "yyyy-MM-dd"));
//                    }});
//                    String reviewType = "";
//                    // 1评审费、2服务费、3其他费用
//                    switch (reviewApplyDto.getTypes()) {
//                        case "1":
//                            reviewType = "评审费";
//                            break;
//                        case "2":
//                            reviewType = "服务费";
//                            break;
//                        case "3":
//                            reviewType = "其他费用";
//                            break;
//                        default:
//                            break;
//                    }
//                    String finalReviewType = reviewType;
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "款项用途");
//                        put("value", finalReviewType);
//                    }});
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "付款事由");
//                        put("value", reviewApplyDto.getPaymentDetails());
//                    }});
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "费用明细");
//                        put("value", reviewApplyDto.getRemark());
//                    }});
//                    SysDept sysDeptReview = remoteDeptService.selectSysDeptByDeptId(reviewApplyDto.getDeptId());
//                    Map<String, Object> belongCompanyReview = remoteDeptService.getBelongCompany2(reviewApplyDto.getDeptId());
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "归属部门");
//                        put("value", belongCompanyReview.get("companyName").toString() + "-" + sysDeptReview.getDeptName());
//                    }});
//                    String finalResultReviewType = result;
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "审批结果");
//                        put("value", finalResultReviewType);
//                    }});
//
//                    break;
//                case "scrapped":
//                    BizScrappedApply bizScrappedApply = scrappedApplyService.selectBizScrappedApplyById(bizBusiness.getTableId());
//                    applyCode = bizScrappedApply.getApplyCode();
//                    Asset asset = assetService.getById(bizScrappedApply.getAssertId());
//
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "申请标题");
//                        put("value", hiTask.getTitle());
//                    }});
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "申请时间");
//                        put("value", DateUtil.format(bizScrappedApply.getCreateTime(), "yyyy-MM-dd HH:mm"));
//                    }});
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "报废原因");
//                        put("value", bizScrappedApply.getRemark());
//                    }});
//                    if (asset == null) {
//                        mpList.add(new HashMap<String, Object>(2) {{
//                            put("label", "物品编号");
//                            put("value", "");
//                        }});
//                        mpList.add(new HashMap<String, Object>(2) {{
//                            put("label", "物品名称");
//                            put("value", "");
//                        }});
//                        mpList.add(new HashMap<String, Object>(2) {{
//                            put("label", "采购时间");
//                            put("value", "");
//                        }});
//                        mpList.add(new HashMap<String, Object>(2) {{
//                            put("label", "采购价格");
//                            put("value", 0);
//                        }});
//                    } else {
//                        mpList.add(new HashMap<String, Object>(2) {{
//                            put("label", "物品编号");
//                            put("value", asset.getAssetSn());
//                        }});
//                        mpList.add(new HashMap<String, Object>(2) {{
//                            put("label", "物品名称");
//                            put("value", asset.getName());
//                        }});
//                        mpList.add(new HashMap<String, Object>(2) {{
//                            put("label", "采购时间");
//                            put("value", DateUtil.format(asset.getPurchaseTime(), "yyyy-MM-dd"));
//                        }});
//                        mpList.add(new HashMap<String, Object>(2) {{
//                            put("label", "采购价格");
//                            put("value", asset.getPurchasePrice() / 100);
//                        }});
//                    }
//
//                    SysDept sysDeptScrapped = remoteDeptService.selectSysDeptByDeptId(bizScrappedApply.getDeptId());
//                    Map<String, Object> belongCompanyScrapped = remoteDeptService.getBelongCompany2(bizScrappedApply.getDeptId());
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "归属部门");
//                        put("value", belongCompanyScrapped.get("companyName").toString() + "-" + sysDeptScrapped.getDeptName());
//                    }});
//                    String finalResult3 = result;
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "审批结果");
//                        put("value", finalResult3);
//                    }});
//                    break;
//                case "contract_ys":
//                    BizContractApplyDto bizContractApplyDto = contractApplyService.selectOne(Long.valueOf(bizBusiness.getTableId()));
//                    applyCode = bizContractApplyDto.getApplyCode();
//
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "申请标题");
//                        put("value", bizContractApplyDto.getTitle());
//                    }});
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "申请时间");
//                        put("value", DateUtil.format(bizContractApplyDto.getCreateTime(), "yyyy-MM-dd HH:mm"));
//                    }});
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "客户名称");
//                        put("value", bizContractApplyDto.getCustomerName());
//                    }});
//
//                    SysDept sysDeptContract = remoteDeptService.selectSysDeptByDeptId(bizContractApplyDto.getDeptId());
//                    Map<String, Object> belongCompanyContract = remoteDeptService.getBelongCompany2(bizContractApplyDto.getDeptId());
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "归属部门");
//                        put("value", belongCompanyContract.get("companyName").toString() + "-" + sysDeptContract.getDeptName());
//                    }});
//                    String finalResultContract = result;
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "审批结果");
//                        put("value", finalResultContract);
//                    }});
//                    break;
//                case "seal":
//                    BizSealApply sealApply = sealApplyService.selectById(Long.valueOf(bizBusiness.getTableId()));
//                    applyCode = sealApply.getApplyCode();
//
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "申请标题");
//                        put("value", sealApply.getTitle());
//                    }});
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "申请时间");
//                        put("value", DateUtil.format(sealApply.getCreateTime(), "yyyy-MM-dd HH:mm"));
//                    }});
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "用印人");
//                        put("value", remoteUserService.selectSysUserByUserId(sealApply.getSealUser()).getUserName());
//                    }});
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "日期");
//                        put("value", DateUtil.format(sealApply.getStampDate(), "yyyy-MM-dd"));
//                    }});
//                    SysDept sysDeptSeal = remoteDeptService.selectSysDeptByDeptId(sealApply.getUserDept());
//                    Map<String, Object> belongCompanySeal = remoteDeptService.getBelongCompany2(sealApply.getUserDept());
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "用印部门");
//                        put("value", belongCompanySeal.get("companyName").toString() + "-" + sysDeptSeal.getDeptName());
//                    }});
//                    String finalResultSeal = result;
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "审批结果");
//                        put("value", finalResultSeal);
//                    }});
//                    break;
//                case "universal":
//                    BizUniversalApply universalApply = universalApplyService.selectById(Long.valueOf(bizBusiness.getTableId()));
//                    applyCode = universalApply.getApplyCode();
//
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "申请标题");
//                        put("value", universalApply.getTitle());
//                    }});
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "申请时间");
//                        put("value", DateUtil.format(universalApply.getCreateTime(), "yyyy-MM-dd HH:mm"));
//                    }});
//
//                    SysDept sysDeptUniversal = remoteDeptService.selectSysDeptByDeptId(universalApply.getDeptId());
//                    Map<String, Object> belongCompanyUniversal = remoteDeptService.getBelongCompany2(universalApply.getDeptId());
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "隶属公司");
//                        put("value", belongCompanyUniversal.get("companyName").toString());
//                    }});
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "隶属部门");
//                        put("value", sysDeptUniversal.getDeptName());
//                    }});
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "申请内容");
//                        put("value", universalApply.getContent());
//                    }});
//                    String finalResultUniversal = result;
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "审批结果");
//                        put("value", finalResultUniversal);
//                    }});
//                    break;
//                default:
//                    break;
//
//            }
            hiTask.setDetailMap((List<Map<String, Object>>) mapInfoService.getMapInfo(bizBusiness).get("list"));
            hiTask.setApplyCode(mapInfoService.getMapInfo(bizBusiness).get("applyCode").toString());
        }
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
