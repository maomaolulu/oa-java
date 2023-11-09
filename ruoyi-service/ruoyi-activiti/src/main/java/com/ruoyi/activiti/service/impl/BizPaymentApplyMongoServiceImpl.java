package com.ruoyi.activiti.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.StatefulException;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.mongodb.client.result.DeleteResult;
import com.ruoyi.activiti.consts.ActivitiConstant;
import com.ruoyi.activiti.domain.SysAttachment;
import com.ruoyi.activiti.domain.budget.PaymentTemplateExcelEntity;
import com.ruoyi.activiti.domain.nbcb.NbcbAccountInfo;
import com.ruoyi.activiti.domain.proc.ActReProcdef;
import com.ruoyi.activiti.domain.proc.ActRuTask;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.domain.purchase.BizGoodsInfo;
import com.ruoyi.activiti.domain.vo.BizBusinessVO;
import com.ruoyi.activiti.domain.vo.DirectoryDTO;
import com.ruoyi.activiti.feign.RemoteFileService;
import com.ruoyi.activiti.mapper.*;
import com.ruoyi.activiti.mapper.nbcb.NbcbAccountInfoMapper;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.service.payment_mongodb.BizPaymentApplyService;
import com.ruoyi.activiti.utils.CollectUtil;
import com.ruoyi.activiti.utils.FileUtil;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.domain.SysDept;
import com.ruoyi.system.domain.SysRole;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteConfigService;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RuntimeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author wuYang
 * @date 2022/10/17 16:32
 */
@Service
@Slf4j
public class BizPaymentApplyMongoServiceImpl implements BizPaymentApplyService {
    @Resource
    MongoTemplate mongoTemplate;
    @Resource
    RemoteUserService remoteUserService;
    @Resource
    RemoteConfigService remoteConfigService;
    @Resource
    RemoteDeptService remoteDeptService;
    @Resource
    RemoteFileService remoteFileService;
    @Resource
    BizBusinessToMapper bizBusinessToMapper;
    @Resource
    BizBusinessTestMapper bizBusinessTestMapper;
    @Resource
    ActReProcdefMapper actReProcdefMapper;
    @Resource
    IBizBusinessService bizBusinessService;
    @Resource
    BizBusinessMapper businessMapper;
    @Resource
    BizGoodsInfoMapper bizGoodsInfoMapper;
    @Resource
    BizProcesskeyDeptMapper bizProcesskeyDeptMapper;
    @Resource
    BizProcesskeyRoleMapper bizProcesskeyRoleMapper;
    @Resource
    NbcbAccountInfoMapper nbcbAccountInfoMapper;
    @Resource
    ActRuTaskMapper actRuTaskMapper;

    /**
     * 模板插入mongodb
     * 禁用为0
     *
     * @param param json参数
     */
    @Override
    public Integer insert(HashMap<String, Object> param) {
        //拿到id 然后我直接找出名字放入

        HashMap baseInformation = (HashMap) param.get("BaseInformation");
        Integer number = null;

        //校验模板name是否重复 如果重复让用户去编辑已存在模板
        Query query = new Query();
        query.addCriteria(Criteria.where("BaseInformation.name").is(baseInformation.get("name").toString()));
        List<HashMap> template = mongoTemplate.find(query, HashMap.class, "template");
        // 有名字重复的
        if (template.size() > 0) {
            List<HashMap> collect = template.stream().sorted(new Comparator<HashMap>() {
                @Override
                public int compare(HashMap o1, HashMap o2) {
                    Integer number1 = (Integer) o1.get("number");
                    Integer number2 = (Integer) o2.get("number");
                    return number2 - number1;
                }
            }).collect(Collectors.toList());
            HashMap hashMap = collect.get(0);
            number = (Integer) hashMap.get("number");
            List<HashMap> ifHide1 = collect.stream().filter(e -> {
                boolean ifHide = e.get("ifHide") == null || (int) e.get("ifHide") != 1;
                return ifHide;
            }).collect(Collectors.toList());
            if (ifHide1.size() > 0) {
                return 0;
            }
        }

        number = number == null ? 0 : number;
        param.put("number", number + 1);


        baseInformation.put("createBy", SystemUtil.getUserNameCn());
        baseInformation.put("createTime", LocalDateTime.now());
        baseInformation.put("updateBy", SystemUtil.getUserNameCn());
        baseInformation.put("updateTime", LocalDateTime.now());
        // 默认就是禁用 0
        baseInformation.put("live", 0);
        mongoTemplate.save(param, "template");
        return 1;
    }

    /**
     * 更新模板
     * 记录历史模板
     *
     * @param param
     */
    @Override
    public int update(HashMap<String, Object> param) {

        HashMap baseInformation = (HashMap) param.get("BaseInformation");
        Integer live = (Integer) baseInformation.get("live");
        if (live == 1) {
            return 0;
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(param.get("id")));
        HashMap template = mongoTemplate.findOne(query, HashMap.class, "template");
        // 修改模板目录
        if (template != null) {
            HashMap baseInformation1 = (HashMap) template.get("BaseInformation");
            String oldDirectoryId = (String) baseInformation1.get("directoryId");
            String newDirectoryId = baseInformation.get("directoryId").toString();
            if (!newDirectoryId.equals(oldDirectoryId)) {
                // 查出所有的老模板
                Query query1 = new Query();
                query1.addCriteria(Criteria.where("BaseInformation.name").is(baseInformation.get("name").toString()));
                List<HashMap> hashMaps = mongoTemplate.find(query1, HashMap.class, "template");
                for (HashMap hashMap : hashMaps) {
                    Query temp = new Query();
                    temp.addCriteria(Criteria.where("_id").is(hashMap.get("_id")));
                    Update update = new Update();
                    update.set("directoryId", newDirectoryId);
                    mongoTemplate.upsert(temp, update, "template");
                }

            }
        }
        // 修改老模板
        Update update = new Update();
        update.set("ifHide", 1);
        mongoTemplate.upsert(query, update, "template");
        throwException(template, "模板更新--该id模板");
        // 拿到最大的number值
        Integer number = 0;
        Query query4 = new Query();
        query4.addCriteria(Criteria.where("BaseInformation.name").is(baseInformation.get("name").toString()));
        List<HashMap> template4 = mongoTemplate.find(query4, HashMap.class, "template");
        List<HashMap> collect = template4.stream().sorted(new Comparator<HashMap>() {
            @Override
            public int compare(HashMap o1, HashMap o2) {
                Integer number1 = (Integer) o1.get("number");
                Integer number2 = (Integer) o2.get("number");
                return number2 - number1;
            }
        }).collect(Collectors.toList());
        if (template4.size() > 0) {
            HashMap hashMap = collect.get(0);
            number = (Integer) hashMap.get("number");
        }

        param.put("number", number + 1);
        // check name is duplicate
//        HashMap oldBaseInformation = ((HashMap) template.get("BaseInformation"));
//        String oldName = ((String) oldBaseInformation.get("name"));
        HashMap newBaseInformation = ((HashMap) param.get("BaseInformation"));
//        String newName = ((String) newBaseInformation.get("name"));
//        if (!oldName.equals(newName)) {
//
//        }
        newBaseInformation.put("updateBy", SystemUtil.getUserNameCn());
        newBaseInformation.put("updateTime", new Date());
        mongoTemplate.insert(param, "template");
        return 1;
    }

    /**
     * 返回二级目录
     *
     * @param
     */
    @Override
    public List<HashMap> secondDirector() {
        Query query = new Query();
        query.addCriteria(Criteria.where("parentId").ne("0"));
        List<HashMap> hashMaps = mongoTemplate.find(query, HashMap.class, "directory");
        for (HashMap hashMap : hashMaps) {
            delete_Id(hashMap);
        }
        return hashMaps;
    }

    /**
     * 查询所有模板列表
     *
     * @return
     */
    @Override
    public Object select(String id, Integer isLive) {
        //判断是否是一级目录
        Query isChildQuery = new Query();
        isChildQuery.addCriteria(Criteria.where("_id").is(id));
        HashMap directory = mongoTemplate.findOne(isChildQuery, HashMap.class, "directory");
        // 第一目录
        if (directory.get("parentId").toString().equals("0")) {
            Query q = new Query();
            q.addCriteria(Criteria.where("parentId").is(id));
            List<HashMap> directory1 = mongoTemplate.find(q, HashMap.class, "directory");
            List<HashMap> result = new ArrayList<>();
            for (HashMap hashMap : directory1) {
                //todo 没有时间
                Query query = new Query();

                if (isLive == 0) {
                    query.addCriteria(Criteria.where("BaseInformation.directoryId").is(hashMap.get("_id").toString()));
                }
                if (isLive == 1) {
                    query.addCriteria(Criteria.where("BaseInformation.directoryId").is(hashMap.get("_id").toString()));
                    query.addCriteria(Criteria.where("BaseInformation.live").is(1));
                }
                if (isLive == 2) {
                    query.addCriteria(Criteria.where("BaseInformation.directoryId").is(hashMap.get("_id").toString()));
                    query.addCriteria(Criteria.where("BaseInformation.live").is(0));
                }

                List<HashMap> template = mongoTemplate.find(query, HashMap.class, "template");
                for (HashMap hashMap1 : template) {
                    hashMap1.put("id", hashMap1.get("_id").toString());
                    hashMap1.remove("_id");
                }
                List<HashMap> one = getOne(template);

                List<HashMap> collect = getHashMaps(one);
                result.addAll(collect);

            }
            return result;

        }
        Query q = new Query();
//        q.fields().include("BaseInformation.createBy");
//        q.fields().include("BaseInformation.processKey");
//        q.fields().include("BaseInformation.mark");
//        q.fields().include("BaseInformation.live");
//        q.fields().include("BaseInformation.type");
//        q.fields().include("BaseInformation.createTime");
//        q.fields().include("BaseInformation.updateBy");
//        q.fields().include("BaseInformation.updateTime");
//        q.fields().include("_id");
//        q.fields().include("number");
        if (isLive == 0) {
            q.addCriteria(Criteria.where("BaseInformation.directoryId").is(id));
        }
        if (isLive == 1) {
            q.addCriteria(Criteria.where("BaseInformation.directoryId").is(id)
                    .and("BaseInformation.live").is(1));

        }
        if (isLive == 2) {
            q.addCriteria(Criteria.where("BaseInformation.directoryId").is(id).and("BaseInformation.live").is(0));
        }

        List<HashMap> template1 = mongoTemplate.find(q, HashMap.class, "template");
        for (HashMap hashMap : template1) {
            hashMap.put("id", hashMap.get("_id").toString());
            hashMap.remove("_id");
        }
        List<HashMap> one = getOne(template1);
        List<HashMap> collect = getHashMaps(one);
        return collect;
    }

    /**
     * 查看模板是否为旧mob
     *
     * @param id
     */
    @Override
    public Boolean checkTemplateIsOld(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        HashMap payment = mongoTemplate.findOne(query, HashMap.class, "payment");
        if (payment == null) {
            return true;
        }
        HashMap baseInformation = (HashMap) payment.get("BaseInformation");
        throwException(baseInformation, "baseInformation");
        List<String> paymentId = (List<String>) baseInformation.get("paymentId");
        throwException(paymentId, "paymentId");
        String tId = paymentId.get(paymentId.size() - 1);

        Query queryT = new Query();
        queryT.addCriteria(Criteria.where("_id").is(tId));
        HashMap template = mongoTemplate.findOne(queryT, HashMap.class, "template");
        Integer a = (Integer) template.get("ifHide");
        if (a == null) {
            return false;
        } else {
            return true;
        }
    }

    private List<HashMap> getOne(List<HashMap> template) {
        List<HashMap> ans = new ArrayList<>();
        Map<Object, List<HashMap>> collect = template.stream().collect(Collectors.groupingBy(new Function<HashMap, Object>() {
            @Override
            public Object apply(HashMap hashMap) {
                HashMap baseInformation = (HashMap) hashMap.get("BaseInformation");
                Object name = baseInformation.get("name");
                return name;
            }
        }));
        for (Map.Entry<Object, List<HashMap>> temp : collect.entrySet()) {
            List<HashMap> value = temp.getValue();
            HashMap onlyOne = getOnlyOne(value);
            ans.add(onlyOne);
        }
        return ans;
    }

    HashMap getOnlyOne(List<HashMap> template) {
        HashMap temp = template.get(0);
        for (HashMap hashMap : template) {
            if ((int) hashMap.get("number") >
                    (int) temp.get("number")) {
                temp = hashMap;
            }
        }
        return temp;
    }

    /**
     * 删除模板template
     *
     * @param id
     */
    @Override
    public boolean delete(String id) {
        //  校验 payment 表中是否有数据再用这张表
        Query query = new Query();
        query.addCriteria(Criteria.where("template").is(id));
        List<HashMap> payment = mongoTemplate.find(query, HashMap.class, "payment");
        if (payment.isEmpty()) {
            Query delete = new Query();
            delete.addCriteria(Criteria.where("_id").is(id));
            HashMap template = mongoTemplate.findOne(delete, HashMap.class, "template");
            HashMap baseInformation = (HashMap) template.get("BaseInformation");
            // 找到所有的name相同的全部逻辑删除
            String name = (String) baseInformation.get("name");
            Query query1 = new Query();
            query1.addCriteria(Criteria.where("BaseInformation.name").is(name));
            List<HashMap> template1 = mongoTemplate.find(query1, HashMap.class, "template");
            for (HashMap hashMap : template1) {
                delete_Id(hashMap);
                Query query2 = new Query();
                query2.addCriteria(Criteria.where("_id").is(hashMap.get("id")));
                Update update = new Update();
                update.set("ifHide", 1);
                mongoTemplate.upsert(query2, update, "template");
            }
            return true;
        }
        return false;

    }

    /**
     * 获取单个模板
     *
     * @param id
     */
    @Override
    public HashMap<String, Object> getTemplate(String id) {
        HashMap hashMap = mongoTemplate.findById(id, HashMap.class, "template");
        delete_Id(hashMap);
        List<HashMap<String, Object>> data = (List) hashMap.get("data");
        HashMap baseInformation = (HashMap) hashMap.get("BaseInformation");
        Boolean isFeeDept = (Boolean) baseInformation.get("isFeeDept");
        String processKey = (String) baseInformation.get("processKey");
        if (isFeeDept) {
            // 获取费用归属部门
            Query query1 = new Query();
            query1.addCriteria(Criteria.where("_id").is("637ae9bbad3a0000ec003783"));
            HashMap<String, Object> configuration = mongoTemplate.findOne(query1, HashMap.class, "configuration");
            throwException(configuration, "费用归属部门控件");
            delete_Id(configuration);
            data.add(0, configuration);
        }

        // 获取付款明细
        Query query1 = new Query();
        query1.addCriteria(Criteria.where("_id").is("63560277662d00005c0019f2"));
        HashMap<String, Object> configuration = mongoTemplate.findOne(query1, HashMap.class, "configuration");
        throwException(configuration, "金额(控件)");
        delete_Id(configuration);
        changeLable(configuration, processKey);
        data.add(data.size(), configuration);

        // 付款事由
        Query query2 = new Query();
        query2.addCriteria(Criteria.where("_id").is("6350b5397b320000a5000913"));
        HashMap<String, Object> configuration1 = mongoTemplate.findOne(query2, HashMap.class, "configuration");
        throwException(configuration1, "付款事由");
        delete_Id(configuration1);
        data.add(data.size(), configuration1);

        List list = new ArrayList();
        list.add(data);
        hashMap.put("detail", list);
        hashMap.put("projectMoney", 0);
        hashMap.put("applyCode", null);
        hashMap.put("userCompany", SystemUtil.getCompanyName());

        hashMap.remove("data");
        return hashMap;
    }

    /**
     * 业务宣传费、渠道服务费、业务分包费 lable 单独处理
     *
     * @param configuration 配置信息
     * @param processKey    流程key
     */
    private void changeLable(HashMap<String, Object> configuration, String processKey) {
        List<String> list = new ArrayList<>();
        list.add("payment-qudaofuwu");
        list.add("payment-yewuxuanchuan");
        list.add("payment-renzhengfenbao");
        if (list.contains(processKey)) {
            configuration.put("label", "此次报销金额");
        }
    }

    //    @Scheduled(cron = "*/20 * * * * ?")
//    public void dailyCheck() {
//        Query query = new Query();
//        query.addCriteria(Criteria.where("createTime").gt(LocalDate.now()));
//        List<HashMap> hashMaps = mongoTemplate.find(query, HashMap.class, "payment");
//        for (HashMap hashMap : hashMaps) {
//            String id = hashMap.get("_id").toString();
//            HashMap map = bizBusinessTestMapper.deletePayment(id);
//            if (map == null) {
//                Query query1 = new Query();
//                query1.addCriteria(Criteria.where("_id").is(hashMap.get("_id")));
//                Update update = new Update();
//                update.set("result",3);
//                mongoTemplate.upsert(query1,update,"payment");
//            }
//        }
//    }
    // 流程中止后 不能让提交发票了 去数据库查看中止的 去删mongodb种的
    @Scheduled(cron = "0 0 */1 * * ?")
    public void dailyCheck1() {

    }

    /**
     * 启动流程
     *
     * @param param
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int startPaymentProcess(HashMap<String, Object> param) {
        BizBusiness business = null;
        List<Integer> array = null;
        List<HashMap<String, Object>> associateApply;
        List<HashMap<String, Object>> associatePurchaseApply = null;
        Long userId = SystemUtil.getUserId();
        try {
            // 开始处理信息
            HashMap baseInformation = (HashMap) param.get("BaseInformation");
            String processKey = (String) baseInformation.get("processKey");

            // 判断角色或部门是否可以发起申请
//            if (!allowApply(processKey)) return -999;

            throwException(baseInformation, "基本信息");
            param.put("userName", SystemUtil.getUserNameCn());
            // 拿到模板选的控件去取他的值返回
            extracted(param);
            // 费用相关部门
            boolean isFeeDept = (boolean) baseInformation.get("isFeeDept");
            if (isFeeDept) {
                List<List<HashMap>> detail = (List<List<HashMap>>) param.get("detail");
                int r = extracted(Double.valueOf(param.get("projectMoney").toString()), param);
                if (r == -1) {
                    return -666;
                }
                // 处理费用归属部门
                array = new ArrayList<>();
                if (detail != null) {
                    throwException(detail, "部门id数组");
                    for (List<HashMap> hashMaps : detail) {
                        for (HashMap hashMap : hashMaps) {
                            String label = (String) hashMap.get("label");
                            if (label.equals("费用归属部门")) {
                                // 找到费用归属部门 拿到部门的id
                                List<Integer> department_id = (List<Integer>) hashMap.get("value");
                                array.add(department_id.get(department_id.size() - 1));
                                // 费用归属部门名称
                                StringBuilder stringBuilder = new StringBuilder();
                                for (Integer integer : department_id) {
                                    SysDept companyName = remoteDeptService.selectSysDeptByDeptId(integer);
                                    throwException(companyName, "费用相关部门id");
                                    stringBuilder.append(companyName.getDeptName());
                                    stringBuilder.append("/");
                                }

                                String s = stringBuilder.toString();
                                hashMap.put("array", hashMap.get("value"));
                                hashMap.put("value", s.substring(0, s.length() - 1));
                            }
                        }
                    }
                }
            }

            // 修改type
            List<String> paymentId = (List<String>) baseInformation.get("paymentId");
            throwException(paymentId, "目录数组");
            StringBuilder sb = new StringBuilder();
            String s1 = paymentId.get(0);
            String s2 = paymentId.get(1);
            String s3 = paymentId.get(2);
            Query query = new Query();
            query.fields().include("name");
            query.addCriteria(Criteria.where("_id").is(s1));
            HashMap directory = mongoTemplate.findOne(query, HashMap.class, "directory");
            sb.append((String) directory.get("name"));
            sb.append("/");
            Query query1 = new Query();
            query1.fields().include("name");
            query1.addCriteria(Criteria.where("_id").is(s2));
            HashMap directory1 = mongoTemplate.findOne(query1, HashMap.class, "directory");
            sb.append((String) directory1.get("name"));
            sb.append("/");
            Query query2 = new Query();
            query2.addCriteria(Criteria.where("_id").is(s3));
            HashMap template = mongoTemplate.findOne(query2, HashMap.class, "template");
            HashMap baseInformation1 = (HashMap) template.get("BaseInformation");
            String name = (String) baseInformation1.get("name");
            sb.append(name);
            baseInformation.put("titleType", (String) baseInformation.get("type"));
            baseInformation.put("type", sb.toString());

            // 发票
            // 判断是否为必须校验的不然略过
            List<HashMap> bill_information = (List<HashMap>) baseInformation.get("bill_information");
            if (!bill_information.isEmpty()) {

                // 必须检验
                List<HashMap> mustCheckList = new ArrayList<>();
                for (HashMap hashMap : bill_information) {
                    if (hashMap.get("bill_type") == null || StringUtils.isBlank(hashMap.get("bill_type").toString())) {
                        continue;
                    }
                    if (hashMap.get("bill_type").toString().equals("电子增值税专用发票") || hashMap.get("bill_type").toString().equals("增值税专用发票")) {
                        mustCheckList.add(hashMap);
                    }
                }
                HashSet<HashMap> hashMaps = new HashSet<>(mustCheckList);
                if (mustCheckList.size() != hashMaps.size()) {
                    return -2;
                }
            }

            // 填入公司和部门名
            HashMap account_information = (HashMap) baseInformation.get("account_information");
            throwException(account_information, "账户信息");

            Integer deptId = (Integer) account_information.get("subjectionDeptId");
            throwException(deptId, "部门名");

            Integer companyId = (Integer) account_information.get("company_id");
            throwException(companyId, "公司名");

            SysDept dept = remoteDeptService.selectSysDeptByDeptId(deptId);
            SysDept company = remoteDeptService.selectSysDeptByDeptId(companyId);
            param.put("deptName", dept.getDeptName());
            param.put("companyName", company.getDeptName());
            String cc = (String) baseInformation.get("cc");
            throwException(cc, "抄送人");

            if (StrUtil.isNotBlank(cc)) {
                cc = String.join(",", CollectUtil.twoClear(cc.split(",")));
                param.put("cc", cc);
            }

            String userName = SystemUtil.getUserName();
            SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
            Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(sysUser.getDeptId());
            String companyId1 = belongCompany2.get("companyId").toString();
            param.put("createTime", LocalDateTime.now());
            param.put("updateTime", LocalDateTime.now());
            param.put("createBy", userName);
            param.put("updateBy", userName);

            //申请人部门
            param.put("eeptId", sysUser.getDeptId());
            param.put("title", sysUser.getUserName() + "提交的" + name + "申请");

            //获取到明细
            List<List<HashMap<String, Object>>> paymentDetails = (List<List<HashMap<String, Object>>>) param.get("detail");
            throwException(paymentDetails, "明细");

            if (paymentDetails == null || paymentDetails.isEmpty()) {
                return 3;
            }

            //插入
            param.put("userId", SystemUtil.getUserId());
            param.put("state", 1);
            param.put("result", 1);
            param.put("feeArray", array);
            // 项目金额
            BigDecimal totalMoney = BigDecimal.ZERO;
            List<HashMap> information_relate = (List<HashMap>) baseInformation.get("information_relate");
//            if ((Boolean) baseInformation.get("isCheckInformation") && information_relate.size() > 0) {
            if (information_relate.size() > 0) {
                // 计算项目总金额
                for (HashMap hashMap : information_relate) {
                    String projece_pay = hashMap.get("projece_pay").toString();
                    if (StrUtil.isNotBlank(projece_pay)) {
                        totalMoney = totalMoney.add(new BigDecimal(projece_pay));
                    }
                }
            }

            param.put("totalMoney", totalMoney);
            HashMap<String, Object> payment = mongoTemplate.insert(param, "payment");
            log.info("mongodb已插入 mysql未插入");
            // 判断是否为草稿
            param.put("id", payment.get("_id"));
            // 将上传的临时文件转为有效文件
            int reimburse = remoteFileService.updateMongo(param.get("id").toString(), (String) param.get("applyCode"));
            if (reimburse == 0) {
                throw new StatefulException("将上传的文件转为有效文件失败");
            }

            // 关联多个审批单
            associateApply = (List<HashMap<String, Object>>) baseInformation.get("approvalData");
            if (associateApply == null) {
                associateApply = new ArrayList<>();
            }

            // 关联多个采购单的物品
            associatePurchaseApply = (List<HashMap<String, Object>>) baseInformation.get("purchaseData");
            associatePurchaseApply = associatePurchaseApply.stream().distinct().collect(Collectors.toList());
            if (associatePurchaseApply.size() >= 1) {
                for (HashMap<String, Object> map : associatePurchaseApply) {
                    long id = Long.parseLong(String.valueOf(map.get("id")));
                    BizGoodsInfo bizGoodsInfo = new BizGoodsInfo();
                    bizGoodsInfo.setId(id);
                    bizGoodsInfo.setFinanceStatus(1);
                    bizGoodsInfoMapper.updateByPrimaryKeySelective(bizGoodsInfo);
                }
            }
            // 去重
            associatePurchaseApply = associatePurchaseApply.stream().distinct().collect(Collectors.toList());

            // 初始化流程
            business = initBusiness(param);
            business.setCompanyId(SystemUtil.getCompanyId());
            business.setCompanyName(SystemUtil.getCompanyName());
            business.setDeptId(SystemUtil.getDeptId());
            business.setDeptName(sysUser.getDept().getDeptName());
            business.setApplyCode((String) param.get("applyCode"));
            bizBusinessService.insertBizBusiness(business);
            log.info("mysql已插入");

            // 获取经营参数
            SysConfig config1 = new SysConfig();
            SysConfig config2 = new SysConfig();
            SysConfig config3 = new SysConfig();
            config1.setConfigKey(companyId1 + "g1" + processKey);
            config2.setConfigKey(companyId1 + "g2" + processKey);
            config3.setConfigKey(companyId1 + "g3" + processKey);
            List<SysConfig> list = remoteConfigService.listOperating(config1);
            List<SysConfig> list2 = remoteConfigService.listOperating(config2);
            List<SysConfig> list3 = remoteConfigService.listOperating(config3);

            Map<String, Object> variables = Maps.newHashMap();
            if (list.size() >= 1) {
                variables.put("g1", Double.valueOf(list.get(0).getConfigValue()));
            }
            if (list2.size() >= 1) {
                variables.put("g2", Double.valueOf(list2.get(0).getConfigValue()));
            }
            if (list3.size() >= 1) {
                variables.put("g3", Double.valueOf(list3.get(0).getConfigValue()));
            }
            // totalMoney 是项目金额
            if (processKey.equals("payment-qudaofuwu") || processKey.equals("payment-yewuxuanchuan")) {
                variables.put("g1", (totalMoney.multiply(new BigDecimal(0.15))));
                variables.put("g2", (totalMoney.multiply(new BigDecimal(0.3))));
                variables.put("g3", (totalMoney.multiply(new BigDecimal(0.5))));
            }
            if (processKey.equals("payment-renzhengfenbao")) {
                variables.put("g1", (totalMoney.multiply(new BigDecimal(0.3))));
            }
            if (processKey.equals("payment-fznbzzdb") || processKey.equals("payment-jttxnzjdb")) {
                variables.put("id", param.get("id").toString());
            }
            Object projectMoney = param.get("projectMoney");
            String key = (String) baseInformation.get("processKey");
            String ccs = baseInformation.get("cc").toString();
            throwException(projectMoney, "付款总金额");
            variables.put("money", Double.valueOf(projectMoney.toString()));
            // insert into cc param
            do_insertKey(key, ccs, projectMoney.toString(), param);
            // 这里可以设置各个负责人，key跟模型的代理变量一致
            variables.put("cc", ccs);

            // 上海量远添加副总经理审批节点
            Map<String, Object> lyVaviables = getLyVaviables(Long.valueOf(deptId), processKey);
            variables.putAll(lyVaviables);

            // 财务总监个人费用需要总裁审批
            Map<String, Object> cfoVaviables = getCfoVaviables(userId, processKey);
            variables.putAll(cfoVaviables);

            bizBusinessService.startProcess(business, variables);

            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            // 手动回滚金额 是value还是array
            backMoney(param);
            String id = param.get("_id").toString();
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(id));
            DeleteResult payment = mongoTemplate.remove(query, "payment");
            // 删除总表
            String procInstId = business.getProcInstId();
            if (procInstId == null) {
                bizBusinessService.deleteBizBusinessById(business.getId());
            } else {
                runtimeService.deleteProcessInstance(procInstId, "异常");
            }

            // 删除关联的东西
            associatePurchaseApply = associatePurchaseApply.stream().distinct().collect(Collectors.toList());
            if (associatePurchaseApply.size() >= 1) {
                for (HashMap<String, Object> map : associatePurchaseApply) {
                    Long idDelete = Long.parseLong(String.valueOf(map.get("id")));
                    BizGoodsInfo bizGoodsInfo = new BizGoodsInfo();
                    bizGoodsInfo.setId(idDelete);
                    bizGoodsInfo.setFinanceStatus(0);
                    bizGoodsInfoMapper.updateByPrimaryKeySelective(bizGoodsInfo);
                }
            }
            log.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return -111;
    }

    /**
     * 判断角色或部门是否可以发起申请
     *
     * @param processKey 流程唯一key
     * @return 结果
     */
    private boolean allowApply(String processKey) {
        Long userId = SystemUtil.getUserId();
        Long deptId = SystemUtil.getDeptId();
        Long comId = SystemUtil.getCompanyId();
        List<String> ans = new ArrayList<>();
        ans.add("payment-shourujianmian");
        if (ans.contains(processKey)) {
            List<Long> temp = bizBusinessTestMapper.getDeptNum("市场部");
            // 本部门负责人
            Integer roleNum = bizBusinessTestMapper.getRoleNum(150, userId);
            return temp.contains(deptId) && roleNum >= 1;
        }
        ans.clear();
        ans.add("payment-renzhengfenbao");
        ans.add("payment-qudaofuwu");
        ans.add("payment-yewuxuanchuan");
        ans.add("payment-fukuaidaishoudaifu");
        if (ans.contains(processKey)) {
            if (183 == comId) {
                // 亿联检测
                List<Long> temp = bizBusinessTestMapper.getDeptNum("项目部");
                List<Long> temp1 = bizBusinessTestMapper.getDeptNum("财务部");
                List<Long> temp2 = bizBusinessTestMapper.getDeptNum("总经办");
                temp.addAll(temp1);
                temp.addAll(temp2);
                return temp.contains(deptId);
            } else if (168 == comId) {
                // 金华职康
                List<Long> temp = bizBusinessTestMapper.getDeptNum("综合部");
                return temp.contains(deptId);
            } else if (281 == comId) {
                // 上海研晰
                List<Long> temp = bizBusinessTestMapper.getDeptNum("客服部");
                List<Long> temp1 = bizBusinessTestMapper.getDeptNum("行政人事部");
                temp.addAll(temp1);
                return temp.contains(deptId);
            } else if (350 == comId) {
                // 亿达检测
                if (("payment-qudaofuwu".equals(processKey)
                        || "payment-renzhengfenbao".equals(processKey))
                        && 786 == userId) {
                    // 翁肖佳 开通渠道服务费、业务分包费申请权限
                    return true;
                }
                if ("payment-fukuaidaishoudaifu".equals(processKey) && 1029 == userId) {
                    // 个人计量部 钟雷婷
                    return true;
                }
                List<Long> temp = bizBusinessTestMapper.getDeptNum("行政部");
                List<Long> temp1 = bizBusinessTestMapper.getDeptNum("财务部");
                List<Long> temp2 = bizBusinessTestMapper.getDeptNum("业务部");
                temp.addAll(temp1);
                temp.addAll(temp2);
                return temp.contains(deptId);
            } else if (182 == comId) {
                // 郑州维安
                List<Long> temp = bizBusinessTestMapper.getDeptNum("管理部");
                return temp.contains(deptId);
            } else if (115 == comId && "payment-renzhengfenbao".equals(processKey)) {
                // 杭州安联
                List<Long> temp = bizBusinessTestMapper.getDeptNum("财务部");
                List<Long> temp1 = bizBusinessTestMapper.getDeptNum("客户服务部");
                temp.addAll(temp1);
                return temp.contains(deptId);
            } else if (161 == comId) {
                // 上海量远
                List<Long> temp = bizBusinessTestMapper.getDeptNum("市场部");
                List<Long> temp1 = bizBusinessTestMapper.getDeptNum("综合部");
                temp.addAll(temp1);
                return temp.contains(deptId);
            } else if (172 == comId) {
                // 宁波安维
                List<Long> temp = bizBusinessTestMapper.getDeptNum("综合部");
                return temp.contains(deptId);
            } else if (118 == comId && ("payment-qudaofuwu".equals(processKey)
                    || "payment-yewuxuanchuan".equals(processKey)
                    || "payment-renzhengfenbao".equals(processKey))) {
                // 嘉兴安联
                List<Long> temp = bizBusinessTestMapper.getDeptNum("市场部");
                return temp.contains(deptId);
            } else if (356 == comId) {
                // 杭州卫康
                if ("payment-yewuxuanchuan".equals(processKey) && 802 == userId) {
                    // 李亚飞 开通业务宣传费申请权限
                    return true;
                }
                List<Long> temp = bizBusinessTestMapper.getDeptNum("管理部");
                return temp.contains(deptId);
            } else {
                // 精确到部门
                List<Long> temp = bizBusinessTestMapper.getDeptNum("客户服务部");
                List<Long> temp1 = bizBusinessTestMapper.getDeptNum("客服部");
                temp.addAll(temp1);
                return temp.contains(deptId);
            }
        }
        ans.clear();
        ans.add("payment-officedecorate");
        ans.add("payment-caigou");
        ans.add("payment-jeiriwailian");
        ans.add("payment-rcbg");
        ans.add("payment-nenghao");
        ans.add("payment-zhinajin");
        ans.add("payment-kaiban");
        if (ans.contains(processKey)) {
            if (183 == comId) {
                // 亿联检测
                List<Long> temp = bizBusinessTestMapper.getDeptNum("行政部");
                List<Long> temp1 = bizBusinessTestMapper.getDeptNum("财务部");
                List<Long> temp2 = bizBusinessTestMapper.getDeptNum("总经办");
                temp.addAll(temp1);
                temp.addAll(temp2);
                return temp.contains(deptId);
            } else if (350 == comId) {
                // 亿达检测
                List<Long> temp = bizBusinessTestMapper.getDeptNum("行政部");
                List<Long> temp1 = bizBusinessTestMapper.getDeptNum("财务部");
                temp.addAll(temp1);
                return temp.contains(deptId);
            } else if (281 == comId) {
                // 上海研晰
                List<Long> temp = bizBusinessTestMapper.getDeptNum("行政人事部");
                List<Long> temp1 = bizBusinessTestMapper.getDeptNum("管理部");
                temp.addAll(temp1);
                return temp.contains(deptId);
            } else if (340 == comId) {
                // 浙江为华
                List<Long> temp = bizBusinessTestMapper.getDeptNum("财务部");
                return temp.contains(deptId);
            } else if (115 == comId && "payment-rcbg".equals(processKey)) {
                // 杭州安联
                List<Long> temp = bizBusinessTestMapper.getDeptNum("综合部");
                List<Long> temp1 = bizBusinessTestMapper.getDeptNum("管理部");
                List<Long> temp2 = bizBusinessTestMapper.getDeptNum("质控部");
                temp.addAll(temp1);
                temp.addAll(temp2);
                return temp.contains(deptId);
            } else if (356 == comId && "payment-rcbg".equals(processKey)) {
                // 杭州卫康 李亚飞 开通日常办公费用申请权限
                List<Long> temp = bizBusinessTestMapper.getDeptNum("综合部");
                List<Long> temp1 = bizBusinessTestMapper.getDeptNum("管理部");
                List<Long> temp2 = bizBusinessTestMapper.getDeptNum("综合管理部");
                temp.addAll(temp1);
                temp.addAll(temp2);
                return temp.contains(deptId) || 802 == userId;
            } else {
                // 精确到部门
                List<Long> temp = bizBusinessTestMapper.getDeptNum("综合部");
                List<Long> temp1 = bizBusinessTestMapper.getDeptNum("管理部");
                List<Long> temp2 = bizBusinessTestMapper.getDeptNum("综合管理部");
                temp.addAll(temp1);
                temp.addAll(temp2);
                return temp.contains(deptId);
            }
        }
        ans.clear();
        ans.add("payment-yusuan");
        ans.add("payment-maincompany");
        if (ans.contains(processKey)) {
            // 精确到财务BP
            Integer roleNum = bizBusinessTestMapper.getRoleNum(142, userId);
            return roleNum >= 1;
        }
        ans.clear();
        ans.add("payment-fznbzzdb");
        if (ans.contains(processKey)) {
            // 精确到财务BP  孙春花也可申请
            Integer roleNum = bizBusinessTestMapper.getRoleNum(142, userId);
            return roleNum >= 1 || 6 == userId;
        }
        ans.clear();
        ans.add("payment-tzsx");
        if (ans.contains(processKey)) {
            // 精确到公司负责人
            Integer roleNum = bizBusinessTestMapper.getRoleNum(151, userId);
            return roleNum >= 1;
        }
        ans.clear();
        ans.add("payment-jttxnzjdb");
        if (ans.contains(processKey)) {
            // 精确到财务总监改为财务经理
            Integer roleNum = bizBusinessTestMapper.getRoleNum(161, userId);
            return roleNum >= 1;
        }
        ans.clear();
        ans.add("payment-huiwu");
        if (ans.contains(processKey)) {
            if (356 == comId) {
                // 杭州卫康 不做限制
                return true;
            } else if (161 == comId) {
                // 上海量远
                List<Long> temp = bizBusinessTestMapper.getDeptNum("综合部");
                return temp.contains(deptId);
            } else if (350 == comId) {
                // 亿达检测
                List<Long> temp = bizBusinessTestMapper.getDeptNum("放射卫生评价部");
                // 直接上级
                Integer roleNum = bizBusinessTestMapper.getRoleNum(145, userId);
                return temp.contains(deptId) || roleNum >= 1;
            } else {
                // 精确到直接上级
                Integer roleNum = bizBusinessTestMapper.getRoleNum(145, userId);
                return roleNum >= 1;
            }
        }
        return true;
    }

    /**
     * 财务总监沈群芳个人费用需要总裁审批
     *
     * @param userId     用户ID
     * @param processKey 流程key
     * @return 集合
     */
    private Map<String, Object> getCfoVaviables(Long userId, String processKey) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("isCFO", 0);
        if (681 == userId) {
            List<String> keys = new ArrayList<>();
            keys.add("payment-xinchoufuli");
            keys.add("payment-pay");
            keys.add("payment-renshiotherfee");
            keys.add("payment-yewuzhaodai");
            if (keys.contains(processKey)) {
                map.put("isCFO", 1);
            }
        }
        return map;
    }

    /**
     * 为量远4个部门添加审批节点
     *
     * @param deptId     部门ID
     * @param processKey 审批key
     * @return map集合
     */
    private Map<String, Object> getLyVaviables(Long deptId, String processKey) {
        Map<String, Object> map = Maps.newHashMap();
        List<String> keys = new ArrayList<>();
        keys.add("payment-peikuanbuchang");
        keys.add("payment-baofei");
        keys.add("payment-deptbeiyong");
        keys.add("payment-yewuzhaodai");
        keys.add("payment-cheliang");
        keys.add("payment-ggxcf");
        keys.add("payment-pay");
        keys.add("payment-syhc");
        keys.add("payment-zhongjiejigou");
        keys.add("payment-xinchoufuli");
        keys.add("payment-renshiotherfee");
        keys.add("payment-zhaobiao");
        keys.add("payment-baozhengjin");

        List<Long> deptIds = new ArrayList<>();
        // 上海量远四大金刚
        deptIds.add(165L);
        deptIds.add(167L);
        deptIds.add(288L);
        deptIds.add(563L);
        if (keys.contains(processKey)) {
            if (deptIds.contains(deptId)) {
                map.put("isLy", 1);
            } else {
                map.put("isLy", 0);
            }
        }
        return map;
    }

    /**
     * //     * 启动流程
     * //     *
     * //     * @param param
     * //
     */

    @Autowired
    private RuntimeService runtimeService;

    /**
     * 找到今年模板或者明细扣减一下
     *
     * @param projectMoney
     * @param payment
     */
    private int extracted(Double projectMoney, HashMap payment) {
        // 当前费用归属部门id
//        Integer deptId = department_id.get(department_id.size() - 1);
        Object projectMoney1 = payment.get("projectMoney");
        BigDecimal money = new BigDecimal(projectMoney1 + "");
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
                    List<Integer> array = (List<Integer>) hashMaps1.get("value");
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
                if (remaining_quota.compareTo(money) < 0) {
                    // 剩余金额不足  无法发起申请
                    return -1;
                }
                month.put("remaining_quota", remaining_quota.subtract(bigDecimalMoney) + "");
                month.put("amount_occupied", new BigDecimal((String) month.get("amount_occupied")).add(bigDecimalMoney) + "");
                // 更新
                Update update = new Update();
                update.set("month" + monthValue, month);
                Query query1 = new Query();
                String id = map.get("_id").toString();
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
                    List<Integer> array = (List<Integer>) hashMap.get("value");
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

                // 是哪一个月分的  金额都是字符串然后变成bigDecimal 去减
                HashMap month = (HashMap) map.get("month" + monthValue);
                //         "amount_occupied": 占用的金额 ,
                //        "remaining_quota": 剩余金额 ,
                //        "confirmed_quota": 确认的金额,
                //        "budget_quota": 预算金额
                BigDecimal remaining_quota = new BigDecimal((String) month.get("remaining_quota"));
                if (remaining_quota.compareTo(money) < 0) {
                    // 剩余金额不足  无法发起申请
                    return -1;
                }
                BigDecimal bigDecimal = new BigDecimal(detailMoney);
                month.put("remaining_quota", remaining_quota.subtract(bigDecimal) + "");
                month.put("amount_occupied", new BigDecimal((String) month.get("amount_occupied")).add(bigDecimal) + "");
                Update update = new Update();
                update.set("month" + monthValue, month);
                Query query1 = new Query();
                query1.addCriteria(Criteria.where("_id").is(map.get("_id")));
                mongoTemplate.upsert(query1, update, "budget_statistics");
            }

        }
        if (!detailIds.isEmpty()) {
            payment.put("detail_budget_id", detailIds);
        }
        HashMap map = new HashMap();
        map.put("user", SystemUtil.getUserNameCn());
        map.put("time", LocalDateTime.now());
        map.put("money", projectMoney);
        map.put("payment", payment.get("applyCode"));
        mongoTemplate.insert(map, "budget_log");
        return 1;
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

    // 多个明细可能取值不一样
    private void extracted(HashMap<String, Object> param) {
        List<String> control_id = (List<String>) param.get("control_id");
        List<List<HashMap>> detail = (List<List<HashMap>>) param.get("detail");
        List<String> ans = new ArrayList<>();
//        throwException(control_id,"模拟control_id数组为空");
        if (control_id == null) {
            for (int i = 0; i < detail.size(); i++) {
                ans.add("费用明细");
            }
        } else {
            for (List<HashMap> hashMaps : detail) {
                StringBuilder sb = new StringBuilder();
                for (HashMap hashMap : hashMaps) {
                    if (control_id.contains(hashMap.get("id").toString())) {
                        sb.append(hashMap.get("value"));
                    }
                }
                ans.add(sb.toString());
            }
        }


        param.put("control_name", ans);
    }

    private String do_insertKey(String key, String ccs, String money, HashMap<String, Object> param) {
        BigDecimal project_money = new BigDecimal(money);
        switch (key) {
            case "payment-renzhengfenbao":
            case "payment-qudaofuwu":
            case "payment-yusuan":
            case "payment-baofei":
            case "payment-tzsx":
            case "payment-kaiban":
            case "payment-nenghao":
            case "payment-pay":
            case "payment-yinghangshouxufei":
            case "payment-juanguan":
                break;
            case "payment-officedecorate":
                if (project_money.compareTo(BigDecimal.valueOf(10000L)) > 0) {
                    List<Long> list4 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "董事长");
                    ccs = insertCC(ccs, list4);
                }
                break;
            case "payment-jttxnzjdb":
                if (project_money.compareTo(BigDecimal.valueOf(1000000L)) < 0) {
                    List<Long> list4 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "董事长");
                    ccs = insertCC(ccs, list4);
                }
                break;
            case "payment-suifei":
                if (project_money.compareTo(BigDecimal.valueOf(50000L)) < 0) {
                    List<Long> list4 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "副总裁");
                    List<Long> list5 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "财务总监");
                    String s = insertCC(ccs, list4);
                    ccs = insertCC(s, list5);
                }
                if (project_money.compareTo(BigDecimal.valueOf(50000L)) > 0 &&
                        project_money.compareTo(BigDecimal.valueOf(500000L)) < 0) {
                    List<Long> list4 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "董事长");
                    ccs = insertCC(ccs, list4);
                }
                break;
            case "payment-fukuaidaishoudaifu":
                List<Long> list7 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "副总裁");
                List<Long> list6 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "分公司总经理(审批)");
                String s2 = insertCC(ccs, list6);
                ccs = insertCC(s2, list7);
                break;
            case "payment-renshiotherfee":
            case "payment-ggxcf":
            case "payment-rcbg":
            case "payment-syhc":
            case "payment-yewuzhaodai":
                if (project_money.compareTo(BigDecimal.valueOf(5000L)) > 0) {
                    List<Long> list4 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "董事长");
                    ccs = insertCC(ccs, list4);
                }
                break;
            case "payment-jeiriwailian":
                List<Long> list16 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "总裁");
                List<Long> list17 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "财务总监");
                List<Long> list18 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "总裁");
                String s = insertCC(ccs, list16);
                String s1 = insertCC(s, list17);
                ccs = insertCC(s1, list18);
                break;
            case "payment-cheliang":
                if (project_money.compareTo(BigDecimal.valueOf(1000L)) < 0) {
                    List<Long> list4 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "总裁办");
                    ccs = insertCC(ccs, list4);
                }
                break;
            case "payment-zhinajin":
                if (project_money.compareTo(BigDecimal.valueOf(10000L)) < 0) {
                    List<Long> list11 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "副总裁");
                    List<Long> list12 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "分公司总经理(审批)");
                    List<Long> list13 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "董事长");
                    List<Long> list14 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "财务总监");
                    String s3 = insertCC(ccs, list11);
                    String s12 = insertCC(s3, list12);
                    String s19 = insertCC(s12, list13);
                    ccs = insertCC(s19, list14);
                } else {
                    if (project_money.compareTo(BigDecimal.valueOf(10000L)) > 0) {
                        List<Long> list13 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "董事长");
                        ccs = insertCC(ccs, list13);
                    }
                }
                break;
            case "payment-hetongfei":
                if (project_money.compareTo(BigDecimal.valueOf(100000L)) < 0) {
                    List<Long> list11 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "副总裁");
                    List<Long> list12 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "分公司总经理(审批)");
                    List<Long> list13 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "董事长");
                    String s3 = insertCC(ccs, list11);
                    String s111 = insertCC(s3, list12);
                    ccs = insertCC(s111, list13);
                }
                if (project_money.compareTo(BigDecimal.valueOf(100000L)) > 0 &&
                        project_money.compareTo(BigDecimal.valueOf(1000000L)) < 0) {
                    List<Long> list11 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "副总裁");
                    List<Long> list12 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "总监");
                    List<Long> list13 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "董事长");
                    String s3 = insertCC(ccs, list11);
                    String s112 = insertCC(s3, list12);
                    ccs = insertCC(s112, list13);
                }
                if (project_money.compareTo(BigDecimal.valueOf(1000000L)) > 0) {
                    List<Long> list13 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "董事长");
                    ccs = insertCC(ccs, list13);
                }
                break;
            case "payment-zhaobiao":
                List<Long> list11 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "副总裁");
                List<Long> list12 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "分公司总经理(审批)");
                String s3 = insertCC(ccs, list11);
                ccs = insertCC(s3, list12);
                break;
            case "payment-xinchoufuli":
            case "payment-maincompany":
                List<Long> list9 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "董事长");
                ccs = insertCC(ccs, list9);
                break;
            case "payment-zanjiekuan":
                if (project_money.compareTo(BigDecimal.valueOf(20000L)) < 0) {
                    List<Long> list4 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "总裁");
                    List<Long> list5 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "财务总监");
                    String s113 = insertCC(ccs, list4);
                    ccs = insertCC(s113, list5);
                } else {
                    List<Long> list569 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "董事长");
                    ccs = insertCC(ccs, list569);
                }
                break;
            case "payment-peikuanbuchang":
                if (project_money.compareTo(BigDecimal.valueOf(2000L)) < 0) {
                    List<Long> list4 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "董事长");
                    List<Long> list5 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "财务总监");
                    String s113 = insertCC(ccs, list4);
                    ccs = insertCC(s113, list5);
                }
                break;
            case "payment-deptbeiyong":
                if (project_money.compareTo(BigDecimal.valueOf(50000L)) > 0) {
                    List<Long> list4 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "董事长");
                    ccs = insertCC(ccs, list4);
                } else {
                    List<Long> list4 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "财务总监");
                    ccs = insertCC(ccs, list4);
                }
                break;
            case "payment-huiwu":
                if (project_money.compareTo(BigDecimal.valueOf(3000L)) > 0 &&
                        project_money.compareTo(BigDecimal.valueOf(10000L)) < 0) {
                    List<Long> list = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "董事长");
                    ccs = insertCC(ccs, list);
                }
                break;
            case "payment-fznbzzdb":
                if (project_money.compareTo(BigDecimal.valueOf(50000L)) < 0) {
                    List<Long> list = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "董事长");
                    List<Long> list1 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "总裁 ");
                    String s8 = insertCC(ccs, list);
                    ccs = insertCC(s8, list1);
                }
                if (project_money.compareTo(BigDecimal.valueOf(50000L)) > 0 &&
                        project_money.compareTo(BigDecimal.valueOf(100000L)) < 0) {
                    List<Long> list = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "董事长");
                    ccs = insertCC(ccs, list);
                }
                break;
            case "payment-shourujianmian":
                if (project_money.compareTo(BigDecimal.valueOf(3000L)) > 0) {
                    List<Long> list = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "财务总监");
                    List<Long> list1 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "总裁 ");
                    String s9 = insertCC(ccs, list);
                    ccs = insertCC(s9, list1);
                }
                break;
            case "payment-zhongjiejigou":
                if (project_money.compareTo(BigDecimal.valueOf(3000L)) > 0) {
                    List<Long> list2 = businessMapper.getUserIdByName(SystemUtil.getDeptId(), "董事长");
                    ccs = insertCC(ccs, list2);
                }
                break;
        }
        return ccs;
    }

    public String insertCC(String ccs, List<Long> list) {
        StringBuilder sb = new StringBuilder(ccs);
        for (Long aLong : list) {
            String temp = String.valueOf(aLong);
            if (ccs.equals("")) {
                sb.append(temp);
            } else {
                sb.append(",");
                sb.append(temp);
            }
        }
        return sb.toString();

    }


    public void throwException(Object anything, String codeMsg) {
        if (anything == null) {
            throw new RuntimeException(codeMsg + "为空");
        }
    }

    /**
     * 获取单个
     *
     * @param id
     */
    @Override
    public HashMap<String, Object> selectById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        HashMap payment = mongoTemplate.findOne(query, HashMap.class, "payment");
        return payment;
    }

    /**
     * 查询目录
     */
    @Override
    public List<HashMap> getDirectory(String type) {
        Query query = new Query();
        query.addCriteria(Criteria.where("parentId").is("0").and("type").is(type));
        List<HashMap> directory = mongoTemplate.find(query, HashMap.class, "directory");

        List<HashMap> father = directory.stream().sorted((o1, o2) -> {
            Integer level1 = (Integer) o1.get("level");
            Integer level2 = (Integer) o2.get("level");
            if (Objects.isNull(level1) || Objects.isNull(level2)) {
                return 0;
            }
            return level1 - level2;
        }).collect(Collectors.toList());
        for (HashMap hashMap : father) {
            // 去掉_id 对结果的影响
            delete_Id(hashMap);
            String id = (String) hashMap.get("id");
            Query childQuery = new Query();
            childQuery.addCriteria(Criteria.where("parentId").is(id));
            List<HashMap> child = mongoTemplate.find(childQuery, HashMap.class, "directory");
            // 根据level 去排序
            List<HashMap> collect = child.stream().sorted((o1, o2) -> {
                Integer level1 = (Integer) o1.get("level");
                Integer level2 = (Integer) o2.get("level");
                if (Objects.isNull(level1) || Objects.isNull(level2)) {
                    return 0;
                }
                return level1 - level2;
            }).collect(Collectors.toList());
            for (HashMap map : collect) {
                delete_Id(map);
            }
            // 屏蔽合同/咨询服务申请
            List<HashMap> mapList = collect.stream().filter(
                    (m) -> !"合同/咨询服务".equals(m.get("name"))
            ).collect(Collectors.toList());
            hashMap.put("child", mapList);

        }
        return father;
    }

    /**
     * 查询所有模板目录列表
     * 史上最恶心代码
     *
     * @param type
     */
    @Override
    public List<HashMap> getDirectoryAll(String type) {
        List<HashMap> directory = getDirectory(type);
        for (HashMap hashMap : directory) {
            List<HashMap> child = (List<HashMap>) hashMap.get("child");
            if (child.size() >= 1) {
                for (HashMap map : child) {
                    String id = (String) map.get("id");
                    List<HashMap> select = (List<HashMap>) select(id, 1);
                    // 查询改目录下的模板
                    List<HashMap> child1 = new ArrayList<>();
                    for (HashMap hashMap1 : select) {
                        HashMap map1 = new HashMap();
                        map1.put("id", hashMap1.get("id"));
                        HashMap baseInformation = (HashMap) hashMap1.get("BaseInformation");
                        map1.put("name", baseInformation.get("name"));
                        map1.put("level", baseInformation.get("level"));
                        map1.put("belong", hashMap1.get("belong"));
                        child1.add(map1);
                    }
                    // 模板根据level排序
                    List<HashMap> collect = child1.stream().filter(x -> x.get("ifHide") == null || (int) x.get("ifHide") == 0).sorted(new Comparator<HashMap>() {
                        @Override
                        public int compare(HashMap o1, HashMap o2) {
                            Integer level1 = (Integer) o1.get("level");
                            Integer level2 = (Integer) o2.get("level");
                            if (level1 == null || level2 == null) {
                                return 0;
                            }
                            return level1 - level2;
                        }
                    }).collect(Collectors.toList());
                    map.put("child", collect);
                }
            }
        }
        return directory;
    }

    public void delete_Id(HashMap hashMap) {
        hashMap.put("id", hashMap.get("_id").toString());
        hashMap.remove("_id");
    }

    /**
     * 增加目录
     *
     * @param
     */
    @Override
    public void addDirectory(DirectoryDTO directoryDTO) {

        HashMap<Object, Object> map = new HashMap<>();
        map.put("name", directoryDTO.getName());
        map.put("parentId", directoryDTO.getParentId());
        map.put("type", directoryDTO.getType());
        map.put("level", directoryDTO.getLevel());
        mongoTemplate.insert(map, "directory");
    }

    /**
     * 删除目录
     *
     * @param id
     */
    @Override
    public int removeDirectory(String id) {
        // 如果目录下有子集不能删除
        Query query = new Query();
        query.addCriteria(Criteria.where("parentId").is(id));
        List<HashMap> directory = mongoTemplate.find(query, HashMap.class, "directory");
        throwException(directory, "mongodb删除时 id" + id + "查询为空");
        if (!directory.isEmpty()) {
            return 0;
        }

        // 如果有模板在使用也不能删除
        Query query1 = new Query();
        query1.addCriteria(Criteria.where("BaseInformation.directoryId").is(id).and("ifHide").ne(1));
        List<HashMap> directory1 = mongoTemplate.find(query1, HashMap.class, "template");
        if (!directory1.isEmpty()) {
            return 0;
        }

        Query removeQuery = new Query();
        removeQuery.addCriteria(Criteria.where("_id").is(id));
        mongoTemplate.findAndRemove(removeQuery, HashMap.class, "directory");
        return 1;
    }

    /**
     * 更新目录
     *
     * @param directoryDTO
     */
    @Override
    public int updateDirectory(DirectoryDTO directoryDTO) {

        // 先去里面查一下 看他们parentId 是否一样
        Query q = new Query();
        q.addCriteria(Criteria.where("_id").is(directoryDTO.getId()));
        HashMap directory = mongoTemplate.findOne(q, HashMap.class, "directory");
        String old = (String) directory.get("parentId");
        if (!old.equals(directoryDTO.getParentId())) {
            Query q1 = new Query();
            q1.addCriteria(Criteria.where("parentId").is(directoryDTO.getId()));
            List<HashMap> directory1 = mongoTemplate.find(q1, HashMap.class, "directory");
            if (!directory1.isEmpty()) {
                return 0;
            }
        }
        Update update = new Update();
        update.set("name", directoryDTO.getName());
        update.set("parentId", directoryDTO.getParentId());
        update.set("level", directoryDTO.getLevel());
        Query query = new Query().addCriteria(Criteria.where("_id").is(directoryDTO.getId()));
        mongoTemplate.updateFirst(query, update, "directory");
        return 1;
    }

    /**
     * 查询单个目录
     *
     * @param id
     */
    @Override
    public HashMap selectOneDirectory(String id) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(id));
        HashMap directory = mongoTemplate.findOne(query, HashMap.class, "directory");
        directory.put("id", directory.get("_id").toString());
        directory.remove("_id");
        return directory;
    }

    /**
     * 查询配置
     */
    @Override
    public HashMap getConfig() {
        Query query = new Query();
        query.addCriteria(Criteria.where("BaseInformation").exists(true));
        HashMap configuration = mongoTemplate.findOne(query, HashMap.class, "configuration");
        delete_Id(configuration);
        HashMap ans = new HashMap();
        Query another = new Query();
        List<String> param = new ArrayList<>();
        param.add("金额");
        param.add("付款事由");
        param.add("费用归属部门");
        another.addCriteria(Criteria.where("BaseInformation").exists(false).and("label").nin(param));
        List<HashMap> configuration1 = mongoTemplate.find(another, HashMap.class, "configuration");
        for (HashMap hashMap : configuration1) {
            delete_Id(hashMap);
        }
        ans.put("config", configuration1);
        ans.put("information", configuration);
        return ans;
    }

    /**
     * 激活/禁用模板
     *
     * @param templateId
     */
    @Override
    public int activation(String templateId) {

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(templateId));
        HashMap template = mongoTemplate.findOne(query, HashMap.class, "template");
        HashMap baseInformation = (HashMap) template.get("BaseInformation");
        Integer live = Integer.parseInt(baseInformation.get("live").toString());

        Update update = new Update();
        update.set("BaseInformation.live", live == 1 ? 0 : 1);
        mongoTemplate.upsert(query, update, "template");
        return live;
    }

    /**
     * 获取付款申请列表
     *
     * @param bizBusiness
     */
    @Override
    public List<BizBusiness> getPaymentApplyList(BizBusinessVO bizBusiness) {
        //  缺失参数
        if (StringUtils.isBlank(bizBusiness.getProcDefKey())) {
            return null;
        }

        Example example = new Example(BizBusiness.class);
        Example.Criteria criteria = example.createCriteria();
//        System.out.println(SystemUtil.getUserId());
        criteria.andEqualTo("userId", SystemUtil.getUserId());
        criteria.andLike("procDefKey", "%" + bizBusiness.getProcDefKey() + "%");
        criteria.andEqualTo("delFlag", 0);
        if (StringUtils.isNotBlank(bizBusiness.getApplyCode())) {
            criteria.andEqualTo("applyCode", bizBusiness.getApplyCode());
        }
        if (bizBusiness.getStartTime() != null && bizBusiness.getEndTime() != null) {
            criteria.andBetween("applyTime", bizBusiness.getStartTime(), bizBusiness.getEndTime());
        }
        if (StringUtils.isNotBlank(bizBusiness.getApplyer())) {
            criteria.andLike("applyer", "%" + bizBusiness.getApplyer() + "%");
        }
        if (bizBusiness.getState() != null) {
            criteria.andEqualTo("status", +bizBusiness.getState());
        }
        if (bizBusiness.getResult() != null) {
            criteria.andEqualTo("result", bizBusiness.getResult());
        }
        if (StringUtils.isNotBlank(bizBusiness.getDeptId())) {
            criteria.andLike("deptId", "%" + bizBusiness.getDeptId() + "%");
        }
        if (StringUtils.isNotBlank(bizBusiness.getTitle())) {
            criteria.andLike("title", "%" + bizBusiness.getTitle() + "%");
        }
        if (StringUtils.isNotBlank(bizBusiness.getCompanyId())) {
            criteria.andLike("companyId", "%" + bizBusiness.getCompanyId() + "%");
        }
        example.setOrderByClause("apply_time DESC");
        List<BizBusiness> bizBusinesses = businessMapper.selectByExample(example);
        for (BizBusiness business : bizBusinesses) {
            business.setCompanyAndDept(business.getCompanyName() + "/" + business.getDeptName());
            String tableId = business.getTableId();
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(tableId));
            HashMap payment = mongoTemplate.findOne(query, HashMap.class, "payment");
            if (payment != null) {
                business.setTotalMoney(payment.get("projectMoney").toString());
                HashMap baseInformation = (HashMap) payment.get("BaseInformation");
                String type = (String) baseInformation.get("type");
                type = type == null ? "" : type;
                business.setType(type);
            }
            if (payment == null) {
                System.out.println(" tableId ------" + business.getTableId());
            }

        }

        return bizBusinesses;
    }

    /**
     * 更具目录id 查询模板
     *
     * @param id
     */
    @Override
    public List<HashMap> getTemplateById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("BaseInformation.directoryId").is(id));
        List<HashMap> template = mongoTemplate.find(query, HashMap.class, "template");
        throwException(template, "mongodb中该Id：" + id + "模板");
        List<HashMap> one = getOne(template);
        for (HashMap hashMap : one) {
            delete_Id(hashMap);
            List<HashMap<String, Object>> data = (List) hashMap.get("data");

            // 获取付款明细
            Query query1 = new Query();
            query1.addCriteria(Criteria.where("_id").is("63560277662d00005c0019f2"));
            HashMap<String, Object> configuration = mongoTemplate.findOne(query1, HashMap.class, "configuration");
            throwException(configuration, "金额");
            delete_Id(configuration);
            data.add(data.size() - 1, configuration);

            // 付款事由
            Query query2 = new Query();
            query2.addCriteria(Criteria.where("_id").is("6350b5397b320000a5000913"));
            HashMap<String, Object> configuration1 = mongoTemplate.findOne(query2, HashMap.class, "configuration");
            throwException(configuration1, "付款事由");
            delete_Id(configuration1);
            data.add(data.size() - 1, configuration1);


            List list = new ArrayList();
            list.add(data);
            hashMap.put("detail", list);
            hashMap.put("projectMoney", 0);
            hashMap.put("applyCode", null);
            hashMap.put("userCompany", SystemUtil.getCompanyName());

            hashMap.remove("data");
        }
        List<HashMap> collect = getHashMaps(one);

        return collect;
    }

    private List<HashMap> getHashMaps(List<HashMap> one) {
        List<HashMap> collect = one.stream().filter(x -> x.get("ifHide") == null || (int) x.get("ifHide") == 0).sorted(new Comparator<HashMap>() {
            @Override
            public int compare(HashMap o1, HashMap o2) {
                HashMap baseInformation1 = (HashMap) o1.get("BaseInformation");
                HashMap baseInformation2 = (HashMap) o2.get("BaseInformation");
                Integer level1 = (Integer) baseInformation1.get("level");
                Integer level2 = (Integer) baseInformation2.get("level");
                if (Objects.isNull(level1) || Objects.isNull(level2)) {
                    return 0;
                }
                return level1 - level2;
            }
        }).collect(Collectors.toList());
        return collect;
    }

    /**
     * 获取详情
     *
     * @param id
     */
    @Override
    public HashMap getApplyDetail(String id, BizBusiness bizBusiness) {

        // 根据id获取详情
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        HashMap payment = mongoTemplate.findOne(query, HashMap.class, "payment");
        throwException(payment, "mongodb中该id:" + id + "新付款详情");
        HashMap information = (HashMap) payment.get("BaseInformation");
        delete_Id(payment);
        payment.put("result", bizBusiness.getResult());
        payment.put("state", bizBusiness.getStatus());

        // 凭证图片
        List<SysAttachment> imgs = remoteFileService.getListMongo(payment.get("id").toString(), "payment-certificate");
        imgs = imgs.stream().sorted((e1, e2) -> e2.getCreatedTime().compareTo(e1.getCreatedTime())).collect(Collectors.toList());
        // 付款附件
        List<SysAttachment> append = remoteFileService.getListMongo(payment.get("id").toString(), "payment-appendix");
        append = append.stream().sorted((e1, e2) -> e2.getCreatedTime().compareTo(e1.getCreatedTime())).collect(Collectors.toList());

        payment.put("procInstId", bizBusiness.getProcInstId());
        payment.put("certificate", imgs);
        payment.put("appendix", append);
        payment.put("userCompany", SystemUtil.getCompanyName());
        payment.put("currentUser", SystemUtil.getUserNameCn());

        Query printQuery = new Query();
        printQuery.addCriteria(Criteria.where("name").is((String) information.get("name")));
        HashMap print = mongoTemplate.findOne(printQuery, HashMap.class, "print");
        if (print == null) {
            payment.put("printNumber", 1);
        } else {
            payment.put("printNumber", print.get("number"));
        }


//
//        // bill 图片和附件
//        List<SysAttachment> billImg = remoteFileService.getListMongo(payment.get("id").toString(), "bill-img");
//        List<SysAttachment> bullAppend = remoteFileService.getListMongo(payment.get("id").toString(), "bill-appendix");

//        HashMap<Long,SysAttachment> billImgMap = new HashMap<>();
//        for (SysAttachment sysAttachment : billImg) {
//            billImgMap.put(sysAttachment.getId(),sysAttachment);
//        }
//        HashMap<Long,SysAttachment> billAppendMap = new HashMap<>();
//        for (SysAttachment sysAttachment : bullAppend) {
//            billAppendMap.put(sysAttachment.getId(),sysAttachment);
//        }


        HashMap baseInformation = (HashMap) payment.get("BaseInformation");
        throwException(baseInformation, "基础信息");
        List<HashMap> approvalData = (List<HashMap>) baseInformation.get("approvalData");
        List<HashMap> purchaseData = (List<HashMap>) baseInformation.get("purchaseData");
        if (!approvalData.isEmpty()) {
            for (HashMap approvalDatum : approvalData) {
                String ids = (String) approvalDatum.get("procInstId");
                HashMap hashMap = bizBusinessTestMapper.approveResult(Long.parseLong(ids.toString()));
                if (hashMap != null) {
                    approvalDatum.put("result", hashMap.get("result"));
                }
            }
        }
        if (!purchaseData.isEmpty()) {
            for (HashMap purchaseDatum : purchaseData) {
                Integer ids = (Integer) purchaseDatum.get("purchaseId");

                HashMap purchase = bizBusinessTestMapper.paymentResult(ids.toString(), "purchase");
                if (purchase != null) {
                    purchaseDatum.put("result", purchase.get("result"));
                }
            }
        }

        List<HashMap> bill_information = (List<HashMap>) baseInformation.get("bill_information");
        throwException(bill_information, "发票信息");

//        for (HashMap hashMap : bill_information) {
//            Long bill_attach_id= Long.parseLong(hashMap.get("bill_attach_id").toString());;
//            Long bill_img_id= Long.parseLong(hashMap.get("bill_img_id").toString());
//            if (bill_attach_id == null && bill_img_id ==null) {
//                continue;
//            }
//           if (bill_attach_id == null ) {
//               hashMap.put("bill_img_id",billImgMap.get(bill_img_id));
//           }
//
//           if (bill_img_id == null) {
//               hashMap.put("bill_attach_id",billImgMap.get(bill_attach_id));
//           }
//
//        }

        if (payment.get("control_name") == null) {
            List<List<HashMap>> detail = (List<List<HashMap>>) payment.get("detail");
            List<String> ans = new ArrayList<>();

            for (int i = 0; i < detail.size(); i++) {
                ans.add("费用明细");
            }
            payment.put("control_name", ans);

        }

        // 添加宁波银行付款单信息
        QueryWrapper<NbcbAccountInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("apply_code", bizBusiness.getApplyCode());
        queryWrapper.eq("status", 1);
        List<NbcbAccountInfo> list = nbcbAccountInfoMapper.selectList(queryWrapper);
        boolean flag = false;
        payment.put("payBillNo", "");
        payment.put("paymentBillNo", "");
        payment.put("addPaymentOrder", false);
        String procInstId = bizBusiness.getProcInstId();
        if (StrUtil.isNotBlank(procInstId)) {
            List<ActRuTask> actRuTasks = actRuTaskMapper.selectListByProcInstId(procInstId);
            if (CollUtil.isNotEmpty(actRuTasks)) {
                ActRuTask actRuTask = actRuTasks.get(0);
                Integer roleNum = bizBusinessTestMapper.getRoleNum(173, SystemUtil.getUserId());
                if (actRuTask != null && ("付款确认人1".equals(actRuTask.getName()) || "付款确认2".equals(actRuTask.getName()) || "付款确认人2".equals(actRuTask.getName())) && roleNum >= 1) {
                    log.error(actRuTask.getName());
                    flag = true;
                }
            }
        }
        if (CollUtil.isEmpty(list) && flag) {
            payment.put("addPaymentOrder", true);
        }
        if (CollUtil.isNotEmpty(list)) {
            String payBillNo = list.get(0).getPayBillNo();
            payment.put("payBillNo", payBillNo);
            if (flag) {
                payment.put("paymentBillNo", payBillNo);
            }
        }
        payment.put("businessKey", bizBusiness.getId());

        return payment;
    }

    /**
     * 判断使用有重复
     *
     * @param bullCode
     * @param billNumber
     */
    @Override
    public boolean ifHasDuplicateBill(String bullCode, String billNumber) {
        Query query = new Query();
        query.addCriteria(Criteria.where("BaseInformation.bill_information.bill_code").in(bullCode)
                .and("BaseInformation.bill_information.bill_number").in(billNumber)
                .and("result").nin(3, 4));
        List<HashMap> payment = mongoTemplate.find(query, HashMap.class, "payment");
        if (payment.size() > 0) {
            return false;
        }

        return true;
    }

    /**
     * 数据导出
     *
     * @param all
     * @param response
     */
    @Override
    public void download(List<BizBusiness> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (BizBusiness hashMap : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("审批编号", hashMap.getApplyCode());
            map.put("标题", hashMap.getTitle());
            map.put("款项类型", hashMap.getProcName());
            map.put("总金额", hashMap.getTotalMoney());
            map.put("审批结果", getResult(hashMap.getResult()));
            map.put("审批状态", getStatus(hashMap.getStatus()));
            map.put("申请人", hashMap.getApplyer());
            map.put("隶属部门", hashMap.getCompanyName() + "/" + hashMap.getDeptName());
            map.put("申请时间", hashMap.getApplyCode());
            list.add(map);

        }

        FileUtil.downloadExcel(list, response, 1);
    }

    /**
     * 获取全部biz_business 按照权限
     *
     * @param applyCode
     * @param type
     * @param applyer
     */
    @Override
    public List<BizBusiness> getAll(String applyCode, String type, String applyer,
                                    Integer companyId, Integer deptId, Boolean hasPurchase) {
        QueryWrapper<BizBusiness> businessQueryWrapper = new QueryWrapper<>();
        businessQueryWrapper.like(StringUtils.isNotBlank(applyCode), "apply_code", applyCode);
        businessQueryWrapper.like(StringUtils.isNotBlank(type), "proc_def_key", type);
        businessQueryWrapper.like(StringUtils.isNotBlank(applyer), "applyer", applyer);
        businessQueryWrapper.eq(companyId != null, "company_id", companyId);
        businessQueryWrapper.eq(deptId != null, "dept_id", deptId);
        businessQueryWrapper.eq("del_flag", 0);
//        businessQueryWrapper.eq("finance_status",0);
//        businessQueryWrapper.apply("dept_id is not null");

        if (hasPurchase != null) {
            ArrayList<Integer> a = new ArrayList() {{
                add(4);
                add(3);
                add(6);
            }};
            if (hasPurchase) {
                businessQueryWrapper.eq("proc_def_key", "purchase");
                businessQueryWrapper.notIn("result", a);
            } else {
                businessQueryWrapper.ne("proc_def_key", "purchase");
                businessQueryWrapper.notIn("result", a);
            }

        }


        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
        List<SysRole> roles = sysUser.getRoles();
        boolean flag = false;
        for (SysRole role : roles) {
            String roleName = role.getRoleName();
            if (roleName.equals("关联审批查看全部")) {
                flag = true;
            }
        }
        if (!flag) {
            return null;
        }


        List<BizBusiness> bizBusinesses = bizBusinessToMapper.selectList(businessQueryWrapper);
        return bizBusinesses;
    }

    /**
     * 变成草稿
     *
     * @param param
     */
    @Override
    public int toDraft(HashMap<String, Object> param) {
        mongoTemplate.insert(param, "draft");
        return 0;
    }

    /**
     * 四级联动 包含明细
     *
     * @param flag@return
     */
    @Override
    public List<HashMap> paymentInfo(Boolean flag, String id) {
        List<HashMap> payment = getDirectoryAll("payment");
        // 获取到模板层后慢慢过滤
        Query query2 = new Query();
        query2.addCriteria(Criteria.where("_id").is(id));
        HashMap one = mongoTemplate.findOne(query2, HashMap.class, "budget_template");
        String start_year = String.valueOf(one.get("start_year"));
        // 去查一下是否有其他的同年的且和我id不同的计划且未被删除的
        Query query4 = new Query();
        query4.addCriteria(Criteria.where("start_year").is(start_year)
                .and("disable").is(false));
        List<HashMap> hashMaps = mongoTemplate.find(query4, HashMap.class, "budget_template");
        if (!flag) {

            for (HashMap hashMap : payment) {
                // 第一层目录
                List<HashMap> child = (List<HashMap>) hashMap.get("child");
                for (HashMap map : child) {
                    // 第二层目录
                    List<HashMap> child1 = (List<HashMap>) map.get("child");
                    //模板 如果不存在year 或者year数组保函该预算年度就返回
                    // todo set 和list 的区别
                    List<HashMap> collect = child1.stream().filter(e -> {
                        List<String> belong = (List<String>) e.get("belong");

                        if (belong == null || belong.size() == 0) {
                            return true;
                        }

                        if (hashMaps.size() <= 1) {
                            return true;
                        }

                        if (belong.contains(id)) {
                            return true;
                        }
                        return false;


                    }).collect(Collectors.toList());
                    map.put("child", collect);
                }
            }


            return payment;
        }
        for (HashMap hashMap : payment) {
            // 第一层目录
            List<HashMap> child = (List<HashMap>) hashMap.get("child");
            for (HashMap map : child) {
                // 第二层目录
                List<HashMap> child1 = (List<HashMap>) map.get("child");
                //模板
                for (HashMap template : child1) {
                    List<HashMap> result = new ArrayList<>();
                    Query query = new Query();
                    query.addCriteria(Criteria.where("_id").is(template.get("id")));
                    String templateId = (String) template.get("id");
                    HashMap templateRes = mongoTemplate.findOne(query, HashMap.class, "template");
                    List<HashMap> data = (List<HashMap>) templateRes.get("data");
                    // 明细
                    for (HashMap datum : data) {
                        if (datum.get("id").toString().equals("634e8588f92e00007b003283")) {
                            String context = (String) datum.get("context");
                            // 拿到明细的字段
                            if (context.contains(",")) {
                                String[] split = context.split(",");
                                for (String s : split) {
                                    HashMap<Object, Object> map1 = generatorForDetail(s, templateId);
                                    // 如果不存在year或者包涵今年的year 都行
                                    List<String> belong = (List<String>) map1.get("belong");

                                    if (belong == null || belong.size() == 0 || hashMaps.size() <= 1 || belong.contains(id)) {
                                        result.add(map1);
                                    }

                                }

                            } else {
                                HashMap<Object, Object> map1 = generatorForDetail(context, templateId);
                                List<String> belong = (List<String>) map1.get("belong");

                                if (belong == null || belong.size() == 0 || hashMaps.size() <= 1 || belong.contains(id)) {
                                    result.add(map1);
                                }
                            }
                        }
                    }
                    template.put("child", result);

                }
            }
        }
        return payment;
    }


    public List<HashMap> paymentInfo1(Boolean flag) {
        List<HashMap> payment = getDirectoryAll("payment");

        if (!flag) {
            return payment;
        }
        for (HashMap hashMap : payment) {
            // 第一层目录
            List<HashMap> child = (List<HashMap>) hashMap.get("child");
            for (HashMap map : child) {
                // 第二层目录
                List<HashMap> child1 = (List<HashMap>) map.get("child");
                //模板
                for (HashMap template : child1) {
                    List<HashMap> result = new ArrayList<>();
                    Query query = new Query();
                    query.addCriteria(Criteria.where("_id").is(template.get("id")));
                    String id = (String) template.get("id");
                    HashMap templateRes = mongoTemplate.findOne(query, HashMap.class, "template");
                    List<HashMap> data = (List<HashMap>) templateRes.get("data");
                    // 明细
                    for (HashMap datum : data) {
                        if (datum.get("id").toString().equals("634e8588f92e00007b003283")) {
                            String context = (String) datum.get("context");
                            // 拿到明细的字段
                            if (context.contains(",")) {
                                String[] split = context.split(",");
                                for (String s : split) {
                                    HashMap<Object, Object> map1 = generatorForDetail(s, id);
                                    result.add(map1);
                                }

                            } else {
                                HashMap<Object, Object> map1 = generatorForDetail(context, id);
                                result.add(map1);
                            }
                        }
                    }
                    template.put("child", result);

                }
            }
        }
        return payment;
    }

    /**
     * 更新维护明细表
     *
     * @param detailName
     * @return
     */
    public HashMap<Object, Object> generatorForDetail(String detailName, String templateId) {

        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(detailName).and("template_id").is(templateId));
        HashMap template_detail1 = mongoTemplate.findOne(query, HashMap.class, "template_detail");
        if (template_detail1 == null) {
            HashMap<Object, Object> map = new HashMap<>();
            map.put("name", detailName);
            map.put("template_id", templateId);
            HashMap<Object, Object> template_detail = mongoTemplate.insert(map, "template_detail");
            delete_Id(template_detail);
            return template_detail;
        } else {
            Update update = new Update();
            update.set("name", detailName);
            update.set("template_id", templateId);
            mongoTemplate.upsert(query, update, "template_detail");
            delete_Id(template_detail1);
            return template_detail1;
        }


    }

    @Scheduled(cron = "0 0/30 8-19 * * ?")
//    @Scheduled(cron = "*/30 * * * * ?")
    @Override
    public void insertPaymentNumber() {
        List<HashMap> payment = paymentInfo1(true);
        // 第一层
        for (int i = 1; i < payment.size() + 1; i++) {
            HashMap hashMap = payment.get(i - 1);
            hashMap.put("identifier", i + "");
            hashMap.put("paymentDetail", (String) hashMap.get("name"));
            insert1(hashMap);
            List<HashMap> child = (List<HashMap>) hashMap.get("child");
            // 第二层
            for (int j = 1; j < child.size() + 1; j++) {
                HashMap hashMap1 = child.get(j - 1);
                hashMap1.put("identifier", i + "-" + j);
                hashMap1.put("paymentDetail", hashMap.get("name") + "/" + (String) hashMap1.get("name"));
                insert1(hashMap1);
                List<HashMap> child1 = (List<HashMap>) hashMap1.get("child");
                // 模板层
                if (child1 != null && child1.size() > 0) {
                    for (int k = 1; k < child1.size() + 1; k++) {
                        HashMap hashMap2 = child1.get(k - 1);
                        hashMap2.put("identifier", i + "-" + j + "-" + k);
                        hashMap2.put("paymentDetail", hashMap.get("name") + "/" + (String) hashMap1.get("name") + "/" + hashMap2.get("name"));
                        insert1(hashMap2);
                    }
                }
            }
        }

    }

    /**
     * 导出财务报表
     *
     * @param response
     * @throws Exception
     */
    @Override
    public void exportPaymentApplyData(HttpServletResponse response) throws Exception {
        //模板地址
        Map<String, Map<String, Map<String, BigDecimal>>> paymentApplyData = getPaymentApplyData(response);
        String path1 = "/home/exportfile/oaces3.xlsx";
        FileUtil.productionExcel(paymentApplyData, path1, "", response);
    }

    /**
     * 导出申请数据
     */
    @Override
    public Map<String, Map<String, Map<String, BigDecimal>>> getPaymentApplyData(HttpServletResponse response) {
        // 数据初始化
        Map<String, Map<String, Map<String, BigDecimal>>> map = initData();

        List<HashMap> depts = mongoTemplate.find(new Query(), HashMap.class, "dept");
        Map<String, Object> deptMap = depts.stream().collect(
                Collectors.toMap(o -> o.get("companyDeptName").toString(), o -> o.get("dept_id"), (m, n) -> n));

        // 获取审批通过的数据
        int year = DateUtil.year(new Date());
        String startTime = year + "-01-01 00:00:00";
        String endTime = year + "-12-31 23:59:59";
        QueryWrapper<BizBusiness> businessQueryWrapper = new QueryWrapper<>();
        businessQueryWrapper.eq("result", 2)
                .eq("status", 2)
                .ge("last_auditor_time", startTime)
                .le("last_auditor_time", endTime);
        List<BizBusiness> bizBusinesses = bizBusinessToMapper.selectList(businessQueryWrapper);
        if (bizBusinesses.size() == 0) return map;
        // 数据库中有重复的 applyCode
        List<BizBusiness> businesses = bizBusinesses.stream().collect(Collectors.collectingAndThen(
                Collectors.toCollection(() -> new TreeSet<>(
                        Comparator.comparing(BizBusiness::getApplyCode))), ArrayList::new));
        // 获取 applyCode->lastAuditorTime Map集合
        Map<String, Date> info = businesses.stream().collect(Collectors.toMap(BizBusiness::getApplyCode, BizBusiness::getLastAuditorTime));

//        // 测试数据
//        Set<String> set = new HashSet<>();
//        // 工资薪金
//        set.add("FK202303141678775965189");
//        // 差旅费
//        set.add("FK202212081670501623868");
//        // 保证金、保证金退款及代收代付
//        set.add("FK202302081675849556220");

        // 获取数据
        Query query = new Query();
        query.addCriteria(Criteria.where("applyCode").in(info.keySet()));
//        query.addCriteria(Criteria.where("applyCode").in(set));
        List<HashMap> hashMaps = mongoTemplate.find(query, HashMap.class, "payment");
        if (hashMaps.size() == 0) return map;

        // TODO 数据收集
        List<String> applyCodeList = new ArrayList<>();
//        Set<String> set = new HashSet<>();

        for (HashMap hashMap : hashMaps) {
            // 获取付款明细
            List<List<HashMap>> detail = (List<List<HashMap>>) hashMap.get("detail");
            for (List<HashMap> list : detail) {
                int deptId = 0;
                String projectMoney = null;
                String name = null;
                HashMap baseInformation = (HashMap) hashMap.get("BaseInformation");
                String baseName = baseInformation.get("name").toString();
                for (HashMap map1 : list) {
                    String label = map1.get("label").toString();
                    if ("费用归属部门".equals(label)) {
                        List<Integer> list1 = (List<Integer>) map1.get("array");
                        if (CollUtil.isEmpty(list1)) {
                            Integer value = (Integer) deptMap.get(map1.get("value").toString());
                            if (value == null) continue;
                            deptId = value;
                        } else {
                            deptId = list1.get(list1.size() - 1);
                        }
                    }
                    if ("金额".equals(label)) {
                        projectMoney = map1.get("value").toString();
                    }
                    if ("付款类别明细".equals(label)) {
                        name = map1.get("value").toString();
                        // 付款类别明细中有两个其他费用 去重
                        if ("聘请中介机构费".equals(baseName) && "其他费用".equals(name)) {
                            name = "聘请中介机构费-" + name;
                        }
                    }
                }
                // 获取applyCode
                String applyCode = hashMap.get("applyCode").toString();

                // 部门id为0 表示未从数据库中获取到部门id
                if (deptId == 0 || StrUtil.isBlank(projectMoney) || StrUtil.isBlank(name)) {
//                     TODO 收集数据 根据applyCode查看部门名称有啥不同
//                    applyCodeList.add(applyCode);
                    continue;
                }

                // 取出审核通过是时间
                Date date = info.get(applyCode);
                int month = date.getMonth() + 1;

                // 获取费用分类变量
                Map<String, Object> variales = getVariales(deptId, name);
                if (variales.size() == 0) {
                    // TODO 收集数据
//                    applyCodeList.add(applyCode + "......" + name);
                    continue;
                }
                String var1 = (String) variales.get("var1");
                Integer var2 = (Integer) variales.get("var2");

                // 获取申请单类型
                String typeNum = getTypeNum(var2, name);
                if (typeNum.contains("9999")) {
                    // TODO 收集数据
//                    applyCodeList.add(applyCode + "......" + name);
                    continue;
                }
                String v3 = "b" + month;
                Map<String, BigDecimal> map2 = map.get(var1).get(typeNum);
                BigDecimal sum = map2.get(v3).add(new BigDecimal(projectMoney));
                map2.put(v3, sum);
            }
        }
        return map;
    }

    /**
     * 判断用户是否有申请权限
     *
     * @param id 模板id
     * @return result
     */
    @Override
    public boolean permitApply(String id) {
        HashMap hashMap = mongoTemplate.findById(id, HashMap.class, "template");
        HashMap baseInformation = (HashMap) hashMap.get("BaseInformation");
        String processKey = (String) baseInformation.get("processKey");
        return allowApply(processKey);
    }

    private Map<String, Object> getVariales(int deptId, String name) {
        // 营业成本 yycb
        List<Integer> yycb = getPaymentDeptList("yycb");
        // 营销费用 yxfy
        List<Integer> yxfy = getPaymentDeptList("yxfy");
        // 管理费用 glfy
        List<Integer> glfy = getPaymentDeptList("glfy");
        // 研发费用 yffy
        List<Integer> yffy = getPaymentDeptList("yffy");
        String var1 = "m";
        int var2;
        Map<String, Object> map = Maps.newHashMap();
        if (includePaymentItem(name) != 9999) {
            // 单独计费项
            var1 += 5;
            var2 = 5;
        } else {
            if (yycb.contains(deptId)) {
                // 营业成本
                var1 += 1;
                var2 = 1;
            } else if (yxfy.contains(deptId)) {
                // 营销费用
                var1 += 2;
                var2 = 2;
            } else if (glfy.contains(deptId)) {
                // 管理费用
                var1 += 3;
                var2 = 3;
            } else if (yffy.contains(deptId)) {
                // 研发费用
                var1 += 4;
                var2 = 4;
            } else {
                return map;
            }
        }
        map.put("var1", var1);
        map.put("var2", var2);
        return map;
    }

    private List<Integer> getPaymentDeptList(String paymentType) {
        List<Integer> list = new ArrayList<>();
        if ("yycb".equals(paymentType)) {
            // 营业成本所属部门 环境公共卫生事业部
            list.add(141);
            // 采样组
            list.add(395);
            // 咨询组
            list.add(396);
            // 辐射事业部
            list.add(142);
            // 职业卫生事业部
            list.add(140);
            // 检评部
            list.add(329);
            // 评价一组、二组、三组
            list.add(330);
            list.add(331);
            list.add(332);
            // 专家/挂靠
            list.add(399);
            // 质控部
            list.add(134);
        } else if ("yxfy".equals(paymentType)) {
            // 营销费用所属部门 杭州市场一部、二部
            list.add(138);
            list.add(358);
        } else if ("glfy".equals(paymentType)) {
            // 管理费用所属部门 综合部
            list.add(136);
            // 管理部
            list.add(133);
            // 客服
            list.add(333);
            // 商务
            list.add(398);
            // 财务
            list.add(135);
        } else {
            // 研发费用所属部门 研发部
            list.add(137);
            // 实验室
            list.add(139);
        }
        return list;
    }

    private Map<String, Map<String, Map<String, BigDecimal>>> initData() {
        // 初始化 费用分类
        Map<String, Map<String, Map<String, BigDecimal>>> map = new HashMap<>();
        for (int k = 1; k <= 5; k++) {
            // 初始化 模板分类
            int l = 18;
            if (k == 2) {
                l = 15;
            } else if (k == 3) {
                l = 27;
            } else if (k == 5) {
                l = 7;
            }
            Map<String, Map<String, BigDecimal>> map1 = new HashMap<>();
            for (int j = 1; j <= l; j++) {
                // 初始化 1-12月份数据
                Map<String, BigDecimal> monthData = new HashMap<>(12);
                for (int i = 1; i <= 12; i++) {
                    monthData.put("b" + i, BigDecimal.ZERO);
                }
                map1.put("a" + j, monthData);
            }
            map.put("m" + k, map1);
        }
        return map;
    }

    private String getTypeNum(int type, String name) {
        switch (type) {
            case 1:
                return "a" + getYycbTypeNum(name);
            case 2:
                return "a" + getYxfyTypeNum(name);
            case 3:
                return "a" + getGlfyTypeNum(name);
            case 4:
                return "a" + getYffyTypeNum(name);
            default:
                return "a" + includePaymentItem(name);
        }
    }

    // 营业成本模块费用类型18个
    private Integer getYycbTypeNum(String name) {
        if ("工资".equals(name)) {
            return 1;
        } else if ("奖金".equals(name)) {
            return 2;
        } else if ("社会保险费".equals(name)) {
            return 3;
        } else if ("住房公积金".equals(name)) {
            return 4;
        } else if ("节日福利费".equals(name) || "员工活动费".equals(name) || "其他福利费".equals(name)) {
            return 5;
        } else if ("快递费".equals(name)) {
            return 6;
        } else if ("差旅费".equals(name)) {
            return 7;
        } else if ("业务分包≥30%".equals(name) || "业务分包＜30%".equals(name)) {
            return 8;
        } else if ("办公费用".equals(name) || "电话费".equals(name) || "网络费".equals(name) || "办公租赁".equals(name)
                || "研发成果相关费用".equals(name) || "报告制作费及资料费".equals(name)) {
            return 9;
        } else if ("普通会务费".equals(name) || "评审会务费".equals(name)) {
            return 10;
        } else if ("油费".equals(name) || "停车费用".equals(name) || "车辆通行费".equals(name) || "车辆维修费".equals(name)
                || "车辆租赁费".equals(name) || "车辆保险费".equals(name) || "其他费用".equals(name)) {
            return 11;
        } else if ("市内交通费用".equals(name)) {
            return 12;
        } else if ("业务招待费".equals(name)) {
            return 13;
        } else if ("节日招待费".equals(name)) {
            return 14;
        } else if ("媒介广告费".equals(name) || "媒体推广费".equals(name) || "其他".equals(name)) {
            return 15;
        } else if ("诉讼费".equals(name) || "律师费".equals(name) || "审计费".equals(name) || "知识产权相关费用".equals(name)
                || "聘请中介机构费-其他费用".equals(name)) {
            return 16;
        } else if ("折旧费".equals(name)) {
            return 17;
        } else {
            return 9999;
        }
    }

    // 营销费用模块费用类型15个
    private Integer getYxfyTypeNum(String name) {
        if ("工资".equals(name)) {
            return 1;
        } else if ("奖金".equals(name)) {
            return 2;
        } else if ("社会保险费".equals(name)) {
            return 3;
        } else if ("住房公积金".equals(name)) {
            return 4;
        } else if ("节日福利费".equals(name) || "员工活动费".equals(name) || "其他福利费".equals(name)) {
            return 5;
        } else if ("办公费用".equals(name) || "电话费".equals(name) || "网络费".equals(name) || "办公租赁".equals(name)
                || "研发成果相关费用".equals(name) || "报告制作费及资料费".equals(name) || "快递费".equals(name)) {
            return 6;
        } else if ("媒介广告费".equals(name) || "媒体推广费".equals(name) || "其他".equals(name)) {
            return 7;
        } else if ("渠道服务费≤15%".equals(name) || "渠道服务费>15%且≤30%".equals(name) || "渠道服务费<30%".equals(name)) {
            return 8;
        } else if ("差旅费".equals(name)) {
            return 9;
        } else if ("市内交通费用".equals(name)) {
            return 10;
        } else if ("业务招待费".equals(name)) {
            return 11;
        } else if ("节日招待费".equals(name)) {
            return 12;
        } else if ("油费".equals(name) || "停车费用".equals(name) || "车辆通行费".equals(name) || "车辆维修费".equals(name)
                || "车辆租赁费".equals(name) || "车辆保险费".equals(name) || "聘请中介机构费-其他费用".equals(name)) {
            return 13;
        } else if ("折旧费".equals(name)) {
            return 14;
        } else {
            return 9999;
        }
    }

    // 管理费用模块费用类型27个
    private Integer getGlfyTypeNum(String name) {
        if ("工资".equals(name)) {
            return 1;
        } else if ("奖金".equals(name)) {
            return 2;
        } else if ("社会保险费".equals(name)) {
            return 3;
        } else if ("住房公积金".equals(name)) {
            return 4;
        } else if ("节日福利费".equals(name) || "员工活动费".equals(name) || "其他福利费".equals(name)) {
            return 5;
        } else if ("快递费".equals(name)) {
            return 6;
        } else if ("差旅费".equals(name)) {
            return 7;
        } else if ("电话费".equals(name)) {
            return 8;
        } else if ("网络费".equals(name)) {
            return 9;
        } else if ("办公费用".equals(name) || "办公租赁".equals(name) || "研发成果相关费用".equals(name)) {
            return 10;
        } else if ("普通会务费".equals(name) || "评审会务费".equals(name)) {
            return 11;
        } else if ("油费".equals(name) || "停车费用".equals(name) || "车辆通行费".equals(name) || "车辆维修费".equals(name)
                || "车辆租赁费".equals(name) || "车辆保险费".equals(name) || "其他费用".equals(name)) {
            return 12;
        } else if ("市内交通费".equals(name)) {
            return 13;
        } else if ("房屋租赁".equals(name)) {
            return 14;
        } else if ("水费".equals(name) || "电费".equals(name) || "物业管理费".equals(name)) {
            return 15;
        } else if ("报告制作费及资料费".equals(name)) {
            return 16;
        } else if ("业务招待费".equals(name)) {
            return 17;
        } else if ("节日招待费".equals(name)) {
            return 18;
        } else if ("总分公司管理费".equals(name)) {
            return 19;
        } else if ("诉讼费".equals(name) || "律师费".equals(name) || "审计费".equals(name) || "知识产权相关费用".equals(name)
                || "聘请中介机构费-其他费用".equals(name)) {
            return 20;
        } else if ("管理税费".equals(name)) {
            return 21;
        } else if ("劳务费".equals(name)) {
            return 22;
        } else if ("租赁费".equals(name)) {
            return 23;
        } else if ("培训费".equals(name)) {
            return 24;
        } else if ("职工教育经费、招聘费及劳保费".equals(name)) {
            return 25;
        } else if ("折旧费".equals(name)) {
            return 26;
        } else {
            return 9999;
        }
    }

    // 研发费用模块费用类型18个
    private Integer getYffyTypeNum(String name) {
        if ("工资".equals(name)) {
            return 1;
        } else if ("奖金".equals(name)) {
            return 2;
        } else if ("社会保险费".equals(name)) {
            return 3;
        } else if ("住房公积金".equals(name)) {
            return 4;
        } else if ("节日福利费".equals(name) || "员工活动费".equals(name) || "其他福利费".equals(name)) {
            return 5;
        } else if ("办公费用".equals(name) || "电话费".equals(name) || "办公租赁".equals(name)
                || "报告制作费及资料费".equals(name)) {
            return 6;
        } else if ("研发成果相关费用".equals(name)) {
            return 7;
        } else if ("油费".equals(name) || "停车费用".equals(name) || "车辆通行费".equals(name) || "车辆维修费".equals(name)
                || "车辆租赁费".equals(name) || "车辆保险费".equals(name) || "其他费用".equals(name)) {
            return 8;
        } else if ("差旅费".equals(name)) {
            return 9;
        } else if ("市内交通费".equals(name)) {
            return 10;
        } else if ("业务招待费".equals(name)) {
            return 11;
        } else if ("实验室耗材、药品".equals(name)) {
            return 12;
        } else if ("仪器校验费".equals(name)) {
            return 13;
        } else if ("装备调试试验费".equals(name)) {
            return 14;
        } else if ("仪器校验、试验、维修等".equals(name)) {
            // excel 表格中的【仪器校验费、装备调试试验费、仪器维修费】都放到这里
            return 15;
        } else if ("网络费".equals(name)) {
            return 16;
        } else if ("折旧费".equals(name)) {
            return 17;
        } else {
            return 9999;
        }
    }

    private int includePaymentItem(String name) {
        // 7类  单独分类
        if ("部门备用金".equals(name)) {
            return 1;
        } else if ("保证金".equals(name) || "保证金退款".equals(name) || "押金退款".equals(name) || "代收代付款项".equals(name)) {
            return 2;
        } else if ("业务退款".equals(name)) {
            return 3;
        } else if ("暂借款".equals(name) || "还款".equals(name)) {
            return 4;
        } else if ("分/子公司内部借款".equals(name)) {
            return 5;
        } else if ("分/子公司内部资金调拨".equals(name)) {
            return 6;
        } else if ("集团体系内公司间资金调拨".equals(name)) {
            return 7;
        }
        return 9999;
    }

    public void data(HttpServletResponse response) {
        try {
            Query query = new Query();
            LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
            LocalDateTime end = LocalDateTime.of(2023, 1, 31, 23, 59, 59);
            query.addCriteria(Criteria.where("result").is(2)
                    .and("state").is(2)
                    .and("createTime").lte(end).gte(start));
            List<HashMap> hashMaps = mongoTemplate.find(query, HashMap.class, "payment");
            for (int i = 0; i < hashMaps.size(); i++) {
                HashMap hashMap = hashMaps.get(i);
                HashMap baseInformation = (HashMap) hashMap.get("BaseInformation");
                String name = baseInformation.get("name").toString();
                String processKey = baseInformation.get("processKey").toString();
                hashMap.put("name", name);
                hashMap.put("processKey", processKey);
            }
            Map<String, List<HashMap>> map = hashMaps.stream().collect(Collectors.groupingBy(hashMap -> hashMap.get("processKey").toString()));
            List<PaymentTemplateExcelEntity> list = new ArrayList<>();
            map.forEach((key, value) -> {
                PaymentTemplateExcelEntity paymentTemplateExcelEntity = new PaymentTemplateExcelEntity();
                BigDecimal count = new BigDecimal("0.0");
                for (int i = 0; i < value.size(); i++) {
                    HashMap hashMap = value.get(i);
                    if (i == 0) {
                        String name = hashMap.get("name").toString();
                        paymentTemplateExcelEntity.setName(name);
                    }
                    count = count.add(new BigDecimal(hashMap.get("projectMoney").toString()));
                }
                paymentTemplateExcelEntity.setProjectMoney(count);
                list.add(paymentTemplateExcelEntity);
            });
            OutputStream os = response.getOutputStream();
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode("财务申请数据汇总", "UTF-8") + ".xlsx");
            response.addHeader("Access-Control-Expose-Headers", "Content-disposition");
            EasyExcel.write(os, PaymentTemplateExcelEntity.class).sheet("财务申请数据汇总").doWrite(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void insert1(HashMap map) {
        HashMap<Object, Object> map1 = new HashMap<>();
        // 先删除再插入
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(map.get("name").toString()));
        mongoTemplate.remove(query, "dictionary");
        map1.put("identifier", map.get("identifier"));
        map1.put("name", map.get("name"));
        map1.put("paymentDetail", map.get("paymentDetail"));
        map1.put("create_time", new Date());
        mongoTemplate.insert(map1, "dictionary");

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

    private String getStatus(Integer statusNum) {
        String status = "";
        switch (statusNum) {
            case 1:
                status = "审批中";
                break;
            case 2:
                status = "结束";
                break;
            case 3:
                status = "撤销";
                break;
            case 4:
                status = "中止";
                break;
            default:
                break;
        }
        return status;
    }


    /**
     * 初始化业务流程
     *
     * @param param
     * @return
     */
    private BizBusiness initBusiness(HashMap<String, Object> param) {
        HashMap baseInformation = (HashMap) param.get("BaseInformation");
        // 查出最新发布的流程定义信息
        Example example = new Example(ActReProcdef.class);
        Example.Criteria criteria = example.createCriteria();
//        criteria.andEqualTo("key", param.get("processKey").toString());
        criteria.andEqualTo("key", baseInformation.get("processKey"));
        example.setOrderByClause("VERSION_ DESC");
        ActReProcdef actReProcdef = actReProcdefMapper.selectByExample(example).get(0);

        BizBusiness business = new BizBusiness();
        business.setTableId(param.get("id").toString());
        business.setProcDefId(actReProcdef.getId());
        business.setTitle(param.get("title").toString());
        business.setProcName(actReProcdef.getName());
        long userId = SystemUtil.getUserId();
        // 付款的userId为费用归属部门的部门负责人
//        Long subjectionDeptId = paymentApply.getSubjectionDeptId();
//        SysDept sysDept = remoteDeptService.selectSysDeptByDeptId(subjectionDeptId);
        business.setUserId(userId);

        SysUser user = remoteUserService.selectSysUserByUserId(userId);
        business.setApplyer(user.getUserName());
        business.setStatus(ActivitiConstant.STATUS_DEALING);
        business.setResult(ActivitiConstant.RESULT_DEALING);
        business.setApplyTime(new Date());
        return business;
    }

    public Long getId() throws UnknownHostException {
        InetAddress localHost = InetAddress.getLocalHost();
        String hostAddress = localHost.getHostAddress();
//        System.out.println(hostAddress);
        String[] split = hostAddress.split("\\.");
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            sb.append(s);
        }
        long l1 = Long.parseLong(sb.toString());
        long l = System.currentTimeMillis();
        return l1 ^ l;
    }
}
