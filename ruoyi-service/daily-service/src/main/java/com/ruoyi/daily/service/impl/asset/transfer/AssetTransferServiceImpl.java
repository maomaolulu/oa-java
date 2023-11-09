package com.ruoyi.daily.service.impl.asset.transfer;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.daily.domain.asset.Asset;
import com.ruoyi.daily.domain.asset.record.BizTransferRecord;
import com.ruoyi.daily.domain.asset.transfer.PurchaseTransferDTO;
import com.ruoyi.daily.domain.asset.transfer.PurchaseTransferVO;
import com.ruoyi.daily.mapper.asset.AssetMapper;
import com.ruoyi.daily.mapper.asset.record.BizTransferRecordMapper;
import com.ruoyi.daily.service.asset.transfer.AssetTransferService;
import com.ruoyi.system.domain.SysDept;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wuYang
 * @date 2022/9/9 16:44
 */
@Slf4j
@Service
public class AssetTransferServiceImpl implements AssetTransferService {

    @Resource
    BizTransferRecordMapper recordMapper;
    @Resource
    AssetMapper assetMapper;
    @Resource
    RemoteDeptService remoteDeptService;




    /**
     * 1. ------------转移采购资产 ----------------
     * 2. 通过assetId 设置责任人和责任部门 并记录原保管人和原责任部门
     * 3. 生成待确认记录 {@link BizTransferRecord}
     * 4. 发送到该保管人账号 ---- 通过 BizTransferRecord id 关联查出
     */
    @Transactional
    @Override
    public void purchaseTransfer(PurchaseTransferDTO dto) {
        Long assetId = dto.getId();
        Asset asset = assetMapper.selectById(assetId);
        if (asset==null){
            throw new RuntimeException("资产不存在");
        }
        asset.setId(dto.getId());
        // 原保管者
        String keeper = asset.getKeeper();
        // 原部门id
        Long oldDeptId = asset.getDeptId();
        asset.setKeeper(dto.getToKeeper());
        SysDept oldDept = remoteDeptService.selectSysDeptByDeptId(oldDeptId);
        // 当前部门id
        Long departmentId = dto.getDepartmentId();
        SysDept sysDept = remoteDeptService.selectSysDeptByDeptId(departmentId);
        asset.setCharger(sysDept.getLeader());
        asset.setDeptId(departmentId);
        asset.setIsTransfer(1);
        assetMapper.updateById(asset);
//        recordMapper.updateGoodInfoById(dto.getGoodsId(),1);

        BizTransferRecord bizTransferRecord = new BizTransferRecord();
        bizTransferRecord.setApplyer(dto.getApplyer());
        bizTransferRecord.setPurchaseCode(dto.getPurchaseCode());
        // 待确认状态
        bizTransferRecord.setIsChecked(0);
        BeanUtils.copyProperties(asset,bizTransferRecord);
        bizTransferRecord.setOriginalState(10);
        bizTransferRecord.setCurrentState(1);
        bizTransferRecord.setDeptId(departmentId);
        bizTransferRecord.setOldDept(oldDept.getDeptName());
        bizTransferRecord.setOldDeptId(oldDeptId);
        // keepId
        bizTransferRecord.setKeeperId(dto.getKeeperId());
        // 采购移交
        bizTransferRecord.setType(0);
        bizTransferRecord.setKeeper(dto.getToKeeper());
        bizTransferRecord.setDeptId(departmentId);
        bizTransferRecord.setAssetId(dto.getId());
        bizTransferRecord.setOldKeeper(keeper);
        bizTransferRecord.setGoodsId(dto.getGoodsId());
        bizTransferRecord.setCreateTime(LocalDateTime.now());
        bizTransferRecord.setHandler(SystemUtil.getUserNameCn());
        recordMapper.insert(bizTransferRecord);


    }

    /**
     * -------------- 本用户确认采购移交 ------------
     * 1. 用户确认完 isChecked 为 True 列表只展示未确认的
     */
    @Override
    public  void checkPurchase(Long recordId) {
        BizTransferRecord bizTransferRecord = new BizTransferRecord();
        bizTransferRecord.setId(recordId);
        bizTransferRecord.setIsChecked(1);
        recordMapper.updateById(bizTransferRecord);

    }

    /**
     * ------------- 展示用户为确认的转移待确认记录 -----------
     * 1. 通过userId = keepId 联表 展示 isChecked = false 的
     */
    @Override
    public  List<PurchaseTransferVO> getUncheckPurchaseListByUser( String purchaseCode, String createBy, String assetSn, String name,LocalDateTime start,LocalDateTime end) {
        Long userId = SystemUtil.getUserId();
        QueryWrapper<BizTransferRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("br.keeper_id", userId)
                    .eq("br.is_checked", 0)
                .eq("a.is_transfer",1)
                .like(StringUtils.isNotBlank(purchaseCode),"pa.purchase_code", purchaseCode)
                .like(StringUtils.isNotBlank(createBy),"pa.create_by", createBy)
                .like(StringUtils.isNotBlank(assetSn),"a.asset_sn", assetSn)
                .like(StringUtils.isNotBlank(name),"a.name", name)
                .orderByDesc("br.create_time");
        // 移交时间区间
        if (start!= null && end != null) {
            queryWrapper.between("g.purchase_date", start, end);
        }

        return recordMapper.getUserUnCheckedList(queryWrapper);
    }

    /**
     * ------------- 展示所有未确认的转移待确认记录 -----------
     * 1. 联表 展示 isChecked = false 的
     */
    @Override
    public void getUncheckPurchaseList() {

    }

    /**
     * 驳回请求
     * 直接转入待移交固定资产
     * 确认过
     * @param id
     */
    @Transactional
    @Override
    public void rejectCheck(Long id) {
        BizTransferRecord bizTransferRecord = recordMapper.selectById(id);
        if (bizTransferRecord == null) {
            log.error("记录为空");
            throw new RuntimeException("[用户拒绝请求]记录为空 id："+id);
        }
        Asset asset = new Asset();
        asset.setId(bizTransferRecord.getAssetId());
        asset.setState(bizTransferRecord.getOriginalState());
        asset.setIsTransfer(0);
        asset.setKeeper(bizTransferRecord.getOldKeeper());
        asset.setDeptId(bizTransferRecord.getOldDeptId());
        assetMapper.updateById(asset);
        bizTransferRecord.setCurrentState(bizTransferRecord.getOriginalState());
        bizTransferRecord.setIsChecked(2);
        recordMapper.updateById(bizTransferRecord);
//        recordMapper.updateGoodInfoById(bizTransferRecord.getGoodsId(),0);


    }
}
