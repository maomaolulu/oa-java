package com.ruoyi.activiti.test;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.activiti.domain.BizAssociateApply;
import com.ruoyi.activiti.domain.BizAssociateGood;
import com.ruoyi.activiti.domain.asset.BizClaimApply;
import com.ruoyi.activiti.domain.asset.BizConsumablesInventory;
import com.ruoyi.activiti.domain.asset.BizScrappedApply;
import com.ruoyi.activiti.domain.car.BizCarApply;
import com.ruoyi.activiti.domain.car.BizCarSubsidyApply;
import com.ruoyi.activiti.domain.fiance.*;
import com.ruoyi.activiti.domain.my_apply.*;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.domain.purchase.BizGoodsInfo;
import com.ruoyi.activiti.domain.purchase.BizGoodsRejectedApply;
import com.ruoyi.activiti.domain.purchase.BizPurchaseApply;
import com.ruoyi.activiti.mapper.*;
import com.ruoyi.activiti.mapper.asset.BizConsumablesInventoryMapper;
import com.ruoyi.activiti.mapper.my_apply.BizDemandFeedbackMapper;
import com.ruoyi.system.domain.SysConfig;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author wuYang
 * @date 2022/9/16 8:54
 */
@Service
public class BizBusinessTest {

    @Resource
    BizBusinessTestMapper bizBusinessTestMapper;

    @Resource
    BizCarApplyMapper bizCarApplyMapper;
    @Resource
    BizCarSubsidyApplyMapper bizCarSubsidyApplyMapper;


    @Resource
    BizPaymentApplyMapper bizPaymentApplyMapper;

    @Resource
    BizSealApplyMapper bizSealApplyMapper;

    @Resource
    BizClaimApplyMapper bizClaimApplyMapper;
    @Resource
    BizUniversalApplyMapper universalApplyMapper;

    @Resource
    BizReimburseApplyMapper bizReimburseApplyMapper;
    @Resource
    BizPurchaseApplyMapper bizPurchaseApplyMapper;
    @Resource
    BizContractApplyMapper contractApplyMapper;
    @Resource
    BizScrappedApplyMapper scrappedApplyMapper;
    @Resource
    BizBusinessFeeApplyMapper bizBusinessFeeApplyMapper;
    @Resource
    BizGoodsRejectedApplyMapper bizGoodsRejectedApplyMapper;
    @Resource
    BizSalaryApprovalMapper bizSalaryApprovalMapper;
    @Resource
    BizSalaryAdjustmentMapper bizSalaryAdjustmentMapper;
    @Resource
    BizDemandFeedbackMapper feedbackMapper;
    @Resource
    BizCoverChargeApplyMapper coverChargeApplyMapper;
    @Resource
    BizReviewApplyMapper reviewApplyMapper;
    @Resource
    BizOtherChargeApplyMapper otherChargeApplyMapper;
    @Resource
    BizConsumablesInventoryMapper bizConsumablesInventoryMapper;
    @Resource
    TaskService taskService;
    @Resource
    RepositoryService repositoryService;
    @Resource
    MongoTemplate mongoTemplate;

    public void changeMongodb() {
        List<HashMap> hashMaps = mongoTemplate.find(new Query(), HashMap.class, "test");
        for (HashMap hashMap : hashMaps) {
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(hashMap.get("_id")));
            Update update = new Update();
            update.set("BaseInformation.isFeeDept", false);
            update.set("BaseInformation.isTemp0", false);
            update.set("BaseInformation.isTemp1", false);
            update.set("BaseInformation.isTemp2", false);
            update.set("BaseInformation.isTemp3", false);
            update.set("BaseInformation.isTemp4", false);
            mongoTemplate.upsert(query, update, "test");
        }
    }

    /**
     * 同步旧数据
     */
    public void syncOldData() {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is("63744e858ee0d136ff2abb4d"));
        HashMap template = mongoTemplate.findOne(query, HashMap.class, "template");

        List<BizBusiness> bizBusinesses = bizBusinessTestMapper.paymentBiz();
        //先拿一个试一下
//        for (BizBusiness bizBusiness : bizBusinesses) {
//            String tableId = bizBusiness.getTableId();
//        }
        BizBusiness bizBusiness = bizBusinesses.get(20);
        String tableId = bizBusiness.getTableId();
        List<BizAssociateGood> good = bizBusinessTestMapper.good(Long.parseLong(tableId));

        List<BizAssociateApply> approve = bizBusinessTestMapper.approve(Long.parseLong(tableId));

        BizPaymentApply payment = bizBusinessTestMapper.payment(Long.parseLong(tableId));
        List<BizPaymentDetail> detail = bizBusinessTestMapper.detail(Long.parseLong(tableId));

        template.put("title", bizBusiness.getTitle());
        template.put("applyCode", bizBusiness.getApplyCode());
        template.put("companyName", bizBusiness.getCompanyName());
        template.put("deptName", bizBusiness.getDeptName());
        HashMap baseInformation = (HashMap) template.get("BaseInformation");
        HashMap information_relate = new HashMap();
        information_relate.put("project_name", payment.getProjectName());
        template.put("projectMoney", payment.getPayCount());
        information_relate.put("project_code", payment.getProjectCode());
        information_relate.put("projece_pay", payment.getProjectMoney());
        information_relate.put("amount", payment.getPaidCount());

        if (good.size() > 0) {
            List<HashMap> goods = new ArrayList<>();
            for (BizAssociateGood bizAssociateGood : good) {
                HashMap temp = new HashMap();
                Long purchaseKey = bizAssociateGood.getPurchaseKey();
                BizBusiness business = bizBusinessTestMapper.getBusiness(purchaseKey);
                temp.put("applyCode", business.getApplyCode());
                BizGoodsInfo good1 = bizBusinessTestMapper.getGood(bizAssociateGood.getGoodId());
                temp.put("name", good1.getName());
                temp.put("model", good1.getModel());
                temp.put("amount", good1.getAmount());
                temp.put("unit", good1.getUnit());
                temp.put("isInvalid", good1.getIsInvalid());
                temp.put("result", business.getResult());
                goods.add(temp);
            }
            baseInformation.put("purchaseData", good);
        }
        if (approve.size() > 0) {
            List<HashMap> approves = new ArrayList<>();
            for (BizAssociateApply bizAssociateApply : approve) {
                HashMap temp = new HashMap();
                BizBusiness business = bizBusinessTestMapper.getBusiness(bizAssociateApply.getAssociateApply());
                temp.put("applyCode", business.getApplyCode());
                temp.put("processName", business.getTitle());
                temp.put("applyer", business.getApplyer());
                temp.put("applyerTime", business.getApplyTime());
                temp.put("result", business.getResult());
                approves.add(temp);
            }
            baseInformation.put("approvalData", approves);
        }
//        // 发票信息
//        HashMap fee = new HashMap();
//        fee.put()


        baseInformation.put("information_relate", information_relate);
        baseInformation.put("remark", payment.getRemark());
        baseInformation.put("remarkSpec ", payment.getRemarkSpec());

        HashMap<Object, Object> account_information = new HashMap<>();
        HashMap<Object, Object> reimbursement_account = new HashMap<>();
        reimbursement_account.put("accountNum", payment.getAccountNum());
        reimbursement_account.put("bank", payment.getBank());
        reimbursement_account.put("name", payment.getName());
        reimbursement_account.put("address", payment.getProvince() + "  " + payment.getCity());
        reimbursement_account.put("branchBank", payment.getBranchBank());
        account_information.put("reimbursement_account", reimbursement_account);

        List<List<HashMap>> list = new ArrayList<>();
        for (BizPaymentDetail bizPaymentDetail : detail) {
            // 查表
            List<HashMap> paymentDetail = new ArrayList<>();
            HashMap<Object, Object> money = new HashMap<>();
            money.put("context", "");
            money.put("label", "金额");
            money.put("id", "63560277662d00005c0019f2");
            money.put("type", "Text");
            money.put("index_name", "detail_money");
            money.put("value", bizPaymentDetail.getThisPayment());
            money.put("required", true);
            paymentDetail.add(money);
            HashMap<Object, Object> reason = new HashMap<>();
            reason.put("context", "");
            reason.put("label", "付款事由");
            reason.put("id", "6350b5397b320000a5000913");
            reason.put("type", "Textarea");
            reason.put("index_name", "payment_reason");
            reason.put("value", bizPaymentDetail.getRemark());
            reason.put("required", true);
            paymentDetail.add(reason);

        }

        template.put("detail", list);
        // 修改完毕就修改payment为 payment-old

    }


    public void test() {
        // 查询出所有的记录 applyer 为空的

        Task task = taskService.createTaskQuery().taskId("1967537").singleResult();
        System.out.println(task == null);


    }

    public boolean isHuiSign(String proc_def_id, Task task) {
        boolean isMultiInstance = false;
        try {

            // 查询流程定义 。
            BpmnModel bpmnModel = repositoryService.getBpmnModel(proc_def_id);
            List<org.activiti.bpmn.model.Process> listp = bpmnModel.getProcesses();
            org.activiti.bpmn.model.Process process = listp.get(0);
            // 根据taskId获取taskDefKey

            FlowNode flowElement = (FlowNode) process.getFlowElement(task.getTaskDefinitionKey());
            if (flowElement instanceof UserTask) {
                UserTask userTask = (UserTask) flowElement;
                if (userTask.getLoopCharacteristics() != null) {
                    isMultiInstance = true;
                    System.out.println("流程包含会签" + userTask.getId());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return isMultiInstance;
    }

    public void test1() {
        List<BizBusiness> aNull = bizBusinessTestMapper.getNull();
        for (BizBusiness bizBusiness : aNull) {
            String tableId = bizBusiness.getTableId();
            String procDefKey = bizBusiness.getProcDefKey();
            if (procDefKey == null) {
                continue;
            }
            if (procDefKey.equals("carApply")) {
                BizCarApply bizCarApply = bizCarApplyMapper.selectById(tableId);
                String carCode = bizCarApply.getCarCode();
                bizBusiness.setApplyCode(carCode);
                bizBusinessTestMapper.updateById(bizBusiness);
            }
            if (procDefKey.equals("carSubsidyApply")) {
                BizCarSubsidyApply bizCarSubsidyApply = bizCarSubsidyApplyMapper.selectById(tableId);
                String carCode = bizCarSubsidyApply.getSubsidyCode();
                bizBusiness.setApplyCode(carCode);
                bizBusinessTestMapper.updateById(bizBusiness);
            }
            // leave 不管
            if (procDefKey.equals("payment")) {
                BizPaymentApply bizPaymentApply = bizPaymentApplyMapper.selectById(tableId);
                String carCode = bizPaymentApply.getPaymentCode();
                bizBusiness.setApplyCode(carCode);
                bizBusinessTestMapper.updateById(bizBusiness);
            }
            if (procDefKey.equals("seal")) {
                BizSealApply bizSealApply = bizSealApplyMapper.selectById(tableId);
                String carCode = bizSealApply.getApplyCode();
                bizBusiness.setApplyCode(carCode);
                bizBusinessTestMapper.updateById(bizBusiness);
            }
            if (procDefKey.equals("claim")) {
                BizClaimApply bizClaimApply = bizClaimApplyMapper.selectByPrimaryKey(tableId);
                String carCode = bizClaimApply.getClaimCode();
                bizBusiness.setApplyCode(carCode);
                bizBusinessTestMapper.updateById(bizBusiness);
            }

            if (procDefKey.equals("universal")) {
                BizUniversalApply bizUniversalApply = universalApplyMapper.selectById(tableId);
                String carCode = bizUniversalApply.getApplyCode();
                bizBusiness.setApplyCode(carCode);
                bizBusinessTestMapper.updateById(bizBusiness);
            }
            if (procDefKey.equals("reimburse")) {
                BizReimburseApply bizReimburseApply = bizReimburseApplyMapper.selectById(tableId);
                String carCode = bizReimburseApply.getReimbursementCode();
                bizBusiness.setApplyCode(carCode);
                bizBusinessTestMapper.updateById(bizBusiness);
            }
            if (procDefKey.equals("purchase")) {
                BizPurchaseApply bizPurchaseApply = bizPurchaseApplyMapper.selectBizPurchaseApplyById(Long.parseLong(tableId));
                String carCode = bizPurchaseApply.getPurchaseCode();
                bizBusiness.setApplyCode(carCode);
                bizBusinessTestMapper.updateById(bizBusiness);
            }
            if (procDefKey.equals("claim")) {
                BizClaimApply bizClaimApply = bizClaimApplyMapper.selectByPrimaryKey(tableId);
                String carCode = bizClaimApply.getClaimCode();
                bizBusiness.setApplyCode(carCode);
                bizBusinessTestMapper.updateById(bizBusiness);
            }
            if (procDefKey.equals("claim")) {
                BizClaimApply bizClaimApply = bizClaimApplyMapper.selectByPrimaryKey(tableId);
                String carCode = bizClaimApply.getClaimCode();
                bizBusiness.setApplyCode(carCode);
                bizBusinessTestMapper.updateById(bizBusiness);
            }
            if (procDefKey.equals("contract_ys")) {
                BizContractApply bizContractApply = contractApplyMapper.selectById(tableId);
                String carCode = bizContractApply.getApplyCode();
                bizBusiness.setApplyCode(carCode);
                bizBusinessTestMapper.updateById(bizBusiness);
            }
            if (procDefKey.equals("scrapped")) {
                BizScrappedApply bizScrappedApply = scrappedApplyMapper.selectById(tableId);
                String carCode = bizScrappedApply.getApplyCode();
                bizBusiness.setApplyCode(carCode);
                bizBusinessTestMapper.updateById(bizBusiness);
            }
            if (procDefKey.equals("businessMoney")) {
                BizBusinessFeeApply bizBusinessFeeApply = bizBusinessFeeApplyMapper.selectById(tableId);
                String carCode = bizBusinessFeeApply.getApplyCode();
                bizBusiness.setApplyCode(carCode);
                bizBusinessTestMapper.updateById(bizBusiness);
            }
            if (procDefKey.equals("goodsRejected")) {
                BizGoodsRejectedApply bizGoodsRejectedApply = bizGoodsRejectedApplyMapper.selectById(tableId);
                String carCode = bizGoodsRejectedApply.getApplyCode();
                bizBusiness.setApplyCode(carCode);
                bizBusinessTestMapper.updateById(bizBusiness);
            }
            if (procDefKey.equals("xz_approval")) {
                BizSalaryApproval bizSalaryApproval = bizSalaryApprovalMapper.selectById(tableId);
                if (bizSalaryApproval == null) {
                    continue;
                }
                String carCode = bizSalaryApproval.getApplyCode();
                bizBusiness.setApplyCode(carCode);
                bizBusinessTestMapper.updateById(bizBusiness);
            }
            if (procDefKey.equals("xz_adjustment")) {
                BizSalaryAdjustment bizSalaryAdjustment = bizSalaryAdjustmentMapper.selectById(tableId);
                if (bizSalaryAdjustment == null) {
                    continue;
                }
                String carCode = bizSalaryAdjustment.getApplyCode();
                bizBusiness.setApplyCode(carCode);
                bizBusinessTestMapper.updateById(bizBusiness);
            }
            if (procDefKey.equals("feedback")) {
                BizDemandFeedback bizDemandFeedback = feedbackMapper.selectById(tableId);
                if (bizDemandFeedback == null) {
                    continue;
                }
                String carCode = bizDemandFeedback.getApplyCode();
                bizBusiness.setApplyCode(carCode);
                bizBusinessTestMapper.updateById(bizBusiness);
            }
            if (procDefKey.equals("cover")) {
                BizCoverChargeApply bizCoverChargeApply = coverChargeApplyMapper.selectById(tableId);
                if (bizCoverChargeApply == null) {
                    continue;
                }
                String carCode = bizCoverChargeApply.getCoverCode();
                bizBusiness.setApplyCode(carCode);
                bizBusinessTestMapper.updateById(bizBusiness);
            }
            if (procDefKey.equals("review")) {
                BizReviewApply bizReviewApply = reviewApplyMapper.selectById(tableId);
                if (bizReviewApply == null) {
                    continue;
                }
                String carCode = bizReviewApply.getReviewCode();
                bizBusiness.setApplyCode(carCode);
                bizBusinessTestMapper.updateById(bizBusiness);
            }
            if (procDefKey.equals("other-charge")) {
                BizOtherChargeApply bizOtherChargeApply = otherChargeApplyMapper.selectById(tableId);
                if (bizOtherChargeApply == null) {
                    continue;
                }
                String carCode = bizOtherChargeApply.getOtherCode();
                bizBusiness.setApplyCode(carCode);
                bizBusinessTestMapper.updateById(bizBusiness);
            }
            if (procDefKey.equals("hcpd")) {
                BizConsumablesInventory bizConsumablesInventory = bizConsumablesInventoryMapper.selectById(tableId);
                if (bizConsumablesInventory == null) {
                    continue;
                }
                String carCode = bizConsumablesInventory.getApplyCode();
                bizBusiness.setApplyCode(carCode);
                bizBusinessTestMapper.updateById(bizBusiness);
            }

        }
    }

    @Resource
    SysConfigMapper sysConfigMapper;

    public void update() {
        Query query = new Query();
        List<HashMap> hashMaps = mongoTemplate.find(query, HashMap.class, "template");
        for (HashMap hashMap : hashMaps) {
            ArrayList<String> objects = new ArrayList<>();
            objects.add("634e8588f92e00007b003283");
//            hashMap.put("control_name",objects);
            Query query1 = new Query();
            query1.addCriteria(Criteria.where("_id").is(hashMap.get("_id").toString()));
            Update update = new Update();
            update.set("control_name", objects);
            mongoTemplate.upsert(query1, update, "template");
        }
    }

    public void gene() {
        List<Integer> allCompanyId = bizBusinessTestMapper.getAllCompanyId();
        List<HashMap> all = bizBusinessTestMapper.getAllProdDef();
        for (Integer integer : allCompanyId) {

            for (HashMap s : all) {
                // 查询出所以不是杭州的公司
                // 插入但是取的是杭州的值
                HashMap hangzhou = bizBusinessTestMapper.getSysConfig("115" + "g1" + s.get("key1").toString());
                if (hangzhou == null) {
                    continue;
                }
                //
                HashMap currentCompany = bizBusinessTestMapper.getSysConfig(integer + "g1" + s.get("key1").toString());
                if (currentCompany == null) {
                    SysConfig sysConfig = new SysConfig();
                    sysConfig.setConfigName(s.get("name1").toString());
                    sysConfig.setConfigKey(integer + "g1" + s.get("key1").toString());
                    sysConfig.setConfigType("N");
                    sysConfig.setConfigValue((String) hangzhou.get("value1"));
                    sysConfig.setRemark(s.get("name1").toString());
                    sysConfig.setCreateBy("wy");
                    sysConfig.setCreateTime(new Date());
                    sysConfig.setUpdateTime(new Date());
                    sysConfig.setUpdateBy("wy");
                    sysConfigMapper.insert(sysConfig);
                } else {
                    String name = bizBusinessTestMapper.getName(integer);

                    bizBusinessTestMapper.update((String) hangzhou.get("value1"), (Integer) currentCompany.get("config_id"), name + s.get("name1").toString() + "金额1");
                }

            }
            for (HashMap s : all) {
                HashMap hangzhou = bizBusinessTestMapper.getSysConfig("115" + "g2" + s.get("key1").toString());
                if (hangzhou == null) {
                    continue;
                }
                HashMap currentCompany = bizBusinessTestMapper.getSysConfig(integer + "g2" + s.get("key1").toString());
                if (currentCompany == null) {
                    SysConfig sysConfig = new SysConfig();
                    sysConfig.setConfigName(s.get("name1").toString());
                    sysConfig.setConfigKey(integer + "g2" + s.get("key1").toString());
                    sysConfig.setConfigType("N");
                    sysConfig.setConfigValue((String) hangzhou.get("value1"));
                    sysConfig.setRemark(s.get("name1").toString());
                    sysConfig.setCreateBy("wy");
                    sysConfig.setCreateTime(new Date());
                    sysConfig.setUpdateTime(new Date());
                    sysConfig.setUpdateBy("wy");
                    sysConfigMapper.insert(sysConfig);
                } else {
                    String name = bizBusinessTestMapper.getName(integer);

                    bizBusinessTestMapper.update((String) hangzhou.get("value1"), (Integer) currentCompany.get("config_id"), name + s.get("name1").toString() + "金额2");
                }

            }
            for (HashMap s : all) {
                HashMap hangzhou = bizBusinessTestMapper.getSysConfig("115" + "g3" + s.get("key1").toString());
                if (hangzhou == null) {
                    continue;
                }
                HashMap currentCompany = bizBusinessTestMapper.getSysConfig(integer + "g3" + s.get("key1").toString());
                if (currentCompany == null) {
                    SysConfig sysConfig = new SysConfig();
                    sysConfig.setConfigName(s.get("name1").toString());
                    sysConfig.setConfigKey(integer + "g3" + s.get("key1").toString());
                    sysConfig.setConfigType("N");
                    sysConfig.setConfigValue((String) hangzhou.get("value1"));
                    sysConfig.setRemark(s.get("name1").toString());
                    sysConfig.setCreateBy("wy");
                    sysConfig.setCreateTime(new Date());
                    sysConfig.setUpdateTime(new Date());
                    sysConfig.setUpdateBy("wy");
                    sysConfigMapper.insert(sysConfig);
                } else {
                    String name = bizBusinessTestMapper.getName(integer);

                    bizBusinessTestMapper.update((String) hangzhou.get("value1"), (Integer) currentCompany.get("config_id"), name + s.get("name1").toString() + "金额3");
                }

            }
        }
    }

    public void getToken() {
        String url = " https://qyapi.weixin.qq.com/cgi-bin/service/get_provider_token";
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("corpid", "ww886f70c97df35620");
        parameters.put("corpsecret", "GlrxKjCyrqNAGv20TNySwaxaArYRmkgCO__8bZiMnmM");
        String post = HttpUtil.post(url, parameters);
        JSONObject jsonObject = JSON.parseObject(post);


    }

    public void insert(HashMap param) {

        mongoTemplate.insert(param, "dept");
    }

    @Scheduled(cron = "0 0 1 * * ?")
//    @Scheduled(cron = "*/30 * * * * ? ")
    public void insertCompanyRelate() {
        List<HashMap<String, Object>> hashMaps = bizBusinessTestMapper.searchCompanyAll();
//        String companyDeptNameNumber = companyDeptNameNumberBuild(hashMaps);
        for (HashMap<String, Object> hashMap : hashMaps) {
            Integer dept_id = (Integer) hashMap.get("dept_id");
            Query query = new Query();
            query.addCriteria(Criteria.where("dept_id").is(dept_id));
            HashMap dept = mongoTemplate.findOne(query, HashMap.class, "dept");
            if (dept == null) {
                HashMap father = buildFather(hashMap);
                hashMap.put("companyDeptNameNumber", father.get("companyDeptNameNumber"));
                hashMap.put("companyDeptName", father.get("companyDeptName"));
                mongoTemplate.insert(hashMap, "dept");
            } else {
                Update update = new Update();
                update.set("dept_name", hashMap.get("dept_name"));
                mongoTemplate.upsert(query, update, "dept");
            }


        }

    }

    private HashMap buildFather(HashMap<String, Object> hashMap) {
        Integer parent = (Integer) hashMap.get("parent_id");
        if (parent == 0L) {
            HashMap<Object, Object> map = new HashMap<>();
            map.put("companyDeptNameNumber", hashMap.get("dept_id"));
            map.put("companyDeptName", hashMap.get("dept_name"));
            return map;
        }
        HashMap father = bizBusinessTestMapper.getParent(parent);
        Integer dept_id = (Integer) father.get("dept_id");
        String dept_name = (String) father.get("dept_name");
        father.put("dept_id", dept_id + "/" + hashMap.get("dept_id"));
        father.put("dept_name", dept_name + "/" + hashMap.get("dept_name"));

        return buildFather(father);
    }


}
