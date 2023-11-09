package com.ruoyi.activiti.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.activiti.domain.asset.*;
import com.ruoyi.activiti.domain.car.BizCarApply;
import com.ruoyi.activiti.domain.car.BizCarSubsidyApply;
import com.ruoyi.activiti.domain.dto.BizContractApplyDto;
import com.ruoyi.activiti.domain.dto.BizCoverChargeApplyDto;
import com.ruoyi.activiti.domain.dto.BizOtherChargeApplyDto;
import com.ruoyi.activiti.domain.dto.BizReviewApplyDto;
import com.ruoyi.activiti.domain.fiance.BizReimburseApply;
import com.ruoyi.activiti.domain.fiance.BizReimburseDetail;
import com.ruoyi.activiti.domain.my_apply.*;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.domain.fiance.BizBusinessFeeApply;
import com.ruoyi.activiti.domain.fiance.BizPaymentApply;
import com.ruoyi.activiti.domain.purchase.BizGoodsInfo;
import com.ruoyi.activiti.domain.purchase.BizGoodsRejectedApply;
import com.ruoyi.activiti.domain.purchase.BizGoodsRejectedDetail;
import com.ruoyi.activiti.domain.purchase.BizPurchaseApply;
import com.ruoyi.activiti.mapper.AaSpuMapper;
import com.ruoyi.activiti.mapper.BizBusinessMapper;
import com.ruoyi.activiti.mapper.BizBusinessPlusMapper;
import com.ruoyi.activiti.service.*;
import com.ruoyi.activiti.service.asset.BizConsumablesInventoryService;
import com.ruoyi.activiti.service.my_apply.BizDemandFeedbackService;
import com.ruoyi.common.redis.util.RedisUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysDept;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.feign.RemoteUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zx
 * @date 2022/2/26 11:56
 */
@Service
public class MapInfoServiceImpl implements MapInfoService {
    private final BizClaimApplyService bizClaimApplyService;
    private final AaSpuMapper aaSpuMapper;
    private final AssetService assetService;
    private final BizScrappedApplyService scrappedApplyService;
    private final IBizPurchaseApplyService purchaseApplyService;
    private final BizReimburseApplyService reimburseApplyService;
    private final RemoteDeptService remoteDeptService;
    private final BizPaymentApplyService paymentApplyService;
    private final BizBusinessFeeApplyService businessFeeApplyService;
    private final BizReviewApplyService reviewApplyService;
    private final BizContractApplyService contractApplyService;
    private final BizSealApplyService sealApplyService;
    private final BizUniversalApplyService universalApplyService;
    private final RemoteUserService remoteUserService;
    private final BizGoodsRejectedApplyService goodsRejectedApplyService;
    private final BizCarApplyService carApplyService;
    private final BizCarSubsidyApplyService carSubsidyApplyService;
    private final BizSalaryAdjustmentService salaryAdjustmentService;
    private final BizProjectAmountApplyService bizProjectAmountApplyService;
    private final BizSalaryApprovalService salaryApprovalService;
    private final BizDemandFeedbackService demandFeedbackService;
    private final BizCoverChargeApplyService coverChargeApplyService;
    private final BizOtherChargeApplyService otherChargeApplyService;
    private final BizConsumablesInventoryService consumablesInventoryService;
    private final BizBidService bizBidService;
    private final IBizContractProjectService bizContractProjectService;
    @Autowired
    private RedisUtils redisUtils;
    @Resource
    com.ruoyi.activiti.service.payment_mongodb.BizPaymentApplyService mongodbPaymentService;

    @Autowired
    public MapInfoServiceImpl(BizClaimApplyService bizClaimApplyService,
                              AaSpuMapper aaSpuMapper,
                              AssetService assetService,
                              BizScrappedApplyService scrappedApplyService,
                              IBizPurchaseApplyService purchaseApplyService,
                              BizReimburseApplyService reimburseApplyService,
                              RemoteDeptService remoteDeptService,
                              BizPaymentApplyService paymentApplyService,
                              BizBusinessFeeApplyService businessFeeApplyService,
                              BizReviewApplyService reviewApplyService,
                              BizContractApplyService contractApplyService,
                              BizSealApplyService sealApplyService,
                              BizUniversalApplyService universalApplyService,
                              RemoteUserService remoteUserService,
                              BizGoodsRejectedApplyService goodsRejectedApplyService,
                              BizCarApplyService carApplyService,
                              BizCarSubsidyApplyService carSubsidyApplyService,
                              BizSalaryAdjustmentService salaryAdjustmentService,
                              BizProjectAmountApplyService bizProjectAmountApplyService,
                              BizSalaryApprovalService salaryApprovalService,
                              BizDemandFeedbackService demandFeedbackService,
                              BizCoverChargeApplyService coverChargeApplyService,
                              BizOtherChargeApplyService otherChargeApplyService,
                              BizConsumablesInventoryService consumablesInventoryService,
                              BizBidService bizBidService,
                              IBizContractProjectService bizContractProjectService) {
        this.bizBidService = bizBidService;
        this.bizClaimApplyService = bizClaimApplyService;
        this.aaSpuMapper = aaSpuMapper;
        this.assetService = assetService;
        this.scrappedApplyService = scrappedApplyService;
        this.purchaseApplyService = purchaseApplyService;
        this.reimburseApplyService = reimburseApplyService;
        this.remoteDeptService = remoteDeptService;
        this.paymentApplyService = paymentApplyService;
        this.businessFeeApplyService = businessFeeApplyService;
        this.reviewApplyService = reviewApplyService;
        this.contractApplyService = contractApplyService;
        this.sealApplyService = sealApplyService;
        this.universalApplyService = universalApplyService;
        this.remoteUserService = remoteUserService;
        this.goodsRejectedApplyService = goodsRejectedApplyService;
        this.carApplyService = carApplyService;
        this.carSubsidyApplyService = carSubsidyApplyService;
        this.salaryAdjustmentService = salaryAdjustmentService;
        this.bizProjectAmountApplyService = bizProjectAmountApplyService;
        this.salaryApprovalService = salaryApprovalService;
        this.demandFeedbackService = demandFeedbackService;
        this.coverChargeApplyService = coverChargeApplyService;
        this.otherChargeApplyService = otherChargeApplyService;
        this.consumablesInventoryService = consumablesInventoryService;
        this.bizContractProjectService = bizContractProjectService;
    }

    /**
     * 获取各个流程详情主要信息
     *
     * @param bizBusiness
     * @return
     */
    @Override
    public Map<String, Object> getMapInfo(BizBusiness bizBusiness) {
        Map<String, Object> mapInfo = new HashMap<>(2);
        String redisStr = redisUtils.get("mapInfo:" + bizBusiness.getId());
        if (redisStr == null) {
//        if (true) {
            String result = getResult(bizBusiness.getResult());
            List<Map<String, Object>> mpList = new ArrayList<>();
            String applyCode = "";
            String procDefKey = bizBusiness.getProcDefKey();
            procDefKey = procDefKey == null ? "" : procDefKey;
            if (StringUtils.isNotBlank(procDefKey) && procDefKey.startsWith("payment-")) {
                HashMap<String, Object> paymentApply = mongodbPaymentService.selectById(bizBusiness.getTableId());

                if (paymentApply != null) {
                    applyCode = (String) paymentApply.get("applyCode");

                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请标题");
                        put("value", paymentApply.get("title"));
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请时间");
                        put("value", DateUtil.format((Date) paymentApply.get("createTime"), "yyyy-MM-dd HH:mm"));
                    }});
                    HashMap baseInformation = (HashMap) paymentApply.get("BaseInformation");
                    String typePayment = (String) baseInformation.get("type");
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "款项类目");
                        put("value", typePayment);
                    }});
//                    SysDept sysDeptPayment = remoteDeptService.selectSysDeptByDeptId(Long.parseLong((String) paymentApply.get("subjectionDeptId")));
//                    Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(Long.parseLong((String) paymentApply.get("subjectionDeptId")));
                    String deptName = (String) paymentApply.get("deptName");
                    String companyName = (String) paymentApply.get("companyName");
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "隶属部门");
                        put("value", companyName + "-" + deptName);
                    }});
                    String finalResultPayment = result;
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "审批结果");
                        put("value", finalResultPayment);
                    }});
                    Map<String, Object> map1 = new HashMap<>();
                    //户名 款项类目 隶属部门 费用明细
                    map1.put("deptName", companyName + "-" + deptName);
                    map1.put("name", baseInformation.get("name"));
                    map1.put("typePayment", typePayment);

//                    map1.put("remark", paymentApply.get("paymentDetails").get(0).getRemark());
                    bizBusiness.setPaymentMap(map1);
                    bizBusiness.setTotalPrice(new BigDecimal(String.valueOf(paymentApply.get("projectMoney"))));
                } else {


                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请标题");
                        put("value", "");
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请时间");
                        put("value", "");
                    }});


                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "款项类目");
                        put("value", "");
                    }});
//                    SysDept sysDeptPayment = remoteDeptService.selectSysDeptByDeptId(Long.parseLong((String) paymentApply.get("subjectionDeptId")));
//                    Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(Long.parseLong((String) paymentApply.get("subjectionDeptId")));

                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "隶属部门");
                        put("value", "");
                    }});
                    String finalResultPayment = result;
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "审批结果");
                        put("value", "");
                    }});
                    Map<String, Object> map1 = new HashMap<>();
                    //户名 款项类目 隶属部门 费用明细
                    map1.put("deptName", "");
                    map1.put("name", "");
                    map1.put("typePayment", "");

//                    map1.put("remark", paymentApply.get("paymentDetails").get(0).getRemark());
                    bizBusiness.setPaymentMap(map1);
                    bizBusiness.setTotalPrice(new BigDecimal(1L));
                }
//                    bizBusiness.setTotalPay(String.valueOf(paymentApply.get("projectMoney")))
            }
            switch (procDefKey) {
                case "claim":
                    BizClaimApply claimApply = bizClaimApplyService.selectBizClaimApplyById(bizBusiness.getTableId());
                    applyCode = claimApply.getClaimCode();
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申领标题");
                        put("value", claimApply.getTitle());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申领日期");
                        put("value", DateUtil.format(claimApply.getCreateTime(), "yyyy-MM-dd HH:mm"));
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "物品用途");
                        put("value", claimApply.getReason());
                    }});
                    List<String> goodsName = new ArrayList<>();
                    List<Long> num = new ArrayList<>();
                    claimApply.getGoods().stream().forEach(goods -> {
                        AaSpu aaSpu = aaSpuMapper.selectByPrimaryKey(goods.getGoodsId());
                        if (aaSpu == null) {
                            goodsName.add("");
                            num.add(0L);
                        } else {
                            goodsName.add(aaSpu.getName());
                            num.add(goods.getClaimNum());
                        }
                    });
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "物品名称");
                        put("value", String.join("、", goodsName));
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请数量");
                        put("value", num.stream().collect(Collectors.summarizingLong(value -> value)).getSum());
                    }});
                    String finalResult = result;
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "审批结果");
                        put("value", finalResult);
                    }});
                    break;
                case "purchase":
                    BizPurchaseApply purchaseApply = purchaseApplyService.selectBizPurchaseApplyById(Long.valueOf(bizBusiness.getTableId()));
                    applyCode = purchaseApply.getPurchaseCode();
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请标题");
                        put("value", purchaseApply.getTitle());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请时间");
                        put("value", DateUtil.format(purchaseApply.getCreateTime(), "yyyy-MM-dd HH:mm"));
                    }});
                    List<BizGoodsInfo> bizGoodsInfos = purchaseApply.getBizGoodsInfos();
//                    SysDept sysDeptPurchase2 = remoteDeptService.selectSysDeptByDeptId(Long.valueOf(bizGoodsInfos.get(0).getUsages()));
                    List<String> useagesNames = new ArrayList<>();
                    if (null != bizGoodsInfos.get(0).getUsages()) {
                        // 物品归属部门（实际使用部门）
                        String[] split = bizGoodsInfos.get(0).getUsages().split(",");
                        for (String dept : split) {
                            String useagesName = remoteDeptService.selectSysDeptByDeptId(Long.valueOf(dept)).getDeptName();
                            useagesNames.add(useagesName);
                        }
                    }
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "使用部门");
                        put("value", String.join("、", useagesNames) + "...");
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "物品名称");
                        put("value", bizGoodsInfos.get(0).getName() + "...");
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申购数量");
                        put("value", bizGoodsInfos.stream().collect(Collectors.summarizingLong(value -> value.getAmount())).getSum());
                    }});
                    String finalResult2 = result;
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "审批结果");
                        put("value", finalResult2);
                    }});
                    break;
                case "businessMoney":
                    BizBusinessFeeApply businessFeeApply = businessFeeApplyService.selectBizBusinessFeeApplyById(Integer.valueOf(bizBusiness.getTableId()));
                    applyCode = businessFeeApply.getApplyCode();
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请标题");
                        put("value", businessFeeApply.getTitle());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请时间");
                        put("value", DateUtil.format(businessFeeApply.getCreateTime(), "yyyy-MM-dd HH:mm"));
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "项目名称");
                        put("value", businessFeeApply.getProjectName());
                    }});
                    Map<String, Object> belongCompanyBusinessMoney = remoteDeptService.getBelongCompany2(businessFeeApply.getDeptId());
                    SysDept sysDept1 = remoteDeptService.selectSysDeptByDeptId(businessFeeApply.getDeptId());
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "隶属部门");
                        put("value", belongCompanyBusinessMoney.get("companyName").toString() + "-" + sysDept1.getDeptName());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请人");
                        put("value", bizBusiness.getApplyer());
                    }});
                    String finalResultBusinessMoney = result;
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "审批结果");
                        put("value", finalResultBusinessMoney);
                    }});
                    break;
//                case "payment-pay":
//                    HashMap<String, Object> paymentApply = mongodbPaymentService.selectById(bizBusiness.getTableId());
//
//                    applyCode = (String) paymentApply.get("applyCode");
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "申请标题");
//                        put("value", paymentApply.get("title"));
//                    }});
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "申请时间");
//                        put("value", DateUtil.format((Date) paymentApply.get("createTime"), "yyyy-MM-dd HH:mm"));
//                    }});
//                    HashMap baseInformation = (HashMap) paymentApply.get("BaseInformation");
//                    String typePayment = (String) baseInformation.get("type");
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "款项类目");
//                        put("value", typePayment);
//                    }});
////                    SysDept sysDeptPayment = remoteDeptService.selectSysDeptByDeptId(Long.parseLong((String) paymentApply.get("subjectionDeptId")));
////                    Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(Long.parseLong((String) paymentApply.get("subjectionDeptId")));
//                    String deptName = (String) paymentApply.get("deptName");
//                    String companyName = (String) paymentApply.get("companyName");
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "隶属部门");
//                        put("value", companyName + "-" + deptName);
//                    }});
//                    String finalResultPayment = result;
//                    mpList.add(new HashMap<String, Object>(2) {{
//                        put("label", "审批结果");
//                        put("value", finalResultPayment);
//                    }});
//                    Map<String, Object> map1 = new HashMap<>();
//                    //户名 款项类目 隶属部门 费用明细
//                    map1.put("deptName", companyName + "-" + deptName);
//                    map1.put("name", paymentApply.get("name"));
//                    map1.put("typePayment", typePayment);
//
////                    map1.put("remark", paymentApply.get("paymentDetails").get(0).getRemark());
//                    bizBusiness.setPaymentMap(map1);
//                    bizBusiness.setTotalPrice(BigDecimal.valueOf((Integer) paymentApply.get("projectMoney")));
//                    break;
                case "payment":
                    if (bizBusiness.getTableId().length() > 20) {
                        delete(bizBusiness, applyCode, mpList, result);
                        break;
                    }
                    BizPaymentApply paymentPay = paymentApplyService.selectById(Long.valueOf(bizBusiness.getTableId()));
                    applyCode = paymentPay.getPaymentCode();
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请标题");
                        put("value", paymentPay.getTitle());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请时间");
                        put("value", DateUtil.format(paymentPay.getCreateTime(), "yyyy-MM-dd HH:mm"));
                    }});
                    String projectType = paymentPay.getProjectType();
                    String typePayment1 = getTypePayment(Integer.valueOf(projectType));
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "款项类目");
                        put("value", typePayment1);
                    }});
                    SysDept sysDeptPayment = remoteDeptService.selectSysDeptByDeptId(paymentPay.getSubjectionDeptId());
                    Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(paymentPay.getSubjectionDeptId());
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "隶属部门");
                        put("value", belongCompany2.get("companyName").toString() + "-" + sysDeptPayment.getDeptName());
                    }});
                    String finalResultPayment1 = result;
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "审批结果");
                        put("value", finalResultPayment1);
                    }});
                    Map<String, Object> map2 = new HashMap<>();
                    //户名 款项类目 隶属部门 费用明细
                    map2.put("deptName", belongCompany2.get("companyName").toString() + "-" + sysDeptPayment.getDeptName());
                    map2.put("name", paymentPay.getName());
                    map2.put("typePayment", typePayment1);
                    map2.put("remark", paymentPay.getPaymentDetails().get(0).getRemark());
                    bizBusiness.setPaymentMap(map2);
                    bizBusiness.setTotalPrice(paymentPay.getProjectMoney());
                    break;
                case "reimburse":
                    BizReimburseApply reimburseApply = reimburseApplyService.selectBizReimburseApplyById(Long.valueOf(bizBusiness.getTableId()));
                    applyCode = reimburseApply.getReimbursementCode();
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请标题");
                        put("value", bizBusiness.getTitle());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请时间");
                        put("value", DateUtil.format(reimburseApply.getCreateTime(), "yyyy-MM-dd HH:mm"));
                    }});
                    List<BizReimburseDetail> reimburseDetails = reimburseApply.getReimburseDetails();
                    Integer types = reimburseDetails.get(0).getTypes();
                    String type = getType(types);
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "报销类别");
                        put("value", type);
                    }});
                    SysDept sysDeptReimburse = remoteDeptService.selectSysDeptByDeptId(reimburseApply.getSubjectionDeptId());
                    Map<String, Object> belongCompanyReimburse = remoteDeptService.getBelongCompany2(reimburseApply.getSubjectionDeptId());
                    if (belongCompanyReimburse.get("companyName") != null) {
                        mpList.add(new HashMap<String, Object>(2) {{
                            put("label", "归属部门");
                            put("value", belongCompanyReimburse.get("companyName").toString() + "-" + sysDeptReimburse.getDeptName());
                        }});
                    } else {
                        mpList.add(new HashMap<String, Object>(2) {{
                            put("label", "归属部门");
                            put("value", "-" + sysDeptReimburse.getDeptName());
                        }});
                    }

                    String finalResultReimburse = result;
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "审批结果");
                        put("value", finalResultReimburse);
                    }});
                    bizBusiness.setTotalPrice(reimburseApply.getReimburseMoneyTotal());
                    Map<String, Object> map = new HashMap<>();
                    //户名
                    map.put("name", reimburseApply.getName());
                    //报销类别
                    map.put("type", type);
                    //隶属部门
                    map.put("deptName", belongCompanyReimburse.get("companyName").toString() + "-" + sysDeptReimburse.getDeptName());
                    //费用明细
                    map.put("remark", reimburseDetails.get(0).getRemark());
                    bizBusiness.setReimburseMap(map);
                    break;

                case "review":
                    BizReviewApplyDto reviewApplyDto = reviewApplyService.selectBizReviewApplyById(Long.valueOf(bizBusiness.getTableId()));
                    applyCode = reviewApplyDto.getReviewCode();
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请标题");
                        put("value", reviewApplyDto.getTitle());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请时间");
                        put("value", DateUtil.format(reviewApplyDto.getCreateTime(), "yyyy-MM-dd"));
                    }});
                    String reviewType = "";
                    // 1评审费、2服务费、3其他费用
                    switch (reviewApplyDto.getTypes()) {
                        case "1":
                            reviewType = "评审费";
                            break;
                        case "2":
                            reviewType = "服务费";
                            break;
                        case "3":
                            reviewType = "其他费用";
                            break;
                        default:
                            break;
                    }
                    String finalReviewType = reviewType;
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "款项用途");
                        put("value", finalReviewType);
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "付款事由");
                        put("value", reviewApplyDto.getPaymentDetails());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "费用明细");
                        put("value", reviewApplyDto.getRemark());
                    }});
                    SysDept sysDeptReview = remoteDeptService.selectSysDeptByDeptId(reviewApplyDto.getDeptId());
                    Map<String, Object> belongCompanyReview = remoteDeptService.getBelongCompany2(reviewApplyDto.getDeptId());
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "归属部门");
                        put("value", belongCompanyReview.get("companyName").toString() + "-" + sysDeptReview.getDeptName());
                    }});
                    String finalResultReviewType = result;
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "审批结果");
                        put("value", finalResultReviewType);
                    }});

                    break;
                case "cover":
                    BizCoverChargeApplyDto coverChargeApplyDto = coverChargeApplyService.selectBizCoverChargeApplyById(Long.valueOf(bizBusiness.getTableId()));
                    applyCode = coverChargeApplyDto.getCoverCode();
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请标题");
                        put("value", coverChargeApplyDto.getTitle());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请时间");
                        put("value", DateUtil.format(coverChargeApplyDto.getCreateTime(), "yyyy-MM-dd"));
                    }});

                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "款项用途");
                        put("value", "服务费");
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "付款事由");
                        put("value", coverChargeApplyDto.getPaymentDetails());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "费用明细");
                        put("value", coverChargeApplyDto.getRemark());
                    }});
                    SysDept sysDeptCover = remoteDeptService.selectSysDeptByDeptId(coverChargeApplyDto.getDeptId());
                    Map<String, Object> belongCompanyCover = remoteDeptService.getBelongCompany2(coverChargeApplyDto.getDeptId());
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "归属部门");
                        put("value", belongCompanyCover.get("companyName").toString() + "-" + sysDeptCover.getDeptName());
                    }});
                    String finalResultCover = result;
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "审批结果");
                        put("value", finalResultCover);
                    }});

                    break;
                case "other-charge":
                    BizOtherChargeApplyDto bizOtherChargeApplyDto = otherChargeApplyService.selectBizOtherChargeApplyById(Long.valueOf(bizBusiness.getTableId()));
                    applyCode = bizOtherChargeApplyDto.getOtherCode();
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请标题");
                        put("value", bizOtherChargeApplyDto.getTitle());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请时间");
                        put("value", DateUtil.format(bizOtherChargeApplyDto.getCreateTime(), "yyyy-MM-dd"));
                    }});

                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "款项用途");
                        put("value", "其他费用");
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "付款事由");
                        put("value", bizOtherChargeApplyDto.getPaymentDetails());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "费用明细");
                        put("value", bizOtherChargeApplyDto.getRemark());
                    }});
                    SysDept sysDeptOther = remoteDeptService.selectSysDeptByDeptId(bizOtherChargeApplyDto.getDeptId());
                    Map<String, Object> belongCompanyOther = remoteDeptService.getBelongCompany2(bizOtherChargeApplyDto.getDeptId());
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "归属部门");
                        put("value", belongCompanyOther.get("companyName").toString() + "-" + sysDeptOther.getDeptName());
                    }});
                    String finalResultOther = result;
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "审批结果");
                        put("value", finalResultOther);
                    }});

                    break;
                case "scrapped":
                    BizScrappedApply bizScrappedApply = scrappedApplyService.selectBizScrappedApplyById(bizBusiness.getTableId());
                    applyCode = bizScrappedApply.getApplyCode();
                    Asset asset = assetService.getById(bizScrappedApply.getAssertId());

                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请标题");
                        put("value", bizScrappedApply.getTitle());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请时间");
                        put("value", DateUtil.format(bizScrappedApply.getCreateTime(), "yyyy-MM-dd HH:mm"));
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "报废原因");
                        put("value", bizScrappedApply.getRemark());
                    }});
                    if (asset == null) {
                        mpList.add(new HashMap<String, Object>(2) {{
                            put("label", "物品编号");
                            put("value", "");
                        }});
                        mpList.add(new HashMap<String, Object>(2) {{
                            put("label", "物品名称");
                            put("value", "");
                        }});
                        mpList.add(new HashMap<String, Object>(2) {{
                            put("label", "采购时间");
                            put("value", "");
                        }});
                        mpList.add(new HashMap<String, Object>(2) {{
                            put("label", "采购价格");
                            put("value", 0);
                        }});
                    } else {
                        mpList.add(new HashMap<String, Object>(2) {{
                            put("label", "物品编号");
                            put("value", asset.getAssetSn());
                        }});
                        mpList.add(new HashMap<String, Object>(2) {{
                            put("label", "物品名称");
                            put("value", asset.getName());
                        }});
                        mpList.add(new HashMap<String, Object>(2) {{
                            put("label", "采购时间");
                            put("value", DateUtil.format(asset.getPurchaseTime(), "yyyy-MM-dd"));
                        }});
                        mpList.add(new HashMap<String, Object>(2) {{
                            put("label", "采购价格");
                            put("value", asset.getPurchasePrice());
                        }});
                    }

                    SysDept sysDeptScrapped = remoteDeptService.selectSysDeptByDeptId(bizScrappedApply.getDeptId());
                    Map<String, Object> belongCompanyScrapped = remoteDeptService.getBelongCompany2(bizScrappedApply.getDeptId());
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "归属部门");
                        put("value", belongCompanyScrapped.get("companyName").toString() + "-" + sysDeptScrapped.getDeptName());
                    }});
                    String finalResult3 = result;
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "审批结果");
                        put("value", finalResult3);
                    }});
                    break;
                case "contract_ys":
                    BizContractApplyDto bizContractApplyDto = contractApplyService.selectOne(Long.valueOf(bizBusiness.getTableId()));
                    applyCode = bizContractApplyDto.getApplyCode();

                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请标题");
                        put("value", bizContractApplyDto.getTitle());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请时间");
                        put("value", DateUtil.format(bizContractApplyDto.getCreateTime(), "yyyy-MM-dd HH:mm"));
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "客户名称");
                        put("value", bizContractApplyDto.getCustomerName());
                    }});

                    SysDept sysDeptContract = remoteDeptService.selectSysDeptByDeptId(bizContractApplyDto.getDeptId());
                    Map<String, Object> belongCompanyContract = remoteDeptService.getBelongCompany2(bizContractApplyDto.getDeptId());
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "归属部门");
                        put("value", belongCompanyContract.get("companyName").toString() + "-" + sysDeptContract.getDeptName());
                    }});
                    String finalResultContract = result;
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "审批结果");
                        put("value", finalResultContract);
                    }});
                    break;
                case "seal":
                    BizSealApply sealApply = sealApplyService.selectById(Long.valueOf(bizBusiness.getTableId()));
                    applyCode = sealApply.getApplyCode();

                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请标题");
                        put("value", sealApply.getTitle());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请时间");
                        put("value", DateUtil.format(sealApply.getCreateTime(), "yyyy-MM-dd HH:mm"));
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "用印人");
                        put("value", remoteUserService.selectSysUserByUserId(sealApply.getSealUser()).getUserName());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "日期");
                        put("value", DateUtil.format(sealApply.getStampDate(), "yyyy-MM-dd"));
                    }});
                    SysDept sysDeptSeal = remoteDeptService.selectSysDeptByDeptId(sealApply.getUserDept());
                    Map<String, Object> belongCompanySeal = remoteDeptService.getBelongCompany2(sealApply.getUserDept());
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "用印部门");
                        put("value", belongCompanySeal.get("companyName").toString() + "-" + sysDeptSeal.getDeptName());
                    }});
                    String finalResultSeal = result;
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "审批结果");
                        put("value", finalResultSeal);
                    }});
                    break;
                case "universal":
                    BizUniversalApply universalApply = universalApplyService.selectById(Long.valueOf(bizBusiness.getTableId()));
                    applyCode = universalApply.getApplyCode();

                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请标题");
                        put("value", universalApply.getTitle());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请时间");
                        put("value", DateUtil.format(universalApply.getCreateTime(), "yyyy-MM-dd HH:mm"));
                    }});

                    SysDept sysDeptUniversal = remoteDeptService.selectSysDeptByDeptId(universalApply.getDeptId());
                    Map<String, Object> belongCompanyUniversal = remoteDeptService.getBelongCompany2(universalApply.getDeptId());
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "隶属公司");
                        if (belongCompanyUniversal.get("code") != null && "500".equals(belongCompanyUniversal.get("code").toString())) {
                            put("value", "");
                            System.err.println("此处数据异常获取不到部门名称");
                        } else {
                            put("value", belongCompanyUniversal.get("companyName").toString());
                        }
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "隶属部门");
                        put("value", sysDeptUniversal.getDeptName());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请内容");
                        put("value", universalApply.getContent());
                    }});
                    String finalResultUniversal = result;
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "审批结果");
                        put("value", finalResultUniversal);
                    }});
                    break;
                case "goodsRejected":
                    BizGoodsRejectedApply goodsRejectedApply = goodsRejectedApplyService.getInfo(Long.valueOf(bizBusiness.getTableId()));
                    applyCode = goodsRejectedApply.getApplyCode();

                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请标题");
                        put("value", goodsRejectedApply.getTitle());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请时间");
                        put("value", DateUtil.format(goodsRejectedApply.getCreateTime(), "yyyy-MM-dd HH:mm"));
                    }});

                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "退货原因");
                        put("value", goodsRejectedApply.getReturnReason());
                    }});
                    List<BizGoodsRejectedDetail> goodsRejectedDetailList = goodsRejectedApply.getGoodsRejectedDetailList();
                    String goodName = "";
                    String purchaseCode = "";

                    if (!goodsRejectedDetailList.isEmpty()) {
                        BizGoodsRejectedDetail detail = goodsRejectedDetailList.get(0);
                        purchaseCode = detail.getPurchaseCode();
                        try {
                            if (1 == detail.getItemType()) {
                                goodName = detail.getAsset().getName();
                            } else {
                                goodName = detail.getAaSku().getName();
                            }
                        } catch (Exception e) {
                            goodName = "";
                        }
                    }


                    final String purchaseCodes = purchaseCode == null ? "" : purchaseCode;
                    final String goodNames = goodName == null ? "" : goodName;
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "物品名称");
                        put("value", goodNames);
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "采购编号");
                        put("value", purchaseCodes);
                    }});
                    String finalResultRejected = result;
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "审批结果");
                        put("value", finalResultRejected);
                    }});
                    break;
                case "carApply":
                    BizCarApply bizCarApply = carApplyService.selectOne(Long.valueOf(bizBusiness.getTableId()));
                    applyCode = bizCarApply.getCarCode();

                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请标题");
                        put("value", bizCarApply.getTitle());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请时间");
                        put("value", DateUtil.format(bizCarApply.getCreateTime(), "yyyy-MM-dd HH:mm"));
                    }});

                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "用车部门");
                        put("value", bizCarApply.getDeptUseName());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "备注");
                        put("value", bizCarApply.getRemark());
                    }});
                    String finalResultCar = result;
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "审批结果");
                        put("value", finalResultCar);
                    }});
                    break;
                case "carSubsidyApply":
                    BizCarSubsidyApply bizCarSubsidyApply = carSubsidyApplyService.selectOne(Long.valueOf(bizBusiness.getTableId()));
                    applyCode = bizCarSubsidyApply.getSubsidyCode();

                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请标题");
                        put("value", bizCarSubsidyApply.getTitle());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请时间");
                        put("value", DateUtil.format(bizCarSubsidyApply.getCreateTime(), "yyyy-MM-dd HH:mm"));
                    }});

                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "部门");
                        put("value", bizCarSubsidyApply.getCompanyUseName() + "-" + bizCarSubsidyApply.getDeptUseName());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "车辆属性");
                        put("value", getCarTypes(bizCarSubsidyApply.getCarTypes()));
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请补贴里程数");
                        put("value", bizCarSubsidyApply.getApplyMileage() + "km");
                    }});
                    String finalResultSubsidy = result;
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "审批结果");
                        put("value", finalResultSubsidy);
                    }});
                    break;
                case "xz_approval":
                    BizSalaryApproval salaryApproval = salaryApprovalService.selectById(Long.valueOf(bizBusiness.getTableId()));
                    applyCode = salaryApproval.getApplyCode();

                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请标题");
                        put("value", salaryApproval.getTitle());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请时间");
                        put("value", DateUtil.format(salaryApproval.getCreateTime(), "yyyy-MM-dd HH:mm"));
                    }});
//                SysDept sysDeptApproval = remoteDeptService.selectSysDeptByDeptId(salaryApproval.getUserDept());
                    Map<String, Object> belongCompanyApproval = remoteDeptService.getBelongCompany2(salaryApproval.getUserDept());
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "入职部门");
                        put("value", belongCompanyApproval.get("companyName") + "-" + salaryApproval.getUserDeptName());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "入职岗位");
                        put("value", salaryApproval.getPost());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "入职日期");
                        put("value", salaryApproval.getOnboardingDate());
                    }});
                    String finalResultApproval = result;
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "审批结果");
                        put("value", finalResultApproval);
                    }});
                    break;
                case "xz_adjustment":
                    BizSalaryAdjustment bizSalaryAdjustment = salaryAdjustmentService.selectById(Long.valueOf(bizBusiness.getTableId()));
                    applyCode = bizSalaryAdjustment.getApplyCode();

                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请标题");
                        put("value", bizSalaryAdjustment.getTitle());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请时间");
                        put("value", DateUtil.format(bizSalaryAdjustment.getCreateTime(), "yyyy-MM-dd HH:mm"));
                    }});
                    Map<String, Object> belongCompanyAdjustment = remoteDeptService.getBelongCompany2(bizSalaryAdjustment.getUserDept());
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "部门");
                        put("value", belongCompanyAdjustment.get("companyName").toString() + "-" + bizSalaryAdjustment.getUserDeptName());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "姓名");
                        put("value", bizSalaryAdjustment.getName());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "工号");
                        put("value", bizSalaryAdjustment.getJobNumber());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "岗位");
                        put("value", bizSalaryAdjustment.getPost());
                    }});
                    String finalResultAdjustment = result;
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "审批结果");
                        put("value", finalResultAdjustment);
                    }});
                    break;
                case "pr_amount":
                    BizProjectAmountApply bizProjectAmountApply = bizProjectAmountApplyService.selectById(Long.valueOf(bizBusiness.getTableId()));
                    applyCode = bizProjectAmountApply.getApplyCode();

                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请标题");
                        put("value", bizProjectAmountApply.getTitle());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请时间");
                        put("value", DateUtil.format(bizProjectAmountApply.getCreateTime(), "yyyy-MM-dd HH:mm"));
                    }});
                    Map<String, Object> belongCompanyPrAmount = remoteDeptService.getBelongCompany2(bizProjectAmountApply.getDeptId());
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "部门");
                        put("value", belongCompanyPrAmount.get("companyName").toString() + "-" + bizProjectAmountApply.getDeptName());
                    }});

                    String finalResultPrAmount = result;
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "审批结果");
                        put("value", finalResultPrAmount);
                    }});
                    break;
                case "feedback":
                    BizDemandFeedback bizDemandFeedback = demandFeedbackService.selectById(Long.valueOf(bizBusiness.getTableId()));
                    applyCode = bizDemandFeedback.getApplyCode();

                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请标题");
                        put("value", bizDemandFeedback.getTitle());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请时间");
                        put("value", DateUtil.format(bizDemandFeedback.getCreateTime(), "yyyy-MM-dd HH:mm"));
                    }});
                    String belongCompany3 = remoteDeptService.getBelongCompany3(bizDemandFeedback.getDeptId());
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "所属部门");
                        put("value", belongCompany3);
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "实施原因");
                        put("value", bizDemandFeedback.getImplementationReasons());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "实施内容");
                        put("value", bizDemandFeedback.getContent());
                    }});
                    String finalResultFeedback = result;
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "审批结果");
                        put("value", finalResultFeedback);
                    }});
                    break;
                case "hcpd":
                    BizConsumablesInventory consumablesInventory = consumablesInventoryService.selectById(Long.valueOf(bizBusiness.getTableId()));
                    applyCode = consumablesInventory.getApplyCode();

                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请标题");
                        put("value", consumablesInventory.getTitle());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请时间");
                        put("value", DateUtil.format(consumablesInventory.getCreateTime(), "yyyy-MM-dd HH:mm"));
                    }});
                    String belongCompanyHcpd = remoteDeptService.getBelongCompany3(consumablesInventory.getDeptId());
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "所属部门");
                        put("value", belongCompanyHcpd);
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "盘点原因");
                        put("value", consumablesInventory.getReason());
                    }});
                    String finalResultHcpd = result;
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "审批结果");
                        put("value", finalResultHcpd);
                    }});
                    break;
                case "quotation":
                    applyCode = bizBusiness.getApplyCode();
                    break;
                case "contract-review":
                    applyCode = bizBusiness.getApplyCode();
                    break;
                case "bid":
                    BizBidApply bizBidApply = bizBidService.findDetail(bizBusiness.getId().toString());
                    applyCode = bizBidApply.getApplyCode();
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请标题");
                        put("value", bizBidApply.getTitle());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请时间");
                        put("value", DateUtil.format(bizBidApply.getCreateTime(), "yyyy-MM-dd HH:mm"));
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "业务员");
                        put("value", bizBidApply.getSalesman());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "隶属公司");
                        put("value", bizBidApply.getComName());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "截止日期");
                        put("value", DateUtil.format(bizBidApply.getDeadline(), "yyyy-MM-dd"));
                    }});
                    break;
                case "contract-project":
                    BizContractProject bizContractProject = bizContractProjectService.findDetail(bizBusiness.getId().toString());
                    applyCode = bizContractProject.getApplyCode();
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请标题");
                        put("value", bizContractProject.getTitle());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "申请时间");
                        put("value", DateUtil.format(bizContractProject.getCreateTime(), "yyyy-MM-dd HH:mm"));
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "项目编号");
                        put("value", bizContractProject.getIdentifier());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "项目类型");
                        put("value", bizContractProject.getType());
                    }});
                    mpList.add(new HashMap<String, Object>(2) {{
                        put("label", "状态项目");
                        put("value", "录入");
                    }});
                    break;
                default:
                    break;
            }
            mapInfo.put("applyCode", applyCode == null ? "" : applyCode);
            mapInfo.put("list", mpList);
            mapInfo.put("paymentMap", bizBusiness.getPaymentMap());
            mapInfo.put("reimburseMap", bizBusiness.getReimburseMap());
            mapInfo.put("totalPrice", bizBusiness.getTotalPrice());
            String str = JSONObject.toJSONString(mapInfo);
            redisUtils.set("mapInfo:" + bizBusiness.getId(), str, 60 * 60 * 24 * 3);
        } else {
            JSONObject json = JSONObject.parseObject(redisStr);
            mapInfo = json;
        }
        return mapInfo;
    }

    @Override
    public String getMapInfoMail(BizBusiness bizBusiness) {
        String result = getResultMail(bizBusiness.getResult());
        if (bizBusiness.getResult() == 1) {
            result = bizBusiness.getAuditors() + result;
        }
        String html = "";
        String procDefKey = bizBusiness.getProcDefKey();
        procDefKey = procDefKey == null ? "" : procDefKey;
        // 如果是动态表单就直接返回
        if (StringUtils.isNotBlank(procDefKey) && procDefKey.startsWith("payment-")) {

            HashMap<String, Object> paymentPay = mongodbPaymentService.selectById(bizBusiness.getTableId());
            HashMap baseInformation = (HashMap) paymentPay.get("BaseInformation");
            String typePayment1 = (String) baseInformation.get("type");
//                SysDept sysDeptPayment = remoteDeptService.selectSysDeptByDeptId(Long.parseLong((String) paymentApply.get("subjectionDeptId")));
//                Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(Long.parseLong((String) paymentApply.get("subjectionDeptId")));
            html = "审批编号：<a>" + paymentPay.get("applyCode") + "</a><br>" +
                    "申请时间：" + DateUtil.format((Date) paymentPay.get("createTime"), "yyyy-MM-dd HH:mm") + "<br>" +
                    "款项类目：" + typePayment1 + "<br>" +
                    "隶属部门：" + paymentPay.get("companyName") + "-" + paymentPay.get("deptName") + "<br>" +
                    "审批结果：<a style='color:#2d8ccc;'>" + result + "</a><br>";

            return html;
        }
        switch (procDefKey) {
            case "claim":
                BizClaimApply claimApply = bizClaimApplyService.selectBizClaimApplyById(bizBusiness.getTableId());

                List<String> goodsName = new ArrayList<>();
                List<Long> num = new ArrayList<>();
                claimApply.getGoods().stream().forEach(goods -> {
                    AaSpu aaSpu = aaSpuMapper.selectByPrimaryKey(goods.getGoodsId());
                    if (aaSpu == null) {
                        goodsName.add("");
                        num.add(0L);
                    } else {
                        goodsName.add(aaSpu.getName());
                        num.add(goods.getClaimNum());
                    }
                });
                html = "审批编号：<a>" + claimApply.getClaimCode() + "</a><br>" +
                        "申领日期：" + DateUtil.format(claimApply.getCreateTime(), "yyyy-MM-dd HH:mm") + "<br>" +
                        "物品用途：" + claimApply.getReason() + "<br>" +
                        "物品名称：" + String.join("、", goodsName) + "<br>" +
                        "申请数量：" + num.stream().collect(Collectors.summarizingLong(value -> value)).getSum() + "<br>" +
                        "审批结果：<a style='color:#2d8ccc;'>" + result + "</a><br>";
                break;
            case "purchase":
                BizPurchaseApply purchaseApply = purchaseApplyService.selectBizPurchaseApplyById(Long.valueOf(bizBusiness.getTableId()));
                List<BizGoodsInfo> bizGoodsInfos = purchaseApply.getBizGoodsInfos();
//                SysDept sysDeptPurchase2 = remoteDeptService.selectSysDeptByDeptId(Long.valueOf(bizGoodsInfos.get(0).getUsages()));
                List<String> useagesNames = new ArrayList<>();
                if (null != bizGoodsInfos.get(0).getUsages()) {
                    // 物品归属部门（实际使用部门）
                    String[] split = bizGoodsInfos.get(0).getUsages().split(",");
                    for (String dept : split) {
                        String useagesName = remoteDeptService.selectSysDeptByDeptId(Long.valueOf(dept)).getDeptName();
                        useagesNames.add(useagesName);
                    }
                }
                html = "审批编号：<a>" + purchaseApply.getPurchaseCode() + "</a><br>" +
                        "申请时间：" + DateUtil.format(purchaseApply.getCreateTime(), "yyyy-MM-dd HH:mm") + "<br>" +
                        "使用部门：" + String.join("、", useagesNames) + "..." + "<br>" +
                        "物品名称：" + bizGoodsInfos.get(0).getName() + "..." + "<br>" +
                        "申购数量：" + bizGoodsInfos.stream().collect(Collectors.summarizingLong(value -> value.getAmount())).getSum() + "<br>" +
                        "审批结果：<a style='color:#2d8ccc;'>" + result + "</a><br>";
                break;
            case "businessMoney":
                BizBusinessFeeApply businessFeeApply = businessFeeApplyService.selectBizBusinessFeeApplyById(Integer.valueOf(bizBusiness.getTableId()));
                Map<String, Object> belongCompanyBusinessMoney = remoteDeptService.getBelongCompany2(businessFeeApply.getDeptId());
                SysDept sysDept1 = remoteDeptService.selectSysDeptByDeptId(businessFeeApply.getDeptId());
                html = "审批编号：<a>" + businessFeeApply.getApplyCode() + "</a><br>" +
                        "申请时间：" + DateUtil.format(businessFeeApply.getCreateTime(), "yyyy-MM-dd HH:mm") + "<br>" +
                        "项目名称：" + businessFeeApply.getProjectName() + "<br>" +
                        "隶属部门：" + belongCompanyBusinessMoney.get("companyName").toString() + "-" + sysDept1.getDeptName() + "<br>" +
                        "申&ensp;请&ensp;人：" + bizBusiness.getApplyer() + "<br>" +
                        "审批结果：<a style='color:#2d8ccc;'>" + result + "</a><br>";
                break;
            case "payment":
                BizPaymentApply paymentApply = paymentApplyService.selectById(Long.valueOf(bizBusiness.getTableId()));
                String projectType = paymentApply.getProjectType();
                String typePayment = getTypePayment(Integer.valueOf(projectType));
                SysDept sysDeptPayment = remoteDeptService.selectSysDeptByDeptId(paymentApply.getSubjectionDeptId());
                Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(paymentApply.getSubjectionDeptId());
                html = "审批编号：<a>" + paymentApply.getPaymentCode() + "</a><br>" +
                        "申请时间：" + DateUtil.format(paymentApply.getCreateTime(), "yyyy-MM-dd HH:mm") + "<br>" +
                        "款项类目：" + typePayment + "<br>" +
                        "隶属部门：" + belongCompany2.get("companyName").toString() + "-" + sysDeptPayment.getDeptName() + "<br>" +
                        "审批结果：<a style='color:#2d8ccc;'>" + result + "</a><br>";
                break;
            case "reimburse":
                BizReimburseApply reimburseApply = reimburseApplyService.selectBizReimburseApplyById(Long.valueOf(bizBusiness.getTableId()));
                List<BizReimburseDetail> reimburseDetails = reimburseApply.getReimburseDetails();
                Integer types = reimburseDetails.get(0).getTypes();
                String type = getType(types);
                SysDept sysDeptReimburse = remoteDeptService.selectSysDeptByDeptId(reimburseApply.getSubjectionDeptId());
                Map<String, Object> belongCompanyReimburse = remoteDeptService.getBelongCompany2(reimburseApply.getSubjectionDeptId());
                html = "审批编号：<a>" + reimburseApply.getReimbursementCode() + "</a><br>" +
                        "申请时间：" + DateUtil.format(reimburseApply.getCreateTime(), "yyyy-MM-dd HH:mm") + "<br>" +
                        "报销类别：" + type + "<br>" +
                        "归属部门：" + belongCompanyReimburse.get("companyName").toString() + "-" + sysDeptReimburse.getDeptName() + "<br>" +
                        "审批结果：<a style='color:#2d8ccc;'>" + result + "</a><br>";
                break;
            case "review":
                BizReviewApplyDto reviewApplyDto = reviewApplyService.selectBizReviewApplyById(Long.valueOf(bizBusiness.getTableId()));
                String reviewType = "";
                // 1评审费、2服务费、3其他费用
                switch (reviewApplyDto.getTypes()) {
                    case "1":
                        reviewType = "评审费";
                        break;
                    case "2":
                        reviewType = "服务费";
                        break;
                    case "3":
                        reviewType = "其他费用";
                        break;
                    default:
                        break;
                }
                String finalReviewType = reviewType;
                SysDept sysDeptReview = remoteDeptService.selectSysDeptByDeptId(reviewApplyDto.getDeptId());
                Map<String, Object> belongCompanyReview = remoteDeptService.getBelongCompany2(reviewApplyDto.getDeptId());
                html = "审批编号：<a>" + reviewApplyDto.getReviewCode() + "</a><br>" +
                        "申请时间：" + DateUtil.format(reviewApplyDto.getCreateTime(), "yyyy-MM-dd HH:mm") + "<br>" +
                        "款项用途：" + finalReviewType + "<br>" +
                        "付款事由：" + reviewApplyDto.getPaymentDetails() + "<br>" +
                        "费用明细：" + reviewApplyDto.getRemark() + "<br>" +
                        "归属部门：" + belongCompanyReview.get("companyName").toString() + "-" + sysDeptReview.getDeptName() + "<br>" +
                        "审批结果：<a style='color:#2d8ccc;'>" + result + "</a><br>";
                break;
            case "scrapped":
                BizScrappedApply bizScrappedApply = scrappedApplyService.selectBizScrappedApplyById(bizBusiness.getTableId());
                Asset asset = assetService.getById(bizScrappedApply.getAssertId());
                SysDept sysDeptScrapped = remoteDeptService.selectSysDeptByDeptId(bizScrappedApply.getDeptId());
                Map<String, Object> belongCompanyScrapped = remoteDeptService.getBelongCompany2(bizScrappedApply.getDeptId());
                if (asset == null) {
                    html = "审批编号：<a>" + bizScrappedApply.getApplyCode() + "</a><br>" +
                            "申请时间：" + DateUtil.format(bizScrappedApply.getCreateTime(), "yyyy-MM-dd HH:mm") + "<br>" +
                            "报废原因：" + bizScrappedApply.getRemark() + "<br>" +
                            "物品编号：<br>" +
                            "物品名称：<br>" +
                            "采购时间：<br>" +
                            "采购价格：0<br>" +
                            "归属部门：" + belongCompanyScrapped.get("companyName").toString() + "-" + sysDeptScrapped.getDeptName() + "<br>" +
                            "审批结果：<a style='color:#2d8ccc;'>" + result + "</a><br>";
                } else {
                    html = "审批编号：<a>" + bizScrappedApply.getApplyCode() + "</a><br>" +
                            "申请时间：" + DateUtil.format(bizScrappedApply.getCreateTime(), "yyyy-MM-dd HH:mm") + "<br>" +
                            "报废原因：" + bizScrappedApply.getRemark() + "<br>" +
                            "物品编号：<a>" + asset.getAssetSn() + "</a><br>" +
                            "物品名称：" + asset.getName() + "<br>" +
                            "采购时间：" + DateUtil.format(asset.getPurchaseTime(), "yyyy-MM-dd") + "<br>" +
                            "采购价格：" + asset.getPurchasePrice() + "<br>" +
                            "归属部门：" + belongCompanyScrapped.get("companyName").toString() + "-" + sysDeptScrapped.getDeptName() + "<br>" +
                            "审批结果：<a style='color:#2d8ccc;'>" + result + "</a><br>";
                }
                break;
            case "contract_ys":
                BizContractApplyDto bizContractApplyDto = contractApplyService.selectOne(Long.valueOf(bizBusiness.getTableId()));

                SysDept sysDeptContract = remoteDeptService.selectSysDeptByDeptId(bizContractApplyDto.getDeptId());
                Map<String, Object> belongCompanyContract = remoteDeptService.getBelongCompany2(bizContractApplyDto.getDeptId());
                html = "审批编号：<a>" + bizContractApplyDto.getApplyCode() + "</a><br>" +
                        "申请时间：" + DateUtil.format(bizContractApplyDto.getCreateTime(), "yyyy-MM-dd HH:mm") + "<br>" +
                        "客户名称：" + bizContractApplyDto.getCustomerName() + "<br>" +
                        "归属部门：" + belongCompanyContract.get("companyName").toString() + "-" + sysDeptContract.getDeptName() + "<br>" +
                        "审批结果：<a style='color:#2d8ccc;'>" + result + "</a><br>";
                break;
            case "seal":
                BizSealApply sealApply = sealApplyService.selectById(Long.valueOf(bizBusiness.getTableId()));
                SysDept sysDeptSeal = remoteDeptService.selectSysDeptByDeptId(sealApply.getUserDept());
                Map<String, Object> belongCompanySeal = remoteDeptService.getBelongCompany2(sealApply.getUserDept());
                html = "审批编号：<a>" + sealApply.getApplyCode() + "</a><br>" +
                        "申请时间：" + DateUtil.format(sealApply.getCreateTime(), "yyyy-MM-dd HH:mm") + "<br>" +
                        "用&ensp;印&ensp;人：" + remoteUserService.selectSysUserByUserId(sealApply.getSealUser()).getUserName() + "<br>" +
                        "日&emsp;期&emsp;：" + DateUtil.format(sealApply.getStampDate(), "yyyy-MM-dd") + "<br>" +
                        "用印部门：" + belongCompanySeal.get("companyName").toString() + "-" + sysDeptSeal.getDeptName() + "<br>" +
                        "审批结果：<a style='color:#2d8ccc;'>" + result + "</a><br>";
                break;
            case "universal":
                BizUniversalApply universalApply = universalApplyService.selectById(Long.valueOf(bizBusiness.getTableId()));
                SysDept sysDeptUniversal = remoteDeptService.selectSysDeptByDeptId(universalApply.getDeptId());
                Map<String, Object> belongCompanyUniversal = remoteDeptService.getBelongCompany2(universalApply.getDeptId());
                html = "审批编号：<a>" + universalApply.getApplyCode() + "</a><br>" +
                        "申请时间：" + DateUtil.format(universalApply.getCreateTime(), "yyyy-MM-dd HH:mm") + "<br>" +
                        "隶属公司：" + belongCompanyUniversal.get("companyName").toString() + "<br>" +
                        "隶属部门：" + sysDeptUniversal.getDeptName() + "<br>" +
                        "申请内容：" + universalApply.getContent() + "<br>" +
                        "审批结果：<a style='color:#2d8ccc;'>" + result + "</a><br>";
                break;
            case "goodsRejected":
                BizGoodsRejectedApply goodsRejectedApply = goodsRejectedApplyService.getInfo(Long.valueOf(bizBusiness.getTableId()));
                List<BizGoodsRejectedDetail> goodsRejectedDetailList = goodsRejectedApply.getGoodsRejectedDetailList();
                String goodName = "";
                String purchaseCode = "";

                if (!goodsRejectedDetailList.isEmpty()) {
                    BizGoodsRejectedDetail detail = goodsRejectedDetailList.get(0);
                    purchaseCode = detail.getPurchaseCode();
                    try {
                        if (1 == detail.getItemType()) {
                            goodName = detail.getAsset().getName();
                        } else {
                            goodName = detail.getAaSku().getName();
                        }
                    } catch (Exception e) {
                        goodName = "";
                    }
                }


                final String purchaseCodes = purchaseCode == null ? "" : purchaseCode;
                final String goodNames = goodName == null ? "" : goodName;
                html = "审批编号：<a>" + goodsRejectedApply.getApplyCode() + "</a><br>" +
                        "申请时间：" + DateUtil.format(goodsRejectedApply.getCreateTime(), "yyyy-MM-dd HH:mm") + "<br>" +
                        "退货原因：" + goodsRejectedApply.getReturnReason() + "<br>" +
                        "物品名称：" + goodNames + "<br>" +
                        "采购编号：<a>" + purchaseCodes + "</a><br>" +
                        "审批结果：<a style='color:#2d8ccc;'>" + result + "</a><br>";
                break;
            case "carApply":
                BizCarApply bizCarApply = carApplyService.selectOne(Long.valueOf(bizBusiness.getTableId()));
                html = "审批编号：<a>" + bizCarApply.getCarCode() + "</a><br>" +
                        "申请时间：" + DateUtil.format(bizCarApply.getCreateTime(), "yyyy-MM-dd HH:mm") + "<br>" +
                        "用车部门：" + bizCarApply.getDeptUseName() + "<br>" +
                        "用&ensp;车&ensp;人：" + bizCarApply.getCarUser() + "<br>" +
                        "备&emsp;注&emsp;：" + bizCarApply.getRemark() + "<br>" +
                        "审批结果：<a style='color:#2d8ccc;'>" + result + "</a><br>";
                break;
            case "carSubsidyApply":
                BizCarSubsidyApply bizCarSubsidyApply = carSubsidyApplyService.selectOne(Long.valueOf(bizBusiness.getTableId()));
                html = "审批编号：<a>" + bizCarSubsidyApply.getSubsidyCode() + "</a><br>" +
                        "申请时间：" + DateUtil.format(bizCarSubsidyApply.getCreateTime(), "yyyy-MM-dd HH:mm") + "<br>" +
                        "部&emsp;门&emsp;：" + bizCarSubsidyApply.getCompanyUseName() + "-" + bizCarSubsidyApply.getDeptUseName() + "<br>" +
                        "车辆属性：" + getCarTypes(bizCarSubsidyApply.getCarTypes()) + "<br>" +
                        "申请补贴里程数：" + bizCarSubsidyApply.getApplyMileage() + "km" + "<br>" +
                        "审批结果：" + result + "<br>";
                break;
            case "xz_approval":
                BizSalaryApproval salaryApproval = salaryApprovalService.selectById(Long.valueOf(bizBusiness.getTableId()));
                Map<String, Object> belongCompanyApproval = remoteDeptService.getBelongCompany2(salaryApproval.getUserDept());
                html = "审批编号：<a>" + salaryApproval.getApplyCode() + "</a><br>" +
                        "申请时间：" + DateUtil.format(salaryApproval.getCreateTime(), "yyyy-MM-dd HH:mm") + "<br>" +
                        "入职部门：" + belongCompanyApproval.get("companyName") + "-" + salaryApproval.getUserDeptName() + "<br>" +
                        "入职岗位：" + salaryApproval.getPost() + "<br>" +
                        "入职日期：" + salaryApproval.getOnboardingDate() + "<br>" +
                        "审批结果：" + result + "<br>";
                break;
            case "xz_adjustment":
                BizSalaryAdjustment bizSalaryAdjustment = salaryAdjustmentService.selectById(Long.valueOf(bizBusiness.getTableId()));
                Map<String, Object> belongCompanyAdjustment = remoteDeptService.getBelongCompany2(bizSalaryAdjustment.getUserDept());
                html = "审批编号：<a>" + bizSalaryAdjustment.getApplyCode() + "</a><br>" +
                        "申请时间：" + DateUtil.format(bizSalaryAdjustment.getCreateTime(), "yyyy-MM-dd HH:mm") + "<br>" +
                        "部&emsp;门&emsp;：" + belongCompanyAdjustment.get("companyName").toString() + "-" + bizSalaryAdjustment.getUserDeptName() + "<br>" +
                        "姓&emsp;名&emsp;：" + bizSalaryAdjustment.getName() + "<br>" +
                        "工&emsp;号&emsp;：" + bizSalaryAdjustment.getJobNumber() + "<br>" +
                        "岗&emsp;位&emsp;：" + bizSalaryAdjustment.getPost() + "<br>" +
                        "审批结果：" + result + "<br>";
                break;
            case "feedback":
                BizDemandFeedback bizDemandFeedback = demandFeedbackService.selectById(Long.valueOf(bizBusiness.getTableId()));
                String belongCompany3 = remoteDeptService.getBelongCompany3(bizDemandFeedback.getDeptId());
                html = "审批编号：<a>" + bizDemandFeedback.getApplyCode() + "</a><br>" +
                        "申请时间：" + DateUtil.format(bizDemandFeedback.getCreateTime(), "yyyy-MM-dd HH:mm") + "<br>" +
                        "所属部门：" + belongCompany3 + "<br>" +
                        "实施原因：" + bizDemandFeedback.getImplementationReasons() + "<br>" +
                        "实施内容：" + bizDemandFeedback.getContent() + "<br>" +
                        "审批结果：" + result + "<br>";
                break;
            case "hcpd":
                BizConsumablesInventory consumablesInventory = consumablesInventoryService.selectById(Long.valueOf(bizBusiness.getTableId()));

                String belongCompanyHcpd = remoteDeptService.getBelongCompany3(consumablesInventory.getDeptId());
                html = "审批编号：<a>" + consumablesInventory.getApplyCode() + "</a><br>" +
                        "申请时间：" + DateUtil.format(consumablesInventory.getCreateTime(), "yyyy-MM-dd HH:mm") + "<br>" +
                        "所属部门：" + belongCompanyHcpd + "<br>" +
                        "实施原因：" + consumablesInventory.getReason() + "<br>" +
                        "审批结果：" + result + "<br>";
                break;
            default:
                break;

        }

        return html;
    }

    private String getCarTypes(Integer type) {
        String types = "";
        switch (type) {
            case 0:
                types = "公车";
                break;
            case 1:
                types = "私车";
                break;
            case 2:
                types = "租车";
                break;
            default:
                break;
        }
        return types;
    }

    private String getResultMail(Integer resultNum) {
        String result = "";
        switch (resultNum) {
            case 1:
                result = "<a style='color:#FAAD14;'>审批中</a>";
                break;
            case 2:
                result = "<a style='color:#52C41A;'>通过</a>";
                break;
            case 3:
                result = "<a style='color:#FF4D4F;'>驳回</a>";
                break;
            case 4:
                result = "<a style='color:#999999;'>撤销</a>";
                break;
            default:
                break;
        }
        return result;
    }

    private String getResult(Integer resultNum) {
        String result = "";
        switch (resultNum) {
            case 1:
                result = "审批中";
                break;
            case 2:
                result = "通过";
                break;
            case 3:
                result = "驳回";
                break;
            case 4:
                result = "撤销";
                break;
            default:
                break;
        }
        return result;
    }


    private String getType(Integer types) {
        String type = "";
        if (types == null) {
            types = 0;
        }
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

    public void delete(BizBusiness bizBusiness, String applyCode, List<Map<String, Object>> mpList, String result) {
        HashMap<String, Object> paymentApply = mongodbPaymentService.selectById(bizBusiness.getTableId());
        //todo 修改为applycode 新数据
        applyCode = (String) paymentApply.get("修改为applycode");
        mpList.add(new HashMap<String, Object>(2) {{
            put("label", "申请标题");
            put("value", paymentApply.get("title"));
        }});
        mpList.add(new HashMap<String, Object>(2) {{
            put("label", "申请时间");
            put("value", DateUtil.format((Date) paymentApply.get("createTime"), "yyyy-MM-dd HH:mm"));
        }});
        HashMap baseInformation = (HashMap) paymentApply.get("BaseInformation");
        String typePayment = (String) baseInformation.get("type");
        mpList.add(new HashMap<String, Object>(2) {{
            put("label", "款项类目");
            put("value", typePayment);
        }});
//                    SysDept sysDeptPayment = remoteDeptService.selectSysDeptByDeptId(Long.parseLong((String) paymentApply.get("subjectionDeptId")));
//                    Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(Long.parseLong((String) paymentApply.get("subjectionDeptId")));
        String deptName = (String) paymentApply.get("deptName");
        String companyName = (String) paymentApply.get("companyName");
        mpList.add(new HashMap<String, Object>(2) {{
            put("label", "隶属部门");
            put("value", companyName + "-" + deptName);
        }});
        String finalResultPayment = result;
        mpList.add(new HashMap<String, Object>(2) {{
            put("label", "审批结果");
            put("value", finalResultPayment);
        }});
        Map<String, Object> map1 = new HashMap<>();
        //户名 款项类目 隶属部门 费用明细
        map1.put("deptName", companyName + "-" + deptName);
        map1.put("name", paymentApply.get("name"));
        map1.put("typePayment", typePayment);

//                    map1.put("remark", paymentApply.get("paymentDetails").get(0).getRemark());
        bizBusiness.setPaymentMap(map1);
        bizBusiness.setTotalPrice(BigDecimal.valueOf((Integer) paymentApply.get("projectMoney")));
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
