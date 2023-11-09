package com.ruoyi.activiti.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.StatefulException;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.ruoyi.activiti.consts.ActivitiConstant;
import com.ruoyi.activiti.domain.BizAssociateGood;
import com.ruoyi.activiti.domain.SysAttachment;
import com.ruoyi.activiti.domain.asset.AaSpu;
import com.ruoyi.activiti.domain.dto.BizSupplierQuoteDto;
import com.ruoyi.activiti.domain.dto.GoodsRunVariableDto;
import com.ruoyi.activiti.domain.proc.ActReProcdef;
import com.ruoyi.activiti.domain.proc.ActRuVariable;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.domain.purchase.BizAnticipateGoods;
import com.ruoyi.activiti.domain.purchase.BizGoodsInfo;
import com.ruoyi.activiti.domain.purchase.BizPurchaseApply;
import com.ruoyi.activiti.domain.purchase.BizSupplierQuote;
import com.ruoyi.activiti.feign.RemoteFileService;
import com.ruoyi.activiti.mapper.*;
import com.ruoyi.activiti.properties.CcProperties;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.service.IBizPurchaseApplyService;
import com.ruoyi.activiti.utils.CollectUtil;
import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.domain.SysDept;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteConfigService;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.feign.RemoteUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.*;

/**
 * 采购申请(新)Service业务层处理
 *
 * @author zx
 * @date 2021-11-16
 */
@Service
@Slf4j
public class BizPurchaseApplyServiceImpl implements IBizPurchaseApplyService {
    private final BizPurchaseApplyMapper bizPurchaseApplyMapper;
    private final RemoteUserService remoteUserService;
    private final ActReProcdefMapper actReProcdefMapper;
    private final IBizBusinessService bizBusinessService;
    private final BizGoodsInfoMapper bizGoodsInfoMapper;
    private final ActRuVariableMapper actRuVariableMapper;
    private final BizAnticipateGoodsMapper bizAnticipateGoodsMapper;
    private final RemoteConfigService remoteConfigService;
    private final RemoteDeptService remoteDeptService;
    private final AaSpuMapper aaSpuMapper;
    private final BizSupplierQuoteMapper bizSupplierQuoteMapper;
    private final RemoteFileService remoteFileService;

    @Autowired
    public BizPurchaseApplyServiceImpl(BizPurchaseApplyMapper bizPurchaseApplyMapper, RemoteUserService remoteUserService, ActReProcdefMapper actReProcdefMapper, IBizBusinessService bizBusinessService, BizGoodsInfoMapper bizGoodsInfoMapper, ActRuVariableMapper actRuVariableMapper, BizAnticipateGoodsMapper bizAnticipateGoodsMapper, RemoteConfigService remoteConfigService, RemoteDeptService remoteDeptService, AaSpuMapper aaSpuMapper, BizSupplierQuoteMapper bizSupplierQuoteMapper, RemoteFileService remoteFileService) {
        this.bizPurchaseApplyMapper = bizPurchaseApplyMapper;
        this.remoteUserService = remoteUserService;
        this.actReProcdefMapper = actReProcdefMapper;
        this.bizBusinessService = bizBusinessService;
        this.bizGoodsInfoMapper = bizGoodsInfoMapper;
        this.actRuVariableMapper = actRuVariableMapper;
        this.bizAnticipateGoodsMapper = bizAnticipateGoodsMapper;
        this.remoteConfigService = remoteConfigService;
        this.remoteDeptService = remoteDeptService;
        this.aaSpuMapper = aaSpuMapper;
        this.bizSupplierQuoteMapper = bizSupplierQuoteMapper;
        this.remoteFileService = remoteFileService;
    }

    /**
     * 查询采购申请(新)详情
     *
     * @param id 采购申请(新)ID
     * @return 采购申请(新)
     */
    @Override
    public BizPurchaseApply selectBizPurchaseApplyById(Long id) {
        return getPurchase(bizPurchaseApplyMapper.selectBizPurchaseApplyById(id));
    }

    private BizPurchaseApply getPurchase(BizPurchaseApply bizPurchaseApply) {
        // 抄送人赋值
        if (StrUtil.isNotBlank(bizPurchaseApply.getCc())) {
            String[] split = bizPurchaseApply.getCc().split(",");
            List<String> ccList = new ArrayList<>();
            for (String cc : split) {
                SysUser sysUser = remoteUserService.selectSysUserByUserId(Long.parseLong(cc));
                if (sysUser == null) {
                    ccList.add("");
                } else {
                    ccList.add(sysUser.getUserName());
                }
            }
            bizPurchaseApply.setCcName(String.join("、", ccList));
        }
        // 申请人部门赋值
        SysDept sysDept = remoteDeptService.selectSysDeptByDeptId(bizPurchaseApply.getDeptId());

        //申请人赋值
//        SysUser sysUser2 = remoteUserService.selectSysUserByUsername(bizPurchaseApply.getCreateBy());
        //申请人
//        bizPurchaseApply.setCreateBy(sysUser2.getUserName());
        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
        //打印人
        bizPurchaseApply.setPdfName(sysUser.getUserName());
        if (sysDept == null) {
            bizPurchaseApply.setDeptFullName("");
        } else {
            bizPurchaseApply.setDeptFullName(sysDept.getDeptName());
        }
        BizGoodsInfo bizGoodsInfo = new BizGoodsInfo();
        bizGoodsInfo.setPurchaseId(bizPurchaseApply.getId());
        List<BizGoodsInfo> bizGoodsInfos = selectBizGoodsInfoList(bizGoodsInfo);
        List<BizAssociateGood> associatedInfo = bizGoodsInfoMapper.getAssociatedInfo(bizGoodsInfo.getPurchaseId());
        for (BizGoodsInfo goodsInfo : bizGoodsInfos) {
            List<Long> applyIds = new ArrayList<>();
             for (BizAssociateGood associateGood : associatedInfo) {
                if(associateGood.getGoodId().equals(goodsInfo.getId())){
                    applyIds.add(associateGood.getApplyId());
                }
            }
             goodsInfo.setApplyIds(applyIds);
            if (null != goodsInfo.getAscriptionDept()) {
                // 费用归属部门
                SysDept sysDept2 = remoteDeptService.selectSysDeptByDeptId(goodsInfo.getAscriptionDept());
                goodsInfo.setAscriptionDeptName(sysDept2.getDeptName());
            }
            if (null != goodsInfo.getUsages()) {
//                    goodsInfo.setUsagesName(remoteDeptService.selectSysDeptByDeptId(Long.valueOf(goodsInfo.getUsages())).getDeptName());
                // 物品归属部门（实际使用部门）
                String[] split = goodsInfo.getUsages().split(",");
                List<String> useagesNames = new ArrayList<>();
                for (String dept : split) {
                    String useagesName = remoteDeptService.selectSysDeptByDeptId(Long.valueOf(dept)).getDeptName();
                    useagesNames.add(useagesName);
                }
                goodsInfo.setUsagesName(String.join("、", useagesNames));
            }
        }
        BizGoodsInfo bizGoodsInfo1 = new BizGoodsInfo();
        try {
            bizGoodsInfo1 = bizGoodsInfos.get(0);
        } catch (Exception e) {
            log.error("采购详情", e);
        }

        bizPurchaseApply.setExpectDate(bizGoodsInfo1.getExpectDate());
        bizPurchaseApply.setItemTypeName(bizGoodsInfo1.getItemTypeName());
        bizPurchaseApply.setItemType(bizGoodsInfo1.getItemType());
        bizPurchaseApply.setBizGoodsInfos(bizGoodsInfos);
        // 费用归属部门
        bizPurchaseApply.setAscriptionDept(bizGoodsInfo1.getAscriptionDeptName());
        // 费用归属部门id
        bizPurchaseApply.setAscriptionDeptId(bizGoodsInfo1.getAscriptionDept());
        // 物品归属部门
        bizPurchaseApply.setUsages(bizGoodsInfo1.getUsagesName());

        Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(bizPurchaseApply.getDeptId());
       if (belongCompany2.get("companyName")!=null){
           bizPurchaseApply.setCompanyName(belongCompany2.get("companyName").toString());
       }
        // 查询附件信息
        List<SysAttachment> certificate = remoteFileService.getList(bizPurchaseApply.getId(), "quote-list");
        if (certificate == null) {
            certificate = new ArrayList<>();
        }
        List<SysAttachment> certificate2 = remoteFileService.getList(bizPurchaseApply.getId(), "purchase-certificate");
        if (certificate == null) {
            certificate = new ArrayList<>();
        }
        bizPurchaseApply.setQuoteList(certificate);
        bizPurchaseApply.setPurchaseList(certificate2);
        return bizPurchaseApply;
    }

    /**
     * 查询采购申请(新)列表
     *
     * @param bizPurchaseApply 采购申请(新)
     * @return 采购申请(新)
     */
    @Override
    @DataScope(deptAlias = "d")
    public List<BizPurchaseApply> selectBizPurchaseApplyList(BizPurchaseApply bizPurchaseApply) {
        //TODO 部门全称
        return bizPurchaseApplyMapper.selectBizPurchaseApplyList(bizPurchaseApply);
    }

    /**
     * 新增采购申请(新)
     *
     * @param bizPurchaseApply 采购申请(新)
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public R insertBizPurchaseApply(BizPurchaseApply bizPurchaseApply) {
        try {
            // 抄送人去重
            String cc = bizPurchaseApply.getCc();
            if (StrUtil.isNotBlank(cc)) {
                cc = String.join(",", CollectUtil.twoClear(cc.split(",")));
                bizPurchaseApply.setCc(cc);
            }
            bizPurchaseApply.setDelFlag("0");
            bizPurchaseApply.setCreateBy(SystemUtil.getUserName());
            bizPurchaseApply.setCreateTime(new Date());
            if(StrUtil.isBlank(bizPurchaseApply.getPurchaseCode())){

                Date date = new Date();
                String timestamp = String.valueOf(date.getTime());
                String today = DateUtil.today();
                today = today.replace("-", "");

                String purchaseCode = "CG" + today + timestamp.substring(0, 12);
                bizPurchaseApply.setPurchaseCode(purchaseCode);
            }
            Long userId = SystemUtil.getUserId();
            SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
            bizPurchaseApply.setDeptId(sysUser.getDeptId());
            bizPurchaseApply.setTitle(sysUser.getUserName() + "提交的采购申请");
            bizPurchaseApplyMapper.insertBizPurchaseApply(bizPurchaseApply);
            List<BizGoodsInfo> bizGoodsInfos = bizPurchaseApply.getBizGoodsInfos();
            if (bizGoodsInfos.isEmpty()) {
                return R.error("请添加物品");
            }
            for (BizGoodsInfo bizGoodsInfo : bizGoodsInfos
            ) {
                if (StrUtil.isBlank(bizGoodsInfo.getName())) {
                    return R.error("物品名称不能为空");
                } else if (StrUtil.isBlank(bizGoodsInfo.getModel())) {
                    return R.error("物品规格型号不能为空");
                } else if (bizGoodsInfo.getUnit() == null) {
                    return R.error("物品单位不能为空");
                }
                if (StrUtil.isBlank(bizGoodsInfo.getUsages())) {
                    return R.error("物品使用部门不能为空");
                } else if (bizGoodsInfo.getAscriptionDept() == null) {
                    return R.error("费用归属部门不能为空");
                } else if (bizGoodsInfo.getAmount() == null || bizGoodsInfo.getAmount() == 0) {
                    return R.error("所有物品采购数量必须大于0");
                }
                if (bizGoodsInfo.getAnticipateGoodsId() != null) {
                    BizAnticipateGoods bizAnticipateGoods = new BizAnticipateGoods();
                    bizAnticipateGoods.setId(bizGoodsInfo.getAnticipateGoodsId());
                    bizAnticipateGoods.setDelFlag("1");
                    bizAnticipateGoodsMapper.updateById(bizAnticipateGoods);
                }
            }
            // 保存物品
            bizGoodsInfos.stream().forEach(bizGoodsInfo -> {

                bizGoodsInfo.setDelFlag("0");
                bizGoodsInfo.setIsInvalid("0");
                bizGoodsInfo.setCreateBy(SystemUtil.getUserName());
                bizGoodsInfo.setCreateTime(new Date());
                bizGoodsInfo.setIsPurchase("0");
                bizGoodsInfo.setIsReceived("0");
                bizGoodsInfo.setIsStorage("0");
                bizGoodsInfo.setIsAcceptance("0");
                bizGoodsInfo.setPurchaseId(bizPurchaseApply.getId());
                //采购物品名称 规格型号  单位
                bizGoodsInfo.setApplyModel(bizGoodsInfo.getModel());
                bizGoodsInfo.setApplyName(bizGoodsInfo.getName());
                bizGoodsInfo.setApplyUnit(bizGoodsInfo.getUnit());
                // 财务状态
                bizGoodsInfo.setFinanceStatus(0);
                bizGoodsInfoMapper.insertSelective(bizGoodsInfo);

            });
            // 保存采购物品记录

            // 初始化流程
            BizBusiness business = initBusiness(bizPurchaseApply);
            business.setCompanyId(SystemUtil.getCompanyId());
            business.setCompanyName(SystemUtil.getCompanyName());
            business.setDeptId(SystemUtil.getDeptId());
            business.setDeptName(sysUser.getDept().getDeptName());
            business.setApplyCode(bizPurchaseApply.getPurchaseCode());
            bizBusinessService.insertBizBusiness(business);
            // 获取经营参数
            SysConfig config3 = new SysConfig();
            config3.setConfigKey("purchase-g1");
            List<SysConfig> list3 = remoteConfigService.listOperating(config3);
            SysConfig config1 = new SysConfig();
            config1.setConfigKey("purchase200");
            List<SysConfig> list = remoteConfigService.listOperating(config1);
            SysConfig config2 = new SysConfig();
            config2.setConfigKey("purchase2000");
            List<SysConfig> list2 = remoteConfigService.listOperating(config2);
            if (list.isEmpty() || list2.isEmpty()) {
                return R.error("请先配置经营参数");
            }
            Map<String, Object> variables = Maps.newHashMap();
            // 这里可以设置各个负责人，key跟模型的代理变量一致
            variables.put("cc", bizPurchaseApply.getCc());

            variables.put("money", BigDecimal.ZERO.longValue());
            variables.put("purchase200", Long.valueOf(list.get(0).getConfigValue()) * 100);
            variables.put("purchase2000", Long.valueOf(list2.get(0).getConfigValue()) * 100);
            variables.put("g1", Long.valueOf(list3.get(0).getConfigValue()) * 100);
            variables.put("companyId",SystemUtil.getCompanyId());
            log.info("采购申请流程参数", variables);
            bizBusinessService.startProcess(business, variables);

            // 将上传的临时文件转为有效文件
            int reimburse = remoteFileService.update(bizPurchaseApply.getId(), bizPurchaseApply.getPurchaseCode());
            if (reimburse == 0) {
                throw new StatefulException("将上传的文件转为有效文件失败");
            }
            return R.ok("提交采购申请成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return R.error("提交采购申请失败");
        }
    }

    /**
     * 修改采购申请(新)
     *
     * @param bizPurchaseApply 采购申请(新)
     * @return 结果
     */
    @Override
    public int updateBizPurchaseApply(BizPurchaseApply bizPurchaseApply) {
        return bizPurchaseApplyMapper.updateBizPurchaseApply(bizPurchaseApply);
    }

    /**
     * 删除采购申请(新)对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteBizPurchaseApplyByIds(String[] ids) {
        return bizPurchaseApplyMapper.deleteBizPurchaseApplyByIds(ids);
    }

    /**
     * 删除采购申请(新)信息
     *
     * @param id 采购申请(新)ID
     * @return 结果
     */
    @Override
    public int deleteBizPurchaseApplyById(Long id) {
        return bizPurchaseApplyMapper.deleteBizPurchaseApplyById(id);
    }

    /**
     * 查询采购物品信息
     *
     * @param id 采购物品信息ID
     * @return 采购物品信息
     */
    @Override
    public BizGoodsInfo selectBizGoodsInfoById(Long id) {
        return bizGoodsInfoMapper.selectBizGoodsInfoById(id);
    }

    /**
     * 查询采购物品信息列表
     *
     * @param bizGoodsInfo 采购物品信息
     * @return 采购物品信息
     */
    @Override
    public List<BizGoodsInfo> selectBizGoodsInfoList(BizGoodsInfo bizGoodsInfo) {
        List<BizGoodsInfo> bizGoodsInfos = bizGoodsInfoMapper.selectBizGoodsInfoList(bizGoodsInfo);
        for (BizGoodsInfo goodsInfo : bizGoodsInfos) {
            if(StrUtil.isNotBlank(goodsInfo.getPurchaser())){
                SysUser sysUser = remoteUserService.selectSysUserByUsername(goodsInfo.getPurchaser());
                goodsInfo.setPurchaser(sysUser.getUserName());
            }
            goodsInfo.setItemTypeName(bizGoodsInfoMapper.getItemTypeName(goodsInfo.getItemType()));
            Optional<String> link = bizGoodsInfoMapper.getLink(goodsInfo.getId());
            if (link != null) {
                goodsInfo.setPurchaseStatus(link.orElse(""));
            }
            String isInvalid = goodsInfo.getIsInvalid()==null?"":goodsInfo.getIsInvalid();
            switch (isInvalid) {
                case "0":
                    goodsInfo.setIsInvalidName("正常");
                    goodsInfo.setPurchaseColor("#67C23A");
                    break;
                case "1":
                    goodsInfo.setIsInvalidName("作废");
                    goodsInfo.setPurchaseColor("#F56C6C");
                    break;
                case "2":
                    goodsInfo.setIsInvalidName("退货");
                    goodsInfo.setPurchaseColor("#E6A23C");
                    break;
                default:
                    goodsInfo.setIsInvalidName("");
                    break;
            }
            Integer financeStatus = goodsInfo.getFinanceStatus()==null?99:goodsInfo.getFinanceStatus();
            switch (financeStatus) {
                case 0:
                    goodsInfo.setFinanceStatusName("未关联");
                    goodsInfo.setFinanceColor("#409EFF");
                    break;
                case 1:
                    goodsInfo.setFinanceStatusName("关联付款");
                    goodsInfo.setFinanceColor("#E6A23C");
                    break;
                case 2:
                    goodsInfo.setFinanceStatusName("关联报销");
                    goodsInfo.setFinanceColor("#E6A23C");
                    break;
                case 3:
                    goodsInfo.setFinanceStatusName("已付款");
                    goodsInfo.setFinanceColor("#67C23A");
                    break;
                case 4:
                    goodsInfo.setFinanceStatusName("已报销");
                    goodsInfo.setFinanceColor("#67C23A");
                    break;
                default:
                    goodsInfo.setIsInvalidName("");
                    break;
            }

        }

        return bizGoodsInfos;
    }

    /**
     * 修改采购物品信息(作废)
     *
     * @param goodsRunVariableDto 采购物品信息
     * @return 结果
     */
    @Override
    public int updateBizGoodsInfo(GoodsRunVariableDto goodsRunVariableDto) {
        try {
            BigDecimal price = BigDecimal.ZERO;

//            for (BizGoodsInfo goodsInfo : goodsRunVariableDto.getBizGoodsInfo()) {
//                bizGoodsInfoMapper.updateBizGoodsInfo(goodsInfo);
//                price = price.add(goodsInfo.getPrice().multiply(new BigDecimal(goodsInfo.getAmount())));
//            }

            // 新增流程运行时金额变量
            QueryWrapper<ActRuVariable> wrapper = new QueryWrapper<>();
            wrapper.eq("NAME_", "money");
            wrapper.eq("PROC_INST_ID_", goodsRunVariableDto.getProcInstId());
            ActRuVariable actRuVariable = actRuVariableMapper.selectOne(wrapper);
            actRuVariable.setLongValue(price.setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).longValue());
            actRuVariableMapper.updateById(actRuVariable);
            return 1;
        } catch (Exception e) {
            log.error("修改采购物品信息:" + e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;

        }
    }


    /**
     * 初始化业务流程
     *
     * @param bizPurchaseApply
     * @return
     * @author zmr
     */
    private BizBusiness initBusiness(BizPurchaseApply bizPurchaseApply) {
        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
        // 查出最新发布的流程定义信息
        Example example = new Example(ActReProcdef.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("key", "purchase");
        example.setOrderByClause("VERSION_ DESC");
        List<ActReProcdef> actReProcdefs = actReProcdefMapper.selectByExample(example);
        ActReProcdef actReProcdef = new ActReProcdef();
        if (!actReProcdefs.isEmpty()) {
            actReProcdef = actReProcdefs.get(0);
        }
        BizBusiness business = new BizBusiness();
        business.setTableId(bizPurchaseApply.getId().toString());
        business.setProcDefId(actReProcdef.getId());
        business.setTitle(bizPurchaseApply.getTitle());
        business.setProcName(actReProcdef.getName());
//        long userId = SystemUtil.getUserId();
        business.setUserId(userId);
        SysUser user = remoteUserService.selectSysUserByUserId(userId);
        business.setApplyer(user.getUserName());
        business.setStatus(ActivitiConstant.STATUS_DEALING);
        business.setResult(ActivitiConstant.RESULT_DEALING);
        business.setApplyTime(new Date());
        return business;
    }

    /**
     * 根据名称查询耗材详情
     *
     * @param name
     * @return
     */
    @Override
    public List<AaSpu> listByName(String name, String deptId) {
        Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(Long.valueOf(deptId));
        String companyId1 = belongCompany2.get("companyId").toString();
        List<AaSpu> aaSpus = aaSpuMapper.listByName(name, Long.valueOf(companyId1));
        if (aaSpus != null && aaSpus.size() > 0) {
            for (AaSpu aaSpu : aaSpus
            ) {
                Map<String, Object> belongCompany = remoteDeptService.getBelongCompany2(Long.valueOf(deptId));
                //隶属公司名称
                aaSpu.setCompanyName(belongCompany.get("companyName").toString());
            }
        }
        return aaSpus;
    }

    /**
     * 根据id查询耗材详情
     *
     * @param id
     * @return
     */
    @Override
    public AaSpu AaSpuById(Long id) {

        return aaSpuMapper.selectByPrimaryKey(id);
    }

    /**
     * 保存供应商报价
     *
     * @param supplierQuoteDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveQuote(BizSupplierQuoteDto supplierQuoteDto) {
        try {
            List<BizSupplierQuote> map = supplierQuoteDto.getMap();
            if (map.size() < 0) {
                throw new RuntimeException("报价信息不能为空");
            }
            for (BizSupplierQuote bizSupplierQuote : map) {
                if (bizSupplierQuote.getId() == null) {
                    bizSupplierQuote.setCreateBy(SystemUtil.getUserName());
                    bizSupplierQuote.setCreateTime(new Date());
                    bizSupplierQuoteMapper.insert(bizSupplierQuote);
                } else {
                    bizSupplierQuote.setUpdateBy(SystemUtil.getUserName());
                    bizSupplierQuote.setUpdateTime(new Date());
                    bizSupplierQuoteMapper.updateById(bizSupplierQuote);
                }
            }
            // 计算报价平均值
            BigDecimal result = map.stream().map(BizSupplierQuote::getQuote).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal avg = result.divide(new BigDecimal(map.size()), 2, BigDecimal.ROUND_HALF_UP);
            Long goodsId = map.get(0).getGoodsId();
            BizGoodsInfo bizGoodsInfo = bizGoodsInfoMapper.selectBizGoodsInfoById(goodsId);
            bizGoodsInfo.setAverageQuote(avg);
            bizGoodsInfoMapper.updateByPrimaryKeySelective(bizGoodsInfo);
            saveVariable(supplierQuoteDto.getProcInstId(), bizGoodsInfo.getPurchaseId());

        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    /**
     * 根据id删除供应商报价
     *
     * @param id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteQuote(Long id, String procInstId) {
        try {

            BizSupplierQuote bizSupplierQuote = bizSupplierQuoteMapper.selectById(id);
            // 重新计算平均值
            QueryWrapper<BizSupplierQuote> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("goods_id", bizSupplierQuote.getGoodsId());
            List<BizSupplierQuote> bizSupplierQuotes = bizSupplierQuoteMapper.selectList(queryWrapper);
            Iterator<BizSupplierQuote> iterator = bizSupplierQuotes.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().getId().equals(id)) {
                    iterator.remove();
                }
            }
            // 更新报价平均值
            BigDecimal avg = BigDecimal.ZERO;
            BigDecimal result = BigDecimal.ZERO;
            if (!bizSupplierQuotes.isEmpty()) {
                result = bizSupplierQuotes.stream().map(BizSupplierQuote::getQuote).reduce(BigDecimal.ZERO, BigDecimal::add);
                avg = result.divide(new BigDecimal(bizSupplierQuotes.size()), 2, BigDecimal.ROUND_HALF_UP);
            }

            BizGoodsInfo bizGoodsInfo = bizGoodsInfoMapper.selectBizGoodsInfoById(bizSupplierQuote.getGoodsId());
            bizGoodsInfo.setAverageQuote(avg);
            bizGoodsInfoMapper.updateByPrimaryKeySelective(bizGoodsInfo);
            bizSupplierQuoteMapper.deleteById(id);
            saveVariable(procInstId, bizGoodsInfo.getPurchaseId());
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    /**
     * 保存采购流程变量
     *
     * @param procInstId
     */
    private void saveVariable(String procInstId, Long applyId) {
        // 新增流程运行时金额变量（王香圆指定使用均价）,20220712按王香圆设计由均价改成总价
        QueryWrapper<ActRuVariable> wrapper = new QueryWrapper<>();
        wrapper.eq("NAME_", "money");
        wrapper.eq("PROC_INST_ID_", procInstId);
        ActRuVariable actRuVariable = actRuVariableMapper.selectOne(wrapper);
        BizGoodsInfo goodsInfo = new BizGoodsInfo();
        goodsInfo.setPurchaseId(applyId);
        List<BizGoodsInfo> bizGoodsInfos = selectBizGoodsInfoList(goodsInfo);

        BigDecimal result = bizGoodsInfos.stream().filter(g -> null != g.getAmount() && null != g.getAverageQuote())
                .map(good -> good.getAverageQuote().multiply(new BigDecimal(good.getAmount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long l = result.setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).longValue();
        actRuVariable.setLongValue(l);
        actRuVariable.setText(String.valueOf(l));
        // 实时变量
        actRuVariableMapper.updateById(actRuVariable);
        // 历史变量
        actRuVariableMapper.updateHiVar(Double.valueOf(l),procInstId);
        log.info("保存采购流程变量:{}", l);
    }

    /**
     * 根据物品id获取供应商报价信息
     *
     * @param id 物品id
     * @return
     */
    @Override
    public List<BizSupplierQuote> getQuote(Long id) {
        QueryWrapper<BizSupplierQuote> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("goods_id", id);
        return bizSupplierQuoteMapper.selectList(queryWrapper);
    }
}
