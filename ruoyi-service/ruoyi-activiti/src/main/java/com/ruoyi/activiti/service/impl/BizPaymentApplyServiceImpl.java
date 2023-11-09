package com.ruoyi.activiti.service.impl;

import cn.hutool.core.exceptions.StatefulException;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.google.common.collect.Maps;
import com.ruoyi.activiti.consts.ActivitiConstant;
import com.ruoyi.activiti.domain.*;
import com.ruoyi.activiti.domain.proc.ActReProcdef;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.domain.fiance.BizPaymentApply;
import com.ruoyi.activiti.domain.fiance.BizPaymentDetail;
import com.ruoyi.activiti.feign.RemoteFileService;
import com.ruoyi.activiti.mapper.*;
import com.ruoyi.activiti.service.BizPaymentApplyService;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.utils.CollectUtil;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteConfigService;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.feign.RemoteUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 付款申请
 *
 * @author zx
 * @date 2021/12/19 20:08
 */
@Service
@Slf4j
public class BizPaymentApplyServiceImpl implements BizPaymentApplyService {
    private final BizPaymentApplyMapper paymentApplyMapper;
    private final BizPaymentDetailMapper paymentDetailMapper;
    private final RemoteUserService remoteUserService;
    private final ActReProcdefMapper actReProcdefMapper;
    private final IBizBusinessService bizBusinessService;
    private final RemoteConfigService remoteConfigService;
    private final RemoteDeptService remoteDeptService;
    private final RemoteFileService remoteFileService;
    private final DistrictsMapper2 districtsMapper;
    private final BizAssociateApplyMapper associateApplyMapper;
    private final BizAssociateGoodMapper associateGoodMapper;

    @Autowired
    public BizPaymentApplyServiceImpl(BizPaymentApplyMapper paymentApplyMapper, BizPaymentDetailMapper paymentDetailMapper, RemoteUserService remoteUserService, ActReProcdefMapper actReProcdefMapper, IBizBusinessService bizBusinessService, RemoteConfigService remoteConfigService, RemoteDeptService remoteDeptService, RemoteFileService remoteFileService, DistrictsMapper2 districtsMapper, BizAssociateApplyMapper associateApplyMapper, BizAssociateGoodMapper associateGoodMapper) {
        this.paymentApplyMapper = paymentApplyMapper;
        this.paymentDetailMapper = paymentDetailMapper;
        this.remoteUserService = remoteUserService;
        this.actReProcdefMapper = actReProcdefMapper;
        this.bizBusinessService = bizBusinessService;
        this.remoteConfigService = remoteConfigService;
        this.remoteDeptService = remoteDeptService;
        this.remoteFileService = remoteFileService;
        this.districtsMapper = districtsMapper;
        this.associateApplyMapper = associateApplyMapper;
        this.associateGoodMapper = associateGoodMapper;
    }

    /**
     * 新增申请
     *
     * @param paymentApply
     * @return
     */
    @Override
    public int insert(BizPaymentApply paymentApply) {
        try {
            // 抄送人去重
            String cc = paymentApply.getCc();
            if (StrUtil.isNotBlank(cc)) {
                cc = String.join(",", CollectUtil.twoClear(cc.split(",")));
                paymentApply.setCc(cc);
            }
            String userName = SystemUtil.getUserName();
            SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
            Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(sysUser.getDeptId());
            String companyId1 = belongCompany2.get("companyId").toString();
            String companyName = belongCompany2.get("companyName").toString();
            paymentApply.setCreateTime(new Date());
            paymentApply.setUpdateTime(new Date());
            paymentApply.setCreateBy(userName);
            paymentApply.setUpdateBy(userName);
            //申请人部门
            paymentApply.setDeptId(sysUser.getDeptId());
            paymentApply.setTitle(sysUser.getUserName() + "提交的付款申请");
            int insert = paymentApplyMapper.insert(paymentApply);
            List<BizPaymentDetail> paymentDetails = paymentApply.getPaymentDetails();
            if (paymentDetails.isEmpty()) {
                return 3;
            }
            for (BizPaymentDetail paymentDetail : paymentDetails) {
                paymentDetail.setCreateBy(userName);
                paymentDetail.setUpdateBy(userName);
                paymentDetail.setParentId(paymentApply.getId());
                int insert1 = paymentDetailMapper.insert(paymentDetail);
            }

            // 关联多个审批单
            List<Map<String, Object>> associateApply = paymentApply.getAssociateApply();
            if (associateApply == null) {
                associateApply = new ArrayList<>();
            }

            // 去重
            associateApply = associateApply.stream().distinct().collect(Collectors.toList());
            associateApply.stream().forEach(associate -> {
                BizAssociateApply associateApply1 = new BizAssociateApply();
                associateApply1.setApplyId(paymentApply.getId());
                associateApply1.setTypes("payment");
                associateApply1.setAssociateApply(Long.valueOf(associate.get("associateApply").toString()));
                associateApply1.setAssociateTitle(associate.get("associateTitle").toString());
                associateApply1.setAssociateTypes(associate.get("associateTypes").toString());
                associateApply1.setCreateTime(new Date());
                associateApply1.setCreateBy(SystemUtil.getUserId().toString());
                associateApplyMapper.insert(associateApply1);
            });
            // 关联多个采购单的物品
            List<Map<String, Object>> associatePurchaseApply = paymentApply.getAssociatePurchaseApply();
            if (associatePurchaseApply == null) {
                associatePurchaseApply = new ArrayList<>();
            }

            // 去重
            associatePurchaseApply = associatePurchaseApply.stream().distinct().collect(Collectors.toList());
            associatePurchaseApply.stream().forEach(associatePurchase->{
                // 修改物品财务状态
                String goodIdStr = associatePurchase.get("goodId").toString();
                if(StrUtil.isNotBlank(goodIdStr)){
                    Arrays.stream(goodIdStr.split(",")).forEach(id->{

                        Long goodId = Long.valueOf(id);
                        associateApplyMapper.updateFinanceStatus(goodId,1);
                        // 保存关联物品
                        BizAssociateGood bizAssociateGood = new BizAssociateGood();
                        bizAssociateGood.setApplyId(paymentApply.getId());
                        bizAssociateGood.setGoodId(goodId);
                        bizAssociateGood.setAssociateId(Long.valueOf(associatePurchase.get("associateId").toString()));
                        bizAssociateGood.setTypes(1);
                        bizAssociateGood.setCreateBy(SystemUtil.getUserNameCn());
                        bizAssociateGood.setCreateTime(new Date());
                        bizAssociateGood.setPurchaseKey(Long.valueOf(associatePurchase.get("associateApply").toString()));
                        associateGoodMapper.insert(bizAssociateGood);
                    });
                }
                BizAssociateApply associateApply1 = new BizAssociateApply();
                associateApply1.setApplyId(paymentApply.getId());
                associateApply1.setTypes("payment");
                associateApply1.setAssociateApply(Long.valueOf(associatePurchase.get("associateApply").toString()));
                associateApply1.setAssociateTitle(associatePurchase.get("associateTitle").toString());
                associateApply1.setAssociateTypes(associatePurchase.get("associateTypes").toString());
                associateApply1.setCreateTime(new Date());
                associateApply1.setCreateBy(SystemUtil.getUserId().toString());
                associateApplyMapper.insert(associateApply1);
            });


            // 初始化流程
            BizBusiness business = initBusiness(paymentApply);
            business.setCompanyId(SystemUtil.getCompanyId());
            business.setCompanyName(SystemUtil.getCompanyName());
            business.setDeptId(SystemUtil.getDeptId());
            business.setDeptName(sysUser.getDept().getDeptName());
            business.setApplyCode(paymentApply.getPaymentCode());
            bizBusinessService.insertBizBusiness(business);

            // 获取经营参数
            SysConfig config1 = new SysConfig();
            config1.setConfigKey(companyId1 + "g1fk");
            List<SysConfig> list = remoteConfigService.listOperating(config1);
//            SysConfig config2 = new SysConfig();
//            config2.setConfigKey(companyId + "g2");
//            List<SysConfig> list2 = remoteConfigService.listOperating(config2);
            if (list.isEmpty()) {
                return 2;
            }

            Map<String, Object> variables = Maps.newHashMap();
            // 这里可以设置各个负责人，key跟模型的代理变量一致
            variables.put("cc", paymentApply.getCc());
            variables.put("g1", Double.valueOf(list.get(0).getConfigValue()));
            variables.put("deptId",paymentApply.getSubjectionDeptId());
//            variables.put("g2", Double.valueOf(list2.get(0).getConfigValue()));
            // 付款总金额
            variables.put("money", paymentApply.getPayCount().doubleValue());
            bizBusinessService.startProcess(business, variables);
            // 将上传的临时文件转为有效文件
            int reimburse = remoteFileService.update(paymentApply.getId(), paymentApply.getPaymentCode());
            if (reimburse == 0) {
                throw new StatefulException("将上传的文件转为有效文件失败");
            }
            return 1;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }

    /**
     * 获取详情
     *
     * @param id
     * @return
     */
    @Override
    public synchronized BizPaymentApply selectById(Long id) {
        BizPaymentApply paymentApply = paymentApplyMapper.selectById(id);
        String reimburseType = paymentApply.getPaymentType() == null ? "" : paymentApply.getPaymentType();
        String accountType = paymentApply.getAccountType();
        List<Map<String, Object>> auditList = getAuditList("");
        for (Map<String, Object> map : auditList) {
            if (reimburseType.equals(map.get("id").toString())) {
                paymentApply.setPaymentTypeName(map.get("name").toString());
            }
        }
        accountType = accountType == null ? "" : accountType;
        switch (accountType) {
            case "1":
                paymentApply.setAccountTypeName("支付宝");
                break;
            case "2":
                paymentApply.setAccountTypeName("个人银行卡");
                break;
            case "3":
                paymentApply.setAccountTypeName("对公银行账号");
                break;
            default:
                throw new StatefulException("账户类型错误");
        }
        QueryWrapper<BizPaymentDetail> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", paymentApply.getId());
        List<BizPaymentDetail> paymentDetails = paymentDetailMapper.selectList(wrapper);
        paymentApply.setPaymentDetails(paymentDetails);
        // 查询附件信息
        // 付款凭证
        List<SysAttachment> certificate = remoteFileService.getList(paymentApply.getId(), "payment-certificate");
        if (certificate == null) {
            certificate = new ArrayList<>();
        }
        // 付款附件
        List<SysAttachment> appendix = remoteFileService.getList(paymentApply.getId(), "payment-appendix");
        if (appendix == null) {
            appendix = new ArrayList<>();
        }
        paymentApply.setVouchers(certificate);
        paymentApply.setAttachment(appendix);
        // 地区信息

        String city = "";
        if (StrUtil.isNotBlank(paymentApply.getProvince())) {
            String province = districtsMapper.selectById(Integer.valueOf(paymentApply.getProvince())).getName();
            paymentApply.setProvinceName(province);
        }else {
            paymentApply.setProvinceName("");
        }
        if (StrUtil.isNotBlank(paymentApply.getCity())) {
            city = districtsMapper.selectById(Integer.valueOf(paymentApply.getCity())).getName();
        }
        paymentApply.setCityName(city);
        Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(paymentApply.getDeptId());
        //申请公司名称
        paymentApply.setCompanyName(belongCompany2.get("companyName").toString());
        //申请部门名称
        paymentApply.setDeptName(remoteDeptService.selectSysDeptByDeptId(paymentApply.getDeptId()).getDeptName());
        if (paymentApply.getSubjectionDeptId() != null) {
            Map<String, Object> belongCompany = remoteDeptService.getBelongCompany2(paymentApply.getSubjectionDeptId());
            log.error("测试*************"+paymentApply.getSubjectionDeptId());

                //隶属公司名称
                paymentApply.setSubjectionCompanyName(belongCompany.get("companyName").toString());

            //隶属部门名称
            paymentApply.setSubjectionDeptName(remoteDeptService.selectSysDeptByDeptId(paymentApply.getSubjectionDeptId()).getDeptName());
        }
        //申请人赋值
        SysUser sysUser2 = remoteUserService.selectSysUserByUsername(paymentApply.getCreateBy());
        paymentApply.setCreateByName(sysUser2.getUserName());
        //打印人赋值
        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
        paymentApply.setPdfName(sysUser.getUserName());
        String ccStr = paymentApply.getCc();
        if (StrUtil.isNotBlank(ccStr)) {
            List<String> ccList = new ArrayList<>();
            for (String cc : ccStr.split(",")) {
                SysUser ccUser = remoteUserService.selectSysUserByUserId(Long.valueOf(cc));
                ccList.add(ccUser.getUserName());
            }
            paymentApply.setCcName(String.join("、", ccList));
        }

        // 获取关联审批单
        QueryWrapper<BizAssociateApply> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("baa.types", "payment");
        wrapper1.eq("baa.apply_id", paymentApply.getId());
        List<Map<String, Object>> associate = associateApplyMapper.getAssociate(wrapper1);
        // 过滤掉采购
        List<Map<String, Object>> associate1 = associate.stream().filter(m -> !"purchase".equals(m.get("associateTypes").toString())).collect(Collectors.toList());
        for (Map<String, Object> map : associate) {
            List<BizBusiness> business = bizBusinessService.selectBizBusinessListAll(new BizBusiness().setId(Long.valueOf(map.get("associateApply").toString())).setProcDefKey(map.get("associateTypes").toString()));
            if (business.isEmpty()) {
                map.put("result", "");
                map.put("auditors", "");
            } else {
                map.put("result", business.get(0).getResult());
                map.put("auditors", business.get(0).getAuditors());
            }
        }
        paymentApply.setAssociateApply(associate1);
        List<BizAssociateGood> associateGoods = new LambdaQueryChainWrapper<>(associateGoodMapper)
                .eq(BizAssociateGood::getApplyId, paymentApply.getId())
                .eq(BizAssociateGood::getTypes, "1")
                .list();
        associate.stream().filter(m -> "purchase".equals(m.get("associateTypes").toString())).forEach(m -> {
            List<String> goods = new ArrayList<>();
            for (BizAssociateGood associateGood : associateGoods) {
                if(m.get("associateApply").toString().equals(associateGood.getPurchaseKey().toString())){
                    goods.add(associateGood.getGoodId().toString());
                }
            }
            m.put("goodId",String.join(",",goods));
        });

        paymentApply.setAssociatePurchaseApply(associate);
        return paymentApply;
    }
//    public BizPaymentApply translateToOld() {
//
//    }
    /**
     * 获取关联审批单下拉列表
     *
     * @return
     */
    @Override
    public List<Map<String, Object>> getAuditList(String source) {

        List<Map<String, Object>> definedList = actReProcdefMapper.getDefinedList();
        if("1".equals(source)){
            Iterator<Map<String, Object>> iterator = definedList.iterator();
            while (iterator.hasNext()){
                Map<String, Object> next = iterator.next();
                if(next.get("id").toString().equals("purchase")){
                    iterator.remove();
                }
            }
        }
        return definedList;
    }

    /**
     * 初始化业务流程
     *
     * @param paymentApply
     * @return
     */
    private BizBusiness initBusiness(BizPaymentApply paymentApply) {
        // 查出最新发布的流程定义信息
        Example example = new Example(ActReProcdef.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("key", "payment");
        example.setOrderByClause("VERSION_ DESC");
        ActReProcdef actReProcdef = actReProcdefMapper.selectByExample(example).get(0);

        BizBusiness business = new BizBusiness();
        business.setTableId(paymentApply.getId().toString());
        business.setProcDefId(actReProcdef.getId());
        business.setTitle(paymentApply.getTitle());
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

}
