package com.ruoyi.activiti.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.ruoyi.activiti.domain.budget.BudgetExcelEntity;
import com.ruoyi.activiti.domain.budget.BudgetExcelTotalEntity;
import com.ruoyi.activiti.mapper.BizBusinessTestMapper;
import com.ruoyi.activiti.service.BizPaymentBudgetService;
import com.ruoyi.system.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author wuYang
 * @date 2022/12/9 10:22
 */
@Slf4j
@Service
public class BizPaymentBudgetServiceImpl implements BizPaymentBudgetService {
    @Resource
    MongoTemplate mongoTemplate;
    @Resource
    BizBusinessTestMapper bizBusinessTestMapper;

    /**
     * 上传
     */
    @Override
    public int upload(MultipartFile file, String id, Boolean flag) {
//       String fileName = "D://fuck.xls";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭

        try {
            String originalFilename = file.getOriginalFilename();
            String[] vs = originalFilename.split("V");
            if (vs.length != 3) {
                return 400;
            }
            String newVersion = vs[1];
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(id));
            HashMap template = mongoTemplate.findOne(query, HashMap.class, "budget_template");
            Integer version = (Integer) template.get("version");
            if (!newVersion.equals(String.valueOf(version))) {
                return 500;
            }
            EasyExcel.read(file.getInputStream(), BudgetExcelEntity.class, new ReadListener<BudgetExcelEntity>() {
                /**
                 * 单次缓存的数据量
                 */
                public static final int BATCH_COUNT = 10000;
                /**
                 *临时存储
                 */
                private List<BudgetExcelEntity> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

                @Override
                public void invoke(BudgetExcelEntity data, AnalysisContext context) {
                    cachedDataList.add(data);
                    if (cachedDataList.size() >= BATCH_COUNT) {
                        // 存储完成清理 list
                        cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
                    }
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    // 先找出所有该大预算下面的子预算 给他们赋值.
                    for (BudgetExcelEntity excelEntity : cachedDataList) {
                        String companyDeptNameNumber = excelEntity.getCompanyDeptNameNumber();
                        String[] split = companyDeptNameNumber.split("/");
                        String deptS = split[split.length - 1];
                        String paymentDetail = excelEntity.getPaymentDetail();
                        String[] split1 = paymentDetail.split("/");
                        String paymentName = split1[split1.length - 1];
                        int dept = Integer.parseInt(deptS);
                        Query childBudget = new Query();
                        childBudget.addCriteria(Criteria.where("subsetObjects").exists(true)
                                .and("parent_id").is(dept).and("budgetId").is(id));
                        HashMap budget_plan = mongoTemplate.findOne(childBudget, HashMap.class, "budget_plan");
                        // 真鸡儿难
                        List<HashMap> subsetObjects = (List<HashMap>) budget_plan.get("subsetObjects");
                        if (flag) {
                            for (HashMap subsetObject : subsetObjects) {
                                String name = (String) subsetObject.get("name");
                                if (name.equals(paymentName)) {
                                    subsetObject.put("month1", StringUtils.isNotBlank(excelEntity.getMonth1()) ? excelEntity.getMonth1() : subsetObject.get("month1"));
                                    subsetObject.put("month2", StringUtils.isNotBlank(excelEntity.getMonth2()) ? excelEntity.getMonth2() : subsetObject.get("month2"));
                                    subsetObject.put("month3", StringUtils.isNotBlank(excelEntity.getMonth3()) ? excelEntity.getMonth3() : subsetObject.get("month3"));
                                    subsetObject.put("month4", StringUtils.isNotBlank(excelEntity.getMonth4()) ? excelEntity.getMonth4() : subsetObject.get("month4"));
                                    subsetObject.put("month5", StringUtils.isNotBlank(excelEntity.getMonth5()) ? excelEntity.getMonth5() : subsetObject.get("month5"));
                                    subsetObject.put("month6", StringUtils.isNotBlank(excelEntity.getMonth6()) ? excelEntity.getMonth6() : subsetObject.get("month6"));
                                    subsetObject.put("month7", StringUtils.isNotBlank(excelEntity.getMonth7()) ? excelEntity.getMonth7() : subsetObject.get("month7"));
                                    subsetObject.put("month8", StringUtils.isNotBlank(excelEntity.getMonth8()) ? excelEntity.getMonth8() : subsetObject.get("month8"));
                                    subsetObject.put("month9", StringUtils.isNotBlank(excelEntity.getMonth9()) ? excelEntity.getMonth9() : subsetObject.get("month9"));
                                    subsetObject.put("month10", StringUtils.isNotBlank(excelEntity.getMonth10()) ? excelEntity.getMonth10() : subsetObject.get("month10"));
                                    subsetObject.put("month11", StringUtils.isNotBlank(excelEntity.getMonth11()) ? excelEntity.getMonth11() : subsetObject.get("month11"));
                                    subsetObject.put("month12", StringUtils.isNotBlank(excelEntity.getMonth12()) ? excelEntity.getMonth12() : subsetObject.get("month12"));
                                    // 更新 统计表
                                    Query query1 = new Query();
                                    query1.addCriteria(Criteria.where("dept_id").is(dept).and("name").is(paymentName).and("budgetId").is(id));
                                    HashMap budget_statistics = mongoTemplate.findOne(query1, HashMap.class, "budget_statistics");
                                    if (budget_statistics == null) {
                                        log.error("该子预算找不到统计表" + "dept_id:" + dept + "name:" + paymentName);
                                    }
                                    for (int i = 1; i < 13; i++) {
                                        HashMap temp = (HashMap) budget_statistics.get("month" + i);
                                        temp.put("budget_quota", subsetObject.get("month" + i));
                                        budget_statistics.put("month" + i, temp);
                                    }
                                    mongoTemplate.remove(query1, "budget_statistics");
                                    mongoTemplate.insert(budget_statistics, "budget_statistics");

                                }
                            }
                            Update update = new Update();
                            update.set("subsetObjects", subsetObjects);
                            mongoTemplate.upsert(childBudget, update, "budget_plan");

                        } else {
                            // 遍历子预算数组 找到name相等的行 然后更新数组
                            for (HashMap subsetObject : subsetObjects) {
                                HashMap baseInformation = (HashMap) subsetObject.get("BaseInformation");
                                String name = (String) baseInformation.get("name");
                                if (name.equals(paymentName)) {
                                    subsetObject.put("month1", StringUtils.isNotBlank(excelEntity.getMonth1()) ? excelEntity.getMonth1() : subsetObject.get("month1"));
                                    subsetObject.put("month2", StringUtils.isNotBlank(excelEntity.getMonth2()) ? excelEntity.getMonth2() : subsetObject.get("month2"));
                                    subsetObject.put("month3", StringUtils.isNotBlank(excelEntity.getMonth3()) ? excelEntity.getMonth3() : subsetObject.get("month3"));
                                    subsetObject.put("month4", StringUtils.isNotBlank(excelEntity.getMonth4()) ? excelEntity.getMonth4() : subsetObject.get("month4"));
                                    subsetObject.put("month5", StringUtils.isNotBlank(excelEntity.getMonth5()) ? excelEntity.getMonth5() : subsetObject.get("month5"));
                                    subsetObject.put("month6", StringUtils.isNotBlank(excelEntity.getMonth6()) ? excelEntity.getMonth6() : subsetObject.get("month6"));
                                    subsetObject.put("month7", StringUtils.isNotBlank(excelEntity.getMonth7()) ? excelEntity.getMonth7() : subsetObject.get("month7"));
                                    subsetObject.put("month8", StringUtils.isNotBlank(excelEntity.getMonth8()) ? excelEntity.getMonth8() : subsetObject.get("month8"));
                                    subsetObject.put("month9", StringUtils.isNotBlank(excelEntity.getMonth9()) ? excelEntity.getMonth9() : subsetObject.get("month9"));
                                    subsetObject.put("month10", StringUtils.isNotBlank(excelEntity.getMonth10()) ? excelEntity.getMonth10() : subsetObject.get("month10"));
                                    subsetObject.put("month11", StringUtils.isNotBlank(excelEntity.getMonth11()) ? excelEntity.getMonth11() : subsetObject.get("month11"));
                                    subsetObject.put("month12", StringUtils.isNotBlank(excelEntity.getMonth12()) ? excelEntity.getMonth12() : subsetObject.get("month12"));
                                    // 更新 统计表
                                    Query query1 = new Query();
                                    query1.addCriteria(Criteria.where("dept_id").is(dept).and("name").is(paymentName).and("budgetId").is(id));
                                    HashMap budget_statistics = mongoTemplate.findOne(query1, HashMap.class, "budget_statistics");
                                    if (budget_statistics == null) {
                                        log.error("该子预算找不到统计表" + "----dept_id:" + dept + "-----name:" + paymentName);
                                    }
                                    if (budget_statistics != null) {
                                        for (int i = 1; i < 13; i++) {
                                            HashMap temp = (HashMap) budget_statistics.get("month" + i);
                                            temp.put("budget_quota", subsetObject.get("month" + i));
                                            budget_statistics.put("month" + i, temp);
                                        }
                                        mongoTemplate.remove(query1, "budget_statistics");
                                        mongoTemplate.insert(budget_statistics, "budget_statistics");
                                    }

                                }
                            }
                            Update update = new Update();
                            update.set("subsetObjects", subsetObjects);
                            mongoTemplate.upsert(childBudget, update, "budget_plan");

                        }

                    }
                    System.out.println(cachedDataList.size());
                    System.out.println("");
                }

                //
            }).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 200;
    }

    /**
     * 下载 模板
     */
    @Override
    public void download(String id, Boolean flag, HttpServletResponse response) {

        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(id));
            HashMap template = mongoTemplate.findOne(query, HashMap.class, "budget_template");
            Integer version = (Integer) template.get("version");
            String fileName = URLEncoder.encode("财务预算" + "V" + version + "V" + System.currentTimeMillis(), "UTF-8");
            OutputStream os = response.getOutputStream();
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            response.addHeader("Access-Control-Expose-Headers", "Content-disposition");
            EasyExcel.write(os, BudgetExcelEntity.class).sheet("预算模板").doWrite(data(id, flag));
        } catch (Exception e) {
            e.printStackTrace();

        }


    }

    private List<BudgetExcelEntity> data(String id, Boolean flag) {
        // 处理数据
        List<BudgetExcelEntity> result = new ArrayList<>();
        processData(id, result, flag);
        List<BudgetExcelEntity> collect = result.stream().distinct().collect(Collectors.toList());
        return collect;

    }

    private void processData(String id, List<BudgetExcelEntity> result, Boolean flag) {
        Query query = new Query();
        // 找出所有该计划下的公司
        query.addCriteria(Criteria.where("budgetId").is(id).and("parent_id").is(0));
        List<HashMap> hashMaps = mongoTemplate.find(query, HashMap.class, "budget_plan");
        // 找出所有公司
        for (HashMap hashMap : hashMaps) {
            delete_Id(hashMap);
            process(hashMap, result, flag);
        }

    }

    private void process(HashMap hashMap, List<BudgetExcelEntity> result, Boolean flag) {
        String budgetId = (String) hashMap.get("budgetId");
        Query query = new Query();
        query.addCriteria(Criteria.where("budgetId").is(budgetId).and("parent_id").is(hashMap.get("dept_id")));
        List<HashMap> hashMaps = mongoTemplate.find(query, HashMap.class, "budget_plan");
        for (HashMap map : hashMaps) {
            List<HashMap> subsetObjects = (List<HashMap>) map.get("subsetObjects");
            List<HashMap> subsetIds = (List<HashMap>) map.get("subsetIds");
            if (subsetIds == null || subsetObjects == null || subsetIds.isEmpty() || subsetObjects.isEmpty()) {
                process(map, result, flag);
            }

            if (subsetObjects != null) {
                for (HashMap subsetObject : subsetObjects) {
                    subsetObject.put("parent_id", map.get("parent_id"));
                    BudgetExcelEntity excelEntity = toOurEntity(subsetObject, flag);
                    result.add(excelEntity);
                }
            }


        }

    }

    private BudgetExcelEntity toOurEntity(HashMap subsetObject, Boolean flag) {
        BudgetExcelEntity excelEntity = new BudgetExcelEntity();
        // 明细
        if (flag) {
            String name = (String) subsetObject.get("name");
            Query query = new Query();
            query.addCriteria(Criteria.where("name").is(name));
            HashMap dictionary = mongoTemplate.findOne(query, HashMap.class, "dictionary");
            excelEntity.setPaymentNumber((String) dictionary.get("identifier"));
            excelEntity.setPaymentDetail((String) dictionary.get("paymentDetail"));
            // 获取公司
            Integer parent_id = (Integer) subsetObject.get("parent_id");
            Query query1 = new Query();
            query1.addCriteria(Criteria.where("dept_id").is(parent_id));
            HashMap dept = mongoTemplate.findOne(query1, HashMap.class, "dept");
            excelEntity.setCompanyDeptName((String) dept.get("companyDeptName"));
            excelEntity.setCompanyDeptNameNumber(String.valueOf(dept.get("companyDeptNameNumber")));
        }
        // 模板
        else {
            // 获取明细
            HashMap baseInformation = (HashMap) subsetObject.get("BaseInformation");
            String name = (String) baseInformation.get("name");
            Query query = new Query();
            query.addCriteria(Criteria.where("name").is(name));
            HashMap dictionary = mongoTemplate.findOne(query, HashMap.class, "dictionary");
            excelEntity.setPaymentNumber((String) dictionary.get("identifier"));
            excelEntity.setPaymentDetail((String) dictionary.get("paymentDetail"));
            // 获取公司
            Integer parent_id = (Integer) subsetObject.get("parent_id");
            Query query1 = new Query();
            query1.addCriteria(Criteria.where("dept_id").is(parent_id));
            HashMap dept = mongoTemplate.findOne(query1, HashMap.class, "dept");
            excelEntity.setCompanyDeptName((String) dept.get("companyDeptName"));
            excelEntity.setCompanyDeptNameNumber(String.valueOf(dept.get("companyDeptNameNumber")));

        }

        excelEntity.setMonth1(new String(String.valueOf(subsetObject.get("month1"))));
        excelEntity.setMonth2(String.valueOf(subsetObject.get("month2")));
        excelEntity.setMonth3(String.valueOf(subsetObject.get("month3")));
        excelEntity.setMonth4(String.valueOf(subsetObject.get("month4")));
        excelEntity.setMonth5(String.valueOf(subsetObject.get("month5")));
        excelEntity.setMonth6(String.valueOf(subsetObject.get("month6")));
        excelEntity.setMonth7(String.valueOf(subsetObject.get("month7")));
        excelEntity.setMonth8(String.valueOf(subsetObject.get("month8")));
        excelEntity.setMonth9(String.valueOf(subsetObject.get("month9")));
        excelEntity.setMonth10(String.valueOf(subsetObject.get("month10")));
        excelEntity.setMonth11(String.valueOf(subsetObject.get("month11")));
        excelEntity.setMonth12(String.valueOf(subsetObject.get("month12")));
        return excelEntity;
    }

    /**
     * 导出
     */
    @Override
    public void out(String id, Boolean flag, HttpServletResponse response) {
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(id));
            HashMap template = mongoTemplate.findOne(query, HashMap.class, "budget_template");
            Integer version = (Integer) template.get("version");
            String name = (String) template.get("budget_name");
            String fileName = URLEncoder.encode("财务预算统计--" + name, "UTF-8");
            OutputStream os = response.getOutputStream();
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            response.addHeader("Access-Control-Expose-Headers", "Content-disposition");
            EasyExcel.write(os, BudgetExcelTotalEntity.class).sheet("预算模板").doWrite(dataTotal(id, flag));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<BudgetExcelTotalEntity> dataTotal(String id, Boolean flag) {
        // 处理数据
        List<BudgetExcelTotalEntity> result = new ArrayList<>();
        processDataTotal(id, result, flag);
        List<BudgetExcelTotalEntity> collect = result.stream().distinct().collect(Collectors.toList());
        return collect;
    }

    private void processDataTotal(String id, List<BudgetExcelTotalEntity> result, Boolean flag) {
        Query query = new Query();
        // 找出所有该计划下的公司
        query.addCriteria(Criteria.where("budgetId").is(id).and("parent_id").is(0));
        List<HashMap> hashMaps = mongoTemplate.find(query, HashMap.class, "budget_plan");
        // 找出所有公司
        for (HashMap hashMap : hashMaps) {
            delete_Id(hashMap);
            processTotal(hashMap, result, flag);
        }
    }

    private void processTotal(HashMap hashMap, List<BudgetExcelTotalEntity> result, Boolean flag) {
        String budgetId = (String) hashMap.get("budgetId");
        Query query = new Query();
        query.addCriteria(Criteria.where("budgetId").is(budgetId).and("parent_id").is(hashMap.get("dept_id")));
        List<HashMap> hashMaps = mongoTemplate.find(query, HashMap.class, "budget_plan");
        for (HashMap map : hashMaps) {
            List<HashMap> subsetObjects = (List<HashMap>) map.get("subsetObjects");
            List<HashMap> subsetIds = (List<HashMap>) map.get("subsetIds");
            if (subsetIds == null || subsetObjects == null || subsetIds.isEmpty() || subsetObjects.isEmpty()) {
                processTotal(map, result, flag);
            }

            if (subsetObjects != null) {
                for (HashMap subsetObject : subsetObjects) {
                    subsetObject.put("parent_id", map.get("parent_id"));
                    // 去拿对应的统计的数据
                    Query queryForStatistic = new Query();
                    String budgetId1 = (String) map.get("budgetId");
                    Integer parent_id = (Integer) map.get("parent_id");
                    String name = "";
                    if (flag) {
                        name = (String) subsetObject.get("name");
                    } else {
                        HashMap baseInformation = (HashMap) subsetObject.get("BaseInformation");
                        name = (String) baseInformation.get("name");
                    }
                    queryForStatistic.addCriteria(Criteria.where("dept_id").is(parent_id)
                            .and("budgetId").is(budgetId1).and("name").is(name));
                    HashMap budget_statistics = mongoTemplate.findOne(queryForStatistic, HashMap.class, "budget_statistics");
                    BudgetExcelTotalEntity excelEntity = toOurTotalEntity(budget_statistics, flag);
                    result.add(excelEntity);
                }
            }


        }

    }

    private BudgetExcelTotalEntity toOurTotalEntity(HashMap statistics, Boolean flag) {
        BudgetExcelTotalEntity budgetExcelTotalEntity = new BudgetExcelTotalEntity();

        String name = (String) statistics.get("name");
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(name));
        HashMap dictionary = mongoTemplate.findOne(query, HashMap.class, "dictionary");
        budgetExcelTotalEntity.setPaymentNumber((String) dictionary.get("identifier"));
        budgetExcelTotalEntity.setPaymentDetail((String) dictionary.get("paymentDetail"));
        // 获取公司
        Integer parent_id = (Integer) statistics.get("dept_id");
        Query query1 = new Query();
        query1.addCriteria(Criteria.where("dept_id").is(parent_id));
        HashMap dept = mongoTemplate.findOne(query1, HashMap.class, "dept");
        budgetExcelTotalEntity.setCompanyDeptName((String) dept.get("companyDeptName"));
        budgetExcelTotalEntity.setCompanyDeptNameNumber(String.valueOf(dept.get("companyDeptNameNumber")));

        HashMap month1 = (HashMap) statistics.get("month1");
        budgetExcelTotalEntity.setMonth1_budget_quota(String.valueOf(month1.get("budget_quota")));
        budgetExcelTotalEntity.setMonth1_confirmed_quota(String.valueOf(month1.get("confirmed_quota")));
        budgetExcelTotalEntity.setMonth1_remaining_quota(String.valueOf(month1.get("remaining_quota")));
        budgetExcelTotalEntity.setMonth1_amount_occupied(String.valueOf(month1.get("amount_occupied")));

        HashMap month2 = (HashMap) statistics.get("month2");
        budgetExcelTotalEntity.setMonth2_budget_quota(String.valueOf(month2.get("budget_quota")));
        budgetExcelTotalEntity.setMonth2_confirmed_quota(String.valueOf(month2.get("confirmed_quota")));
        budgetExcelTotalEntity.setMonth2_remaining_quota(String.valueOf(month2.get("remaining_quota")));
        budgetExcelTotalEntity.setMonth2_amount_occupied(String.valueOf(month2.get("amount_occupied")));


        HashMap month3 = (HashMap) statistics.get("month3");
        budgetExcelTotalEntity.setMonth3_budget_quota(String.valueOf(month3.get("budget_quota")));
        budgetExcelTotalEntity.setMonth3_confirmed_quota(String.valueOf(month3.get("confirmed_quota")));
        budgetExcelTotalEntity.setMonth3_remaining_quota(String.valueOf(month3.get("remaining_quota")));
        budgetExcelTotalEntity.setMonth3_amount_occupied(String.valueOf(month3.get("amount_occupied")));

        HashMap month4 = (HashMap) statistics.get("month4");
        budgetExcelTotalEntity.setMonth4_budget_quota(String.valueOf(month4.get("budget_quota")));
        budgetExcelTotalEntity.setMonth4_confirmed_quota(String.valueOf(month4.get("confirmed_quota")));
        budgetExcelTotalEntity.setMonth4_remaining_quota(String.valueOf(month4.get("remaining_quota")));
        budgetExcelTotalEntity.setMonth4_amount_occupied(String.valueOf(month4.get("amount_occupied")));

        HashMap month5 = (HashMap) statistics.get("month5");
        budgetExcelTotalEntity.setMonth5_budget_quota(String.valueOf(month5.get("budget_quota")));
        budgetExcelTotalEntity.setMonth5_confirmed_quota(String.valueOf(month5.get("confirmed_quota")));
        budgetExcelTotalEntity.setMonth5_remaining_quota(String.valueOf(month5.get("remaining_quota")));
        budgetExcelTotalEntity.setMonth5_amount_occupied(String.valueOf(month5.get("amount_occupied")));

        HashMap month6 = (HashMap) statistics.get("month6");
        budgetExcelTotalEntity.setMonth6_budget_quota(String.valueOf(month6.get("budget_quota")));
        budgetExcelTotalEntity.setMonth6_confirmed_quota(String.valueOf(month6.get("confirmed_quota")));
        budgetExcelTotalEntity.setMonth6_remaining_quota(String.valueOf(month6.get("remaining_quota")));
        budgetExcelTotalEntity.setMonth6_amount_occupied(String.valueOf(month6.get("amount_occupied")));

        HashMap month7 = (HashMap) statistics.get("month7");
        budgetExcelTotalEntity.setMonth7_budget_quota(String.valueOf(month7.get("budget_quota")));
        budgetExcelTotalEntity.setMonth7_confirmed_quota(String.valueOf(month7.get("confirmed_quota")));
        budgetExcelTotalEntity.setMonth7_remaining_quota(String.valueOf(month7.get("remaining_quota")));
        budgetExcelTotalEntity.setMonth7_amount_occupied(String.valueOf(month7.get("amount_occupied")));

        HashMap month8 = (HashMap) statistics.get("month8");
        budgetExcelTotalEntity.setMonth8_budget_quota(String.valueOf(month8.get("budget_quota")));
        budgetExcelTotalEntity.setMonth8_confirmed_quota(String.valueOf(month8.get("confirmed_quota")));
        budgetExcelTotalEntity.setMonth8_remaining_quota(String.valueOf(month8.get("remaining_quota")));
        budgetExcelTotalEntity.setMonth8_amount_occupied(String.valueOf(month8.get("amount_occupied")));

        HashMap month9 = (HashMap) statistics.get("month9");
        budgetExcelTotalEntity.setMonth9_budget_quota(String.valueOf(month9.get("budget_quota")));
        budgetExcelTotalEntity.setMonth9_confirmed_quota(String.valueOf(month9.get("confirmed_quota")));
        budgetExcelTotalEntity.setMonth9_remaining_quota(String.valueOf(month9.get("remaining_quota")));
        budgetExcelTotalEntity.setMonth9_amount_occupied(String.valueOf(month9.get("amount_occupied")));

        HashMap month10 = (HashMap) statistics.get("month10");
        budgetExcelTotalEntity.setMonth10_budget_quota(String.valueOf(month10.get("budget_quota")));
        budgetExcelTotalEntity.setMonth10_confirmed_quota(String.valueOf(month10.get("confirmed_quota")));
        budgetExcelTotalEntity.setMonth10_remaining_quota(String.valueOf(month10.get("remaining_quota")));
        budgetExcelTotalEntity.setMonth10_amount_occupied(String.valueOf(month10.get("amount_occupied")));

        HashMap month11 = (HashMap) statistics.get("month11");
        budgetExcelTotalEntity.setMonth11_budget_quota(String.valueOf(month11.get("budget_quota")));
        budgetExcelTotalEntity.setMonth11_confirmed_quota(String.valueOf(month11.get("confirmed_quota")));
        budgetExcelTotalEntity.setMonth11_remaining_quota(String.valueOf(month11.get("remaining_quota")));
        budgetExcelTotalEntity.setMonth11_amount_occupied(String.valueOf(month11.get("amount_occupied")));

        HashMap month12 = (HashMap) statistics.get("month12");
        budgetExcelTotalEntity.setMonth12_budget_quota(String.valueOf(month12.get("budget_quota")));
        budgetExcelTotalEntity.setMonth12_confirmed_quota(String.valueOf(month12.get("confirmed_quota")));
        budgetExcelTotalEntity.setMonth12_remaining_quota(String.valueOf(month12.get("remaining_quota")));
        budgetExcelTotalEntity.setMonth12_amount_occupied(String.valueOf(month12.get("amount_occupied")));
        return budgetExcelTotalEntity;
    }


    /**
     * 生成草稿预算模板
     */
    @Override
    public int budgetInsert(HashMap map) {
        map.put("create_time", LocalDateTime.now());
        map.put("update_time", LocalDateTime.now());
        map.put("first_start", LocalDateTime.now());
        map.put("state", 0);
        map.put("create_by", SystemUtil.getUserNameCn());
        map.put("update_by", SystemUtil.getUserNameCn());
        map.put("disable", false);
        map.put("version", 1);

        mongoTemplate.insert(map, "budget_template");
        return 0;
    }

    /**
     * 发布
     *
     * @param id
     */
    @Override
    public int budgetPublish(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        Update update = new Update();
        update.set("state", 2);
        update.set("update_time", LocalDateTime.now());
        mongoTemplate.upsert(query, update, "budget_template");
        return 1;
    }

    /**
     * 修改
     */
    @Override
    public void budgetUpdate(HashMap map) {
        String id = (String) map.get("id");
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        HashMap template = mongoTemplate.findOne(query, HashMap.class, "budget_template");
        mongoTemplate.remove(query, "budget_template");
        map.put("_id", map.get("id"));
        map.remove("id");
        map.put("version", (Integer) (template.get("version")) + 1);
        budgetInsert(map);

    }

    public void delete_Id(HashMap hashMap) {
        hashMap.put("id", hashMap.get("_id").toString());
        hashMap.remove("_id");
    }

    /**
     * 禁用
     *
     * @param id
     */
    @Override
    public int budgetDisable(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        HashMap template = mongoTemplate.findOne(query, HashMap.class, "budget_template");
        if ((Integer) template.get("state") == 2) {
            Update update = new Update();
            update.set("state", 3);
            mongoTemplate.upsert(query, update, "budget_template");
            return -1;
        } else {
            Update update = new Update();
            update.set("state", 2);
            mongoTemplate.upsert(query, update, "budget_template");
            return 1;
        }
    }

    /**
     * 逻辑删除
     *
     * @param id
     */
    @Override
    public void budgetDelete(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        Update update = new Update();
        update.set("disable", true);
        mongoTemplate.upsert(query, update, "budget_template");
        // 删除所有预算引用模板
        Query query_reference = new Query();
        query_reference.addCriteria(Criteria.where("belong").regex(id));
        List<HashMap> template = mongoTemplate.find(query_reference, HashMap.class, "template");
        for (HashMap map : template) {
            List<String> belong = (List<String>) map.get("belong");
            if (belong != null) {
                belong.remove(id);
                Query query1 = new Query();
                query1.addCriteria(Criteria.where("_id").is(map.get("_id")));
                Update update1 = new Update();
                update1.set("belong", belong);
                mongoTemplate.upsert(query1, update1, "template");
            }
        }

        Query query_detail = new Query();
        query_detail.addCriteria(Criteria.where("belong").regex(id));
        List<HashMap> detail = mongoTemplate.find(query_detail, HashMap.class, "template_detail");
        for (HashMap map : detail) {
            List<String> belong = (List<String>) map.get("belong");
            if (belong != null) {
                belong.remove(id);
                Query query1 = new Query();
                query1.addCriteria(Criteria.where("_id").is(map.get("_id")));
                Update update1 = new Update();
                update1.set("belong", belong);
                mongoTemplate.upsert(query1, update1, "template_detail");
            }
        }
    }

    /**
     * 查询全部预算模板
     */
    @Override
    public List<HashMap> budgetSelectAll(String budgetName, Integer state) {
        Query query = new Query();
        query.addCriteria(Criteria.where("disable").is(false));
        if (StringUtils.isNotBlank(budgetName)) {
            query.addCriteria(Criteria.where("budget_name").regex(budgetName));
        }
        if (state != null) {
            query.addCriteria(Criteria.where("state").is(state));
        }
        List<HashMap> hashMaps = mongoTemplate.find(query, HashMap.class, "budget_template");
        for (HashMap hashMap : hashMaps) {
            delete_Id(hashMap);
//            // 判断是否已过期
//            String start_year = (String) hashMap.get("start_year");
//            int year = LocalDateTime.now().getYear();
//            if (Integer.parseInt(start_year) < year) {
//                hashMap.put("state",1);
//            }
        }
        return hashMaps;
    }

    /**
     * 查询单个
     *
     * @param id
     */
    @Override
    public HashMap budgetSelect(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        HashMap template = mongoTemplate.findOne(query, HashMap.class, "budget_template");
        delete_Id(template);
        return template;
    }

    /**
     * 针对第一次选公司
     *
     * @param map
     */
    @Override
    public List<HashMap> firstChoice(HashMap map) {
        // 获得公司数组去除部门id
        List<Integer> list = (List<Integer>) map.get("list");
        // 获取所有的公司id
        List<Integer> companyIds = bizBusinessTestMapper.onlyCompany(0L);
        // 只剩下公司
        List<Integer> result = list.stream().filter(companyIds::contains).collect(Collectors.toList());

        List<HashMap> ans = new ArrayList<>();
        // 拿到所有公司的计划
        for (Integer company : result) {
            Query query = new Query();
            query.addCriteria(Criteria.where("dept_id").is(company));
            // 只能有一个公司计划  不然就覆盖
            HashMap budget_plan = mongoTemplate.findOne(query, HashMap.class, "budget_plan");
            ans.add(budget_plan);
        }
        return ans;

    }

    /**
     * 针对前端回传公司代码变成为对象
     *
     * @param map
     */
    @Override
    public List<HashMap> handelDeptIdToObject(HashMap map) {
        List<Integer> key = (List<Integer>) map.get("key");
        String id = (String) map.get("id");
        List<HashMap> ans = new ArrayList<>();
        for (Integer deptId : key) {
            Query query = new Query();
            query.addCriteria(Criteria.where("dept_id").is(deptId).and("budgetId").is(id));
            HashMap plan = mongoTemplate.findOne(query, HashMap.class, "budget_plan");
            // 不存在该公司或者部门的计划
            if (plan == null) {
                Query query1 = new Query();
                query1.addCriteria(Criteria.where("dept_id").is(deptId));
                HashMap dept = mongoTemplate.findOne(query1, HashMap.class, "dept");
                for (int i = 1; i < 13; i++) {
                    dept.put("month" + i, 0);
                }
                dept.put("subsetIds", new ArrayList<HashMap>());
                dept.put("subsetObjects", new ArrayList<HashMap>());
                plan = dept;
                ans.add(plan);
            } else {
                delete_Id(plan);
                ans.add(plan);
            }


        }
        return ans;
    }

    /**
     * 保存
     *
     * @param map
     */
    @Transactional
    @Override
    public int savePlan(HashMap map) {
//        mongoTemplate.insert(map,"budget_plan");
        // 遍历取值 分开存储
        List<HashMap> list = (List<HashMap>) map.get("list");
        String id = (String) map.get("id");
        // 精确到明细就是true 精确到模板是false
        boolean flag = (boolean) map.get("flag");
        Boolean is_cumulative = (Boolean) map.get("is_cumulative");
        Integer overhead_method = (Integer) map.get("overhead_method");
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        HashMap template = mongoTemplate.findOne(query, HashMap.class, "budget_template");
        Update update = new Update();
        update.set("is_cumulative", is_cumulative);
        update.set("overhead_method", overhead_method);
        update.set("update_by", SystemUtil.getUserNameCn());
        update.set("update_time", LocalDateTime.now());

        // 判断是否过期
        String start_year = (String) template.get("start_year");
        int year = LocalDateTime.now().getYear();
        if (Integer.parseInt(start_year) < year) {
            update.set("state", 1);
        }

        mongoTemplate.upsert(query, update, "budget_template");
        // 遍历插入预算计划表
        process(list, id, flag);


        return 1;
    }

    private void process(List<HashMap> list, String budgetId, boolean flag) {
        for (HashMap hashMap : list) {
            List<HashMap> childrens = (List<HashMap>) hashMap.get("childrens");
            if (childrens != null && !childrens.isEmpty()) {
                process(childrens, budgetId, flag);
            }
            hashMap.remove("childrens");
            // 一个部门一个id 如果没有id就说明直接插入
            String id = (String) hashMap.get("id");
            // 直接插入 考虑到前端会回传id
            if (id == null) {
                hashMap.put("budgetId", budgetId);
                // 判断是否为有子预算 如果有就插入并绑定这个部门
                checkIfSubChild(hashMap, budgetId, flag);
                mongoTemplate.insert(hashMap, "budget_plan");
            }
            // 说明已经有了就更新
            else {
                // 先通过id删除
                Query query = new Query();
                query.addCriteria(Criteria.where("_id").is(id));
                mongoTemplate.remove(query, "budget_plan");
                // 判断是否为有子预算 如果有就插入或更新并绑定这个部门
                checkIfSubChild(hashMap, budgetId, flag);
                hashMap.put("budgetId", budgetId);
//                hashMap.put("_id",id);
                mongoTemplate.insert(hashMap, "budget_plan");

            }

        }
    }

    /**
     * 判断是否为有子预算 如果有就插入并绑定这个部门
     *
     * @param hashMap
     */
    private void checkIfSubChild(HashMap hashMap, String budgetId, boolean flag) {
        List<HashMap> subsetIds = (List<HashMap>) hashMap.get("subsetIds");
        List<HashMap> subsetObjects = (List<HashMap>) hashMap.get("subsetObjects");
        ThreadFactory build = new ThreadFactoryBuilder().setNameFormat("taskExecutor-").build();
        ThreadPoolExecutor taskExecutor = new ThreadPoolExecutor(
                2,
                2,
                60L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1),
                build
        );
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<String> ids = new ArrayList<>();
                if (subsetObjects != null) {
                    for (HashMap subsetObject : subsetObjects) {
                        ids.add((String) subsetObject.get("id"));
                    }
                    // 明细
                    if (flag) {

                        Query query = new Query();
                        query.addCriteria(Criteria.where("belong").regex(budgetId).and("_id").nin(ids));
                        //要被删除的
                        List<HashMap> hashMaps = mongoTemplate.find(query, HashMap.class, "template_detail");
                        for (HashMap map : hashMaps) {
                            List<String> belong = (List<String>) map.get("belong");
                            belong.remove(budgetId);
                            Update update = new Update();
                            update.set("belong", belong);
                            mongoTemplate.upsert(query, update, "template_detail");
                        }


                    } else {
                        Query query = new Query();
                        query.addCriteria(Criteria.where("belong").regex(budgetId).and("_id").nin(ids));
                        //要被删除的
                        List<HashMap> hashMaps = mongoTemplate.find(query, HashMap.class, "template");
                        for (HashMap map : hashMaps) {
                            List<String> belong = (List<String>) map.get("belong");
                            belong.remove(budgetId);
                            Update update = new Update();
                            update.set("belong", belong);
                            mongoTemplate.upsert(query, update, "template");
                        }
                    }
                }
            }
        });
        if (subsetIds == null || subsetObjects == null || subsetIds.isEmpty() || subsetObjects.isEmpty()) {
            return;
        }
        hashMap.remove("subsetIds");
        hashMap.remove("subsetObjects");
        Query query = new Query();
        query.addCriteria(Criteria.where("parent_id").is(hashMap.get("dept_id")).and("isSub").is(true).and("budgetId").is(budgetId));
        HashMap budget_plan = mongoTemplate.findOne(query, HashMap.class, "budget_plan");
        if (budget_plan == null) {
            HashMap<Object, Object> map = new HashMap<>();
            map.put("parent_id", hashMap.get("dept_id"));
            map.put("subsetIds", subsetIds);
            map.put("subsetObjects", subsetObjects);
            // 判断是否为子预算
            map.put("isSub", true);
            map.put("budgetId", budgetId);
            mongoTemplate.insert(map, "budget_plan");
            insertStatistics(map, flag, budgetId);
            // 明细 ----- 更新系统模板和明细状态
            if (flag) {
                for (HashMap subsetObject : subsetObjects) {
                    Query query1 = new Query();
                    query1.addCriteria(Criteria.where("name").is(subsetObject.get("name")));
                    HashMap template_detail = mongoTemplate.findOne(query1, HashMap.class, "template_detail");
                    List<String> belongs = (List<String>) template_detail.get("belong");
                    Set<String> belong = null;
                    if (belongs != null) {
                        belong = new HashSet<>(belongs);
                    }
                    if (belong == null) {
                        belong = new HashSet<>();
                    }
                    belong.add(budgetId);
                    Update update = new Update();
                    update.set("belong", belong);
                    mongoTemplate.upsert(query1, update, "template_detail");

                }


            }
            // 模板
            else {
                for (HashMap subsetObject : subsetObjects) {
                    // 模板

                    Query query1 = new Query();
                    query1.addCriteria(Criteria.where("_id").is(subsetObject.get("id")));
                    HashMap template_detail = mongoTemplate.findOne(query1, HashMap.class, "template");
                    List<String> belongs = (List<String>) template_detail.get("belong");
                    Set<String> belong = null;
                    if (belongs != null) {
                        belong = new HashSet<>(belongs);
                    }
                    if (belong == null) {
                        belong = new HashSet<>();
                    }
                    belong.add(budgetId);
                    Update update = new Update();
                    update.set("belong", belong);
                    mongoTemplate.upsert(query1, update, "template");

                }
            }
            // end-----
        } else {
            budget_plan.put("subsetObjects", subsetObjects);
            // 更新
            Update update = new Update();
            update.set("subsetIds", subsetIds);
            update.set("subsetObjects", subsetObjects);
            mongoTemplate.upsert(query, update, "budget_plan");
            insertStatistics(budget_plan, flag, budgetId);
            Query templateQuery = new Query();
            templateQuery.addCriteria(Criteria.where("_id").is(budgetId));
            HashMap resultTemplate = mongoTemplate.findOne(templateQuery, HashMap.class, "budget_template");
            Update templateUpdate = new Update();
            Integer version = resultTemplate.get("version") == null ? 1 : (Integer) resultTemplate.get("version");
            templateUpdate.set("version", version + 1);
            mongoTemplate.upsert(templateQuery, templateUpdate, "budget_template");
        }

    }

    /**
     * 更新统计表
     *
     * @param budget_plan
     */
    private void updateStatistics(HashMap budget_plan, boolean flag) {
//        Query query = new Query();
//        query.addCriteria(Criteria.where("dept_id").is(budget_plan.get("dept_id"))  );
//        HashMap budget_statistics = mongoTemplate.findOne(query, HashMap.class, "budget_statistics");
//        delete_Id(budget_statistics);
//        String id = (String) budget_statistics.get("id");

    }

    /**
     * 保存是时候插入统计表
     *
     * @param budget_plan
     */
    private void insertStatistics(HashMap budget_plan, boolean flag, String budgetId) {
        Integer parent_id = (Integer) budget_plan.get("parent_id");
        List<HashMap> subsetObjects = (List<HashMap>) budget_plan.get("subsetObjects");
        if (flag) {
            for (HashMap subsetObject : subsetObjects) {
                HashMap map = new HashMap();
                map.put("name", subsetObject.get("name"));
                map.put("dept_id", parent_id);
                for (int i = 1; i < 13; i++) {
                    HashMap<Object, Object> temp = new HashMap<>();
                    String s = subsetObject.get("month" + i) + "";
                    temp.put("budget_quota", s);
                    temp.put("amount_occupied", String.valueOf(0));
                    temp.put("confirmed_quota", String.valueOf(0));
                    temp.put("remaining_quota", s);
                    map.put("month" + i, temp);

                }
                Query query = new Query();
                query.addCriteria(Criteria.where("name").is(subsetObject.get("name"))
                        .and("dept_id").is(parent_id).and("budgetId").is(budgetId));
                HashMap temp = mongoTemplate.findOne(query, HashMap.class, "budget_statistics");
                if (temp == null) {
                    map.put("budgetId", budgetId);
                    mongoTemplate.insert(map, "budget_statistics");
                } else {
                    // 删除旧的
                    mongoTemplate.remove(query, "budget_statistics");
                    map.put("budgetId", budgetId);
                    mongoTemplate.insert(map, "budget_statistics");
                }

            }
        }
        // 精确到模板
        else {

            for (HashMap subsetObject : subsetObjects) {
                HashMap map = new HashMap();
                HashMap baseInformation = (HashMap) subsetObject.get("BaseInformation");
                map.put("name", baseInformation.get("name"));
                map.put("dept_id", parent_id);
                for (int i = 1; i < 13; i++) {
                    String s = subsetObject.get("month" + i) + "";
                    HashMap<Object, Object> temp = new HashMap<>();
                    temp.put("budget_quota", s);
                    temp.put("amount_occupied", String.valueOf(0));
                    temp.put("confirmed_quota", String.valueOf(0));
                    temp.put("remaining_quota", s);
                    map.put("month" + i, temp);
                }
                Query query = new Query();
                query.addCriteria(Criteria.where("name").is(baseInformation.get("name"))
                        .and("dept_id").is(parent_id).and("budgetId").is(budgetId));
                HashMap temp = mongoTemplate.findOne(query, HashMap.class, "budget_statistics");
                if (temp == null) {
                    map.put("budgetId", budgetId);
                    mongoTemplate.insert(map, "budget_statistics");
                } else {


                    mongoTemplate.remove(query, "budget_statistics");
                    map.put("budgetId", budgetId);
                    mongoTemplate.insert(map, "budget_statistics");
                }

            }
        }
    }

    /**
     * 针对前端回传明细代码变成为对象 可能是模板也有可能是明细
     *
     * @param map
     */
    @Override
    public List<HashMap> handeDetailIdToObject(HashMap map) {
        List<HashMap> result = new ArrayList<>();
        boolean flag = (boolean) map.get("flag");
        List<String> param = (List<String>) map.get("key");
        // 明细 为 true
        if (flag) {
            // 查明细表
            for (String s : param) {
                Query query = new Query();
                query.addCriteria(Criteria.where("_id").is(s));
                HashMap template_detail = mongoTemplate.findOne(query, HashMap.class, "template_detail");
                if (template_detail == null) {
                    continue;
                }
                for (int i = 1; i < 13; i++) {
                    template_detail.put("month" + i, 0);
                }
                delete_Id(template_detail);
                result.add(template_detail);
            }
        } else {
            // 查模板表
            for (String s : param) {
                Query query = new Query();
                query.addCriteria(Criteria.where("_id").is(s));
                HashMap template_detail = mongoTemplate.findOne(query, HashMap.class, "template");
                if (template_detail == null) {
                    continue;
                }
                for (int i = 1; i < 13; i++) {
                    template_detail.put("month" + i, 0);
                }
                delete_Id(template_detail);
                result.add(template_detail);
            }
        }
        return result;
    }

    /**
     * @param id
     */
    @Override
    public List<HashMap> selectForPlan(String id) {
        List<HashMap> result = new ArrayList<>();
        // select parentId 然后递归
        Query query = new Query();
        query.addCriteria(Criteria.where("budgetId").is(id).and("parent_id").is(0));
        List<HashMap> companies = mongoTemplate.find(query, HashMap.class, "budget_plan");
        for (HashMap company : companies) {
            findChild(company, id);
            delete_Id(company);
            result.add(company);
        }
        return result;
    }

    /**
     * 查询
     *
     * @param id
     */
    @Override
    public List<HashMap> selectForLook(String id) {
        List<HashMap> result = new ArrayList<>();
        // select parentId 然后递归
        Query query = new Query();
        query.addCriteria(Criteria.where("budgetId").is(id).and("parent_id").is(0));
        // 拿到所有公司的预算计划
        List<HashMap> companies = mongoTemplate.find(query, HashMap.class, "budget_plan");
        for (HashMap company : companies) {
            for (int i = 1; i < 13; i++) {
                HashMap<Object, Object> map = new HashMap<>();
                map.put("amount_occupied", "0");
                map.put("remaining_quota", "0");
                map.put("confirmed_quota", "0");
                map.put("budget_quota", "0");
                company.put("month" + i, map);
            }
            lookChild(company, id);
            delete_Id(company);
            result.add(company);
        }

        processData(result);
        return result;
    }

    /**
     * 累加子预算
     *
     * @param result
     */
    private List<HashMap> processData(List<HashMap> result) {
        for (HashMap map : result) {
            // 如果是子预算的就直接累加到对应的部门上去
            List<HashMap> subsetObjects = (List<HashMap>) map.get("subsetObjects");
            if (subsetObjects != null) {
                // 累加12个月的数据
                for (int i = 1; i < 13; i++) {
                    // 例如第一个月
                    BigDecimal v1 = BigDecimal.ZERO;
                    BigDecimal v2 = BigDecimal.ZERO;
                    BigDecimal v3 = BigDecimal.ZERO;
                    BigDecimal v4 = BigDecimal.ZERO;
                    for (HashMap subsetObject : subsetObjects) {

                        // 当月
                        HashMap month = (HashMap) subsetObject.get("month" + i);
                        BigDecimal amount_occupied = v1.add(new BigDecimal((String) month.get("amount_occupied")));
                        v1 = amount_occupied;
                        BigDecimal remaining_quota = v2.add(new BigDecimal((String) (month.get("remaining_quota"))));
                        v2 = remaining_quota;
                        BigDecimal confirmed_quota = v3.add(new BigDecimal((String) month.get("confirmed_quota")));
                        v3 = confirmed_quota;
                        BigDecimal budget_quota = v4.add(new BigDecimal((String) (month.get("budget_quota"))));
                        v4 = budget_quota;
                    }
                    HashMap month = (HashMap) map.get("month" + i);
                    month.put("amount_occupied", String.valueOf(new BigDecimal((String) month.get("amount_occupied")).add(v1)));
                    month.put("remaining_quota", String.valueOf(new BigDecimal((String) month.get("remaining_quota")).add(v2)));
                    month.put("confirmed_quota", String.valueOf(new BigDecimal((String) month.get("confirmed_quota")).add(v3)));
                    month.put("budget_quota", String.valueOf(new BigDecimal((String) month.get("budget_quota")).add(v4)));
                    map.put("month" + i, month);
                }


            }
        }


        for (HashMap map : result) {
            List<HashMap> childrens = (List<HashMap>) map.get("childrens");
            if (childrens != null) {
                List<HashMap> hashMaps = processData(childrens);

                for (int i = 1; i < 13; i++) {
                    // 例如第一个月
                    BigDecimal v1 = BigDecimal.ZERO;
                    BigDecimal v2 = BigDecimal.ZERO;
                    BigDecimal v3 = BigDecimal.ZERO;
                    BigDecimal v4 = BigDecimal.ZERO;
                    for (HashMap hashMap : hashMaps) {
                        // 当月
                        HashMap month = (HashMap) hashMap.get("month" + i);
                        BigDecimal amount_occupied = v1.add(new BigDecimal((String) month.get("amount_occupied")));
                        v1 = amount_occupied;
                        BigDecimal remaining_quota = v2.add(new BigDecimal((String) (month.get("remaining_quota"))));
                        v2 = remaining_quota;
                        BigDecimal confirmed_quota = v3.add(new BigDecimal((String) month.get("confirmed_quota")));
                        v3 = confirmed_quota;
                        BigDecimal budget_quota = v4.add(new BigDecimal((String) (month.get("budget_quota"))));
                        v4 = budget_quota;
                    }
                    HashMap month = (HashMap) map.get("month" + i);
                    month.put("amount_occupied", String.valueOf(new BigDecimal((String) month.get("amount_occupied")).add(v1)));
                    month.put("remaining_quota", String.valueOf(new BigDecimal((String) month.get("remaining_quota")).add(v2)));
                    month.put("confirmed_quota", String.valueOf(new BigDecimal((String) month.get("confirmed_quota")).add(v3)));
                    month.put("budget_quota", String.valueOf(new BigDecimal((String) month.get("budget_quota")).add(v4)));
                    map.put("month" + i, month);

                }
            }
        }

        return result;
    }

    private HashMap processSubData(HashMap map) {
        List<HashMap> subsetObjects = (List<HashMap>) map.get("subsetObjects");
        if (subsetObjects != null) {
            HashMap<Object, Object> temp = new HashMap<>();
            temp.put("amount_occupied", "0");
            temp.put("remaining_quota", "0");
            temp.put("confirmed_quota", "0");
            temp.put("budget_quota", "0");
            for (HashMap subsetObject : subsetObjects) {
                for (int i = 1; i < 13; i++) {
                    HashMap month = (HashMap) subsetObject.get("month" + i);
                    temp.put("amount_occupied", new BigDecimal((String) temp.get("amount_occupied")).add(new BigDecimal((String) month.get("amount_occupied"))));
                    temp.put("remaining_quota", new BigDecimal((String) temp.get("remaining_quota")).add(new BigDecimal((String) month.get("remaining_quota"))));
                    temp.put("confirmed_quota", new BigDecimal((String) temp.get("confirmed_quota")).add(new BigDecimal((String) month.get("confirmed_quota"))));
                    temp.put("budget_quota", new BigDecimal((String) temp.get("budget_quota")).add(new BigDecimal((String) month.get("budget_quota"))));
                }
            }
            return temp;
        }

//        List<HashMap> childrens = (List<HashMap>) map.get("childrens");
//        for (HashMap children : childrens) {
//            for (int i = 1; i < 13; i++) {
//                HashMap month = (HashMap) children.get("month" + i);
//
//            }
//
//            HashMap result = processSubData(children);
//            temp.put("amount_occupied",new BigDecimal((String)temp.get("amount_occupied") ).add(new BigDecimal((String)result.get("amount_occupied") )));
//            temp.put("remaining_quota",new BigDecimal((String)temp.get("remaining_quota") ).add(new BigDecimal((String)result.get("remaining_quota") )));
//            temp.put("confirmed_quota",new BigDecimal((String)temp.get("confirmed_quota") ).add(new BigDecimal((String)result.get("confirmed_quota") )));
//            temp.put("budget_quota",new BigDecimal((String)temp.get("budget_quota") ).add(new BigDecimal((String)result.get("budget_quota") )));
//        }
//        return temp;
        return null;
    }

    /**
     * 修改预算名字
     *
     * @param id
     */
    @Override
    public int updateName(String id, String name) {
        Update update = new Update();
        update.set("budget_name", name);
        update.set("update_time", LocalDateTime.now());
        update.set("update_by", SystemUtil.getUserNameCn());
        HashMap oneByMongo = findOneByMongo("_id", id, "budget_template", update);

        return 0;
    }

    private HashMap findOneByMongo(String target, Object anything, String collectName, Update update) {
        Query query = new Query();
        query.addCriteria(Criteria.where(target).is(anything));
        HashMap one = mongoTemplate.findOne(query, HashMap.class, collectName);
        if (update == null) {
            return one;
        }
        mongoTemplate.upsert(query, update, collectName);
        return one;
    }

    private void findChild(HashMap companies, String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("parent_id").is(companies.get("dept_id")).and("budgetId").is(id));
        List<HashMap> child = mongoTemplate.find(query, HashMap.class, "budget_plan");
        List<HashMap> subsetObjects = (List<HashMap>) companies.get("subsetObjects");
        if (child != null && !child.isEmpty() && (subsetObjects == null || subsetObjects.isEmpty())) {
            companies.put("childrens", child);
        }
        for (HashMap hashMap : child) {
            // 用来解耦
            checkAddSub(hashMap, id);
            delete_Id(hashMap);
            findChild(hashMap, id);
        }
    }

    private void lookChild(HashMap companies, String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("parent_id").is(companies.get("dept_id")).and("budgetId").is(id));
        List<HashMap> child = mongoTemplate.find(query, HashMap.class, "budget_plan");
        List<HashMap> subsetObjects = (List<HashMap>) companies.get("subsetObjects");
        if (child != null && !child.isEmpty() && (subsetObjects == null || subsetObjects.isEmpty())) {
            companies.put("childrens", child);
        }
        for (HashMap hashMap : child) {

            for (int i = 1; i < 13; i++) {
                HashMap<Object, Object> map = new HashMap<>();
                map.put("amount_occupied", "0");
                map.put("remaining_quota", "0");
                map.put("confirmed_quota", "0");
                map.put("budget_quota", "0");
                hashMap.put("month" + i, map);
            }

            // 用来解耦
            lookAddSub(hashMap, id);
            delete_Id(hashMap);
            lookChild(hashMap, id);
        }
    }


    /**
     * 返回给前端时组合所有的父子关系
     *
     * @param hashMap
     * @param id
     */
    private void checkAddSub(HashMap hashMap, String id) {
        Integer dept_id = (Integer) hashMap.get("dept_id");
        Query query = new Query();
        query.addCriteria(Criteria.where("parent_id").is(dept_id).and("isSub").is(true).and("budgetId").is(id));
        HashMap budget_plan = mongoTemplate.findOne(query, HashMap.class, "budget_plan");
        if (budget_plan == null) {
            return;
        }
        hashMap.put("subsetIds", budget_plan.get("subsetIds"));
        hashMap.put("subsetObjects", budget_plan.get("subsetObjects"));

    }

    /**
     * 返回给前端时组合所有的父子关系
     *
     * @param hashMap
     * @param id
     */
    private void lookAddSub(HashMap hashMap, String id) {
        Integer dept_id = (Integer) hashMap.get("dept_id");
        Query query = new Query();
        query.addCriteria(Criteria.where("parent_id").is(dept_id).and("isSub").is(true).and("budgetId").is(id));
        HashMap budget_plan = mongoTemplate.findOne(query, HashMap.class, "budget_plan");
        if (budget_plan == null) {
            return;
        }
        List<HashMap> subsetObjects = (List<HashMap>) budget_plan.get("subsetObjects");
        for (HashMap subsetObject : subsetObjects) {
            String name = (String) subsetObject.get("name");
            if (name == null) {
                HashMap baseInformation = (HashMap) subsetObject.get("BaseInformation");
                name = (String) baseInformation.get("name");
            }
            Query statisQuery = new Query();
            statisQuery.addCriteria(Criteria.where("budgetId").is(id).and("name").is(name)
                    .and("dept_id").is(budget_plan.get("parent_id")));
            HashMap budget_statistics = mongoTemplate.findOne(statisQuery, HashMap.class, "budget_statistics");
            if (budget_statistics != null) {
                for (int i = 1; i < 13; i++) {
                    subsetObject.put("month" + i, budget_statistics.get("month" + i));
                }
            }
        }
        hashMap.put("subsetIds", budget_plan.get("subsetIds"));
        hashMap.put("subsetObjects", budget_plan.get("subsetObjects"));

    }

    /**
     * 月底自动转账
     */
    @Scheduled(cron = "0 59 23 28-31 * ?")
    public void execute() {
        final Calendar c = Calendar.getInstance();
        if (c.get(Calendar.DATE) == c.getActualMaximum(Calendar.DATE)) {
            //show your code
            // 当前月份
            int monthValue = LocalDateTime.now().getMonthValue();
            // 如果是 12 月 不做处理
            if (monthValue == 12) {
                return;
            }
            // 当前年份
            int year = LocalDateTime.now().getYear();
            Query query = new Query();
            List<HashMap> hashMaps = mongoTemplate.find(query, HashMap.class, "budget_statistics");
            for (HashMap hashMap : hashMaps) {
                String budgetId = (String) hashMap.get("budgetId");
                Query temp = new Query();
                temp.addCriteria(Criteria.where("_id").is(budgetId));
                HashMap template = mongoTemplate.findOne(temp, HashMap.class, "budget_template");
                Boolean is_cumulative = (Boolean) template.get("is_cumulative");
                String start_year = (String) template.get("start_year");
                Integer state = (Integer) template.get("state");
                if (is_cumulative && start_year.equals(year + "") && state == 2) {
                    HashMap monthCurrent = (HashMap) hashMap.get("month" + monthValue);
                    HashMap monthNext = (HashMap) hashMap.get("month" + monthValue + 1);
                    // current
                    Object remaining_quota = monthCurrent.get("remaining_quota");
                    BigDecimal bigDecimal = new BigDecimal((String) remaining_quota);

                    //next
                    Object remaining_quota_next = monthNext.get("remaining_quota");
                    BigDecimal bigDecimal_next = new BigDecimal((String) remaining_quota_next);

                    BigDecimal add = bigDecimal.add(bigDecimal_next);
                    monthNext.put("remaining_quota", add);
                    Update update = new Update();
                    update.set("month" + monthValue + 1, monthNext);
                    Query tempQuery = new Query();
                    tempQuery.addCriteria(Criteria.where("_id").is(hashMap.get("_id")));
                    mongoTemplate.upsert(tempQuery, update, "budget_statistics");

                }

            }
        }
    }

    public void delete(List<String> ids, String id) {

        // 1. 去明细找 找不到去模板里面找
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        HashMap template_detail = mongoTemplate.findOne(query, HashMap.class, "template_detail");
        // 2. 是模板的id
        if (template_detail == null) {
            HashMap template = mongoTemplate.findOne(query, HashMap.class, "template");
            if (template == null) {
                return;
            }

            HashMap BaseInformation = (HashMap) template.get("BaseInformation");
            String directoryId = (String) BaseInformation.get("directoryId");
            ids.remove(directoryId);
            delete(ids, directoryId);
        } else {
            String template_id = (String) template_detail.get("template_id");
            ids.remove(template_id);
            delete(ids, template_id);
        }

    }

    /**
     * 查询当月该部门的余额
     *
     * @param map
     */
    @Override
    public String selectMoney(HashMap map) {
        // 预算id
        String id = (String) map.get("id");
        // 部门id
        Integer deptId = (Integer) map.get("deptId");
        // 月份
        Integer month = (Integer) map.get("month");
        // 付款明细id
        String typeId = (String) map.get("typeId");
        // 是否为明细 true 为明细
        Boolean flag = (Boolean) map.get("flag");
        // name
        String name;
        if (flag) {
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(typeId));
            HashMap template_detail = mongoTemplate.findOne(query, HashMap.class, "template_detail");
            name = (String) template_detail.get("name");
        } else {
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(typeId));
            HashMap template = mongoTemplate.findOne(query, HashMap.class, "template");
            HashMap baseInformation = (HashMap) template.get("BaseInformation");
            name = (String) baseInformation.get("name");
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(name).and("budgetId").is(id).and("dept_id").is(deptId));
        HashMap budget_statistics = mongoTemplate.findOne(query, HashMap.class, "budget_statistics");
        if (ObjectUtils.isEmpty(budget_statistics)) {
            return null;
        }
        HashMap o = (HashMap) budget_statistics.get("month" + month);
        String budget_quota = (String) o.get("remaining_quota");

        return budget_quota;
    }

    /**
     * 手动调剂
     *
     * @param map
     */
    @Override
    public int resetMoney(HashMap map) {
        // 预算id
        String id = (String) map.get("id");


        // 部门id
        Integer deptId = (Integer) map.get("deptId");
        // 月份
        Integer fromMonth = (Integer) map.get("fromMonth");
        Integer toMonth = (Integer) map.get("toMonth");
        // 付款明细id
        String fromTypeId = (String) map.get("fromTypeId");
        String toTypeId = (String) map.get("toTypeId");
        // 是否为明细 true 为明细
        Boolean flag = (Boolean) map.get("flag");
        // money
        String money = (String) map.get("money");
        if (id == null || deptId == null || fromMonth == null || toMonth == null || fromTypeId == null || toTypeId == null) {
            return 0;
        }
        // 日志
        mongoTemplate.insert(map, "budget_handle_log");

        // name
        String name;
        // another name
        String name0;
        // 如果是同一个月份 同一个类型的就不用管了
        if (Objects.equals(fromMonth, toMonth) && fromTypeId.equals(toTypeId) && 1 == 0) {
            if (flag) {
                Query query = new Query();
                query.addCriteria(Criteria.where("_id").is(fromTypeId));
                HashMap template_detail = mongoTemplate.findOne(query, HashMap.class, "template_detail");
                name = (String) template_detail.get("name");
            } else {
                Query query = new Query();
                query.addCriteria(Criteria.where("_id").is(fromTypeId));
                HashMap template = mongoTemplate.findOne(query, HashMap.class, "template");
                HashMap baseInformation = (HashMap) template.get("BaseInformation");
                name = (String) baseInformation.get("name");
            }
            Query query = new Query();
            query.addCriteria(Criteria.where("name").is(name).and("budgetId").is(id).and("dept_id").is(deptId));
            HashMap budget_statistics = mongoTemplate.findOne(query, HashMap.class, "budget_statistics");
            // 更新money
            HashMap from = (HashMap) budget_statistics.get("month" + fromMonth);


            String budget_quota = (String) from.get("budget_quota");
            // 共计money 直接赋值
            from.put("budget_quota", money);

            String remaining_quota = (String) from.get("remaining_quota");
            // 判断一下 是加还是减
            if (new BigDecimal(budget_quota).compareTo(new BigDecimal(money)) < 0) {
                BigDecimal subtract = new BigDecimal(money).subtract(new BigDecimal(budget_quota));
                BigDecimal add = new BigDecimal(remaining_quota).add(subtract);
                from.put("remaining_quota", add.toString());
            } else {
                BigDecimal subtract = new BigDecimal(budget_quota).subtract(new BigDecimal(money));
                BigDecimal subtract1 = new BigDecimal(remaining_quota).subtract(subtract);
                // 减完发现小于0 就直接变成0
                if (subtract1.compareTo(BigDecimal.ZERO) < 0) {
                    subtract1 = BigDecimal.ZERO;
                }
                from.put("remaining_quota", subtract1.toString());
            }

            Update update = new Update();
            update.set("month" + fromMonth, from);
            mongoTemplate.upsert(query, update, "budget_statistics");

        } else {

            // 同一个部门一个付款类型但是月份不同
            if (fromTypeId.equals(toTypeId)) {
                if (flag) {
                    Query query = new Query();
                    query.addCriteria(Criteria.where("_id").is(fromTypeId));
                    HashMap template_detail = mongoTemplate.findOne(query, HashMap.class, "template_detail");
                    name = (String) template_detail.get("name");
                } else {
                    Query query = new Query();
                    query.addCriteria(Criteria.where("_id").is(fromTypeId));
                    HashMap template = mongoTemplate.findOne(query, HashMap.class, "template");
                    HashMap baseInformation = (HashMap) template.get("BaseInformation");
                    name = (String) baseInformation.get("name");
                }
                Query query = new Query();
                query.addCriteria(Criteria.where("name").is(name).and("budgetId").is(id).and("dept_id").is(deptId));
                HashMap budget_statistics = mongoTemplate.findOne(query, HashMap.class, "budget_statistics");
                // 更新money
                HashMap from = (HashMap) budget_statistics.get("month" + fromMonth);
                HashMap to = (HashMap) budget_statistics.get("month" + toMonth);
                String budget_quota_from = (String) from.get("budget_quota");
                String budget_quota_to = (String) to.get("budget_quota");
                // 从
                BigDecimal bigDecimalFrom = new BigDecimal(budget_quota_from);
                BigDecimal bigDecimalTo = new BigDecimal(budget_quota_to);
                BigDecimal subtract = bigDecimalFrom.subtract(new BigDecimal(money));
                from.put("budget_quota", String.valueOf(subtract));

                BigDecimal add = bigDecimalTo.add(new BigDecimal(money));
                to.put("budget_quota", String.valueOf(add));

                // 调节剩余金额
                String remaining_quota_from = (String) from.get("remaining_quota");
                String remaining_quota_to = (String) to.get("remaining_quota");
                // 从
                BigDecimal bigDecimalFrom0 = new BigDecimal(remaining_quota_from);
                BigDecimal bigDecimalTo0 = new BigDecimal(remaining_quota_to);
                BigDecimal subtract0 = bigDecimalFrom0.subtract(new BigDecimal(money));
                from.put("remaining_quota", String.valueOf(subtract0));

                BigDecimal add0 = bigDecimalTo0.add(new BigDecimal(money));
                to.put("remaining_quota", String.valueOf(add0));
                Update update = new Update();
                update.set("month" + fromMonth, from);
                update.set("month" + toMonth, to);
                mongoTemplate.upsert(query, update, "budget_statistics");
            }
            // 同一个部门不同付款类型 不同月份
            else {
                if (flag) {
                    Query query = new Query();
                    query.addCriteria(Criteria.where("_id").is(fromTypeId));
                    HashMap template_detail = mongoTemplate.findOne(query, HashMap.class, "template_detail");
                    name = (String) template_detail.get("name");
                    Query query0 = new Query();
                    query0.addCriteria(Criteria.where("_id").is(toTypeId));
                    HashMap template_detail0 = mongoTemplate.findOne(query0, HashMap.class, "template_detail");
                    name0 = (String) template_detail0.get("name");
                } else {
                    Query query = new Query();
                    query.addCriteria(Criteria.where("_id").is(fromTypeId));
                    HashMap template = mongoTemplate.findOne(query, HashMap.class, "template");
                    HashMap baseInformation = (HashMap) template.get("BaseInformation");
                    name = (String) baseInformation.get("name");

                    Query query0 = new Query();
                    query0.addCriteria(Criteria.where("_id").is(fromTypeId));
                    HashMap template0 = mongoTemplate.findOne(query0, HashMap.class, "template");
                    HashMap baseInformation0 = (HashMap) template0.get("BaseInformation");
                    name0 = (String) baseInformation0.get("name");
                }
                Query query = new Query();
                query.addCriteria(Criteria.where("name").is(name).and("budgetId").is(id).and("dept_id").is(deptId));
                HashMap budget_statistics = mongoTemplate.findOne(query, HashMap.class, "budget_statistics");
                if (budget_statistics == null) {
                    return 0;
                }

                Query query0 = new Query();
                query0.addCriteria(Criteria.where("name").is(name0).and("budgetId").is(id).and("dept_id").is(deptId));
                HashMap budget_statistics0 = mongoTemplate.findOne(query0, HashMap.class, "budget_statistics");
                if (budget_statistics0 == null) {
                    return 0;
                }
                // 更新money
                HashMap from = (HashMap) budget_statistics.get("month" + fromMonth);
                HashMap to = (HashMap) budget_statistics0.get("month" + toMonth);
                String budget_quota_from = (String) from.get("budget_quota");
                String budget_quota_to = (String) to.get("budget_quota");
                // 从
                BigDecimal bigDecimalFrom = new BigDecimal(budget_quota_from);
                BigDecimal bigDecimalTo = new BigDecimal(budget_quota_to);
                BigDecimal subtract = bigDecimalFrom.subtract(new BigDecimal(money));
                from.put("budget_quota", String.valueOf(subtract));

                BigDecimal add = bigDecimalTo.add(new BigDecimal(money));
                to.put("budget_quota", String.valueOf(add));

                // 调节剩余金额
                String remaining_quota_from = (String) from.get("remaining_quota");
                String remaining_quota_to = (String) to.get("remaining_quota");
                // 从
                BigDecimal bigDecimalFrom0 = new BigDecimal(remaining_quota_from);
                BigDecimal bigDecimalTo0 = new BigDecimal(remaining_quota_to);
                BigDecimal subtract0 = bigDecimalFrom0.subtract(new BigDecimal(money));
                from.put("remaining_quota", String.valueOf(subtract0));

                BigDecimal add0 = bigDecimalTo0.add(new BigDecimal(money));
                to.put("remaining_quota", String.valueOf(add0));

                Update update = new Update();
                update.set("month" + fromMonth, from);
                mongoTemplate.upsert(query, update, "budget_statistics");

                Update update0 = new Update();
                update0.set("month" + toMonth, to);
                mongoTemplate.upsert(query0, update0, "budget_statistics");


            }

//
//
        }


        return 1;
    }

    /**
     * 修改已经发布的预算金额
     *
     * @return 是否更新成功
     */
    @Override
    public int updateBudgetMoney(HashMap hashMap) {

        // 通过type_id获取name
        Query query0 = new Query();
        query0.addCriteria(Criteria.where("_id").is(hashMap.get("type_id")));
        HashMap template = mongoTemplate.findOne(query0, HashMap.class, "template");
        HashMap baseInformation = (HashMap) template.get("BaseInformation");
        String name = (String) baseInformation.get("name");

        String budgetId = (String) hashMap.get("budgetId");
        Integer dept_id = (Integer) hashMap.get("dept_id");

        // 获取原有数据
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(name).and("budgetId").is(budgetId).and("dept_id").is(dept_id));
        HashMap budget_statistics = mongoTemplate.findOne(query, HashMap.class, "budget_statistics");
        if (budget_statistics.isEmpty()) throw new RuntimeException("未获取到预算数据");
        String toMonth = "month" + hashMap.get("month");
        String new_budget_quota = (String) hashMap.get("budget_quota");
        HashMap data = (HashMap) budget_statistics.get(toMonth);

        // 新数据
        Update update = new Update();
        HashMap new_budget = new HashMap<>();
        new_budget.put("budget_quota", new_budget_quota);
        // 计算预算余额 预算金额减去占用金额
        BigDecimal newBudgetMoney = new BigDecimal(new_budget_quota);
        BigDecimal remainBudgetMoney = new BigDecimal((String) data.get("amount_occupied"));
        new_budget.put("remaining_quota", newBudgetMoney.subtract(remainBudgetMoney));
        new_budget.put("amount_occupied", data.get("amount_occupied"));
        new_budget.put("confirmed_quota", data.get("confirmed_quota"));
        update.set(toMonth, new_budget);
        mongoTemplate.upsert(query, update, "budget_statistics");
        return 1;
    }

}
