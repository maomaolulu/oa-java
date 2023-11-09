package com.ruoyi.activiti.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.activiti.domain.asset.*;
import com.ruoyi.activiti.domain.fiance.BizPayBack;
import com.ruoyi.activiti.mapper.AaSkuMapper;
import com.ruoyi.activiti.mapper.AaSpuMapper;
import com.ruoyi.activiti.mapper.AaTranscationMapper;
import com.ruoyi.activiti.mapper.BizClaimMapper;
import com.ruoyi.activiti.service.BizClaimService;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.util.DataScopeUtil;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.feign.RemoteUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;

/**
 * 领用申请
 *
 * @author zh
 * @date 22022-1-6
 */
@Service
@Slf4j
public class BizClaimServiceImpl implements BizClaimService {
    private final BizClaimMapper bizClaimMapper;
    private final RemoteUserService remoteUserService;
    private final RemoteDeptService remoteDeptService;
    private final AaSkuMapper aaSkuMapper;
    private final AaSpuMapper aaSpuMapper;
    private final AaTranscationMapper aaTranscationMapper;

    @Autowired
    private DataScopeUtil dataScopeUtil;

    @Autowired
    public BizClaimServiceImpl(BizClaimMapper bizClaimMapper, RemoteUserService remoteUserService, RemoteDeptService remoteDeptService, AaSkuMapper aaSkuMapper, AaSpuMapper aaSpuMapper, AaTranscationMapper aaTranscationMapper) {
        this.bizClaimMapper = bizClaimMapper;
        this.remoteUserService = remoteUserService;
        this.remoteDeptService = remoteDeptService;
        this.aaSkuMapper = aaSkuMapper;
        this.aaSpuMapper = aaSpuMapper;
        this.aaTranscationMapper = aaTranscationMapper;
    }

    /**
     * 领用申请列表
     *
     * @param bizClaim 领用申请
     * @return 领用申请
     */
    @Override
    public List<BizClaim> selectBizClaim(BizClaim bizClaim) {
        QueryWrapper<BizPayBack> mapper = new QueryWrapper<>();
        mapper.eq("cl.del_flag", 0);
        if (bizClaim.getCreateBy() == null) {
            mapper.eq("cl.status", 0);
        }
        mapper.orderByDesc("cl.id");
        //查询回款管理单条
        mapper.eq(bizClaim.getId() != null, "cl.id", bizClaim.getId());
        //查询本人数据
        mapper.eq(bizClaim.getCreateBy() != null, "cl.create_by", bizClaim.getCreateBy());
        /**申请人*/
        mapper.like(StrUtil.isNotBlank(bizClaim.getCreateByName()), "cl.create_by_name", bizClaim.getCreateByName());
        /**物品名称*/
        mapper.like(StrUtil.isNotBlank(bizClaim.getName()), "sp.name", bizClaim.getName());
        /**物品编码*/
        mapper.like(StrUtil.isNotBlank(bizClaim.getSpuSn()), "sp.spu_sn", bizClaim.getSpuSn());

        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);

        String sql = dataScopeUtil.getScopeSql(sysUser, "d", null);

        if (StrUtil.isNotBlank(sql)) {
            mapper.apply(sql);
        }
        List<BizClaim> list = bizClaimMapper.selectAll(mapper);
        for (BizClaim bizClaim1 : list) {
            Long l = bizClaimMapper.countExWarehouse(bizClaim1.getSpuId());
            Long l1 = bizClaimMapper.countWarehousing(bizClaim1.getSpuId());
            if (l == null) {
                l = 0L;
            }
            if (l1 == null) {
                l1 = 0L;
            }
            long l2 = (l1 - l);
            if (bizClaim1.getSpuType().equals("46")) {
                bizClaim1.setSpuTypeName("办公用品");
            } else if (bizClaim1.getSpuType().equals("47")) {
                bizClaim1.setSpuTypeName("办公耗材");
            } else if (bizClaim1.getSpuType().equals("48")) {
                bizClaim1.setSpuTypeName("办公设备");
            } else if (bizClaim1.getSpuType().equals("49")) {
                bizClaim1.setSpuTypeName("检测耗材");
            } else if (bizClaim1.getSpuType().equals("50")) {
                bizClaim1.setSpuTypeName("计量类耗材");
            } else if (bizClaim1.getSpuType().equals("51")) {
                bizClaim1.setSpuTypeName("标准物质");
            } else if (bizClaim1.getSpuType().equals("52")) {
                bizClaim1.setSpuTypeName("劳保用品");
            } else if (bizClaim1.getSpuType().equals("53")) {
                bizClaim1.setSpuTypeName("仪器设备");
            } else if (bizClaim1.getSpuType().equals("63")) {
                bizClaim1.setSpuTypeName("员工福利");
            } else if (bizClaim1.getSpuType().equals("64")) {
                bizClaim1.setSpuTypeName("招待物品");
            } else if (bizClaim1.getSpuType().equals("65")) {
                bizClaim1.setSpuTypeName("其他");
            }
            Map<String, Object> belongCompany = remoteDeptService.getBelongCompany2(bizClaim1.getDeptId());
            //隶属公司名称
            if (belongCompany.get("companyName") == null) continue;
            bizClaim1.setCompanyName(belongCompany.get("companyName").toString());

        }
        return list;
    }


    /**
     * 领用申请新增
     *
     * @param bizClaims 领用申请管理
     * @return 结果
     */
    @Override
    public R insertBizClaim(List<BizClaim> bizClaims) {
        try {
            for (BizClaim bizClaim : bizClaims) {
                if (bizClaim.getSkAmount() - bizClaim.getAmount() < 0) {
                    return R.error("领用物品数量不能大于剩余数量");
                }

                bizClaim.setDelFlag("0");
                bizClaim.setCreateBy(SystemUtil.getUserId());
                bizClaim.setCreateTime(new Date());
                SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
                bizClaim.setDeptId(sysUser.getDeptId());
                bizClaim.setCreateByName(sysUser.getUserName());
                bizClaimMapper.insert(bizClaim);

            }

            return R.ok("提交领用申请成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return R.error("提交领用申请失败");
        }
    }


    /**
     * 领用申请修改
     *
     * @param
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R update(List<BizClaim> bizClaims) {
        try {
            List<SkuDto> list = new ArrayList<>();
            for (BizClaim bizClaim : bizClaims) {
                Long userId = SystemUtil.getUserId();
                SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
                bizClaim.setUpdateBy(sysUser.getLoginName());
                bizClaim.setUpdateTime(new Date());
                bizClaim.setApprover(sysUser.getUserName());
                bizClaim.setApproverId(userId);
                bizClaimMapper.updateById(bizClaim);
                bizClaim = bizClaimMapper.selectById(bizClaim.getId());
                SkuDto skuDto = new SkuDto();
                skuDto.setAmount(bizClaim.getAmount());
                skuDto.setApplier(bizClaim.getCreateByName());
                skuDto.setDeptId(bizClaim.getDeptId());
                skuDto.setOperator(SystemUtil.getUserNameCn());
                skuDto.setSpuId(bizClaim.getSpuId());
                skuDto.setTransType(4);
                list.add(skuDto);
            }
            // 直接出库
            skuOut(list);
            return R.ok("操作成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return R.error("操作失败");
        }
    }


    /**
     * 删除领用申请
     *
     * @param
     * @return
     */
    @Override
    public R delete(String[] ids) {
        for (String id : Arrays.asList(ids)) {
            BizClaim bizClaim = new BizClaim();
            bizClaim.setId(Long.valueOf(id));
            bizClaim.setDelFlag("1");
            bizClaimMapper.updateById(bizClaim);
        }
        return R.ok("删除成功");
    }

    /**
     * 直接出库
     *
     * @param data
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void skuOut(List<SkuDto> data) {
        String userNameCn = SystemUtil.getUserNameCn();
        Date date = new Date();
        data.forEach(item -> {

            AaSpu aaSpu = aaSpuMapper.selectByPrimaryKey(item.getSpuId());
            final Integer oldNum = aaSpu.getStorageNum() == null ? 0 : aaSpu.getStorageNum();
            // 1、保存批次记录
            AaSku aaSku = new AaSku();
            aaSku.setAmount(item.getAmount().longValue());
            aaSku.setOperation(2);
            aaSku.setSpuId(item.getSpuId());
            aaSku.setCompanyId(aaSpu.getCompanyId());
            aaSku.setName(aaSpu.getName());
            aaSku.setOperator(userNameCn);
            aaSku.setCreateTime(date);
            aaSkuMapper.insert(aaSku);

            // 2、更新当前库存
            Integer storageNum = aaSpu.getStorageNum() - item.getAmount();
            aaSpu.setStorageNum(storageNum);
            aaSpuMapper.updateByPrimaryKeySelective(aaSpu);

            // 3、保存出入库记录
            AaTranscation aaTranscation = new AaTranscation();
            aaTranscation.setModel(aaSpu.getModel());
            aaTranscation.setName(aaSpu.getName());
            aaTranscation.setCreateTime(date);
            aaTranscation.setCreateBy(userNameCn);
            aaTranscation.setApplier(item.getApplier());
            aaTranscation.setAmount(item.getAmount());
            // 原spu_id 2021-12-28 李小龙更改为sku_id
            aaTranscation.setIdentifier(aaSku.getId());
            aaTranscation.setItemSn(aaSpu.getSpuSn());
            aaTranscation.setTransType(3);
            if (item.getTransType() != null) {
                aaTranscation.setTransType(item.getTransType());
            }
            aaTranscation.setCompanyId(aaSpu.getCompanyId());
            // 申请人部门
            aaTranscation.setDeptId(item.getDeptId());
            aaTranscation.setItemType(2);
            // 经办人
            aaTranscation.setOperator(userNameCn);
            // *100的 入库求和减去出库求和
            aaTranscation.setSpuAmount(oldNum);
            aaTranscationMapper.insertSelective(aaTranscation);
        });
    }


}
