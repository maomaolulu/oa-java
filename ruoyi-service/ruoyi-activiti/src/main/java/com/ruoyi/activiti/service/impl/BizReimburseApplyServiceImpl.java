package com.ruoyi.activiti.service.impl;

import cn.hutool.core.exceptions.StatefulException;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.google.common.collect.Maps;
import com.ruoyi.activiti.consts.ActivitiConstant;
import com.ruoyi.activiti.domain.BizAssociateApply;
import com.ruoyi.activiti.domain.BizAssociateGood;
import com.ruoyi.activiti.domain.SysAttachment;
import com.ruoyi.activiti.domain.fiance.BizReimburseApply;
import com.ruoyi.activiti.domain.fiance.BizReimburseDetail;
import com.ruoyi.activiti.domain.proc.ActReProcdef;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.feign.RemoteFileService;
import com.ruoyi.activiti.mapper.*;
import com.ruoyi.activiti.service.BizReimburseApplyService;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.utils.CollectUtil;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteConfigService;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 报销申请
 *
 * @author zx
 * @date 2021/12/16 11:08
 */
@Service
@Slf4j
public class BizReimburseApplyServiceImpl implements BizReimburseApplyService {
    private final BizReimburseApplyMapper reimburseApplyMapper;
    private final BizReimburseDetailMapper reimburseDetailMapper;
    private final RemoteUserService remoteUserService;
    private final ActReProcdefMapper actReProcdefMapper;
    private final IBizBusinessService bizBusinessService;
    private final RemoteConfigService remoteConfigService;
    private final RemoteDeptService remoteDeptService;
    private final RemoteFileService remoteFileService;
    private final BizAssociateApplyMapper associateApplyMapper;
    private final BizAssociateGoodMapper associateGoodMapper;

    @Autowired
    public BizReimburseApplyServiceImpl(BizReimburseApplyMapper reimburseApplyMapper, BizReimburseDetailMapper reimburseDetailMapper, RemoteUserService remoteUserService, ActReProcdefMapper actReProcdefMapper, IBizBusinessService bizBusinessService, RemoteConfigService remoteConfigService, RemoteDeptService remoteDeptService, RemoteFileService remoteFileService, BizAssociateApplyMapper associateApplyMapper, BizAssociateGoodMapper associateGoodMapper) {
        this.reimburseApplyMapper = reimburseApplyMapper;
        this.reimburseDetailMapper = reimburseDetailMapper;
        this.remoteUserService = remoteUserService;
        this.actReProcdefMapper = actReProcdefMapper;
        this.bizBusinessService = bizBusinessService;
        this.remoteConfigService = remoteConfigService;
        this.remoteDeptService = remoteDeptService;
        this.remoteFileService = remoteFileService;
        this.associateApplyMapper = associateApplyMapper;
        this.associateGoodMapper = associateGoodMapper;
    }


    /**
     * 新增申请
     *
     * @param reimburseApply
     * @return
     */
    @Override
    public R insert(BizReimburseApply reimburseApply) {
        try {
            // 抄送人去重
            String cc = reimburseApply.getCc();
            if (StrUtil.isNotBlank(cc)) {
                cc = String.join(",", CollectUtil.twoClear(cc.split(",")));
                reimburseApply.setCc(cc);
            }
            if (StrUtil.isBlank(reimburseApply.getAccountType())) {
                return R.error("请填写账户类型");
            }
            String userName = SystemUtil.getUserName();
            SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
            Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(sysUser.getDeptId());
            String companyId1 = belongCompany2.get("companyId").toString();
            String companyName = belongCompany2.get("companyName").toString();
            reimburseApply.setCreateTime(new Date());
            reimburseApply.setUpdateTime(new Date());
            reimburseApply.setCreateBy(userName);
            reimburseApply.setUpdateBy(userName);
            reimburseApply.setDeptId(sysUser.getDeptId());
            int insert = reimburseApplyMapper.insert(reimburseApply);
            // 关联多个审批单
            List<Map<String, Object>> associateApply = reimburseApply.getAssociateApply();
            if (associateApply == null) {
                associateApply = new ArrayList<>();
            }
            // 去重
            associateApply = associateApply.stream().distinct().collect(Collectors.toList());
            associateApply.stream().forEach(associate -> {
                BizAssociateApply associateApply1 = new BizAssociateApply();
                associateApply1.setApplyId(reimburseApply.getId());
                associateApply1.setTypes("reimburse");
                associateApply1.setAssociateApply(Long.valueOf(associate.get("associateApply").toString()));
                associateApply1.setAssociateTitle(associate.get("associateTitle").toString());
                associateApply1.setAssociateTypes(associate.get("associateTypes").toString());
                associateApply1.setCreateTime(new Date());
                associateApply1.setCreateBy(SystemUtil.getUserId().toString());
                associateApplyMapper.insert(associateApply1);
            });
            // 关联多个采购单的物品
            List<Map<String, Object>> associatePurchaseApply = reimburseApply.getAssociatePurchaseApply();
            if (associatePurchaseApply == null) {
                associatePurchaseApply = new ArrayList<>();
            }

            // 去重
            associatePurchaseApply = associatePurchaseApply.stream().distinct().collect(Collectors.toList());
            associatePurchaseApply.stream().forEach(associatePurchase -> {
                // 修改物品财务状态
                String goodIdStr = associatePurchase.get("goodId").toString();
                if (StrUtil.isNotBlank(goodIdStr)) {
                    Arrays.stream(goodIdStr.split(",")).forEach(id -> {

                        Long goodId = Long.valueOf(id);
                        associateApplyMapper.updateFinanceStatus(goodId, 2);
                        // 保存关联物品
                        BizAssociateGood bizAssociateGood = new BizAssociateGood();
                        bizAssociateGood.setApplyId(reimburseApply.getId());
                        bizAssociateGood.setGoodId(goodId);
                        bizAssociateGood.setAssociateId(Long.valueOf(associatePurchase.get("associateId").toString()));
                        bizAssociateGood.setTypes(2);
                        bizAssociateGood.setCreateBy(SystemUtil.getUserNameCn());
                        bizAssociateGood.setCreateTime(new Date());
                        bizAssociateGood.setPurchaseKey(Long.valueOf(associatePurchase.get("associateApply").toString()));
                        associateGoodMapper.insert(bizAssociateGood);
                    });
                }
                BizAssociateApply associateApply1 = new BizAssociateApply();
                associateApply1.setApplyId(reimburseApply.getId());
                associateApply1.setTypes("reimburse");
                associateApply1.setAssociateApply(Long.valueOf(associatePurchase.get("associateApply").toString()));
                associateApply1.setAssociateTitle(associatePurchase.get("associateTitle").toString());
                associateApply1.setAssociateTypes(associatePurchase.get("associateTypes").toString());
                associateApply1.setCreateTime(new Date());
                associateApply1.setCreateBy(SystemUtil.getUserId().toString());
                associateApplyMapper.insert(associateApply1);
            });
            List<BizReimburseDetail> reimburseDetails = reimburseApply.getReimburseDetails();
            if (reimburseDetails.isEmpty()) {
                return R.error("报销明细不能为空");
            }
            for (BizReimburseDetail reimburseDetail : reimburseDetails) {
                reimburseDetail.setCreateBy(userName);
                reimburseDetail.setUpdateBy(userName);
                reimburseDetail.setParentId(reimburseApply.getId());
                int insert1 = reimburseDetailMapper.insert(reimburseDetail);
            }
            // 初始化流程
            BizBusiness business = initBusiness(reimburseApply);
            business.setCompanyId(SystemUtil.getCompanyId());
            business.setCompanyName(SystemUtil.getCompanyName());
            business.setDeptId(SystemUtil.getDeptId());
            business.setDeptName(sysUser.getDept().getDeptName());
            business.setApplyCode(reimburseApply.getReimbursementCode());
            bizBusinessService.insertBizBusiness(business);

            // 获取经营参数
            SysConfig config1 = new SysConfig();
            config1.setConfigKey(companyId1 + "g1bx");
            List<SysConfig> list = remoteConfigService.listOperating(config1);
//            SysConfig config2 = new SysConfig();
//            config2.setConfigKey(companyId + "g2");
//            List<SysConfig> list2 = remoteConfigService.listOperating(config2);
            if (list.isEmpty()) {
                log.error(companyName + "缺少经营参数");
                return R.error(companyName + "缺少经营参数");
            }

            Map<String, Object> variables = Maps.newHashMap();
            // 这里可以设置各个负责人，key跟模型的代理变量一致
            variables.put("cc", reimburseApply.getCc());
            variables.put("g1", Double.valueOf(list.get(0).getConfigValue()));
            variables.put("deptId",reimburseApply.getSubjectionDeptId());
//            variables.put("g2", Double.valueOf(list2.get(0).getConfigValue()));
            // 报销总金额
            variables.put("money", reimburseApply.getReimburseMoneyTotal().doubleValue());
            bizBusinessService.startProcess(business, variables);

            // 将上传的临时文件转为有效文件
            int reimburse = remoteFileService.update(reimburseApply.getId(), reimburseApply.getReimbursementCode());
            if (reimburse == 0) {
                throw new StatefulException("将上传的文件转为有效文件失败");
            }
            return R.ok("提交成功");
        } catch (Exception e) {
            log.error("新增报销申请失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return R.error("提交申请失败");
        }
    }

    public static void main(String[] args) {
        Long a = 115L;
        System.out.println(a + "g1");
        double a1 = 0.89;
        double a2 = 0.09;
        System.out.println(a1 > a2);
    }

    /**
     * 获取详情
     *
     * @param id
     * @return
     */
    @Override
    public BizReimburseApply selectBizReimburseApplyById(Long id) {

        BizReimburseApply reimburseApply = reimburseApplyMapper.selectById(id);
        String reimburseType = reimburseApply.getReimburseType();
        String accountType = reimburseApply.getAccountType();
        if (StrUtil.isNotBlank(reimburseType)) {
            List<Map<String, Object>> auditList = getAuditList("","");
            for (Map<String, Object> map : auditList) {
                if (reimburseType.equals(map.get("id").toString())) {
                    reimburseApply.setReimburseTypeName(map.get("name").toString());
                }
            }
        }
        accountType = accountType == null ? "" : accountType;
        switch (accountType) {
            case "1":
                reimburseApply.setAccountTypeName("支付宝");
                break;
            case "2":
                reimburseApply.setAccountTypeName("个人银行卡");
                break;
            case "3":
                reimburseApply.setAccountTypeName("对公银行账号");
                break;
            default:
                throw new StatefulException("账户类型错误");
        }
        QueryWrapper<BizReimburseDetail> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", reimburseApply.getId());
        List<BizReimburseDetail> bizReimburseDetails = reimburseDetailMapper.selectList(wrapper);
        for (BizReimburseDetail bizReimburseDetail : bizReimburseDetails
        ) {
            Integer types = bizReimburseDetail.getTypes();
            if (types != null) {
                if (types == 1) {
                    bizReimburseDetail.setTypesName("差旅费");
                } else if (types == 2) {
                    bizReimburseDetail.setTypesName("招待费");
                } else if (types == 3) {
                    bizReimburseDetail.setTypesName("设备采购");
                } else if (types == 4) {
                    bizReimburseDetail.setTypesName("耗材采购");
                } else if (types == 5) {
                    bizReimburseDetail.setTypesName("办公用品");
                } else if (types == 6) {
                    bizReimburseDetail.setTypesName("活动经费");
                } else if (types == 7) {
                    bizReimburseDetail.setTypesName("其他费用");
                } else if (types == 8) {
                    bizReimburseDetail.setTypesName("房租/水电费");
                }
            } else {
                bizReimburseDetail.setTypesName("");
            }
        }
        reimburseApply.setReimburseDetails(bizReimburseDetails);
        // 查询附件信息
        // 报销凭证
        List<SysAttachment> certificate = remoteFileService.getList(reimburseApply.getId(), "reimburse-certificate");
        if (certificate == null) {
            certificate = new ArrayList<>();
        }
        // 报销附件
        List<SysAttachment> appendix = remoteFileService.getList(reimburseApply.getId(), "reimburse-appendix");
        if (appendix == null) {
            appendix = new ArrayList<>();
        }
        reimburseApply.setVouchers(certificate);
        reimburseApply.setAttachment(appendix);
        Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(reimburseApply.getDeptId());
        //申请人公司
        reimburseApply.setCompanyName(belongCompany2.get("companyName").toString());

        //申请部门名称
        reimburseApply.setDeptName(remoteDeptService.selectSysDeptByDeptId(reimburseApply.getDeptId()).getDeptName());
        if (reimburseApply.getSubjectionDeptId() != null) {
            System.out.println(reimburseApply.getSubjectionDeptId());
            Map<String, Object> belongCompany = remoteDeptService.getBelongCompany2(reimburseApply.getSubjectionDeptId());
            if (belongCompany!=null && belongCompany.get("companyName")!= null) {
                //隶属公司名称
                reimburseApply.setSubjectionCompanyName(belongCompany.get("companyName").toString());
                //隶属部门名称

            }
            reimburseApply.setSubjectionDeptName(remoteDeptService.selectSysDeptByDeptId(reimburseApply.getSubjectionDeptId()).getDeptName());

        }
        //申请人赋值
        SysUser sysUser2 = remoteUserService.selectSysUserByUsername(reimburseApply.getCreateBy());
        reimburseApply.setCreateByName(sysUser2.getUserName());
        //打印人赋值
        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
        reimburseApply.setPdfName(sysUser.getUserName());
        String ccStr = reimburseApply.getCc();
        if (StrUtil.isNotBlank(ccStr)) {
            List<String> ccList = new ArrayList<>();
            for (String cc : ccStr.split(",")) {
                SysUser ccUser = remoteUserService.selectSysUserByUserId(Long.valueOf(cc));
                ccList.add(ccUser.getUserName());
            }
            reimburseApply.setCcName(String.join("、", ccList));
        }

        // 获取关联审批单
        QueryWrapper<BizAssociateApply> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("baa.types", "reimburse");
        wrapper1.eq("baa.apply_id", reimburseApply.getId());
        List<Map<String, Object>> associate = associateApplyMapper.getAssociate(wrapper1);
        // 过滤掉采购
        List<Map<String, Object>> associate1 = associate.stream().filter(m -> !"purchase".equals(m.get("associateTypes").toString())).collect(Collectors.toList());

        for (Map<String, Object> map : associate1) {
            List<BizBusiness> business = bizBusinessService.selectBizBusinessListAll(new BizBusiness().setId(Long.valueOf(map.get("associateApply").toString())).setProcDefKey(map.get("associateTypes").toString()));
            if (business.isEmpty()) {
                map.put("result", "");
                map.put("auditors", "");
            } else {
                map.put("result", business.get(0).getResult());
                map.put("auditors", business.get(0).getAuditors());
            }
        }
        reimburseApply.setAssociateApply(associate1);
        List<BizAssociateGood> associateGoods = new LambdaQueryChainWrapper<>(associateGoodMapper)
                .eq(BizAssociateGood::getApplyId, reimburseApply.getId())
                .eq(BizAssociateGood::getTypes, "2")
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

        reimburseApply.setAssociatePurchaseApply(associate);
        return reimburseApply;
    }

    /**
     * 获取关联审批单下拉列表
     *
     * @return
     */
    @Override
    public List<Map<String, Object>> getAuditList(String source,String type) {

        List<Map<String, Object>> definedList = actReProcdefMapper.getDefinedList();
        // 去除采购
        if("1".equals(source)){
            Iterator<Map<String, Object>> iterator = definedList.iterator();
            while (iterator.hasNext()){
                Map<String, Object> next = iterator.next();
                if(next.get("id").toString().equals("purchase")){
                    iterator.remove();
                }
            }
        }
        if (type.equals("payment-")) {
            List<Map<String, Object>> id = definedList.stream().filter(e -> {
                return e.get("id").toString().contains("payment-");
            }).collect(Collectors.toList());
            return id;
        }
        return definedList;
    }

    /**
     * 初始化业务流程
     *
     * @param reimburseApply
     * @return
     */
    private BizBusiness initBusiness(BizReimburseApply reimburseApply) {
        // 查出最新发布的流程定义信息
        Example example = new Example(ActReProcdef.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("key", "reimburse");
        example.setOrderByClause("VERSION_ DESC");
        ActReProcdef actReProcdef = actReProcdefMapper.selectByExample(example).get(0);

        BizBusiness business = new BizBusiness();
        business.setTableId(reimburseApply.getId().toString());
        business.setProcDefId(actReProcdef.getId());
//        QueryWrapper<ActReProcdef> wrapper = new QueryWrapper<>();
//        wrapper.eq("p.ID_", reimburseApply.getReimburseType());
//        Map<String, Object> definedName = actReProcdefMapper.getDefinedName(wrapper);
//        // 流程名称+审批编码+“报销”
        business.setProcName(actReProcdef.getName());
        long userId = SystemUtil.getUserId();
        // 报销的userId为费用归属部门的部门负责人
//        Long subjectionDeptId = reimburseApply.getSubjectionDeptId();
//        SysDept sysDept = remoteDeptService.selectSysDeptByDeptId(subjectionDeptId);
        business.setUserId(userId);

        SysUser user = remoteUserService.selectSysUserByUserId(userId);
        business.setTitle(user.getUserName() + "提交的报销申请");
        business.setApplyer(user.getUserName());
        business.setStatus(ActivitiConstant.STATUS_DEALING);
        business.setResult(ActivitiConstant.RESULT_DEALING);
        business.setApplyTime(new Date());
        return business;
    }
}
