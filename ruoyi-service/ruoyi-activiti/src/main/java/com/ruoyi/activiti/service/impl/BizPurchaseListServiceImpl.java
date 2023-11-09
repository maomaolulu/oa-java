package com.ruoyi.activiti.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.ruoyi.activiti.consts.UrlConstants;
import com.ruoyi.activiti.domain.asset.AaSpu;
import com.ruoyi.activiti.domain.dto.AssetPurchaseDto;
import com.ruoyi.activiti.domain.dto.BizPurchaseListDto;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.domain.purchase.*;
import com.ruoyi.activiti.mapper.*;
import com.ruoyi.activiti.mapper.asset.BizEquipWarehouseRecordMapper;
import com.ruoyi.activiti.service.BizPurchaseListService;
import com.ruoyi.activiti.utils.MailService;
import com.ruoyi.system.domain.SysDept;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteConfigService;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.util.DataScopeUtil;
import com.ruoyi.system.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 采购申请(新)Service业务层处理
 *
 * @author zh
 * @date 2021-12-10
 */
@Service
@Slf4j
public class BizPurchaseListServiceImpl implements BizPurchaseListService {
    private final BizPurchaseListMapper bizPurchaseListMapper;
    private final BizGoodsInfoMapper bizGoodsInfoMapper;
    private final RemoteConfigService remoteConfigService;
    private final RemoteDeptService remoteDeptService;
    private final AaSpuMapper aaSpuMapper;
    private final MailService mailService;
    private final BizGoodsRecordMapper goodsRecordMapper;
    private final BizTransferRecordMapper transferRecordMapper;
    private final BizSupplierQuoteMapper bizSupplierQuoteMapper;
    private final BizBusinessMapper bizBusinessMapper;

    @Autowired
    private DataScopeUtil dataScopeUtil;
    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    public BizPurchaseListServiceImpl(BizPurchaseListMapper bizPurchaseListMapper,
                                      RemoteUserService remoteUserService,
                                      BizGoodsInfoMapper bizGoodsInfoMapper,
                                      RemoteConfigService remoteConfigService,
                                      RemoteDeptService remoteDeptService,
                                      AaSpuMapper aaSpuMapper,
                                      MailService mailService,
                                      BizGoodsRecordMapper goodsRecordMapper,
                                      BizTransferRecordMapper transferRecordMapper,
                                      DataScopeUtil dataScopeUtil,
                                      BizSupplierQuoteMapper bizSupplierQuoteMapper,
                                      BizBusinessMapper bizBusinessMapper) {
        this.bizPurchaseListMapper = bizPurchaseListMapper;
        this.remoteUserService = remoteUserService;
        this.bizGoodsInfoMapper = bizGoodsInfoMapper;
        this.remoteConfigService = remoteConfigService;
        this.remoteDeptService = remoteDeptService;
        this.aaSpuMapper = aaSpuMapper;
        this.mailService = mailService;
        this.goodsRecordMapper = goodsRecordMapper;
        this.transferRecordMapper = transferRecordMapper;
        this.dataScopeUtil = dataScopeUtil;
        this.bizSupplierQuoteMapper = bizSupplierQuoteMapper;
        this.bizBusinessMapper = bizBusinessMapper;
    }

    @Override
    /**
     * 采购订单所有
     */
    public List<BizPurchaseListDto> selectGoodsListAll(BizPurchaseListDto dto) {
        QueryWrapper<BizPurchaseApply> bizPurchaseApplyQueryWrapper = new QueryWrapper<>();
        bizPurchaseApplyQueryWrapper.orderByDesc("good.id");
        bizPurchaseApplyQueryWrapper.eq("bu.proc_def_key", "purchase");
        bizPurchaseApplyQueryWrapper.eq("bu.del_flag", 0);
        bizPurchaseApplyQueryWrapper.eq("good.del_flag", 0);
        /**采购申请编号*/
        bizPurchaseApplyQueryWrapper.like(StrUtil.isNotBlank(dto.getPurchaseCode()), "pur.purchase_code", dto.getPurchaseCode());
        /**申请时间*/
        String createTime1 = dto.getCreateTime1();
        String createTime2 = dto.getCreateTime2();

        bizPurchaseApplyQueryWrapper.between(StrUtil.isNotBlank(createTime1) && StrUtil.isNotBlank(createTime2), "good.create_time", createTime1, createTime2);

        /**物品类型 （资产类别）*/
        bizPurchaseApplyQueryWrapper.eq(dto.getItemType() != null, "good.item_type", dto.getItemType());
        /**物品名称 */
        bizPurchaseApplyQueryWrapper.like(StrUtil.isNotBlank(dto.getName()), "good.name", dto.getName());
        /**规格型号 */
        bizPurchaseApplyQueryWrapper.like(StrUtil.isNotBlank(dto.getModel()), "good.model", dto.getModel());
        /**公司名称搜索*/
        bizPurchaseApplyQueryWrapper.like(StrUtil.isNotBlank(dto.getAncestors()), "d.ancestors", "," + dto.getAncestors() + ",");
        /**采购申请部门*/
        bizPurchaseApplyQueryWrapper.eq(dto.getDeptId() != null, "d.dept_id", dto.getDeptId());
        /**申请人*/
        bizPurchaseApplyQueryWrapper.like(StrUtil.isNotBlank(dto.getCreateBy()), "u.user_name", dto.getCreateBy());
        bizPurchaseApplyQueryWrapper.and(i -> i.eq("bu.result", 2).or().eq("bu.result", 1));
        bizPurchaseApplyQueryWrapper.and(i -> i.eq("bu.status", 2).or().eq("bu.status", 1));
        /**审批状态 (0:已通过;1：审批中) */
        bizPurchaseApplyQueryWrapper.eq(dto.getBuStatus() != null && dto.getBuStatus() == 0, "bu.result", 2);
        bizPurchaseApplyQueryWrapper.eq(dto.getBuStatus() != null && dto.getBuStatus() == 0, "bu.status", 2);
        if (dto.getBuStatus() != null && dto.getBuStatus() == 1) {
            bizPurchaseApplyQueryWrapper.and(i -> i.ne("bu.status", 2).or().ne("bu.result", 2));
        }
        /**订货状态查询*/
        Integer status = dto.getStatus();
        //用于搜索状态（0：等待采购；1：已采购；2:等待验收；3：已验收）
        if (status != null) {
            if (status == 0) {
                bizPurchaseApplyQueryWrapper.eq("good.is_purchase", 0);
            } else if (status == 1) {
                //是否采购
                bizPurchaseApplyQueryWrapper.eq("good.is_purchase", 1);
                //是否到货
                bizPurchaseApplyQueryWrapper.eq("good.is_received", 0);
            } else if (status == 2) {
                //是否到货
                bizPurchaseApplyQueryWrapper.eq("good.is_received", 1);
                //是否验收
                bizPurchaseApplyQueryWrapper.eq("good.is_acceptance", 0);
            } else if (status == 3) {
                //是否验收
                bizPurchaseApplyQueryWrapper.eq("good.is_acceptance", 1);
                //是否入库
                bizPurchaseApplyQueryWrapper.eq("good.is_storage", 0);
            } else if (status == 4) {//已入库
                //是否入库
                bizPurchaseApplyQueryWrapper.eq("good.is_storage", 1);
            }
        }

        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);

        String sql = dataScopeUtil.getScopeSql(sysUser, "d", null);

        if (StrUtil.isNotBlank(sql)) {
            bizPurchaseApplyQueryWrapper.apply(sql);
        }

        List<BizPurchaseListDto> list = bizPurchaseListMapper.selectBizPurchaseListAll(bizPurchaseApplyQueryWrapper);
        for (BizPurchaseListDto dto1 : list) {
            Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(dto1.getDeptId());
            dto1.setCompanyName(belongCompany2.get("companyName").toString());
            if (dto1.getBuStatus() == 2 && dto1.getResult() == 2) {
                dto1.setStatusAndResult("通过");
            } else {
                dto1.setStatusAndResult("处理中");
            }
            if (dto1.getIsPurchase().equals("0")) {
                //等待采购
                dto1.setGoodResult("等待采购");
            } else if (dto1.getIsReceived().equals("0")) {
                //已采购
                dto1.setGoodResult("已采购");
            } else if (dto1.getIsAcceptance().equals("0")) {
                //等待验收
                dto1.setGoodResult("等待验收");
            } else if (dto1.getIsStorage().equals("0")) {
                //已验收
                dto1.setGoodResult("已验收");
            } else {
                //已入库
                dto1.setGoodResult("已入库");
            }
        }
        return list;
    }

    @Override
    public List<BizPurchaseListDto> selectGoodsListFixed(BizPurchaseListDto dto) {
        Long userId = SystemUtil.getUserId();
        QueryWrapper<BizPurchaseApply> bizPurchaseApplyQueryWrapper = new QueryWrapper<>();
        bizPurchaseApplyQueryWrapper.like(StrUtil.isNotBlank(dto.getPurchaseCode()), "pur.purchase_code", dto.getPurchaseCode());
        bizPurchaseApplyQueryWrapper.eq("good.good_type", 0);
        bizPurchaseApplyQueryWrapper.eq("bu.user_id", userId);
        bizPurchaseApplyQueryWrapper.eq("bu.del_flag", 0);
        bizPurchaseApplyQueryWrapper.eq("bu.proc_def_key", "purchase");
        bizPurchaseApplyQueryWrapper.eq("good.old", 0);
        bizPurchaseApplyQueryWrapper.orderByDesc("bu.id");

        return bizPurchaseListMapper.selectGoodsListFixed(bizPurchaseApplyQueryWrapper);
    }

    @Override
    /**
     * 采购订货和采购计划
     */
    public List<BizPurchaseListDto> selectBizPurchaseListAll(BizPurchaseListDto dto) {
        QueryWrapper<BizPurchaseApply> bizPurchaseApplyQueryWrapper = new QueryWrapper<>();
        bizPurchaseApplyQueryWrapper.eq("bu.proc_def_key", "purchase");
        bizPurchaseApplyQueryWrapper.eq("bu.del_flag", 0);
        bizPurchaseApplyQueryWrapper.eq("good.del_flag", 0);
        bizPurchaseApplyQueryWrapper.eq("bu.result", 2);
        bizPurchaseApplyQueryWrapper.eq("bu.status", 2);
        bizPurchaseApplyQueryWrapper.and(qw -> qw.eq("good.is_invalid", 0).or().apply("good.is_invalid is null"));

        /**物品类型 */
        bizPurchaseApplyQueryWrapper.eq(dto.getItemType() != null, "good.item_type", dto.getItemType());
        /**物品名称 */
        bizPurchaseApplyQueryWrapper.like(StrUtil.isNotBlank(dto.getName()), "good.name", dto.getName());
        /**规格型号 */
        bizPurchaseApplyQueryWrapper.like(StrUtil.isNotBlank(dto.getModel()), "good.model", dto.getModel());
        /**公司名称搜索*/
        bizPurchaseApplyQueryWrapper.like(StrUtil.isNotBlank(dto.getAncestors()), "d.ancestors", "," + dto.getAncestors() + ",");
        /**采购申请编号*/
        bizPurchaseApplyQueryWrapper.like(StrUtil.isNotBlank(dto.getPurchaseCode()), "pur.purchase_code", dto.getPurchaseCode());
        /**采购申请部门*/
        bizPurchaseApplyQueryWrapper.eq(dto.getDeptId() != null, "d.dept_id", dto.getDeptId());
        /**申请人*/
        bizPurchaseApplyQueryWrapper.eq(StrUtil.isNotBlank(dto.getCreateBy()), "u.user_name", dto.getCreateBy());

        if (dto.getStatus() != null) {
            if (dto.getStatus() == 0) {
                bizPurchaseApplyQueryWrapper.orderByDesc("good.id");
                //采购清单页面 是否采购为空
                bizPurchaseApplyQueryWrapper.eq("good.is_purchase", 0);
            }
            if (dto.getStatus() == 1) {
                bizPurchaseApplyQueryWrapper.orderByDesc("good.purchase_date", "good.id");
                //采购计划页面  是否采购不为空  是否到货为空
                bizPurchaseApplyQueryWrapper.eq("good.is_purchase", 1);
                bizPurchaseApplyQueryWrapper.eq("good.is_received", 0);
            }
        }

        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);

        String sql = dataScopeUtil.getScopeSql(sysUser, "d", null);

        if (StrUtil.isNotBlank(sql)) {
            bizPurchaseApplyQueryWrapper.apply(sql);
        }
        List<BizPurchaseListDto> list = bizPurchaseListMapper.selectBizPurchaseListAll(bizPurchaseApplyQueryWrapper);
        boolean flag = StrUtil.isNotBlank(dto.getExport());
        for (BizPurchaseListDto dto1 : list) {
            Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(dto1.getDeptId());
            dto1.setCompanyName(belongCompany2.get("companyName").toString());
            if (dto1.getApplyModel() == null) {
                dto1.setApplyModel(dto1.getModel());
            }
            if (dto1.getApplyName() == null) {
                dto1.setApplyName(dto1.getName());
            }
            if (dto1.getApplyUnit() == null) {
                dto1.setApplyUnit(dto1.getUnit());
            }

            // 导出数据处理
            if (flag) {
                // 资产类别
                Integer itemType = dto1.getItemType();
                if (itemType == null) {
                    itemType = 9999;
                }
                switch (itemType) {
                    case 46:
                        dto1.setItemTypeCn("办公用品");
                        break;
                    case 47:
                        dto1.setItemTypeCn("办公耗材");
                        break;
                    case 48:
                        dto1.setItemTypeCn("办公设备");
                        break;
                    case 49:
                        dto1.setItemTypeCn("检测耗材");
                        break;
                    case 50:
                        dto1.setItemTypeCn("计量类耗材");
                        break;
                    case 51:
                        dto1.setItemTypeCn("标准物质");
                        break;
                    case 52:
                        dto1.setItemTypeCn("劳保用品");
                        break;
                    case 53:
                        dto1.setItemTypeCn("仪器设备");
                        break;
                    case 63:
                        dto1.setItemTypeCn("员工福利");
                        break;
                    case 64:
                        dto1.setItemTypeCn("招待物品");
                        break;
                    case 65:
                        dto1.setItemTypeCn("其他");
                        break;
                    case 83:
                        dto1.setItemTypeCn("体检耗材");
                        break;
                    default:
                        dto1.setItemTypeCn("未知");
                }
                // 供应商名称 备注
                QueryWrapper<BizSupplierQuote> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("goods_id", dto1.getGoodsId());
                List<BizSupplierQuote> bizSupplierQuotes = bizSupplierQuoteMapper.selectList(queryWrapper);
                if (CollUtil.isNotEmpty(bizSupplierQuotes)) {
                    List<String> supplierNames = new ArrayList<>();
                    List<String> remarks = new ArrayList<>();
                    for (BizSupplierQuote bizSupplierQuote : bizSupplierQuotes) {
                        String supplierName = bizSupplierQuote.getSupplierName();
                        if (StrUtil.isNotBlank(supplierName)) {
                            supplierNames.add(supplierName);
                        }
                        String remark = bizSupplierQuote.getRemark();
                        if (StrUtil.isNotBlank(remark)) {
                            remarks.add(remark);
                        }
                    }
                    dto1.setSupplierName(String.join("、", supplierNames));
                    dto1.setRemarkSupplier(String.join("、", remarks));
                }
            }
        }
        return list;
    }

    /**
     * 采购验收页面
     */
    @Override
    public List<BizPurchaseListDto> selectBizPurchaseCheck(BizPurchaseListDto dto) {
        QueryWrapper<BizPurchaseApply> bizPurchaseApplyQueryWrapper = new QueryWrapper<>();
        bizPurchaseApplyQueryWrapper.eq("bu.proc_def_key", "purchase");
        bizPurchaseApplyQueryWrapper.eq("bu.del_flag", 0);
        bizPurchaseApplyQueryWrapper.eq("good.del_flag", 0);
        bizPurchaseApplyQueryWrapper.eq("bu.result", 2);
        bizPurchaseApplyQueryWrapper.eq("bu.status", 2);
        bizPurchaseApplyQueryWrapper.orderByDesc("good.id");
        /**物品类型 */
        bizPurchaseApplyQueryWrapper.eq(dto.getItemType() != null, "good.item_type", dto.getItemType());
        /**物品名称 */
        bizPurchaseApplyQueryWrapper.like(StrUtil.isNotBlank(dto.getName()), "good.name", dto.getName());
        /**规格型号 */
        bizPurchaseApplyQueryWrapper.like(StrUtil.isNotBlank(dto.getModel()), "good.model", dto.getModel());
        /**公司名称搜索*/
        bizPurchaseApplyQueryWrapper.like(StrUtil.isNotBlank(dto.getAncestors()), "d.ancestors", "," + dto.getAncestors() + ",");

        // 采购验收界面
        //是否已到货（申请验收）
        bizPurchaseApplyQueryWrapper.eq("good.is_received", "1");
        //是否已验收
        bizPurchaseApplyQueryWrapper.eq("good.is_acceptance", "0");
        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
        //本人才能查看本人的申请
        bizPurchaseApplyQueryWrapper.eq("good.create_by", sysUser.getLoginName().trim());
        String sql = dataScopeUtil.getScopeSql(sysUser, "d", null);

        if (StrUtil.isNotBlank(sql)) {
            bizPurchaseApplyQueryWrapper.apply(sql);
        }

        List<BizPurchaseListDto> list = bizPurchaseListMapper.selectBizPurchaseListAll(bizPurchaseApplyQueryWrapper);
        for (BizPurchaseListDto dto1 : list
        ) {
            Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(dto1.getDeptId());
            dto1.setCompanyName(belongCompany2.get("companyName").toString());
        }
        return list;
    }

    @Override
    /**
     * 采购入库
     */
    public List<BizPurchaseListDto> selectBizPurchaseApplyList(BizPurchaseListDto dto) {
        QueryWrapper<BizPurchaseApply> bizPurchaseApplyQueryWrapper = new QueryWrapper<>();
        //判断是固定资产还是流动资产 0：固定资产 1：流动资产
        bizPurchaseApplyQueryWrapper.eq(StrUtil.isNotBlank(dto.getGoodType()), "good.good_type", dto.getGoodType());
        bizPurchaseApplyQueryWrapper.orderByDesc("good.id");
        //判断是否是老数据 0:新数据 1：老数据
        bizPurchaseApplyQueryWrapper.eq(dto.getOld() != null, "good.old", dto.getOld());

        // todo 运营2.0上线 过滤仪器设备入库信息
        // bizPurchaseApplyQueryWrapper.ne("good.item_type",53);

        bizPurchaseApplyQueryWrapper.eq("bu.proc_def_key", "purchase");
        bizPurchaseApplyQueryWrapper.eq("bu.del_flag", 0);
        bizPurchaseApplyQueryWrapper.eq("good.del_flag", 0);
        bizPurchaseApplyQueryWrapper.eq("bu.result", 2);
        bizPurchaseApplyQueryWrapper.eq("bu.status", 2);
        // 未作废、未退货
        bizPurchaseApplyQueryWrapper.and(qw -> qw.eq("good.is_invalid", 0).or().apply("good.is_invalid is null"));
        bizPurchaseApplyQueryWrapper.like(StrUtil.isNotBlank(dto.getCompanyId()), "d.ancestors", "," + dto.getCompanyId() + ",");


        /**采购申请编号*/
        bizPurchaseApplyQueryWrapper.like(StrUtil.isNotBlank(dto.getPurchaseCode()), "pur.purchase_code", dto.getPurchaseCode());
        /**采购申请部门*/
        bizPurchaseApplyQueryWrapper.eq(dto.getDeptId() != null, "d.dept_id", dto.getDeptId());
        /**申请人*/
        bizPurchaseApplyQueryWrapper.eq(StrUtil.isNotBlank(dto.getCreateBy()), "u.user_name", dto.getCreateBy());
        /**申请时间*/
        String createTime1 = dto.getCreateTime1();
        String createTime2 = dto.getCreateTime2();

        bizPurchaseApplyQueryWrapper.between(StrUtil.isNotBlank(createTime1) && StrUtil.isNotBlank(createTime2), "good.create_time", createTime1, createTime2);

        if (StrUtil.isBlank(dto.getIsTransfer())) {
            bizPurchaseApplyQueryWrapper.gt("good.warehousing_amount", 0);
            bizPurchaseApplyQueryWrapper.eq("good.is_storage", "0");
            bizPurchaseApplyQueryWrapper.eq("good.is_acceptance", "1");
        } else {
            bizPurchaseApplyQueryWrapper.eq("aa.is_transfer", 0);
            bizPurchaseApplyQueryWrapper.eq("good.is_transfer", "0");
            // 过滤保管人为空的
            bizPurchaseApplyQueryWrapper.and(qw -> qw.apply("aa.keeper is not null or aa.keeper <> ''"));
        }


        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);

        String sql = dataScopeUtil.getScopeSql(sysUser, "d", null);

        if (StrUtil.isNotBlank(sql)) {
            bizPurchaseApplyQueryWrapper.apply(sql);
        }
        List<BizPurchaseListDto> bizPurchaseListDtos;
        if (StrUtil.isBlank(dto.getIsTransfer())) {
            bizPurchaseListDtos = bizPurchaseListMapper.selectBizPurchaseListAll(bizPurchaseApplyQueryWrapper);
        } else {
            bizPurchaseListDtos = bizPurchaseListMapper.selectBizPurchaseListAllTransfer(bizPurchaseApplyQueryWrapper);
        }

        if (bizPurchaseListDtos != null && bizPurchaseListDtos.size() > 0) {
            for (BizPurchaseListDto bizPurchaseListDto : bizPurchaseListDtos) {
                // 补充公司名称
                if (bizPurchaseListDto.getDeptId() != null) {
                    String companyName = remoteDeptService.getBelongCompany2(bizPurchaseListDto.getDeptId()).get("companyName").toString();
                    bizPurchaseListDto.setCompanyName(companyName);
                }
                if (bizPurchaseListDto.getSpuId() != null) {
                    AaSpu aaSpu = aaSpuMapper.selectByPrimaryKey(bizPurchaseListDto.getSpuId());
                    bizPurchaseListDto.setAaSpu(aaSpu);
                }
                if (bizPurchaseListDto.getApplyModel() == null) {
                    bizPurchaseListDto.setApplyModel(bizPurchaseListDto.getModel());
                }
                if (bizPurchaseListDto.getApplyName() == null) {
                    bizPurchaseListDto.setApplyName(bizPurchaseListDto.getName());
                }
                if (bizPurchaseListDto.getApplyUnit() == null) {
                    bizPurchaseListDto.setApplyUnit(bizPurchaseListDto.getUnit());
                }
            }
        }
        return bizPurchaseListDtos;
    }

    /**
     * 去采购页面 修改isPurchase是否采购为是
     */
    @Override
    public BizGoodsInfo updateBizGoodsInfo(BizGoodsInfo bizGoodsInfo) {

        bizGoodsInfo.setWarehousingAmount(bizGoodsInfo.getActualAmount());
        bizGoodsInfo.setPurchaseDate(new Date());
        bizGoodsInfo.setPayer(SystemUtil.getUserNameCn());
        bizGoodsInfo.setPayTime(new Date());
        bizGoodsInfoMapper.updateByPrimaryKeySelective(bizGoodsInfo);
        saveGoodsRecord(bizGoodsInfo, "已下单");
        return bizGoodsInfo;
    }

    /**
     * 保存采购记录
     *
     * @param bizGoodsInfo
     */
    private void saveGoodsRecord(BizGoodsInfo bizGoodsInfo, String link) {
        QueryWrapper<BizGoodsRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("goods_id", bizGoodsInfo.getId());
        wrapper.like("link", "已移交");
        Integer integer = goodsRecordMapper.selectCount(wrapper);
        BizGoodsInfo bizGoodsInfo1 = bizGoodsInfoMapper.selectByPrimaryKey(bizGoodsInfo.getId());
        if (integer == 0) {
            if ("已移交".equals(link)) {
                link = "（1/" + bizGoodsInfo1.getActualAmount() + "）已移交";
                bizGoodsInfo1.setTransferNum(1);
            }
            BizGoodsRecord goodsRecord = new BizGoodsRecord();
            goodsRecord.setGoodsId(bizGoodsInfo.getId());
            goodsRecord.setLink(link);
            goodsRecord.setUserName(remoteUserService.selectSysUserByUserId(SystemUtil.getUserId()).getUserName());
            goodsRecord.setDelFlag("0");
            goodsRecord.setCreateBy(SystemUtil.getUserName());
            goodsRecord.setCreateTime(new Date());
            goodsRecordMapper.insert(goodsRecord);
        } else {

            Integer transferNum = bizGoodsInfo1.getTransferNum() == null ? 0 : bizGoodsInfo1.getTransferNum();

            BizGoodsRecord bizGoodsRecord = goodsRecordMapper.selectOne(wrapper);
            bizGoodsInfo1.setTransferNum(transferNum + 1);
            if (bizGoodsInfo1.getTransferNum().equals(bizGoodsInfo1.getActualAmount())) {
                bizGoodsInfo1.setIsTransfer("1");
            }
            bizGoodsRecord.setLink("（" + (transferNum + 1) + "/" + bizGoodsInfo.getActualAmount() + "）已移交");
            bizGoodsRecord.setUserName(remoteUserService.selectSysUserByUserId(SystemUtil.getUserId()).getUserName());
            bizGoodsRecord.setUpdateBy(SystemUtil.getUserName());
            bizGoodsRecord.setUpdateTime(new Date());
            goodsRecordMapper.updateById(bizGoodsRecord);
        }
        bizGoodsInfoMapper.updateByPrimaryKeySelective(bizGoodsInfo1);
    }

    /**
     * 申请验收 修改isReceived是否到货为是（提醒采购人验收）、采购入库
     */
    @Override
    public void updateBizGoodsInfoList(List<BizGoodsInfo> bizGoodsInfoList) {
        for (BizGoodsInfo bizGoodsInfo : bizGoodsInfoList) {
            // 申请验收，不满足则是修改入库字段
            if (bizGoodsInfo.getIsReceived() != null && bizGoodsInfo.getIsReceived().equals("1")) {
                bizGoodsInfo.setArrivalDate(new Date());
//                bizGoodsInfo.setWarehousingAmount(bizGoodsInfo.getActualAmount());
                BizGoodsInfo bizGoodsInfo1 = bizGoodsInfoMapper.selectBizGoodsInfoById(bizGoodsInfo.getId());
                //申请
                String createBy = bizGoodsInfo1.getCreateBy();
                //名称
                String name = bizGoodsInfo1.getName();
                //规格型号
                String model = bizGoodsInfo1.getModel();

                SysUser sysUser = remoteUserService.selectSysUserByUsername(createBy);
                SysUser sysUser2 = remoteUserService.selectSysUserByUsername(SystemUtil.getUserName());

                String txt = "<body>" +
                        "<p>" +
                        "您的申购的" + name + "(" + model + ")由采购" + sysUser2.getUserName() + "申请验收了，请及时处理。" +
                        "</p>" +
                        "<br><br><h4 style='color:#aaaaaa;'><strong>提示：更多数据，请前往云管家平台查看</strong></h4></body>";
                if (StrUtil.isBlank(sysUser.getEmail())) {
                    continue;
                }
                mailService.send(txt, "采购验收", sysUser.getEmail(), sysUser2.getUserName(), sysUser.getCid());

                saveGoodsRecord(bizGoodsInfo, "已到货");
            }
            if (bizGoodsInfo.getIsStorage() != null && bizGoodsInfo.getIsStorage().equals("1")) {

                // 流动资产不需要移交
                if (!bizGoodsInfo.getGoodType().equals("1")) {
                    bizGoodsInfo.setIsTransfer("0");
                }
                saveGoodsRecord(bizGoodsInfo, "已入库");
            }
            // 仪器设备添加到货时间
            BizGoodsInfo bizGoodsInfo1 = bizGoodsInfoMapper.selectBizGoodsInfoById(bizGoodsInfo.getId());
            if ("53".equals(bizGoodsInfo1.getItemType())) {
                bizGoodsInfo.setArrivalDate(new Date());
            }
            bizGoodsInfoMapper.updateByPrimaryKeySelective(bizGoodsInfo);
        }
    }

    /**
     * 申请人申请验收（提醒库管入库）
     */
    @Override
    public void updateBizGoodsInfoList2(List<BizGoodsInfo> bizGoodsInfoList) {
        for (BizGoodsInfo bizGoodsInfo : bizGoodsInfoList) {
//                bizGoodsInfo.setWarehousingAmount(bizGoodsInfo.getActualAmount());
            BizGoodsInfo bizGoodsInfo1 = bizGoodsInfoMapper.selectBizGoodsInfoById(bizGoodsInfo.getId());
            //名称
            String name = bizGoodsInfo1.getName();
            //规格型号
            String model = bizGoodsInfo1.getModel();
            //申购数量
            Integer amount = bizGoodsInfo1.getAmount();
            // 仪器设备推送到运营2.0
            if ("53".equals(bizGoodsInfo1.getItemType())) {
//                BizEquipWarehouseRecord bizEquipPurchaseRecord = new BizEquipWarehouseRecord();
//                bizEquipPurchaseRecord.setGoodsName(name);
//                bizEquipPurchaseRecord.setModel(model);
//                bizEquipPurchaseRecord.setAmount(amount);
                // 采购时间
                Date purchaseDate = bizGoodsInfo1.getPurchaseDate();
//                bizEquipPurchaseRecord.setPurchaseDate(purchaseDate);
                Map<String, Object> paramMap = Maps.newHashMap();
                paramMap.put("goodsName",name);
                paramMap.put("model",model);
                paramMap.put("actualAmount",amount);
                paramMap.put("unit",bizGoodsInfo1.getUnit());
                QueryWrapper<BizPurchaseApply> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("id", bizGoodsInfo1.getPurchaseId());
                BizPurchaseApply bizPurchaseApply = bizPurchaseListMapper.selectOne(queryWrapper);
                // 申请单号
                String purchaseCode = bizPurchaseApply.getPurchaseCode();
//                bizEquipPurchaseRecord.setPurchaseCode(purchaseCode);
                paramMap.put("purchaseCode", purchaseCode);
                // 申请信息
                BizBusiness bizBusiness = new BizBusiness();
                bizBusiness.setApplyCode(purchaseCode);
                BizBusiness bizBusiness1 = bizBusinessMapper.selectOne(bizBusiness);
                // 申请人
                Long userId = bizBusiness1.getUserId();
//                bizEquipPurchaseRecord.setApplyUserId(userId);
                paramMap.put("userId", userId);
                String applier = bizBusiness1.getApplyer();
                paramMap.put("applier", applier);
                // 申请公司
                String companyName = bizBusiness1.getCompanyName();
                paramMap.put("companyName", companyName);
                Long companyId = bizBusiness1.getCompanyId();
//                bizEquipPurchaseRecord.setCompanyId(companyId);
                // 申请部门
                String deptName = bizBusiness1.getDeptName();
                paramMap.put("deptName", deptName);
                Long deptId = bizBusiness1.getDeptId();
//                bizEquipPurchaseRecord.setDeptId(deptId);
                // 采购人
                String purchaser = bizGoodsInfo1.getPurchaser();
//                bizEquipPurchaseRecord.setPurchaser(purchaser);
                paramMap.put("payer", purchaser);
                // 采购单价
                BigDecimal singlePrice = bizGoodsInfo1.getSinglePrice();
//                bizEquipPurchaseRecord.setSinglePrice(singlePrice);
                paramMap.put("singlePrice", singlePrice);
                // 到货时间
                Date arrivalDate = bizGoodsInfo1.getArrivalDate();
                paramMap.put("arrivedTime", arrivalDate);
                // 供应商
                String supplier = bizGoodsInfo1.getSupplier();
                paramMap.put("supplier", supplier);
                // 采购订单id
                Long purchaseId = bizGoodsInfo1.getPurchaseId();
                paramMap.put("orderId", purchaseId);
                // 推送仪器设备数据到运营2.0
                String configValue = remoteConfigService.findConfigUrl().getConfigValue();
                if ("test".equals(configValue)) {
                    HttpUtil.createPost(UrlConstants.YY2_PURCHASE_TEST).body(JSON.toJSONString(paramMap)).execute().body();
                } else {
//                    HttpUtil.createPost(UrlConstants.YY2_PURCHASE_ONLINE).body(JSON.toJSONString(paramMap)).execute().body();
                }
//                bizEquipPurchaseRecordMapper.insert(bizEquipPurchaseRecord);
            }
            // 获取库管
            SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
            Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(sysUser.getDeptId());
            List<Map<String, Object>> users = bizGoodsInfoMapper.getLibraryTube("%," + belongCompany2.get("companyId").toString() + ",%");
            String txt = "<body>" +
                    "<p>" +
                    name + "(" + model + ")已经验收合格，请尽快入库。" +
                    "</p>" +
                    "<br><br><h4 style='color:#aaaaaa;'><strong>提示：更多数据，请前往云管家平台查看</strong></h4></body>";
            for (Map<String, Object> user : users) {
                if (user.get("email") == null) {
                    continue;
                }
                if (StrUtil.isBlank(user.get("email").toString())) {
                    continue;
                }
                String cid = "";
                if (user.get("cid") != null) {
                    cid = user.get("cid").toString();
                }
                mailService.send(txt, "采购入库", user.get("email").toString(), sysUser.getUserName(), cid);
            }
            bizGoodsInfoMapper.updateByPrimaryKeySelective(bizGoodsInfo);
            saveGoodsRecord(bizGoodsInfo, "已验收");
        }
    }


    @Override
    public BizPurchaseApply selectByGoodId(Long goodId) {

        return bizPurchaseListMapper.selectByGoodsId(goodId);
    }

    /**
     * 采购计划修改
     */
    @Override
    public void updateBizGoodsInfoPlan(BizGoodsInfo bizGoodsInfo) {
        bizGoodsInfo.setWarehousingAmount(bizGoodsInfo.getActualAmount());
        bizGoodsInfoMapper.updateByPrimaryKeySelective(bizGoodsInfo);

    }

    /**
     * 资产入库（未移交）
     *
     * @param id
     */
    @Override
    public void storageNoTransfer(Long id) {
        BizGoodsInfo bizGoodsInfo = bizGoodsInfoMapper.selectByPrimaryKey(id);
        // 流动资产不需要移交
        if (!bizGoodsInfo.getGoodType().equals("1")) {
            bizGoodsInfo.setIsTransfer("0");
        }
        bizGoodsInfoMapper.updateByPrimaryKeySelective(bizGoodsInfo);
        saveGoodsRecord(bizGoodsInfo, "已入库");
    }

    /**
     * 查询采购流程
     *
     * @param id 采购物品id
     * @return
     */
    @Override
    public List<BizGoodsRecord> getGoodsRecord(Long id) {
        QueryWrapper<BizGoodsRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("goods_id", id);
        queryWrapper.orderByAsc("create_time");
        List<BizGoodsRecord> bizGoodsRecords = goodsRecordMapper.selectList(queryWrapper);
        for (BizGoodsRecord bizGoodsRecord : bizGoodsRecords) {
            // 移交节点添加移交目标
            if (bizGoodsRecord.getLink().contains("移交")) {
                QueryWrapper<BizTransferRecord> wrapper = new QueryWrapper<>();
                wrapper.eq("goods_id", id);
                List<String> list = new ArrayList<>();
                transferRecordMapper.selectList(wrapper).stream().forEach(u -> {
                    if (u.getIsChecked() == 1) {
                        list.add(u.getKeeper() + "(已确认 " + DateUtil.format(u.getUpdateTime(), DatePattern.NORM_DATETIME_PATTERN) + ")");
                    } else {
                        list.add(u.getKeeper() + "(未确认)");
                    }
                });
                bizGoodsRecord.setUsers(list);
            }
        }
        return bizGoodsRecords;
    }

    /**
     * 移交
     *
     * @param transferRecords 移交记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void storageTransfer(List<BizTransferRecord> transferRecords) {
        try {
            for (BizTransferRecord transferRecord : transferRecords) {
                BizGoodsInfo bizGoodsInfo = bizGoodsInfoMapper.selectByPrimaryKey(transferRecord.getGoodsId());
//                bizGoodsInfo.setIsTransfer("1");
//
//                bizGoodsInfoMapper.updateByPrimaryKeySelective(bizGoodsInfo);
                saveGoodsRecord(bizGoodsInfo, "已移交");
                // 更新固定资产信息 所属部门、责任人、保管人
                SysDept sysDept = remoteDeptService.selectSysDeptByDeptId(transferRecord.getDeptId());
                transferRecordMapper.updateAssetInfo(transferRecord.getAssetId(), transferRecord.getDeptId(), sysDept.getLeader(), transferRecord.getKeeper());

                // 保存移交记录
                transferRecord.setCreateTime(new Date());
                transferRecord.setCreateBy(SystemUtil.getUserName());
                transferRecord.setIsChecked(0);
                transferRecordMapper.insert(transferRecord);

            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }

    }

    /**
     * 查询移交记录
     *
     * @param bizTransferRecord
     * @return
     */
    @Override
    public List<BizTransferRecord> getTransferRecord(BizTransferRecord bizTransferRecord) {
        QueryWrapper<BizTransferRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(bizTransferRecord.getPurchaseCode()), "tr.purchase_code", bizTransferRecord.getPurchaseCode());
        queryWrapper.like(StrUtil.isNotBlank(bizTransferRecord.getApplyer()), "tr.applyer", bizTransferRecord.getApplyer());
        queryWrapper.eq(bizTransferRecord.getCompanyId() != null, "tr.company_id", bizTransferRecord.getCompanyId());
        queryWrapper.eq(bizTransferRecord.getDeptId() != null, "tr.dept_id", bizTransferRecord.getDeptId());
        queryWrapper.eq("tr.is_checked", 0);
        queryWrapper.eq(bizTransferRecord.getKeeperId() != null, "tr.keeper_id", bizTransferRecord.getKeeperId());
        queryWrapper.like(StrUtil.isNotBlank(bizTransferRecord.getCreateBy()), "tr.create_by", bizTransferRecord.getCreateBy());
        queryWrapper.like(StrUtil.isNotBlank(bizTransferRecord.getAssetSn()), "a.asset_sn", bizTransferRecord.getAssetSn());
        // 移交时间区间
        if (bizTransferRecord.getStartTime() != null && bizTransferRecord.getEndTime() != null) {
            queryWrapper.between("tr.create_time", bizTransferRecord.getStartTime(), bizTransferRecord.getEndTime());
        }
        queryWrapper.orderByDesc("tr.create_time");
        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
        String sql = dataScopeUtil.getScopeSql(sysUser, "d", null);

        if (StrUtil.isNotBlank(sql)) {
            queryWrapper.apply(sql);
        }
        return transferRecordMapper.getTransferRecord(queryWrapper);
    }

    /**
     * 采购物品作废
     *
     * @param id
     */
    @Override
    public void cancelGoods(Long id) {
        BizGoodsInfo bizGoodsInfo = bizGoodsInfoMapper.selectByPrimaryKey(id);
        bizGoodsInfo.setIsInvalid("1");
        bizGoodsInfoMapper.updateByPrimaryKeySelective(bizGoodsInfo);
    }

    /**
     * 入库前退货
     *
     * @param ids
     */
    @Override
    public void beforeStorageReturn(List<Long> ids) {
        for (Long id : ids) {
            BizGoodsInfo bizGoodsInfo = bizGoodsInfoMapper.selectByPrimaryKey(id);
            // 0/null 正常 1作废 2入库前退货
            bizGoodsInfo.setIsInvalid("2");
            bizGoodsInfoMapper.updateByPrimaryKeySelective(bizGoodsInfo);
        }
    }

    /**
     * 确认移交
     *
     * @param ids
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkTransfer(List<Long> ids) {
        try {
            for (Long id : ids) {

                BizTransferRecord bizTransferRecord = transferRecordMapper.selectById(id);
                bizTransferRecord.setIsChecked(1);
                bizTransferRecord.setUpdateBy(SystemUtil.getUserName());
                bizTransferRecord.setUpdateTime(new Date());
                transferRecordMapper.updateById(bizTransferRecord);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    /**
     * 设为库存
     *
     * @param assetIds
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setStorage(List<Long> assetIds) {
        try {
            for (Long assetId : assetIds) {
                transferRecordMapper.setStorage(assetId);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    /**
     * 导出资产采购数据
     *
     * @param assetPurchaseDto
     * @return
     */
    @Override
    public List<Map<String, Object>> exportAssetPurchaseExcel(AssetPurchaseDto assetPurchaseDto) {

        Long companyId = SystemUtil.getCompanyId();

        return bizPurchaseListMapper.exportAssetPurchaseExcel();
    }
}
