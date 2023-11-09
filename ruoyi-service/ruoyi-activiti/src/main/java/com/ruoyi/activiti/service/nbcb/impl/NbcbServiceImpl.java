package com.ruoyi.activiti.service.nbcb.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.nbcb.sdk.OpenSDK;
import com.ruoyi.activiti.domain.nbcb.NBCBDto;
import com.ruoyi.activiti.domain.nbcb.NbcbAccountInfo;
import com.ruoyi.activiti.domain.proc.ActRuTask;
import com.ruoyi.activiti.domain.proc.BizAudit;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.mapper.ActRuTaskMapper;
import com.ruoyi.activiti.mapper.nbcb.NbcbAccountInfoMapper;
import com.ruoyi.activiti.properties.NbcbProperties;
import com.ruoyi.activiti.service.ActTaskService;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.service.nbcb.NbcbSevice;
import com.ruoyi.activiti.utils.CodeUtil;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.feign.RemoteConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author yrb
 * @Date 2023/6/6 9:49
 * @Version 1.0
 * @Description 宁波银行财资平台自动打款相关接口
 */
@Service
@Slf4j
public class NbcbServiceImpl implements NbcbSevice {
    @Resource
    MongoTemplate mongoTemplate;
    private final IBizBusinessService businessService;
    private final NbcbProperties nbcbProperties;
    private final NbcbAccountInfoMapper nbcbAccountInfoMapper;
    private final ActTaskService actTaskService;
    private final ActRuTaskMapper actRuTaskMapper;
    private final RemoteConfigService remoteConfigService;

    public NbcbServiceImpl(IBizBusinessService businessService,
                           NbcbProperties nbcbProperties,
                           NbcbAccountInfoMapper nbcbAccountInfoMapper,
                           ActTaskService actTaskService,
                           ActRuTaskMapper actRuTaskMapper,
                           RemoteConfigService remoteConfigService) {
        this.businessService = businessService;
        this.nbcbProperties = nbcbProperties;
        this.nbcbAccountInfoMapper = nbcbAccountInfoMapper;
        this.actTaskService = actTaskService;
        this.actRuTaskMapper = actRuTaskMapper;
        this.remoteConfigService = remoteConfigService;
    }

    private NbcbAccountInfo findAccountInfo(String tableId) {
        // 根据id获取详情
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(tableId));
        HashMap payment = mongoTemplate.findOne(query, HashMap.class, "payment");
        throwException(payment, "mongodb中该id:" + tableId + "新付款详情");
        HashMap information = (HashMap) payment.get("BaseInformation");
        delete_Id(payment);
        HashMap accountInformation = (HashMap) information.get("account_information");
        HashMap reimbursementAccount = (HashMap) accountInformation.get("reimbursement_account");
        NbcbAccountInfo nbcbAccountInfo = new NbcbAccountInfo();
        nbcbAccountInfo.setRcvAcc((String) reimbursementAccount.get("accountNum"));
        nbcbAccountInfo.setRcvName((String) reimbursementAccount.get("name"));
        nbcbAccountInfo.setAmt(String.valueOf(payment.get("projectMoney")));
        nbcbAccountInfo.setApplyCode(String.valueOf(payment.get("applyCode")));
        nbcbAccountInfo.setCreateTime(new Date());
        // 处理银行名称
        String bank = (String) reimbursementAccount.get("bank");
        String branchBank = (String) reimbursementAccount.get("branchBank");
        String keyword = "支行";
        if (bank.contains("分行") || branchBank.contains("分行")) {
            keyword = "分行";
        }
        if (StrUtil.isBlank(branchBank) || bank.contains(keyword)) {
            nbcbAccountInfo.setRcvBankName(bank);
        } else if (StrUtil.isNotBlank(branchBank) && branchBank.contains(keyword) && branchBank.contains(bank)) {
            nbcbAccountInfo.setRcvBankName(branchBank);
        } else {
            nbcbAccountInfo.setRcvBankName(bank + branchBank);
        }
        return nbcbAccountInfo;
    }

    /**
     * @return
     */
    @Override
    public String singleTransfer(NBCBDto nbcbDto) {
        try {
            Map<String, Object> paramMap = Maps.newHashMap();
            // 客户号
            paramMap.put("custId", nbcbDto.getCustId());
            // 交易流水号
            paramMap.put("serialNo", nbcbDto.getBatchSerialNo());
            // 单位代码
            paramMap.put("corpCode", nbcbDto.getBusinessCode());
            // 收款账号
            paramMap.put("rcvAcc", nbcbDto.getTotalAmt());
            // 付款账号
            paramMap.put("payAcc", nbcbDto.getTotalAmt());
            // 收款方行名
            paramMap.put("rcvBankName", nbcbDto.getTotalAmt());
            // 收款户名
            paramMap.put("rcvName", nbcbDto.getTotalAmt());
            // 用途
            paramMap.put("purpose", nbcbDto.getTotalAmt());
            // 金额
            paramMap.put("amt", nbcbDto.getTotalAmt());
            Map<String, Object> data = Maps.newHashMap();
            data.put("Data", paramMap);
            // 返回报文
            String response = OpenSDK.send(nbcbDto.getProductID(), nbcbDto.getServiceID(), JSON.toJSONString(data));
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * @return
     */
    @Override
    public String querySingleTransferResult(NBCBDto nbcbDto) {
        try {
            Map<String, Object> paramMap = Maps.newHashMap();
            // 客户号
            paramMap.put("custId", nbcbDto.getCustId());
            // 交易流水号
            paramMap.put("serialNo", nbcbDto.getBatchSerialNo());
            Map<String, Object> data = Maps.newHashMap();
            data.put("Data", paramMap);
            // 返回报文
            String response = OpenSDK.send(nbcbDto.getProductID(), nbcbDto.getServiceID(), JSON.toJSONString(data));
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 付款单提交接口
     *
     * @param bizBusiness 申请单主要信息
     * @return result
     */
    @Override
    public void paymentOrderApiAdd(BizBusiness bizBusiness) {
        try {
            // 判断审批单所属公司是否需要推送
            String corpCode = getCorpCode(bizBusiness.getCompanyId());
            if (corpCode == null) return;
            NbcbAccountInfo accountInfo = findAccountInfo(bizBusiness.getTableId());
            Map<String, Object> paramMap = Maps.newHashMap();
            // 客户号
            paramMap.put("custId", nbcbProperties.getCustId());
            // 单位编码
            paramMap.put("corpCode", corpCode);
            // 付款单号
            String payBillNo = CodeUtil.getCode("AL", 20);
            accountInfo.setPayBillNo(payBillNo);
            paramMap.put("payBillNo", payBillNo);
            // 付款账号(非必填)
            paramMap.put("payAcc", getPayAcc(corpCode));
            // 供应商名称(收款人姓名)
            paramMap.put("supplyName", accountInfo.getRcvName());
            // 供应商账号(收款人账号)
            String rcvAcc = accountInfo.getRcvAcc();
            // 收款账号处理
            String acc = formatAcc(rcvAcc);
            accountInfo.setRcvAcc(rcvAcc);
            paramMap.put("supplyAcc", acc);
            // 供应商开户行名(收款人开户行名)
            String rcvBankName = accountInfo.getRcvBankName();
            String bankName = formatBankName(rcvBankName);
            paramMap.put("supplyBankName", bankName);
            // 供应商开户行行号
            // paramMap.put("supplyBankNo", nbcbDto.getSupplyBankNo());
            // 付款单金额
            paramMap.put("amt", accountInfo.getAmt());
            Map<String, Object> data = Maps.newHashMap();
            data.put("Data", paramMap);
            String response = OpenSDK.send("paymentOrderApiAdd", "paymentOrderApiAdd", JSON.toJSONString(data));
            String retCode = getResponseMap(response).get("retCode");
            if ("0000".equals(retCode)) {
                accountInfo.setStatus(1);
            } else {
                accountInfo.setStatus(0);
                accountInfo.setResponse(response);
            }
            // 通用业务类主键ID
            accountInfo.setBusinessKey(bizBusiness.getId().toString());
            nbcbAccountInfoMapper.insert(accountInfo);
            for (int i = 0; i < 11; i++) {
                log.error(response);
            }
        } catch (Exception e) {
            log.error("提交付款单发生异常=====" + e);
        }
    }

    /**
     * 自动审批（每天定时执行）
     */
    @Override
    @Scheduled(cron = "0 0 1 * * ?")
    public void autoAudit() {
        try {
            SysConfig sysConfig = remoteConfigService.findConfigUrl();
            // 测试环境不执行该定时任务
            if ("test".equals(sysConfig.getConfigValue())) {
                return;
            }
            log.info("财资平台自动审批定时任务开始执行=================================================");
            // 查询推送成功且未自动审批的付款单
            QueryWrapper<NbcbAccountInfo> queryWrapper = new QueryWrapper<>();
            // 推送成功
            queryWrapper.eq("status", 1);
            // 正在处理中
            queryWrapper.eq("result", 1);
            List<NbcbAccountInfo> list = nbcbAccountInfoMapper.selectList(queryWrapper);
            if (CollUtil.isEmpty(list)) return;
            int num = list.size() + 1;
            log.info("查到" + num + "个付款单,开始自动审批===================================");
            for (NbcbAccountInfo nbcbAccountInfo : list) {
                String businessKey = nbcbAccountInfo.getBusinessKey();
                if (StrUtil.isBlank(businessKey)) continue;
                BizBusiness bizBusiness = businessService.selectBizBusinessById(businessKey);
                if (bizBusiness != null && bizBusiness.getStatus() != 1) {
                    NbcbAccountInfo info = new NbcbAccountInfo();
                    info.setId(nbcbAccountInfo.getId());
                    // 其他渠道付款
                    info.setResult(4);
                    nbcbAccountInfoMapper.updateById(info);
                    continue;
                }
                // 查询付款单状态
                log.error("尝试获取付款单号==========================================");
                String payBillNo = nbcbAccountInfo.getPayBillNo();
                log.error("付款单号为===========================" + payBillNo);
                if (StrUtil.isBlank(payBillNo)) continue;
                log.error("尝试获取付款信息===============================");
                String response = paymentOrderApiQry(payBillNo);
                log.error("获取到的付款信息为：===================" + response);
                log.error("获取到的付款信息为：===================" + response);
                Map<String, String> responseMap = getResponseMap(response);
                String retCode = responseMap.get("retCode");
                log.error("解析返回值获取返回code===================================" + retCode);
                log.error("解析返回值获取返回code===================================" + retCode);
                log.error("解析返回值获取返回code===================================" + retCode);
                log.error("解析返回值获取返回code===================================" + retCode);
                if (StrUtil.isNotBlank(retCode) && "0000".equals(retCode)) {
                    String status = responseMap.get("status");
                    log.error("获取返回的status===================================" + status);
                    log.error("获取返回的status===================================" + status);
                    log.error("获取返回的status===================================" + status);
                    log.error("获取返回的status===================================" + status);
                    if (StrUtil.isNotBlank(status)) {
                        Long id = nbcbAccountInfo.getId();
                        if ("2".equals(status)) {
                            log.error("开始自动审批==========================");
                            autoAudit(businessKey, id);
                        } else if ("-2".equals(status)) {
                            log.error("作废付款单=======================");
                            NbcbAccountInfo nbcbAccountInfo1 = new NbcbAccountInfo();
                            nbcbAccountInfo1.setId(id);
                            // 付款单已作废
                            nbcbAccountInfo1.setResult(3);
                            nbcbAccountInfoMapper.updateById(nbcbAccountInfo1);
                        }
                    }
                }
                Thread.sleep(1000);
            }
            log.error("定时任务执行完成========================");
            log.error("定时任务执行完成========================");
            log.error("定时任务执行完成========================");
            log.error("定时任务执行完成========================");
            log.error("定时任务执行完成========================");
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    private void autoAudit(String businessKey, Long id) {
        // 自动审批通过
        BizAudit bizAudit = new BizAudit();
        BizBusiness bizBusiness = businessService.selectBizBusinessById(businessKey);
        bizAudit.setApplyer(bizBusiness.getApplyer());
        bizAudit.setBusinessKey(bizBusiness.getId());
//            bizAudit.setComment("自动审批");
        bizAudit.setProcDefKey(bizBusiness.getProcDefKey());
        bizAudit.setProcName(bizBusiness.getTitle());
        bizAudit.setResult(2);
        String procInstId = bizBusiness.getProcInstId();
        bizAudit.setProcInstId(procInstId);
        List<ActRuTask> actRuTasks = actRuTaskMapper.selectListByProcInstId(procInstId);
        ActRuTask actRuTask = actRuTasks.get(0);
        bizAudit.setTaskId(actRuTask.getId());
        bizAudit.setTaskDefKey(actRuTask.getTaskDefKey());
        actTaskService.autoAudit(bizAudit);
        NbcbAccountInfo info = new NbcbAccountInfo();
        log.error("id=======================" + id);
        log.error("id=======================" + id);
        log.error("id=======================" + id);
        log.error("id=======================" + id);
        log.error("id=======================" + id);
        log.error("id=======================" + id);
        log.error("id=======================" + id);
        log.error("id=======================" + id);
        info.setId(id);
        info.setResult(2);
        nbcbAccountInfoMapper.updateById(info);
    }

    /**
     * 测试自动审批
     *
     * @param businessKey
     */
    @Override
    public void testAutoAudit(String businessKey, Long id) {
        // 自动审批通过
        BizAudit bizAudit = new BizAudit();
        BizBusiness bizBusiness = businessService.selectBizBusinessById(businessKey);
        bizAudit.setApplyer(bizBusiness.getApplyer());
        bizAudit.setBusinessKey(bizBusiness.getId());
//            bizAudit.setComment("自动审批");
        bizAudit.setProcDefKey(bizBusiness.getProcDefKey());
        bizAudit.setProcName(bizBusiness.getTitle());
        bizAudit.setResult(2);
        String procInstId = bizBusiness.getProcInstId();
        bizAudit.setProcInstId(procInstId);
        List<ActRuTask> actRuTasks = actRuTaskMapper.selectListByProcInstId(procInstId);
        ActRuTask actRuTask = actRuTasks.get(0);
        bizAudit.setTaskId(actRuTask.getId());
        bizAudit.setTaskDefKey(actRuTask.getTaskDefKey());
        actTaskService.autoAudit(bizAudit);
        if (id != null) {
            NbcbAccountInfo info = new NbcbAccountInfo();
            log.error("id=======================" + id);
            log.error("id=======================" + id);
            log.error("id=======================" + id);
            log.error("id=======================" + id);
            log.error("id=======================" + id);
            log.error("id=======================" + id);
            log.error("id=======================" + id);
            log.error("id=======================" + id);
            info.setId(id);
            info.setResult(2);
            nbcbAccountInfoMapper.updateById(info);
        }
    }

    /**
     * 测试推送付款单
     *
     * @param nbcbDto 入参
     */
    @Override
    public void testPaymentOrderApiAdd(NBCBDto nbcbDto) {
        try {
            NbcbAccountInfo accountInfo = new NbcbAccountInfo();
            accountInfo.setRcvName(nbcbDto.getSupplyName());
            accountInfo.setRcvAcc(nbcbDto.getSupplyAcc());
            accountInfo.setRcvBankName(nbcbDto.getSupplyBankName());
            accountInfo.setAmt("0.01");
            accountInfo.setApplyCode("test");
            accountInfo.setBusinessKey("1000");
            accountInfo.setCreateTime(new Date());
            Map<String, Object> paramMap = Maps.newHashMap();
            // 客户号
            paramMap.put("custId", nbcbProperties.getCustId());
            // 单位编码
            String corpCode = nbcbDto.getCorpCode();
            paramMap.put("corpCode", corpCode);
            // 付款单号
            String payBillNo = CodeUtil.getCode("AL", 20);
            accountInfo.setPayBillNo(payBillNo);
            paramMap.put("payBillNo", payBillNo);
            // 付款账号(非必填)
            paramMap.put("payAcc", getPayAcc(corpCode));
            // 供应商名称(收款人姓名)
            paramMap.put("supplyName", accountInfo.getRcvName());
            // 供应商账号(收款人账号)
            String rcvAcc = accountInfo.getRcvAcc();
            // 收款账号处理
            String acc = formatAcc(rcvAcc);
            accountInfo.setRcvAcc(rcvAcc);
            paramMap.put("supplyAcc", acc);
            // 供应商开户行名(收款人开户行名)
            paramMap.put("supplyBankName", accountInfo.getRcvBankName());
            // 供应商开户行行号
//            paramMap.put("supplyBankNo", nbcbDto.getSupplyBankNo());
            // 付款单金额
            paramMap.put("amt", accountInfo.getAmt());
            Map<String, Object> data = Maps.newHashMap();
            data.put("Data", paramMap);
            String param = JSON.toJSONString(data);
            String response = OpenSDK.send("paymentOrderApiAdd", "paymentOrderApiAdd", param);
            String retCode = getResponseMap(response).get("retCode");
            if ("0000".equals(retCode)) {
                accountInfo.setStatus(1);
            } else {
                accountInfo.setStatus(0);
                accountInfo.setResponse(response);
            }
            nbcbAccountInfoMapper.insert(accountInfo);
            for (int i = 0; i < 11; i++) {
                log.error(response);
                log.error(param);
            }
        } catch (Exception e) {
            log.error("提交付款单发生异常=====" + e);
        }
    }

    /**
     * 推送付款单(财务推送)
     *
     * @param nbcbDto 入参
     */
    @Override
    public String paymentOrderApiAdd(NBCBDto nbcbDto) {
        try {
            NbcbAccountInfo accountInfo = new NbcbAccountInfo();
            // 收款人姓名或收款公司名称
            String name = nbcbDto.getName();
            accountInfo.setRcvName(name);
            // 收款账号
            String accountNum = nbcbDto.getAccountNum();
            accountInfo.setRcvAcc(accountNum);
            // 转账金额
            String projectMoney = nbcbDto.getProjectMoney();
            accountInfo.setAmt(projectMoney);
            accountInfo.setApplyCode(nbcbDto.getApplyCode());
            accountInfo.setBusinessKey(nbcbDto.getBusinessKey());
            accountInfo.setCreateTime(new Date());
            // 处理收款银行
            String branchBank = nbcbDto.getBranchBank();
            if (StrUtil.isBlank(branchBank)) {
                branchBank = "";
            }
            accountInfo.setRcvBankName(nbcbDto.getBank() + branchBank);
            Map<String, Object> paramMap = Maps.newHashMap();
            // 客户号
            paramMap.put("custId", nbcbProperties.getCustId());
            // 单位编码
            BizBusiness bizBusiness = businessService.selectBizBusinessById(nbcbDto.getBusinessKey());
            String corpCode = getCorpCode(bizBusiness.getCompanyId());
            paramMap.put("corpCode", corpCode);
            // 付款单号
            String payBillNo = CodeUtil.getCode("AL", 20);
            accountInfo.setPayBillNo(payBillNo);
            paramMap.put("payBillNo", payBillNo);
            // 付款账号(非必填)
            paramMap.put("payAcc", getPayAcc(corpCode));
            // 供应商名称(收款人姓名)
            paramMap.put("supplyName", name);
            // 供应商账号(收款人账号)
            paramMap.put("supplyAcc", accountNum);
            // 供应商开户行名(收款人开户行名)
            paramMap.put("supplyBankName", accountInfo.getRcvBankName());
            // 供应商开户行行号
//            paramMap.put("supplyBankNo", nbcbDto.getSupplyBankNo());
            // 付款单金额
            paramMap.put("amt", projectMoney);
            Map<String, Object> data = Maps.newHashMap();
            data.put("Data", paramMap);
            String param = JSON.toJSONString(data);
            String response = OpenSDK.send("paymentOrderApiAdd", "paymentOrderApiAdd", param);
            String retCode = getResponseMap(response).get("retCode");
            String str = "";
            if ("0000".equals(retCode)) {
                accountInfo.setStatus(1);
                if (nbcbAccountInfoMapper.insert(accountInfo) > 0) {
                    str = payBillNo;
                }
            }
            return str;
        } catch (Exception e) {
            log.error("提交付款单发生异常=====" + e);
            return "推送付款单发生异常";
        }
    }

    /**
     * 通过付款单号查询付款状态
     *
     * @param nbcbAccountInfo 付款单信息 1·23
     */
    @Override
    public void paymentOrderApiQry(NbcbAccountInfo nbcbAccountInfo) {
        String payBillNo = nbcbAccountInfo.getPayBillNo();
        String businessKey = nbcbAccountInfo.getBusinessKey();
        log.error(businessKey);
        log.error("正在操作付款单号为：" + payBillNo + "的付款单===============================");
        log.error("正在操作付款单号为：" + payBillNo + "的付款单===============================");
        log.error("正在操作付款单号为：" + payBillNo + "的付款单===============================");
        log.error("正在操作付款单号为：" + payBillNo + "的付款单===============================");
        log.info("正在操作付款单号为：" + payBillNo + "的付款单===============================");
        log.info("正在操作付款单号为：" + payBillNo + "的付款单===============================");
        log.info("正在操作付款单号为：" + payBillNo + "的付款单===============================");
        log.info("正在操作付款单号为：" + payBillNo + "的付款单===============================");
        log.error("付款单号========================================" + businessKey);
        log.error("付款单号========================================" + businessKey);
        log.error("付款单号========================================" + businessKey);
        log.error("付款单号========================================" + businessKey);
        String response = paymentOrderApiQry(payBillNo);
        log.error("付款单返回结果================" + response);
        log.error("付款单返回结果================" + response);
        log.error("付款单返回结果================" + response);
        log.error("付款单返回结果================" + response);
        log.error("付款单返回结果================" + response);
        log.error("付款单返回结果================" + response);
        Map<String, String> responseMap = getResponseMap(response);
        String retCode = responseMap.get("retCode");
        log.error("解析返回值获取返回code===================================" + retCode);
        log.error("解析返回值获取返回code===================================" + retCode);
        log.error("解析返回值获取返回code===================================" + retCode);
        log.error("解析返回值获取返回code===================================" + retCode);
        if (StrUtil.isNotBlank(retCode) && "0000".equals(retCode)) {
            String status = responseMap.get("status");
            log.error("获取返回的status===================================" + status);
            log.error("获取返回的status===================================" + status);
            log.error("获取返回的status===================================" + status);
            log.error("获取返回的status===================================" + status);
            log.error("获取返回的status===================================" + status);
            log.error("获取返回的status===================================" + status);
            log.error("获取返回的status===================================" + status);
            log.error("获取返回的status===================================" + status);
            log.error("获取返回的status===================================" + status);
            log.error("获取返回的status===================================" + status);
            log.error("获取返回的status===================================" + status);
            log.error("获取返回的status===================================" + status);
            log.error("获取返回的status===================================" + status);
            log.error("获取返回的status===================================" + status);
            log.error("获取返回的status===================================" + status);
            log.error("获取返回的status===================================" + status);
            log.error("获取返回的status===================================" + status);
            log.error("获取返回的status===================================" + status);
            if (StrUtil.isNotBlank(status)) {
                if (StrUtil.isNotBlank(businessKey)) {
                    log.error("开始审批=======================================");
                    log.error("开始审批=======================================");
                    log.error("开始审批=======================================");
                    log.error("开始审批=======================================");
                    log.error("开始审批=======================================");
                    log.error("开始审批=======================================");
                    log.error("开始审批=======================================");
                    log.error("开始审批=======================================");
                    log.error("开始审批=======================================");
                    log.error("开始审批=======================================");
                    log.error("开始审批=======================================");
                    log.error("开始审批=======================================");
                    log.error("开始审批=======================================");
                    log.error("开始审批=======================================");
                    log.error("开始审批=======================================");
                    log.error("开始审批=======================================");
                    log.error("开始审批=======================================");
                    log.error("开始审批=======================================");
                    log.error("开始审批=======================================");
                    log.error("开始审批=======================================");
                    log.error("开始审批=======================================");
                    log.error("开始审批=======================================");
                    Long id = nbcbAccountInfo.getId();
                    log.error("主键ID===========================" + id);
                    log.error("主键ID===========================" + id);
                    log.error("主键ID===========================" + id);
                    log.error("主键ID===========================" + id);
                    if ("2".equals(status)) {
                        log.error("自动审批中==========================");
                        log.error("自动审批中==========================");
                        log.error("自动审批中==========================");
                        log.error("自动审批中==========================");
                        log.error("自动审批中==========================");
                        log.error("自动审批中==========================");
                        log.error("自动审批中==========================");
                        log.error("自动审批中==========================");
                        log.error("自动审批中==========================");
                        log.error("自动审批中==========================");
                        log.error("自动审批中==========================");
                        log.error("自动审批中==========================");
                        log.error("自动审批中==========================");
                        log.error("自动审批中==========================");
                        log.error("自动审批中==========================");
                        autoAudit(businessKey, id);
                    } else if ("-2".equals(status)) {
                        log.error("作废付款单=======================");
                        NbcbAccountInfo nbcbAccountInfo1 = new NbcbAccountInfo();
                        nbcbAccountInfo1.setId(id);
                        // 付款单已作废
                        nbcbAccountInfo1.setResult(3);
                        nbcbAccountInfoMapper.updateById(nbcbAccountInfo1);
                    }
                }
            }
        }
    }

    @Override
    public void test(NBCBDto nbcbDto) {
        paymentOrderApiQry(nbcbDto.getPayBillNo());
    }

    private String paymentOrderApiQry(String payBillNo) {
        try {
            Map<String, Object> paramMap = Maps.newHashMap();
            // 客户号
            paramMap.put("custId", nbcbProperties.getCustId());
            // 单位编码
            paramMap.put("payBillNo", payBillNo);
            Map<String, Object> data = Maps.newHashMap();
            data.put("Data", paramMap);
            String response = OpenSDK.send("paymentOrderApiQry", "paymentOrderApiQry", JSON.toJSONString(data));
            String retCode = getResponseMap(response).get("retCode");
            for (int i = 0; i < 11; i++) {
                log.error(retCode);
                log.error(response);
            }
            return response;
        } catch (Exception e) {
            log.error("发生异常");
            return "";
        }
    }

    /**
     * 账号格式化
     *
     * @param rcvAcc 收款账号
     * @return 账号
     */
    private String formatAcc(String rcvAcc) {
        // 去掉账号中间的空格
        return rcvAcc.replaceAll(" ", "");
    }

    /**
     * 收款银行名称格式化
     *
     * @param rcvBankName 收款银行名称
     * @return 新的银行名称
     */
    private String formatBankName(String rcvBankName) {
        if ("杭州银行".equals(rcvBankName) || rcvBankName.startsWith("杭州银行")) {
            return "杭州银行股份有限公司";
        } else if ("杭州联合农村商业银行".equals(rcvBankName) || rcvBankName.startsWith("杭州联合银行")) {
            return "杭州联合农村商业银行股份有限公司";
        } else if (rcvBankName.contains("农商银行")) {
            return "上海农商银行";
        } else if ("浙江泰隆银行缙云壶镇小微企业专营支行".equals(rcvBankName)) {
            return "浙江泰隆商业银行股份有限公司缙云壶镇小微企业专营支行";
        } else {
            return rcvBankName;
        }
    }

    private Map<String, String> getResponseMap(String response) {
        Object data = JSONObject.parseObject(response).get("Data");
        JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(data));
        String retCode = "";
        String status = "";
        Object o1 = jsonObject.get("retCode");
        if (o1 != null) {
            retCode = (String) jsonObject.get("retCode");
        }
        Object o2 = jsonObject.get("status");
        if (o2 != null) {
            status = (String) jsonObject.get("status");
        }
        Map<String, String> map = Maps.newHashMap();
        map.put("retCode", retCode);
        map.put("status", status);
        return map;
    }

    private void throwException(Object anything, String codeMsg) {
        if (anything == null) {
            throw new RuntimeException(codeMsg + "为空");
        }
    }

    private void delete_Id(HashMap hashMap) {
        hashMap.put("id", hashMap.get("_id").toString());
        hashMap.remove("_id");
    }

    /**
     * 获取财资corpCode
     *
     * @param companyId 公司id
     * @return corpCode
     */
    private String getCorpCode(Long companyId) {
        Map<Long, String> map = Maps.newHashMap();
        // 杭州安联
        map.put(115L, "1000");
        // 金华职康
        map.put(168L, "1002");
        // 宁波安联
        map.put(119L, "1003");
        // 安维安全
        map.put(172L, "1004");
        // 亿达检测
        map.put(350L, "1007");
        // 卫康环保
        map.put(356L, "1008");
        // 嘉兴安联
        map.put(118L, "1010");
        // 上海量远
        map.put(161L, "1011");
        return map.get(companyId);
    }

    /**
     * 获取分公司付款账号
     *
     * @param corpCode 公司代码
     * @return 付款账号
     */
    private String getPayAcc(String corpCode) {
        Map<String, String> map = Maps.newHashMap();
        // 安维安全
        map.put("1004", "50010122001424703");
        // 金华职康
        map.put("1002", "1208011009200256160");
        // 宁波安联
        map.put("1003", "50010122000754459");
        // 亿达检测
        map.put("1007", "71010122002751821");
        // 卫康环保
        map.put("1008", "71010122002751612");
        // 嘉兴安联
        map.put("1010", "89010122000737109");
        // 上海量远
        map.put("1011", "70050122000602450");
        // 杭州安联
        map.put("1000", "375376238523");
        return map.get(corpCode);
    }
}
