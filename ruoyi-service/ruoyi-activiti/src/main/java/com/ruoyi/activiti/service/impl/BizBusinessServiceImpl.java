package com.ruoyi.activiti.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.StatefulException;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.page.PageMethod;
import com.google.common.collect.Lists;
import com.ruoyi.activiti.consts.ActivitiConstant;
import com.ruoyi.activiti.consts.UrlConstants;
import com.ruoyi.activiti.domain.BizReassignmentRecord;
import com.ruoyi.activiti.domain.asset.*;
import com.ruoyi.activiti.domain.car.BizReserveDetail;
import com.ruoyi.activiti.domain.dto.NeedManageDto;
import com.ruoyi.activiti.domain.my_apply.BizOrSign;
import com.ruoyi.activiti.domain.my_apply.BizProjectAmountApply;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.domain.proc.BizBusinessPlus;
import com.ruoyi.activiti.domain.purchase.BizGoodsInfo;
import com.ruoyi.activiti.domain.purchase.BizGoodsRecord;
import com.ruoyi.activiti.domain.purchase.BizGoodsRejectedApply;
import com.ruoyi.activiti.domain.purchase.BizGoodsRejectedDetail;
import com.ruoyi.activiti.feign.MessageEntity;
import com.ruoyi.activiti.feign.RemoteSocketService;
import com.ruoyi.activiti.mapper.*;
import com.ruoyi.activiti.mapper.asset.BizConsumablesInventoryDetailMapper;
import com.ruoyi.activiti.mapper.asset.BizConsumablesInventoryMapper;
import com.ruoyi.activiti.service.*;
import com.ruoyi.activiti.utils.MailService;
import com.ruoyi.activiti.vo.HiTaskVo;
import com.ruoyi.common.annotation.AuditFilter;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteConfigService;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.*;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>File：BizBusinessServiceImpl.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2020 2020年1月6日 下午3:38:49</p>
 * <p>Company: zmrit.com </p>
 *
 * @author zmr
 * @version 1.0
 */
@Service
@Slf4j
public class BizBusinessServiceImpl implements IBizBusinessService {
    @Autowired
    private RepositoryService repositoryService;
    @Resource
    MongoTemplate mongoTemplatel;
    private static final String SCRAPPED_KEY = "scrapped";
    private static final String CLAIM_KEY = "claim";
    private static final String GOODS_REJECTED = "goodsRejected";
    private static final String CAR_APPLY = "carApply";
    private static final String CAR_SUBSIDY_APPLY = "carSubsidyApply";
    private static final String PURCHASE = "purchase";
    private static final String HCPD = "hcpd";
    private static final String FEEDBACK = "feedback";
    private static final String UNIVERSAL = "universal";
    private static final String PAYMENT = "payment-";
    private static final String PR_AMOUNT = "pr_amount";

    private final BizScrappedApplyMapper scrappedApplyMapper;
    private final BizClaimApplyMapper claimApplyMapper;
    private final BizClaimGoodsMapper claimGoodsMapper;
    private final AaSkuMapper aaSkuMapper;
    private final AssetService assetService;
    private final AaSpuMapper aaSpuMapper;
    private final BizOrSignMapper orSignMapper;
    private final AaTranscationMapper aaTranscationMapper;
    private final BizBusinessMapper businessMapper;
    private final BizBusinessPlusMapper businessPlusMapper;
    private final RuntimeService runtimeService;
    private final IdentityService identityService;
    private final TaskService taskService;
    private final IBizNodeService bizNodeService;
    private final RemoteUserService remoteUserService;
    private final RemoteDeptService remoteDeptService;
    private final ActReProcdefMapper actReProcdefMapper;
    private final BizGoodsRejectedApplyMapper goodsRejectedApplyMapper;
    private final BizGoodsRejectedDetailService goodsRejectedDetailService;
    private final RemoteSocketService remoteSocketService;
    private final BizReserveDetailMapper reserveDetailMapper;
    private final BizConsumablesInventoryMapper consumablesInventoryMapper;
    private final BizConsumablesInventoryDetailMapper consumablesInventoryDetailMapper;
    private final BizGoodsInfoMapper goodsInfoMapper;
    private final BizProjectAmountApplyMapper bizProjectAmountApplyMapper;
    private final MapInfoService mapInfoService;
    private final MailService mailService;
    private final BizGoodsRecordMapper goodsRecordMapper;
    private final IBizAuditService auditService;
    private final BizReassignmentRecordMapper reassignmentRecordMapper;
    private final RemoteConfigService remoteConfigService;
    private final ProcessService processService;

    @Resource
    BizBusinessTestMapper bizBusinessTestMapper;

    @Autowired
    @Lazy
    public BizBusinessServiceImpl(BizScrappedApplyMapper scrappedApplyMapper,
                                  BizClaimApplyMapper claimApplyMapper,
                                  BizClaimGoodsMapper claimGoodsMapper,
                                  AaSkuMapper aaSkuMapper,
                                  AssetService assetService,
                                  AaSpuMapper aaSpuMapper,
                                  BizOrSignMapper orSignMapper,
                                  AaTranscationMapper aaTranscationMapper,
                                  BizBusinessMapper businessMapper,
                                  BizBusinessPlusMapper businessPlusMapper,
                                  RuntimeService runtimeService,
                                  IdentityService identityService,
                                  TaskService taskService,
                                  IBizNodeService bizNodeService,
                                  RemoteUserService remoteUserService,
                                  RemoteDeptService remoteDeptService,
                                  ActReProcdefMapper actReProcdefMapper,
                                  BizGoodsRejectedApplyMapper goodsRejectedApplyMapper,
                                  BizGoodsRejectedDetailService goodsRejectedDetailService,
                                  RemoteSocketService remoteSocketService,
                                  BizReserveDetailMapper reserveDetailMapper,
                                  BizConsumablesInventoryMapper consumablesInventoryMapper,
                                  BizConsumablesInventoryDetailMapper consumablesInventoryDetailMapper,
                                  BizGoodsInfoMapper goodsInfoMapper,
                                  BizProjectAmountApplyMapper bizProjectAmountApplyMapper,
                                  MapInfoService mapInfoService, MailService mailService,
                                  BizGoodsRecordMapper goodsRecordMapper,
                                  IBizAuditService auditService,
                                  BizReassignmentRecordMapper reassignmentRecordMapper,
                                  RemoteConfigService remoteConfigService,
                                  ProcessService processService) {
        this.scrappedApplyMapper = scrappedApplyMapper;
        this.claimApplyMapper = claimApplyMapper;
        this.claimGoodsMapper = claimGoodsMapper;
        this.aaSkuMapper = aaSkuMapper;
        this.assetService = assetService;
        this.aaSpuMapper = aaSpuMapper;
        this.orSignMapper = orSignMapper;
        this.aaTranscationMapper = aaTranscationMapper;
        this.businessMapper = businessMapper;
        this.businessPlusMapper = businessPlusMapper;
        this.runtimeService = runtimeService;
        this.identityService = identityService;
        this.taskService = taskService;
        this.bizNodeService = bizNodeService;
        this.remoteUserService = remoteUserService;
        this.remoteDeptService = remoteDeptService;
        this.actReProcdefMapper = actReProcdefMapper;
        this.goodsRejectedApplyMapper = goodsRejectedApplyMapper;
        this.goodsRejectedDetailService = goodsRejectedDetailService;
        this.remoteSocketService = remoteSocketService;
        this.reserveDetailMapper = reserveDetailMapper;
        this.consumablesInventoryMapper = consumablesInventoryMapper;
        this.consumablesInventoryDetailMapper = consumablesInventoryDetailMapper;
        this.goodsInfoMapper = goodsInfoMapper;
        this.bizProjectAmountApplyMapper = bizProjectAmountApplyMapper;
        this.mapInfoService = mapInfoService;
        this.mailService = mailService;
        this.goodsRecordMapper = goodsRecordMapper;
        this.auditService = auditService;
        this.reassignmentRecordMapper = reassignmentRecordMapper;
        this.remoteConfigService = remoteConfigService;
        this.processService = processService;
    }

    /**
     * 查询流程业务我的申请详情
     *
     * @param id 流程业务ID
     * @return 流程业务
     */
    @Override
    public BizBusiness selectBizBusinessById(String id) {
        BizBusiness business = businessMapper.selectByPrimaryKey(id);
        return business;
    }

    /**
     * 查询流程业务（审批时专用）
     *
     * @param id 流程业务ID
     * @return 流程业务
     */
    @Override
    public BizBusiness selectBizBusinessById2(String id) {
        BizBusiness business = businessMapper.selectByPrimaryKey(id);
        return business;
    }


    /**
     * 查询流程业务列表 我的申请列表
     *
     * @param bizBusiness 流程业务
     * @return 流程业务
     */
    @Override
    public List<BizBusiness> selectBizBusinessList(BizBusiness bizBusiness) {
        bizBusiness.setUserId(SystemUtil.getUserId());
        // 合同项目修改
        if (StrUtil.isNotBlank(bizBusiness.getFunction())) {
            // 管理员
            Integer roleNum = bizBusinessTestMapper.getRoleNum(1, SystemUtil.getUserId());
            if (roleNum > 0) {
                bizBusiness.setUserId(null);
            }
        }

        bizBusiness.setDelFlag(false);
        if (StrUtil.isNotEmpty(bizBusiness.getSearchValue())) {
            bizBusiness.setTitle(bizBusiness.getSearchValue());
        }
        List<BizBusiness> select = selectBizBusinessListAll2(bizBusiness);
        select.stream().forEach(bizBusiness1 -> bizBusiness1.setComment(businessMapper.getComment(bizBusiness1.getProcInstId())));
        return select;
    }

    /**
     * 查询流程业务列表
     *
     * @param bizBusiness 流程业务
     * @return 流程业务集合
     */
    @Override
    public List<BizBusiness> selectBizBusinessListForRelate(BizBusiness bizBusiness) {
        bizBusiness.setUserId(SystemUtil.getUserId());
        bizBusiness.setDelFlag(false);
        if (StrUtil.isNotEmpty(bizBusiness.getSearchValue())) {
            bizBusiness.setTitle(bizBusiness.getSearchValue());
        }
        Page<Object> page = PageHelper.getLocalPage();
        PageMethod.clearPage();
        List<BizBusiness> select = selectBizBusinessListAll2(bizBusiness);
        select.stream().forEach(bizBusiness1 -> {
            bizBusiness1.setComment(businessMapper.getComment(bizBusiness1.getProcInstId()));
        });
        if (bizBusiness.getHasPurchase() != null) {

            if (bizBusiness.getHasPurchase()) {
                select = select.stream().filter(e -> e.getProcDefKey().equals("purchase") && e.getResult() != 3 && e.getResult() != 4 && e.getResult() != 6).collect(Collectors.toList());
            } else {
                select = select.stream().filter(e -> !e.getProcDefKey().equals("purchase") && e.getResult() != 3 && e.getResult() != 4 && e.getResult() != 6).collect(Collectors.toList());
            }
            PageHelper.startPage(page.getPageNum(), page.getPageSize(), page.getOrderBy());
            return select;
        }
        return select;
    }

    /**
     * 查询流程业务列表 我的申请(流程图小程序)
     *
     * @param bizBusiness 流程业务
     * @return 流程业务
     */
    @Override
    public List<BizBusiness> selectBizBusinessListAll(BizBusiness bizBusiness) {
        bizBusiness.setDelFlag(false);
        if (StrUtil.isNotEmpty(bizBusiness.getSearchValue())) {
            bizBusiness.setTitle(bizBusiness.getSearchValue());
        }
        return businessMapper.select(bizBusiness);
    }

    /**
     * 查询流程业务列表(流程监控列表)
     *
     * @param bizBusiness 流程业务
     * @return 流程业务
     */
    @Override
    public List<BizBusiness> selectBizBusinessListAll2(BizBusiness bizBusiness) {
        Example example = new Example(BizBusiness.class);
        Example.Criteria criteria = example.createCriteria();
        if (StrUtil.isNotBlank(bizBusiness.getGoodsName())) {
            Page<Object> page = PageHelper.getLocalPage();
            PageMethod.clearPage();
            List<String> strings = businessMapper.selectInstIdByGoodName(bizBusiness.getGoodsName());
            PageHelper.startPage(page.getPageNum(), page.getPageSize(), page.getOrderBy());
            if (strings.isEmpty()) {
                strings.add("0000000");
            }
            criteria.andIn("procInstId", strings);
        }
        // 我的转交
        if (bizBusiness.getIds() != null && !bizBusiness.getIds().isEmpty()) {
            criteria.andIn("id", bizBusiness.getIds());
        }
        if (StrUtil.isNotBlank(bizBusiness.getApplyer())) {
            criteria.andLike("applyer", "%" + bizBusiness.getApplyer() + "%");
        }
        if (null != bizBusiness.getUserId()) {
            criteria.andEqualTo("userId", bizBusiness.getUserId());
        }
        if (null != bizBusiness.getStartTime() && null != bizBusiness.getEndTime()) {
            criteria.andBetween("applyTime", bizBusiness.getStartTime(), bizBusiness.getEndTime());
        }
        //todo
        if (StrUtil.isNotBlank(bizBusiness.getProcDefKey())) {
            criteria.andLike("procDefKey", "%" + bizBusiness.getProcDefKey() + "%");
        }
        if (StrUtil.isNotBlank(bizBusiness.getProcName())) {
            criteria.andLike("procName", "%" + bizBusiness.getProcName() + "%");
        }
        if (StrUtil.isNotBlank(bizBusiness.getTitle())) {
            criteria.andLike("title", "%" + bizBusiness.getTitle() + "%");
        }
        if (null != bizBusiness.getResult()) {
            criteria.andEqualTo("result", bizBusiness.getResult());
        }
        if (null != bizBusiness.getStatus()) {
            criteria.andEqualTo("status", bizBusiness.getStatus());
        }
        if (StrUtil.isNotBlank(bizBusiness.getApplyCode())) {
            criteria.andLike("applyCode", "%" + bizBusiness.getApplyCode() + "%");
        }
        if (null != bizBusiness.getCompanyId()) {
            criteria.andEqualTo("companyId", bizBusiness.getCompanyId());
        }
        if (null != bizBusiness.getDeptId()) {
            criteria.andEqualTo("deptId", bizBusiness.getDeptId());
        }
        criteria.andEqualTo("delFlag", 0);
        example.setOrderByClause("apply_time DESC");
        return businessMapper.selectByExample(example);
    }

    /**
     * 新增流程业务
     *
     * @param bizBusiness 流程业务
     * @return 结果
     */
    @Override
    public int insertBizBusiness(BizBusiness bizBusiness) {
        return businessMapper.insertSelective(bizBusiness);
    }

    /**
     * 修改流程业务
     *
     * @param bizBusiness 流程业务
     * @return 结果
     */
    @Override
    public int updateBizBusiness(BizBusiness bizBusiness) {
        return businessMapper.updateByPrimaryKeySelective(bizBusiness);
    }

    /**
     * 删除流程业务对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteBizBusinessByIds(String ids) {
        return businessMapper.deleteByIds(ids);
    }

    /**
     * 删除流程业务信息
     *
     * @param id 流程业务ID
     * @return 结果
     */
    @Override
    public int deleteBizBusinessById(Long id) {
        return businessMapper.deleteByPrimaryKey(id);
    }

    /* (non-Javadoc)
     * @see com.ruoyi.activiti.service.IBizBusinessService#deleteBizBusinessLogic(java.lang.String)
     */
    @Override
    public int deleteBizBusinessLogic(String ids) {
        Example example = new Example(BizBusiness.class);
        example.createCriteria().andIn("id", Lists.newArrayList(ids.split(",")));
        return businessMapper.updateByExampleSelective(new BizBusiness().setDelFlag(true), example);
    }

    /* (non-Javadoc)
     * @see com.ruoyi.activiti.service.IBizBusinessService#startProcess(com.ruoyi.activiti.domain.proc.BizBusiness, java.util.Map)
     */
    @Override
    @AuditFilter(method = "startProcess")
    @Transactional(rollbackFor = Exception.class)
    public Set<String> startProcess(BizBusiness business, Map<String, Object> variables) {
        // 流程变量插入申请人所在公司id
        SysUser sysUser = remoteUserService.selectSysUserByUserId(business.getUserId());
        Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(sysUser.getDeptId());
        Long companyId = Long.valueOf(belongCompany2.get("companyId").toString());
        variables.put("deptId", companyId);
        // 流程变量插入申请人是否直属于集团
        variables.put("isGroup", sysUser.getIsGroup());
        log.error("isGroup:{}", sysUser.getIsGroup());
        variables.put("isLeader", sysUser.getIsLeader());
        log.error("isLeader:{}", sysUser.getIsLeader());
        // 启动流程用户
        identityService.setAuthenticatedUserId(business.getUserId().toString());
        // 获取第一个节点会签人员
        BpmnModel bpmnModel = new BpmnModel();
        try {
            bpmnModel = repositoryService.getBpmnModel(business.getProcDefId());
        } catch (Exception e) {
            log.error("流程定义不存在");
        }
        // 流程是否包含会签
        List<String> multiInstanceList = new ArrayList<>();
        Collection<FlowElement> flowElements = bpmnModel.getMainProcess().getFlowElements();
        for (FlowElement flowElement : flowElements) {
            if (flowElement instanceof UserTask) {
                UserTask userTask = (UserTask) flowElement;
                if (userTask.getLoopCharacteristics() != null) {
                    System.out.println("流程包含会签" + userTask.getId());
                    multiInstanceList.add(userTask.getId());
                }
            }
        }
        //获得流程模型的所有节点
        List<Process> processes = bpmnModel.getProcesses();
        if (processes.isEmpty()) {
            log.error("获得流程模型的所有节点失败");
        }
        processes.get(0).getFlowElements();
        List<StartEvent> startEventList = processes.get(0).findFlowElementsOfType(StartEvent.class);
        List<SequenceFlow> sequenceFlowList = processes.get(0).findFlowElementsOfType(SequenceFlow.class);

        String id = startEventList.get(0).getId();
        for (SequenceFlow sequenceFlow : sequenceFlowList) {
            if (sequenceFlow.getSourceRef().equals(id) && multiInstanceList.contains(sequenceFlow.getTargetRef())) {
                Set<String> auditors = bizNodeService.getAuditors(sequenceFlow.getTargetRef(), business.getUserId(), business.getProcDefKey());
                variables.put("assigneeList", new ArrayList<>(auditors));
            }
        }
        // 启动流程 需传入业务表id变量
        ProcessInstance pi = runtimeService.startProcessInstanceById(business.getProcDefId(),
                business.getId().toString(), variables);
        // 设置流程实例名称
        runtimeService.setProcessInstanceName(pi.getId(), business.getTitle());
//        BizBusiness bizBusiness = new BizBusiness().setId(business.getId()).setProcInstId(pi.getId()).setProcDefKey(pi.getProcessDefinitionKey());
        business.setId(business.getId())
                .setProcInstId(pi.getId())
                .setProcDefKey(pi.getProcessDefinitionKey());
        // 假如开始就没有任务，那就认为是中止的流程，通常是不存在的
        Set<String> strings = setAuditor(business, ActivitiConstant.RESULT_SUSPEND, business.getUserId());
        if (!strings.isEmpty() && strings.contains("1")) {
            throw new StatefulException("无审批人无法发起");
        }
        // 保存临时抄送记录
        if (variables.containsKey("cc")) {
            String cc = variables.get("cc").toString();
            if (StrUtil.isNotBlank(cc)) {
                for (String userId : cc.split(",")) {
                    int i = businessMapper.checkCC(cc, business.getProcInstId());
                    if (i == 0) {
                        businessMapper.insertCC(userId, business.getProcInstId(), business.getApplyer());
                    }
                }
            }
        }
        if (strings.isEmpty()) {
            throw new StatefulException(292, "下个节点无审批人");
        }
        return strings;
    }

    /**
     * 是否包含会签
     *
     * @param procDefId
     * @return
     */
    private Boolean isHasAllSign(String procDefId) {
        boolean isMultiInstance = false;
        BpmnModel bpmnModel = new BpmnModel();
        bpmnModel = repositoryService.getBpmnModel(procDefId);
        Collection<FlowElement> flowElements = bpmnModel.getMainProcess().getFlowElements();
        for (FlowElement flowElement : flowElements) {
            if (flowElement instanceof UserTask) {
                UserTask userTask = (UserTask) flowElement;
                if (userTask.getLoopCharacteristics() != null) {
                    isMultiInstance = true;
                    System.out.println("流程包含会签" + userTask.getId());
                }
            }
        }
        return isMultiInstance;
    }

    @Override
    public Set<String> setAuditor(BizBusiness business, int result, long currentUserId) {
        Set<String> auditors = new HashSet<>();
        List<String> auditorNames = new ArrayList<>();
        // 驳回流程结束
        if (result == 3) {
            business.setCurrentTask(ActivitiConstant.END_TASK_NAME)
                    .setStatus(ActivitiConstant.STATUS_FINISH)
                    .setResult(result)
                    .setAuditors("-");
            updateBizBusiness(business);
            getBusinessReject(business);
            pushSocket(business.getProcDefKey());
            SysUser sysUser = remoteUserService.selectSysUserByUserId(currentUserId);
            pushGetui(sysUser.getCid(), currentUserId, business.getProcDefKey());
            return null;
        }
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(business.getProcInstId()).list();
        if (null != tasks && tasks.size() > 0) {
            Task task = tasks.get(0);
            // 如果有指定下一个审批人则以指定为准
            if (business.getCustomizedUserId() != null) {
                auditors.add(business.getCustomizedUserId().toString());
            } else {
                auditors = bizNodeService.getAuditors(task.getTaskDefinitionKey(), currentUserId, business.getProcDefKey());
            }
            System.out.println(task.getTaskDefinitionKey() + "-------" + currentUserId + "--size:{}" + auditors.size());
            auditors.stream().forEach(auditor -> {
                SysUser sysUser = remoteUserService.selectSysUserByUserId(Long.valueOf(auditor));
                auditorNames.add(sysUser.getUserName());
                pushGetui(sysUser.getCid(), Long.valueOf(auditor), business.getProcDefKey());
            });
            if (null != auditors && auditors.size() > 0) {
                boolean flag = true;
                Set<BizOrSign> signs = new HashSet<>();
                // 添加审核候选人
                for (String auditor : auditors) {
                    if (currentUserId == 729 && auditor.equals("5")) {
                        auditor = "348";
                    }
                    // 判断是否为或签
                    if (auditors.size() > 1 && !isHasAllSign(business.getProcDefId())) {
                        BizOrSign bizOrSign = new BizOrSign();
                        bizOrSign.setProcessKey(business.getProcInstId());
                        bizOrSign.setUserId(Long.parseLong(auditor));
                        signs.add(bizOrSign);
                    }
                    taskService.addCandidateUser(task.getId(), auditor);
                }
                if (auditors.size() > 1 && !isHasAllSign(business.getProcDefId())) {
                    signs.forEach(e -> {
                        // 查询是否有重复
                        BizBusiness exist = orSignMapper.isExist(e.getProcessKey(), e.getUserId());
                        if (exist == null) {
                            orSignMapper.insert(e);
                        }
                    });
                }
                business.setCurrentTask(task.getName());
            } else {
                //TODO 没有审批人未配置
                auditorNames.add("未配置");
                taskService.addCandidateUser(task.getId(), "1");

                business.setAuditors(String.join("/", auditorNames));
                updateBizBusiness(business);
                pushSocket(business.getProcDefKey());
                // 审批更新自己的待办数量
                SysUser sysUserCurrent = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
                pushGetui(sysUserCurrent.getCid(), SystemUtil.getUserId(), business.getProcDefKey());
                auditors.add("1");
                return auditors;
//                SysUser user = remoteUserService.selectSysUserByUserId(getCurrentUserId());
//                bizAudit.setAuditor(user.getUserName() + "-" + user.getLoginName());
//                bizAudit.setAuditorId(user.getUserId());
//                bizAuditService.insertBizAudit(bizAudit);
//                BizBusiness bizBusiness = new BizBusiness().setId(bizAudit.getBusinessKey())
//                        .setProcInstId(bizAudit.getProcInstId());
//                businessService.setAuditor(bizBusiness, bizAudit.getResult(), getCurrentUserId());
//                String taskName = "下一节点";
//                if (StrUtil.isNotEmpty(task.getName())) {
//                    taskName = task.getName();
//                }
//                runtimeService.deleteProcessInstance(task.getProcessInstanceId(),
//                        ActivitiConstant.SUSPEND_PRE + taskName + "没有审批人");
//                business.setCurrentTask(ActivitiConstant.END_TASK_NAME).setStatus(ActivitiConstant.STATUS_SUSPEND)
//                        .setResult(ActivitiConstant.RESULT_SUSPEND);
            }
            business.setAuditors(String.join("/", auditorNames));
            updateBizBusiness(business);
            pushSocket(business.getProcDefKey());
            // 审批更新自己的待办数量
            SysUser sysUserCurrent = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
            pushGetui(sysUserCurrent.getCid(), SystemUtil.getUserId(), business.getProcDefKey());
            // TODO 邮件通知申请人\经办人验收
            if ("sid-896FC0F0-BAF7-41AD-B4BE-E48D6A51AE2F".equals(business.getTaskDefKey()) && "feedback".equals(business.getProcDefKey())) {
                Set<Long> historyUsers = getHistoryUsers(business);
                String mapInfoMail = mapInfoService.getMapInfoMail(business);
                String txt = "<body>" +
                        "<p>" +
                        business.getApplyer() + "于" + DateUtil.format(new Date(), "yyyy年MM月dd日 HH时mm分") + "提交的需求反馈（审批编号" + business.getApplyCode() + "）已上线，请验收确认！" +
                        "</p>" +
                        "<p>" +
                        "</p><br>" + mapInfoMail + "<br><h4 style='color:#aaaaaa;'><strong>提示：更多数据，请前往云管家平台查看</strong></h4></body>";
                for (Long historyUser : historyUsers) {
                    SysUser sysUser = remoteUserService.selectSysUserByUserId(historyUser);

                    if (StrUtil.isNotBlank(sysUser.getEmail())) {
                        mailService.send(txt, business.getTitle(), sysUser.getEmail(), SystemUtil.getUserNameCn(), sysUser.getCid());
                    }
                }
                SysUser sysUser = remoteUserService.selectSysUserByUserId(business.getUserId());
                String txt2 = "<body>" +
                        "<p>" +
                        "您于" + DateUtil.format(new Date(), "yyyy年MM月dd日 HH时mm分") + "提交的需求反馈（审批编号" + business.getApplyCode() + "）已上线，请验收确认！" +
                        "</p>" +
                        "<p>" +
                        "</p><br>" + mapInfoMail + "<br><h4 style='color:#aaaaaa;'><strong>提示：更多数据，请前往云管家平台查看</strong></h4></body>";
                if (StrUtil.isNotBlank(sysUser.getEmail())) {
                    mailService.send(txt2, business.getTitle(), sysUser.getEmail(), SystemUtil.getUserNameCn(), sysUser.getCid());
                }
            }
            if ("sid-656A2088-B767-403C-B56A-31E4D937E21C".equals(business.getTaskDefKey()) && "universal".equals(business.getProcDefKey())) {
                Set<Long> historyUsers = getHistoryUsers(business);
                String mapInfoMail = mapInfoService.getMapInfoMail(business);
                String txt = "<body>" +
                        "<p>" +
                        business.getApplyer() + "于" + DateUtil.format(new Date(), "yyyy年MM月dd日 HH时mm分") + "提交的通用审批（审批编号" + business.getApplyCode() + "）已处理完成，请验收确认！" +
                        "</p>" +
                        "<p>" +
                        "</p><br>" + mapInfoMail + "<br><h4 style='color:#aaaaaa;'><strong>提示：更多数据，请前往云管家平台查看</strong></h4></body>";
                for (Long historyUser : historyUsers) {
                    SysUser sysUser = remoteUserService.selectSysUserByUserId(historyUser);

                    if (StrUtil.isNotBlank(sysUser.getEmail())) {
                        mailService.send(txt, business.getTitle(), sysUser.getEmail(), SystemUtil.getUserNameCn(), sysUser.getCid());
                    }
                }
                SysUser sysUser = remoteUserService.selectSysUserByUserId(business.getUserId());
                String txt2 = "<body>" +
                        "<p>" +
                        "您于" + DateUtil.format(new Date(), "yyyy年MM月dd日 HH时mm分") + "提交的通用审批（审批编号" + business.getApplyCode() + "）已处理完成，请验收确认！" +
                        "</p>" +
                        "<p>" +
                        "</p><br>" + mapInfoMail + "<br><h4 style='color:#aaaaaa;'><strong>提示：更多数据，请前往云管家平台查看</strong></h4></body>";
                if (StrUtil.isNotBlank(sysUser.getEmail())) {
                    mailService.send(txt2, business.getTitle(), sysUser.getEmail(), SystemUtil.getUserNameCn(), sysUser.getCid());
                }
            }
            return auditors;
        } else {
            // 任务结束
            business.setCurrentTask(ActivitiConstant.END_TASK_NAME)
                    .setStatus(ActivitiConstant.STATUS_FINISH)
                    .setResult(result)
                    .setAuditors("-");
            updateBizBusiness(business);
            // 流程通过
            getBusiness2(business);
            // 临时抄送转正式
            changeCC(business);
            return auditors;
        }

    }


    @Override
    public Set<String> setInstanceUsers(BizBusiness business, int result, long currentUserId) {
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(business.getProcInstId()).list();
        Set<String> auditors = new HashSet<>();
        if (null != tasks && tasks.size() > 0) {
            Task task = tasks.get(0);
            auditors = bizNodeService.getAuditors(task.getTaskDefinitionKey(), currentUserId, business.getProcDefKey());
        }
        return auditors;
    }

    /**
     * 临时抄送转正式
     *
     * @param business
     */
    @Override
    @AuditFilter(method = "changeCC")
    public void changeCC(BizBusiness business) {
        try {
            log.error("测试抄送邮件");
            businessMapper.changeCC(business.getProcInstId());
            List<Map<String, Object>> cc = businessMapper.getCC(business.getProcInstId());
            String mapInfoMail = mapInfoService.getMapInfoMail(business);
            for (Map<String, Object> string : cc) {
                if (StrUtil.isBlank(string.get("cc_id").toString())) {
                    continue;
                }
                SysUser sysUser = remoteUserService.selectSysUserByUserId(Long.parseLong(string.get("cc_id").toString()));
                String txt2 = "<body>" +
                        "<p>" +
                        "您有一条" + business.getApplyer() + "抄送的信息，请及时查看。" +
                        "</p>" +
                        "<br>" + mapInfoMail + "<br><h4 style='color:#aaaaaa;'><strong>提示：更多数据，请前往云管家平台查看</strong></h4></body>";
                if (StrUtil.isNotBlank(sysUser.getEmail())) {
                    mailService.send(txt2, business.getTitle().replace("提交", "抄送"), sysUser.getEmail(), sysUser.getUserName(), sysUser.getCid());
                }
                pushSocketCc(business.getProcDefKey(), businessMapper.getUnReadCount(sysUser.getUserId().toString()), sysUser.getUserId().toString());
                pushGetui(sysUser.getCid(), sysUser.getUserId(), business.getProcDefKey());
            }

        } catch (Exception e) {
            log.error("临时抄送转正式" + business.getProcInstId(), e);
        }
    }

    /**
     * 查询我的转交
     *
     * @param bizBusiness
     * @return
     */
    @Override
    public List<BizBusiness> selectMyReassignment(BizBusiness bizBusiness) {
        Long userId = SystemUtil.getUserId();
        Page<Object> page = PageHelper.getLocalPage();
        PageMethod.clearPage();
        Set<Long> ids = new HashSet<>();
        List<BizReassignmentRecord> records = reassignmentRecordMapper.selectList(new LambdaQueryWrapper<BizReassignmentRecord>().eq(BizReassignmentRecord::getSourceUser, userId));
        records.stream().forEach(record -> ids.add(Long.valueOf(record.getBusinessKey())));
        bizBusiness.setIds(ids);
        PageHelper.startPage(page.getPageNum(), page.getPageSize(), page.getOrderBy());
        if (records.isEmpty()) {
            return new ArrayList<>();
        }
        bizBusiness.setDelFlag(false);
        if (StrUtil.isNotEmpty(bizBusiness.getSearchValue())) {
            bizBusiness.setTitle(bizBusiness.getSearchValue());
        }
        List<BizBusiness> select = selectBizBusinessListAll2(bizBusiness);
        select.stream().forEach(bizBusiness1 -> {
            bizBusiness1.setComment(businessMapper.getComment(bizBusiness1.getProcInstId()));
        });

        return select;
    }

    /**
     * 通过资产id 查询流程
     *
     * @param id 流程业务ID
     * @return 流程业务
     */
    @Override
    public BizBusiness selectBizBusinessByAssetId(Long id) {
        List<BizBusiness> bizBusinessList = businessMapper.selectBizBusinessByAssetId(id);
        if (bizBusinessList.isEmpty()) {
            return null;
        }
        return bizBusinessList.get(0);
    }

    /**
     * 通过物品id 查询流程
     *
     * @param goodId 物品id
     * @return 流程业务
     */
    @Override
    public BizBusiness selectBizBusinessByGoodId(Long goodId) {
        List<BizBusiness> bizBusinesses = businessMapper.selectBizBusinessByGoodId(goodId);
        if (bizBusinesses.isEmpty()) {
            return null;
        }
        return bizBusinesses.get(0);
    }

    /**
     * 报废申请列表
     *
     * @param scrappedDto
     * @return
     */
    @Override
    public List<BizBusiness> selectScrappedList(ScrappedDto scrappedDto) {
        List<BizBusiness> bizBusinesses = businessMapper.selectScrappedList(scrappedDto.getApplyCode(), scrappedDto.getAssetSn(), scrappedDto.getGoodsName(), SystemUtil.getUserId());
        return bizBusinesses;
    }

    @OperLog(title = "流程驳回事件监听", businessType = BusinessType.UPDATE)
    @Transactional(rollbackFor = Exception.class)
    public void getBusinessReject(BizBusiness business) {
        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
        String tableIdStr = business.getTableId();

        try {
            // 用车申请
            if (CAR_APPLY.equals(business.getProcDefKey())) {
                Long tableId = Long.valueOf(tableIdStr);
                QueryWrapper<BizReserveDetail> wrapper1 = new QueryWrapper<>();
                wrapper1.eq("apply_id", tableId);
                BizReserveDetail reserveDetail = reserveDetailMapper.selectOne(wrapper1);
                reserveDetail.setDelFlag("1");
                reserveDetail.setUpdateTime(new Date());
                reserveDetail.setUpdateBy(sysUser.getLoginName());
                reserveDetail.setRemark("驳回");
                reserveDetailMapper.updateById(reserveDetail);

            }
        } catch (Exception e) {
            log.error("驳回", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    private void pushSocketCc(String procDefKey, int count, String userId) {
        // 获取socket-io在线用户
        List<String> users = remoteSocketService.getUsers();
        // 刷新在线的抄送用户的抄送数量
        users.stream().forEach(user -> {
            if (user.equals(userId)) {
                MessageEntity messageEntity = new MessageEntity();
                messageEntity.setMessage(count + "");
                messageEntity.setUserId(user);
                messageEntity.setProcDefKey(procDefKey);
                remoteSocketService.cc(messageEntity);
            }
        });
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
        log.error("business663:" + String.join(",", users));
        // 实时刷新所有在线用户待办未读消息数量
        users.stream().forEach(user -> {
            TaskQuery query = taskService.createTaskQuery().taskCandidateOrAssigned(user);
//            query = query.processDefinitionKeyIn(Arrays.asList("purchase;reimburse;payment;claim;businessMoney;scrapped;review;universal;seal;contract_ys;carApply;carSubsidyApply;feedback;cover;other-charge;hcpd;xz_approval;xz_adjustment;".split(";")));
            long count = query.count();
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setMessage(count + "");
            messageEntity.setUserId(user);
            messageEntity.setProcDefKey(procDefKey);
            String ing = remoteSocketService.ing(messageEntity);
            log.info(user + "socket发送结果:" + ing);
        });
    }

    @OperLog(title = "流程结束事件监听2", businessType = BusinessType.UPDATE)
    @Transactional(rollbackFor = Exception.class)
    public void getBusiness2(BizBusiness bizBusiness) {
        Long userId;
        if (StrUtil.isNotBlank(bizBusiness.getAutoAdit())) {
            userId = 1L;
        } else {
            userId = SystemUtil.getUserId();
        }
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
        String operator = sysUser.getUserName();
        String tableIdStr = bizBusiness.getTableId();

        Date date = new Date();
        try {
            // 同步审批状态 通过 status=2
            processService.syncAuditStatus(bizBusiness.getProcDefKey(), 2, tableIdStr);
            // 通用审批
            if (UNIVERSAL.equals(bizBusiness.getProcDefKey())) {
                SysUser user = remoteUserService.selectSysUserByUserId(bizBusiness.getUserId());
                String mapInfoMail = mapInfoService.getMapInfoMail(bizBusiness);
                String txt = "<body>" +
                        "<p>" +
                        user.getUserName() + "于" + DateUtil.format(new Date(), "yyyy年MM月dd日 HH时mm分") + "提交的通用审批（审批编号" + bizBusiness.getApplyCode() + "）已验收通过，望悉知！" +
                        "</p>" +
                        "<p>" +
                        "</p><br>" + mapInfoMail + "<br><h4 style='color:#aaaaaa;'><strong>提示：更多数据，请前往云管家平台查看</strong></h4></body>";
                Set<Long> users = getHistoryUsers(bizBusiness);
                for (Long au : users) {
                    if (null != au) {
                        SysUser sysUser1 = remoteUserService.selectSysUserByUserId(au);
                        mailService.send(txt, bizBusiness.getTitle(), sysUser1.getEmail(), SystemUtil.getUserNameCn(), sysUser1.getCid());
                    }
                }
            }
            // 付款审批
            //todo
            if (bizBusiness.getProcDefKey().contains(PAYMENT)) {
                String mongoTableId = bizBusiness.getTableId();
                Query query = new Query();
                query.addCriteria(Criteria.where("_id").is(mongoTableId));
                int monthValue = LocalDateTime.now().getMonthValue();
                HashMap payment = mongoTemplatel.findOne(query, HashMap.class, "payment");
                if (payment != null) {
                    Update update = new Update();
                    update.set("result", 2);
                    update.set("state", 2);
                    mongoTemplatel.upsert(query, update, "payment");
                    backMoney(payment);
                }
            }
            // 需求反馈
            if (FEEDBACK.equals(bizBusiness.getProcDefKey())) {
                SysUser user = remoteUserService.selectSysUserByUserId(bizBusiness.getUserId());
                String mapInfoMail = mapInfoService.getMapInfoMail(bizBusiness);
                String txt = "<body>" +
                        "<p>" +
                        user.getUserName() + "于" + DateUtil.format(new Date(), "yyyy年MM月dd日 HH时mm分") + "提交的需求反馈（审批编号" + bizBusiness.getApplyCode() + "）已验收通过，望悉知！" +
                        "</p>" +
                        "<p>" +
                        "</p><br>" + mapInfoMail + "<br><h4 style='color:#aaaaaa;'><strong>提示：更多数据，请前往云管家平台查看</strong></h4></body>";
                Set<Long> users = getHistoryUsers(bizBusiness);
                for (Long au : users) {
                    if (null != au) {
                        SysUser sysUser1 = remoteUserService.selectSysUserByUserId(au);
                        mailService.send(txt, bizBusiness.getTitle(), sysUser1.getEmail(), SystemUtil.getUserNameCn(), sysUser1.getCid());
                    }
                }
            }
            // 需求反馈
            if (PR_AMOUNT.equals(bizBusiness.getProcDefKey())) {
                Long tableId = Long.valueOf(tableIdStr);

                BizProjectAmountApply bizProjectAmountApply = bizProjectAmountApplyMapper.selectById(tableId);

                HashMap<String, Object> paramMap = new HashMap<>();
                //项目编号
                paramMap.put("identifier", bizProjectAmountApply.getIdentifier());
                //项目金额
                paramMap.put("totalMoney", bizProjectAmountApply.getNewTotalMoney());
                /** 业务费 */
                paramMap.put("commission", bizProjectAmountApply.getNewCommission());
                /** 评审费 */
                paramMap.put("evaluationFee", bizProjectAmountApply.getNewEvaluationFee());
                /** 服务费  */
                paramMap.put("serviceCharge", bizProjectAmountApply.getNewServiceCharge());
                /** 分包费  */
                paramMap.put("subprojectFee", bizProjectAmountApply.getNewSubprojectFee());
                /** 虚拟税费 */
                paramMap.put("virtualTax", bizProjectAmountApply.getNewVirtualTax());
                /** 其他支出 */
                paramMap.put("otherExpenses", bizProjectAmountApply.getNewOtherExpenses());
                cn.hutool.json.JSONObject josmmap = JSONUtil.parseObj(paramMap);
                String msg = null;
                try {
                    SysConfig configUrl = remoteConfigService.findConfigUrl();
                    if ("test".equals(configUrl.getConfigValue())) {
                        msg = HttpUtil.post(UrlConstants.JPUPDATE_TEST, josmmap.toString());
                    } else {
                        msg = HttpUtil.post(UrlConstants.JPUPDATE_ONLINE, josmmap.toString());
                    }
                    JSONObject jsonObject = JSON.parseObject(msg);
                    Object o = jsonObject.get("code");
                    if ("200".equals(o.toString())) {
                        bizProjectAmountApply.setUpResult("修改项目金额成功");
                    } else {
                        bizProjectAmountApply.setUpResult("修改项目金额失败");
                    }
                } catch (Exception e) {
                    log.error("修改项目金额失败" + msg);
                    bizProjectAmountApply.setUpResult("修改项目金额失败");

                }
                bizProjectAmountApply.setUpResultInfo(msg);
                bizProjectAmountApplyMapper.updateById(bizProjectAmountApply);


                SysUser user = remoteUserService.selectSysUserByUserId(bizBusiness.getUserId());
                String mapInfoMail = mapInfoService.getMapInfoMail(bizBusiness);
                String txt = "<body>" +
                        "<p>" +
                        user.getUserName() + "于" + DateUtil.format(new Date(), "yyyy年MM月dd日 HH时mm分") + "提交的项目金额调整（审批编号" + bizBusiness.getApplyCode() + "）已通过，望悉知！" +
                        "</p>" +
                        "<p>" +
                        "</p><br>" + mapInfoMail + "<br><h4 style='color:#aaaaaa;'><strong>提示：更多数据，请前往云管家平台查看</strong></h4></body>";
                Set<Long> users = getHistoryUsers(bizBusiness);
                for (Long au : users) {
                    if (null != au) {
                        SysUser sysUser1 = remoteUserService.selectSysUserByUserId(au);
                        mailService.send(txt, bizBusiness.getTitle(), sysUser1.getEmail(), SystemUtil.getUserNameCn(), sysUser1.getCid());
                    }
                }
            }
            // 耗材盘点
            if (HCPD.equals(bizBusiness.getProcDefKey())) {
                Long tableId = Long.valueOf(tableIdStr);
                BizConsumablesInventory bizConsumablesInventory = consumablesInventoryMapper.selectById(tableId);
                Integer types = bizConsumablesInventory.getTypes();
                QueryWrapper<BizConsumablesInventoryDetail> wrapper = new QueryWrapper<>();
                wrapper.eq("apply_id", bizConsumablesInventory.getId());
                List<BizConsumablesInventoryDetail> details = consumablesInventoryDetailMapper.selectList(wrapper);
                if (details.isEmpty()) {
                    log.error("无盘点耗材" + tableId);
                    return;
                }
                Integer transType = 0;
                if (types == 1) {
                    transType = 7;
                }
                if (types == 2) {
                    transType = 8;
                }
                for (BizConsumablesInventoryDetail detail : details) {
                    // 根据spu_id统计sku库存
//                    Integer oldAmount = aaSkuMapper.countSkuAmount(detail.getSpuId());

                    AaSku aaSku = new AaSku();
                    aaSku.setAmount(detail.getInventoryNum().longValue());
                    aaSku.setOperation(types);
                    aaSku.setSpuId(detail.getSpuId());
                    aaSku.setCompanyId(bizConsumablesInventory.getCompanyId());
                    aaSku.setName(detail.getName());
                    aaSku.setOperator(operator);
                    aaSku.setCreateTime(date);
                    aaSkuMapper.insert(aaSku);

                    AaSpu aaSpu = aaSpuMapper.selectByPrimaryKey(aaSku.getSpuId());
                    // 更新现有库存
                    final Integer oldAmount = aaSpu.getStorageNum();
                    Integer storageNum = 0;
                    if (types == 1) {
                        // 盈入库
                        storageNum = aaSpu.getStorageNum() + detail.getInventoryNum();
                    }
                    if (types == 2) {
                        // 亏出库
                        storageNum = aaSpu.getStorageNum() - detail.getInventoryNum();
                    }
                    aaSpu.setStorageNum(storageNum);
                    AaTranscation aaTranscation = new AaTranscation();
                    aaTranscation.setModel(aaSpu.getModel());
                    aaTranscation.setName(aaSpu.getName());
                    aaTranscation.setCreateTime(date);
                    aaTranscation.setCreateBy(operator);
                    aaTranscation.setApplier(bizBusiness.getApplyer());
                    aaTranscation.setAmount(detail.getInventoryNum());
                    // 原spu_id 2021-12-28 李小龙更改为sku_id
                    aaTranscation.setIdentifier(aaSku.getId());
//                            AaSpu aaSpu = aaSpuMapper.selectByPrimaryKey(aaSku.getSpuId());
                    aaTranscation.setItemSn(aaSpu.getSpuSn());

                    aaTranscation.setTransType(transType);
                    aaTranscation.setCompanyId(aaSpu.getCompanyId());
                    // 申请人部门
                    aaTranscation.setDeptId(bizConsumablesInventory.getDeptId());
                    aaTranscation.setItemType(2);
                    // 经办人
                    aaTranscation.setOperator(bizBusiness.getApplyer());
                    //  入库求和减去出库求和
                    aaTranscation.setSpuAmount(oldAmount);
                    aaTranscationMapper.insertSelective(aaTranscation);
                    aaSpu.setUpdateTime(new Date());
                    aaSpu.setUpdateBy(bizBusiness.getApplyer());
                    aaSpuMapper.updateByPrimaryKeySelective(aaSpu);
                }
            }
            // 采购申请
            if (PURCHASE.equals(bizBusiness.getProcDefKey())) {

                //给申请人公司的采购人员发送通知
                Set<Long> longs = remoteUserService.selectUserIdsHasRoles("81");
                // 过滤有申请人所在部门审批权限的用户
                for (Long aLong : longs) {
                    SysUser sysUserAudit = remoteUserService.selectSysUserByUserId(aLong);
                    if (StrUtil.isNotEmpty(sysUserAudit.getOtherDeptId())) {
                        List<String> strings = Arrays.asList(sysUserAudit.getOtherDeptId().split(";"));
                        if (strings.contains(sysUser.getDeptId().toString())) {
                            String mapInfoMail = mapInfoService.getMapInfoMail(bizBusiness);
                            String txt = "<body>" +
                                    "<p>" +
                                    sysUser.getUserName() + "提交的采购申请在" + DateUtil.format(new Date(), "yyyy年MM月dd日 HH时mm分") + "已通过，请及时采购。" +
                                    "</p>" +
                                    "<p>" +
                                    "</p><br>" + mapInfoMail + "<br><h4 style='color:#aaaaaa;'><strong>提示：更多数据，请前往云管家平台查看</strong></h4></body>";
                            mailService.send(txt, "采购提醒", sysUserAudit.getEmail(), sysUser.getUserName(), sysUserAudit.getCid());
                        }
                    }
                }
                // 查询采购物品信息
                Example example = new Example(BizGoodsInfo.class);
                example.selectProperties("id", "goodType").and().andEqualTo("purchaseId", bizBusiness.getTableId());
                List<BizGoodsInfo> bizGoodsInfos = goodsInfoMapper.selectByExample(example);
                for (BizGoodsInfo bizGoodsInfo : bizGoodsInfos) {
                    // 保存采购记录
                    BizGoodsRecord goodsRecord = new BizGoodsRecord();
                    goodsRecord.setGoodsId(bizGoodsInfo.getId());
                    goodsRecord.setLink("待采购");
                    goodsRecord.setUserName(bizBusiness.getApplyer());
                    goodsRecord.setDelFlag("0");
                    goodsRecord.setCreateBy(remoteUserService.selectSysUserByUserId(bizBusiness.getUserId()).getLoginName());
                    goodsRecord.setCreateTime(new Date());
                    goodsRecordMapper.insert(goodsRecord);
                }


            }
            // 用车申请
            if (CAR_APPLY.equals(bizBusiness.getProcDefKey())) {
                Long tableId = Long.valueOf(tableIdStr);
                QueryWrapper<BizReserveDetail> wrapper1 = new QueryWrapper<>();
                wrapper1.eq("apply_id", tableId);
                BizReserveDetail reserveDetail = reserveDetailMapper.selectOne(wrapper1);
                reserveDetail.setCarStatus("1");
                reserveDetailMapper.updateById(reserveDetail);

            }
            // 还车补贴申请
            if (CAR_SUBSIDY_APPLY.equals(bizBusiness.getProcDefKey())) {

            }
            // 退货出库
            if (GOODS_REJECTED.equals(bizBusiness.getProcDefKey())) {
                Long tableId = Long.valueOf(tableIdStr);
                BizGoodsRejectedApply info = goodsRejectedApplyMapper.selectById(tableId);
                List<BizGoodsRejectedDetail> goodsRejectedDetailList = goodsRejectedDetailService.getListByGoodsId(tableId);
                for (BizGoodsRejectedDetail bizGoodsRejectedDetail : goodsRejectedDetailList) {

                    String type = bizGoodsRejectedDetail.getItemType().toString();
                    if (type.equals("1")) {
                        Asset byId = assetService.getById(bizGoodsRejectedDetail.getAssertId());
                        if (byId == null) {
                            throw new StatefulException("资产信息异常");
                        }
                        byId.setState(9);
                        int update = assetService.update(byId);
                        if (update != 1) {
                            throw new StatefulException("资产退货失败");
                        }
                        AaTranscation aaTranscation = new AaTranscation();
                        aaTranscation.setModel(byId.getModel());
                        aaTranscation.setName(byId.getName());
                        aaTranscation.setCreateTime(date);
                        aaTranscation.setCreateBy(operator);
                        aaTranscation.setApplier(bizBusiness.getApplyer());
                        aaTranscation.setAmount(1);
                        aaTranscation.setIdentifier(byId.getId());
                        aaTranscation.setItemSn(byId.getAssetSn());
                        aaTranscation.setTransType(6);
                        aaTranscation.setCompanyId(byId.getCompanyId());
                        // 资产所属部门
                        aaTranscation.setDeptId(byId.getDeptId());
                        aaTranscation.setItemType(1);
                        // 经办人
                        aaTranscation.setOperator(operator);
                        aaTranscation.setSpuAmount(1);
                        aaTranscationMapper.insertSelective(aaTranscation);
                    }
                    if (type.equals("2")) {

                        // 2021-12-05 按李小龙设计，耗材出库往sku中插入一条数据（必要字段operation=2，spu_id ， amount）
                        AaSku aaSku = aaSkuMapper.selectById(bizGoodsRejectedDetail.getAssertId());
                        // 根据spu_id统计sku库存
                        Integer oldAmount = aaSkuMapper.countSkuAmount(aaSku.getSpuId());

                        aaSku.setId(null);
                        aaSku.setAmount(bizGoodsRejectedDetail.getNum().longValue());
                        aaSku.setOperation(2);
                        aaSkuMapper.insert(aaSku);
                        AaSpu aaSpu = aaSpuMapper.selectByPrimaryKey(aaSku.getSpuId());
                        AaTranscation aaTranscation = new AaTranscation();
                        aaTranscation.setModel(aaSpu.getModel());
                        aaTranscation.setName(aaSpu.getName());
                        aaTranscation.setCreateTime(date);
                        aaTranscation.setCreateBy(operator);
                        aaTranscation.setApplier(bizBusiness.getApplyer());
                        aaTranscation.setAmount(bizGoodsRejectedDetail.getNum());
                        // 原spu_id 2021-12-28 李小龙更改为sku_id
                        aaTranscation.setIdentifier(aaSku.getId());
//                            AaSpu aaSpu = aaSpuMapper.selectByPrimaryKey(aaSku.getSpuId());
                        aaTranscation.setItemSn(aaSpu.getSpuSn());
                        aaTranscation.setTransType(6);
                        aaTranscation.setCompanyId(aaSpu.getCompanyId());
                        // 申请人部门
                        aaTranscation.setDeptId(info.getDeptId().longValue());
                        aaTranscation.setItemType(2);
                        // 经办人
                        aaTranscation.setOperator(operator);
                        aaTranscation.setSpuAmount(oldAmount);
                        aaTranscationMapper.insertSelective(aaTranscation);
                        aaSpu.setStorageNum(aaSpu.getStorageNum() - bizGoodsRejectedDetail.getNum());
                        aaSpuMapper.updateByPrimaryKeySelective(aaSpu);
                    }
                }
            }
            // 报废出库
            if (SCRAPPED_KEY.equals(bizBusiness.getProcDefKey())) {
                Long tableId = Long.valueOf(tableIdStr);
                BizScrappedApply bizScrappedApply = scrappedApplyMapper.selectBizScrappedApplyById(tableId);
                if (bizScrappedApply == null) {
                    throw new StatefulException("申请信息异常");
                }
                Asset byId = assetService.getById(bizScrappedApply.getAssertId());
                if (byId == null) {
                    throw new StatefulException("资产信息异常");
                }
                byId.setState(6);
                int update = assetService.update(byId);
                if (update != 1) {
                    throw new StatefulException("资产报废失败");
                }
                AaTranscation aaTranscation = new AaTranscation();
                // 2021-12-21 李小龙说按王香圆需求加规格型号
                aaTranscation.setModel(byId.getModel());
                aaTranscation.setName(byId.getName());
                aaTranscation.setCreateTime(date);
                aaTranscation.setCreateBy(operator);
                aaTranscation.setApplier(bizBusiness.getApplyer());
                aaTranscation.setAmount(1);
                aaTranscation.setIdentifier(byId.getId());
                aaTranscation.setItemSn(byId.getAssetSn());
                aaTranscation.setTransType(5);
                aaTranscation.setCompanyId(byId.getCompanyId());
                // 资产所属部门
                aaTranscation.setDeptId(byId.getDeptId());
                aaTranscation.setItemType(1);
                // 经办人
                aaTranscation.setOperator(bizBusiness.getApplyer());
                aaTranscation.setSpuAmount(1);
                aaTranscationMapper.insertSelective(aaTranscation);
            }
            // 领用申请
            if (CLAIM_KEY.equals(bizBusiness.getProcDefKey())) {
                Long tableId = Long.valueOf(tableIdStr);
                BizClaimApply claimApply = claimApplyMapper.selectByPrimaryKey(tableId);
                Example example = new Example(BizClaimGoods.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("claimId", claimApply.getId());
                List<BizClaimGoods> bizClaimGoods = claimGoodsMapper.selectByExample(example);
                bizClaimGoods.stream().forEach(bizClaimGoods1 -> {
//                    AaSku aaSku = aaSkuMapper.selectByPrimaryKey(bizClaimGoods1.getGoodsId());
                    AaSpu aaSpu = aaSpuMapper.selectByPrimaryKey(bizClaimGoods1.getGoodsId());
                    if (aaSpu == null) {
                        log.error("领用申请id：{},spuId" + bizClaimGoods1.getGoodsId() + "为空", claimApply.getId());
                        return;
                    }
//                    for (BizClaimGoods bizClaimGood : bizClaimGoods) {
//                        if (bizClaimGood.getGoodsId().equals(aaSpu.getId())) {
                    // 根据spu_id统计sku库存
                    Integer oldAmount = aaSkuMapper.countSkuAmount(aaSpu.getId());
//                            aaSku.setAmount(aaSku.getAmount() - bizClaimGoods1.getClaimNum());
//                            aaSkuMapper.updateByPrimaryKeySelective(aaSku);
                    // 2021-12-05 按李小龙设计，耗材出库往sku中插入一条数据（必要字段operation=2，spu_id ， amount）
                    AaSku aaSku = new AaSku();
                    aaSku.setAmount(bizClaimGoods1.getClaimNum());
                    aaSku.setSpuId(aaSpu.getId());
                    aaSku.setOperation(2);
                    aaSkuMapper.insert(aaSku);

                    AaTranscation aaTranscation = new AaTranscation();
                    aaTranscation.setModel(aaSpu.getModel());
                    aaTranscation.setName(aaSpu.getName());
                    aaTranscation.setCreateTime(date);
                    aaTranscation.setCreateBy(operator);
                    aaTranscation.setApplier(bizBusiness.getApplyer());
                    aaTranscation.setAmount(bizClaimGoods1.getClaimNum().intValue());
                    // 原spu_id 2021-12-28 李小龙更改为sku_id
                    aaTranscation.setIdentifier(aaSku.getId());
//                            AaSpu aaSpu = aaSpuMapper.selectByPrimaryKey(aaSku.getSpuId());
                    aaTranscation.setItemSn(aaSpu.getSpuSn());
                    aaTranscation.setTransType(4);
                    aaTranscation.setCompanyId(aaSpu.getCompanyId());
                    // 申请人部门
                    aaTranscation.setDeptId(claimApply.getDeptId().longValue());
                    aaTranscation.setItemType(2);
                    // 经办人
                    aaTranscation.setOperator(operator);
                    aaTranscation.setSpuAmount(oldAmount);
                    aaTranscationMapper.insertSelective(aaTranscation);
//                        }
//                    }
                    aaSpu.setStorageNum(aaSpu.getStorageNum() - bizClaimGoods1.getClaimNum().intValue());
                    aaSpuMapper.updateByPrimaryKeySelective(aaSpu);
                });
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    private Set<Long> getHistoryUsers(BizBusiness bizBusiness) {
        HiTaskVo hiTaskVo = new HiTaskVo();
        hiTaskVo.setProcInstId(bizBusiness.getProcInstId());
        List<HiTaskVo> historyTaskList = auditService.getHistoryTaskList(hiTaskVo);
        Set<Long> users = new HashSet<>();
        historyTaskList.stream().filter(a -> a.getAuditorId() != null).forEach(historyTaskVo -> users.add(historyTaskVo.getAuditorId()));
        return users;
    }

    @Resource
    MongoTemplate mongoTemplate;

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
                BigDecimal confirmed_quota = new BigDecimal((String) month.get("confirmed_quota"));

                month.put("confirmed_quota", confirmed_quota.add(bigDecimal) + "");

                Update update = new Update();
                update.set("month" + monthValue, month);
                Query query1 = new Query();
                query1.addCriteria(Criteria.where("_id").is(map.get("_id")));
                mongoTemplate.upsert(query1, update, "budget_statistics");
            }

        }
    }

    /**
     * 抄送
     *
     * @param businessKey
     * @param ccList
     */
    @Override
    public void ccEmail(String businessKey, List<String> ccList, String operator) {

        try {
            BizBusiness bizBusiness = businessMapper.selectByPrimaryKey(businessKey);
            ccList.stream().forEach(cc -> {
                System.out.println("--------------------------------");
                System.out.println(cc);
                System.out.println(operator);
                System.out.println(bizBusiness.getProcInstId());
                System.out.println("-----------------------------------");
                int i = businessMapper.checkCC(cc, bizBusiness.getProcInstId());
                if (i == 0) {
                    businessMapper.insertCC(cc, bizBusiness.getProcInstId(), "");
                }
            });

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    /**
     * 流程统计分析1
     *
     * @param bizBusiness
     * @return
     */
//    @Override
    public List<Map<String, Object>> selectCount(BizBusiness bizBusiness) {
        List<Map<String, Object>> maps = new ArrayList<>();
        List<Map<String, Object>> definedList = actReProcdefMapper.getDefinedList();
        for (Map<String, Object> map : definedList) {
            Example example = new Example(BizBusiness.class);
            Example.Criteria criteria = example.createCriteria();
            if (StrUtil.isNotBlank(bizBusiness.getApplyer())) {
                criteria.andLike("applyer", "%" + bizBusiness.getApplyer() + "%");
            }
            if (null != bizBusiness.getStartTime() && null != bizBusiness.getEndTime()) {
                criteria.andBetween("applyTime", bizBusiness.getStartTime(), bizBusiness.getEndTime());
            }
            if (null != bizBusiness.getResult()) {
                criteria.andEqualTo("result", bizBusiness.getResult());
            }
            if (null != bizBusiness.getStatus()) {
                criteria.andEqualTo("status", bizBusiness.getStatus());
            }
            criteria.andEqualTo("delFlag", 0);
            Map<String, Object> map1 = new HashMap<>();
            String key = map.get("id").toString();
            if (StrUtil.isNotBlank(key)) {
                criteria.andEqualTo("procDefKey", key);
            }
            int count = businessMapper.selectCountByExample(example);
            map1.put("name", map.get("name").toString());
            map1.put("count", count);
            maps.add(map1);

        }
        return maps;
    }

    @Override
    public List<Map<String, Object>> selectCount1(BizBusiness bizBusiness) {

        List<Map<String, Object>> maps = new ArrayList<>();
        List<Map<String, Object>> definedList = actReProcdefMapper.getDefinedList();
        for (Map<String, Object> map : definedList) {
            QueryWrapper<BizBusinessPlus> wrapper = new QueryWrapper<>();
            wrapper.between(null != bizBusiness.getDuration() && null != bizBusiness.getDurationStart(), "a.duration", bizBusiness.getDurationStart(), bizBusiness.getDuration());
            wrapper.like(StrUtil.isNotBlank(bizBusiness.getApplyer()), "a.applyer", bizBusiness.getApplyer());
            wrapper.eq(null != bizBusiness.getResult(), "a.result", bizBusiness.getResult());
            wrapper.eq(null != bizBusiness.getStatus(), "a.status", bizBusiness.getStatus());
            wrapper.eq("a.del_flag", 0);
            wrapper.like(null != bizBusiness.getCompanyId(), "a.ancestors", "," + bizBusiness.getCompanyId() + ",");
            wrapper.eq(null != bizBusiness.getDeptId(), "a.dept_id", bizBusiness.getDeptId());
            Map<String, Object> map1 = new HashMap<>(2);
            String key = map.get("id").toString();
            wrapper.eq(StrUtil.isNotBlank(key), "a.proc_def_key", key);
            String sql = "1=1";
            if (StrUtil.isNotBlank(bizBusiness.getSingleDate())) {
                DateTime singleDate = DateUtil.parse(bizBusiness.getSingleDate());
                String start = DateUtil.format(singleDate, "yyyy-MM-dd") + " 00:00:00";
                String end = DateUtil.format(singleDate, "yyyy-MM-dd") + " 23:59:59";
                sql = "bu.apply_time between '" + start + "' and '" + end + "'";
            } else if (null != bizBusiness.getStartTime() && null != bizBusiness.getEndTime()) {
                String start = DateUtil.format(bizBusiness.getStartTime(), "yyyy-MM-dd") + " 00:00:00";
                String end = DateUtil.format(bizBusiness.getEndTime(), "yyyy-MM-dd") + " 23:59:59";
                sql = "bu.apply_time between '" + start + "' and '" + end + "'";
            } else {
                Date lastMon = com.ruoyi.activiti.utils.DateUtil.getLastTimeInterval();
                Date lastSun = DateUtil.offsetDay(lastMon, 6);
                sql = "bu.apply_time between '" + DateUtil.format(lastMon, "yyyy-MM-dd") + " 00:00:00" + "' and '" + DateUtil.format(lastSun, "yyyy-MM-dd") + " 23:59:59" + "'";
            }
            List<Map<String, Object>> maps1 = businessPlusMapper.listProc(wrapper, sql);
            Double average = maps1.stream().mapToDouble(t -> Double.valueOf(t.get("duration").toString())).average().orElse(0D);
            int count = businessPlusMapper.listCount(wrapper, sql);
            map1.put("name", map.get("name").toString());
            map1.put("count", count);
            map1.put("average", average / 1000 / 60 / 60);
            maps.add(map1);

        }
        return maps;
    }

    @Override
    public List<Map<String, Object>> selectCount2(BizBusiness bizBusiness) {

        List<Map<String, Object>> maps = new ArrayList<>();
        List<Map<String, Object>> companies = businessPlusMapper.listCompany();
        for (Map<String, Object> map : companies) {
            QueryWrapper<BizBusinessPlus> wrapper = new QueryWrapper<>();
            wrapper.between(null != bizBusiness.getDuration() && null != bizBusiness.getDurationStart(), "a.duration", bizBusiness.getDurationStart(), bizBusiness.getDuration());
            wrapper.like(StrUtil.isNotBlank(bizBusiness.getApplyer()), "a.applyer", bizBusiness.getApplyer());
            wrapper.eq(null != bizBusiness.getResult(), "a.result", bizBusiness.getResult());
            wrapper.eq(null != bizBusiness.getStatus(), "a.status", bizBusiness.getStatus());
            wrapper.eq("a.del_flag", 0);
            wrapper.eq(null != bizBusiness.getDeptId(), "a.dept_id", bizBusiness.getDeptId());
            Map<String, Object> map1 = new HashMap<>(2);
            wrapper.eq(StrUtil.isNotBlank(bizBusiness.getProcDefKey()), "a.proc_def_key", bizBusiness.getProcDefKey());
            String name = map.get("dept_name").toString();
            String id = map.get("dept_id").toString();
            wrapper.like(null != id, "a.ancestors", "," + id + ",");
            String sql = "1=1";
            if (StrUtil.isNotBlank(bizBusiness.getSingleDate())) {
                DateTime singleDate = DateUtil.parse(bizBusiness.getSingleDate());
                String start = DateUtil.format(singleDate, "yyyy-MM-dd") + " 00:00:00";
                String end = DateUtil.format(singleDate, "yyyy-MM-dd") + " 23:59:59";
                sql = "bu.apply_time between '" + start + "' and '" + end + "'";
            } else if (null != bizBusiness.getStartTime() && null != bizBusiness.getEndTime()) {
                String start = DateUtil.format(bizBusiness.getStartTime(), "yyyy-MM-dd") + " 00:00:00";
                String end = DateUtil.format(bizBusiness.getEndTime(), "yyyy-MM-dd") + " 23:59:59";
                sql = "bu.apply_time between '" + start + "' and '" + end + "'";
            } else {
                Date lastMon = com.ruoyi.activiti.utils.DateUtil.getLastTimeInterval();
                Date lastSun = DateUtil.offsetDay(lastMon, 6);
                sql = "bu.apply_time between '" + DateUtil.format(lastMon, "yyyy-MM-dd") + " 00:00:00" + "' and '" + DateUtil.format(lastSun, "yyyy-MM-dd") + " 23:59:59" + "'";
            }
            List<Map<String, Object>> maps1 = businessPlusMapper.listProc(wrapper, sql);

            Double average = maps1.stream().mapToDouble(t -> Double.valueOf(t.get("duration").toString())).average().orElse(0D);

//            long sum = maps1.stream().filter(m -> m.containsKey("duration")).mapToLong(t -> Long.valueOf(t.get("duration").toString())).sum();

            int count = businessPlusMapper.listCount(wrapper, sql);

            map1.put("name", name);
            map1.put("count", count);
            map1.put("average", average / 1000 / 60 / 60);
//            map1.put("sum",sum);
            maps.add(map1);

        }
        return maps;
    }

    /**
     * 查询需求
     *
     * @param needManageDto
     * @return
     */
    @Override
    public List<NeedManageDto> getNeed(NeedManageDto needManageDto) {
        QueryWrapper<NeedManageDto> wrapper = new QueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(needManageDto.getApplyCode()), "f.apply_code", needManageDto.getApplyCode());
        wrapper.eq("b.proc_def_key", "feedback");
        wrapper.eq("b.del_flag", "0");
        wrapper.eq(needManageDto.getStatus() != null, "b.status", needManageDto.getStatus());
        wrapper.eq(needManageDto.getResult() != null, "b.result", needManageDto.getResult());
        wrapper.like(StrUtil.isNotBlank(needManageDto.getTitle()), "b.title", needManageDto.getTitle());
        wrapper.between(needManageDto.getStartDate() != null && needManageDto.getEndDate() != null, "b.apply_time", needManageDto.getStartDate() + " 00:00:00", needManageDto.getEndDate() + " 23:59:59");
        wrapper.eq(needManageDto.getDeptId() != null, "f.dept_id", needManageDto.getDeptId());
        wrapper.eq(needManageDto.getCompanyId() != null, "f.company_id", needManageDto.getCompanyId());
        wrapper.eq(StrUtil.isNotBlank(needManageDto.getAffiliatedSystem()), "f.affiliated_system", needManageDto.getAffiliatedSystem());
        // 所属系统
        wrapper.in(needManageDto.getAffiliatedSystemList() != null && !needManageDto.getAffiliatedSystemList().isEmpty(), "f.affiliated_system", needManageDto.getAffiliatedSystemList());
        wrapper.orderByDesc("b.apply_time");
        List<NeedManageDto> need = businessPlusMapper.getNeed(wrapper);
        return need;
    }
}
