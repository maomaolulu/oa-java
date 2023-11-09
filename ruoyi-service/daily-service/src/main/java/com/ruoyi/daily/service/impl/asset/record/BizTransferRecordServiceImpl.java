package com.ruoyi.daily.service.impl.asset.record;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.daily.domain.asset.Asset;
import com.ruoyi.daily.domain.asset.record.BizTransferRecord;
import com.ruoyi.daily.domain.asset.record.dto.AssetDTO;
import com.ruoyi.daily.domain.asset.record.dto.AssetSelectDTO;
import com.ruoyi.daily.domain.asset.record.dto.AssetStockDTO;
import com.ruoyi.daily.domain.asset.record.vo.BizTransferRecordVO;
import com.ruoyi.daily.mapper.asset.AssetMapper;
import com.ruoyi.daily.mapper.asset.record.BizTransferRecordMapper;
import com.ruoyi.daily.service.asset.record.BizTransferRecordService;
import com.ruoyi.system.domain.SysDept;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by WuYang on 2022/8/22 17:36
 */
@Service
@Slf4j
public class BizTransferRecordServiceImpl implements BizTransferRecordService {
    private  final RemoteDeptService deptService;
    private final BizTransferRecordMapper bizTransferRecordMapper;
    private final AssetMapper assetMapper;


    @Autowired
    public BizTransferRecordServiceImpl(RemoteDeptService deptService, BizTransferRecordMapper bizTransferRecordMapper, AssetMapper assetMapper) {
        this.deptService = deptService;
        this.bizTransferRecordMapper = bizTransferRecordMapper;
        this.assetMapper = assetMapper;
    }

    /**
     * 资产转移
     * 1）固定资产以下信息发生变化：
     *① “责任部门→移至部门”
     *② “责任人→责任部门的部门负责人”
     * ③ “保管人→移至保管人”
     * 2）在“移交记录”（6.2）页面增加一条数据。
     */
    @Transactional
    @Override
    public void assetTransfer(AssetDTO assetDTO) {
        // 修改改资产的部门 以及部门责任人 (部门表 leader) 以及保管人
        SysDept sysDept = deptService.selectSysDeptByDeptId(assetDTO.getDepartmentId());
        if (sysDept == null) {
            log.error("该部门id:{}不存在",assetDTO.getDepartmentId());
            throw new RuntimeException("部门id不存在");
        }
        // leader
        String leader = sysDept.getLeader();
        // 部门
        String deptName = sysDept.getDeptName();
        Asset asset = new Asset();
        asset.setId(assetDTO.getId());
        asset.setKeeper(assetDTO.getToKeeper());
        asset.setDeptName(deptName);
        asset.setDeptId(assetDTO.getDepartmentId());
        asset.setCharger(leader);
        asset.setState(1);


        // 添加记录 物品状态已用 ；类型资产移交；移交状态通过
        BizTransferRecord bizTransferRecord = new BizTransferRecord();
        Asset assetEntity = assetMapper.selectById(assetDTO.getId());
        if (assetEntity == null) {
            log.error("该资产id:{}不存在",assetDTO.getId());
            throw new RuntimeException("该资产id不存在");
        }

        // 拷贝属性
        BeanUtils.copyProperties(assetEntity,bizTransferRecord);
        // 设置物品状态
        bizTransferRecord.setIsChecked(1);
        bizTransferRecord.setOriginalState(assetEntity.getState());
        bizTransferRecord.setCurrentState(1);
        bizTransferRecord.setOldDeptId(assetEntity.getDeptId());
        bizTransferRecord.setType(1);
        bizTransferRecord.setDeptName(sysDept.getDeptName());
        bizTransferRecord.setKeeper(assetDTO.getToKeeper());
        bizTransferRecord.setDeptId(assetDTO.getDepartmentId());
        bizTransferRecord.setAssetId(asset.getId());
        bizTransferRecord.setOldKeeper(assetDTO.getOldKeeper());
        bizTransferRecord.setCreateTime(LocalDateTime.now());
        SysDept oldDept = deptService.selectSysDeptByDeptId(assetEntity.getDeptId());
        if (oldDept == null) {
            log.error("该部门id:{}不存在",assetEntity.getDeptId());
            throw new RuntimeException("部门id不存在");
        }
        bizTransferRecord.setOldDept(oldDept.getDeptName());
        bizTransferRecord.setHandler(SystemUtil.getUserNameCn());



        bizTransferRecordMapper.insert(bizTransferRecord);
        assetMapper.updateById(asset);

    }
    /**
     * 设为库存
     * 1）固定资产以下信息发生变化：
     * ① “物品状态→库存”
     *  ② “责任部门→操作人所在部门”
     *  ③ “责任人→责任部门的部门负责人”
     *  ④ “保管人→操作人”
     （2）“资产移交—移交记录”页面增加一条数据
     */
    @Transactional
    @Override
    public void toStock(AssetStockDTO assetStockDTO) {
        Long userDeptId = SystemUtil.getDeptId();

        if (userDeptId == null) {
            log.error("当前操作人部门为空");
            throw  new RuntimeException("当前操作人部门为空");
        }
        String userName = SystemUtil.getUserNameCn();
        Asset asset = assetMapper.selectById(assetStockDTO.getId());
        if (asset == null) {
            log.error("该资产id:{}不存在",assetStockDTO.getId());
            throw new RuntimeException("该资产id不存在");
        }
        // 原物品状态
        Integer originState = asset.getState();
        asset.setId(assetStockDTO.getId());
        // 设置库存状态
        asset.setState(10);
        // 设置部门Id
        asset.setDeptId(userDeptId);
        // 设置保管人
        asset.setKeeper(userName);
        // 设置leader和部门
        SysDept sysDept = deptService.selectSysDeptByDeptId(userDeptId);
        if (sysDept == null) {
            log.error("该部门id:{}不存在",userDeptId);
            throw new RuntimeException("部门id不存在");
        }
        String leader = sysDept.getLeader();
        String deptName = sysDept.getDeptName();
        asset.setCharger(leader);
        asset.setDeptName(deptName);
        assetMapper.updateById(asset);

        // 添加记录 物品状态已用 ；类型资产移交；移交状态通过
        BizTransferRecord bizTransferRecord = new BizTransferRecord();
        // 拷贝属性
        BeanUtils.copyProperties(asset,bizTransferRecord);
        // 设置物品状态
        bizTransferRecord.setIsChecked(1);
        bizTransferRecord.setOriginalState(originState);
        bizTransferRecord.setCurrentState(10);
        bizTransferRecord.setDeptId(userDeptId);
        bizTransferRecord.setType(1);
        bizTransferRecord.setKeeper(SystemUtil.getUserNameCn());
        bizTransferRecord.setHandler(userName);
        bizTransferRecord.setAssetId(asset.getId());
        bizTransferRecord.setCreateTime(LocalDateTime.now());
        // 原来保管
        Long oldDeptId = assetStockDTO.getOldDept();
        SysDept oldDept = deptService.selectSysDeptByDeptId(oldDeptId);
        bizTransferRecord.setOldDept(oldDept.getDeptName());
        bizTransferRecord.setOldKeeper(assetStockDTO.getKeeper());

        bizTransferRecordMapper.insert(bizTransferRecord);
        System.out.println("666");


    }

    @Override
    public List<BizTransferRecordVO> getList(AssetSelectDTO dto) {
        // 查询语句
        QueryWrapper<BizTransferRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(dto.getCompanyName()), "company.dept_name", dto.getCompanyName());
        queryWrapper.like(StrUtil.isNotBlank(dto.getAssetSn()), "a.asset_sn",dto.getAssetSn());
        queryWrapper.like(StrUtil.isNotBlank(dto.getName()), "good.name",dto.getName());
        queryWrapper.like(StrUtil.isNotBlank(dto.getOldKeeper()), "tr.old_keeper",dto.getOldKeeper());
        queryWrapper.eq(StrUtil.isNotBlank(dto.getKeeper()), "tr.keeper",dto.getKeeper());
        queryWrapper.eq(dto.getType() != null, "tr.type", dto.getType());
        queryWrapper.eq(dto.getIsCheck() != null, "tr.is_checked", dto.getIsCheck());
        queryWrapper.like(StrUtil.isNotBlank(dto.getCategoryName()),"ac.category_name", dto.getCategoryName());

        // 移交时间区间
        if (dto.getStartTime() != null && dto.getEndTime() != null) {
            queryWrapper.between("tr.create_time", dto.getStartTime(), dto.getEndTime());
        }
        queryWrapper.orderByDesc("tr.create_time");


        return bizTransferRecordMapper.transfer(queryWrapper);
    }

    /**
     * 修改assert companyid
     */
    @Override
    public void repairAsset() {
        // 查询出所有的asset
        List<Asset> assets = assetMapper.selectList(null);
        // todo id 问题
        // 遍历 去除companyId 去和dept 联表
        for (Asset asset : assets) {
            asset.setLabelCode(asset.getId().toString());
            Long companyId = asset.getCompanyId();
            SysDept sysDept = deptService.selectSysDeptByDeptId(companyId);
            Long parentId = sysDept.getParentId();
            if (parentId == 0){
                continue;
            }
            while ( parentId!= 0) {

                SysDept temp = deptService.selectSysDeptByDeptId(sysDept.getParentId());
                parentId = temp.getParentId();
                companyId = temp.getDeptId();

            }
            assetMapper.updateCompanyId(asset.getLabelCode(),companyId);
        }
    }



}
