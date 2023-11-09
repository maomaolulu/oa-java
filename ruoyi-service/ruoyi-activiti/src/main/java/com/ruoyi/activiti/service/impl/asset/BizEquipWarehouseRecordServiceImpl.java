package com.ruoyi.activiti.service.impl.asset;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.activiti.domain.asset.BizEquipWarehouseRecord;
import com.ruoyi.activiti.mapper.AssetMapper;
import com.ruoyi.activiti.mapper.asset.BizEquipWarehouseRecordMapper;
import com.ruoyi.activiti.service.asset.BizEquipWarehouseRecordService;
import com.ruoyi.system.domain.SysDept;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.feign.RemoteUserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Author yrb
 * @Date 2023/8/8 16:50
 * @Version 1.0
 * @Description 仪器设备入库记录
 */
@Service
public class BizEquipWarehouseRecordServiceImpl implements BizEquipWarehouseRecordService {
    private final BizEquipWarehouseRecordMapper bizEquipWarehouseRecordMapper;
    private final RemoteDeptService remoteDeptService;
    private final RemoteUserService remoteUserService;
    private final AssetMapper assetMapper;

    public BizEquipWarehouseRecordServiceImpl(BizEquipWarehouseRecordMapper bizEquipWarehouseRecordMapper,
                                              RemoteDeptService remoteDeptService,
                                              RemoteUserService remoteUserService,
                                              AssetMapper assetMapper) {
        this.bizEquipWarehouseRecordMapper = bizEquipWarehouseRecordMapper;
        this.remoteDeptService = remoteDeptService;
        this.remoteUserService = remoteUserService;
        this.assetMapper = assetMapper;
    }

    /**
     * 获取仪器设备入库列表
     *
     * @param bizEquipWarehouseRecord 设备入库信息
     * @return 集合
     */
    @Override
    public List<BizEquipWarehouseRecord> selectBizEquipWarehouseRecordList(BizEquipWarehouseRecord bizEquipWarehouseRecord) {
        QueryWrapper<BizEquipWarehouseRecord> queryWrapper = new QueryWrapper<>();
        // 公司
        queryWrapper.eq(bizEquipWarehouseRecord.getCompanyId() != null, "t1.company_id", bizEquipWarehouseRecord.getCompanyId());
        // 部门
        queryWrapper.eq(bizEquipWarehouseRecord.getDeptId() != null, "t1.dept_id", bizEquipWarehouseRecord.getDeptId());
        // 设备名称（物品名称）
        queryWrapper.like(StrUtil.isNotBlank(bizEquipWarehouseRecord.getGoodsName()), "t1.goods_name", bizEquipWarehouseRecord.getGoodsName());
        // 设备编号（物品编号）
        queryWrapper.eq(StrUtil.isNotBlank(bizEquipWarehouseRecord.getEquipCode()), "t2.equip_code", bizEquipWarehouseRecord.getEquipCode());
        // 设备状态
        queryWrapper.eq(bizEquipWarehouseRecord.getStatus() != null, "t2.`status`", bizEquipWarehouseRecord.getStatus());
        // 标签打印
        queryWrapper.eq(bizEquipWarehouseRecord.getPrintLabel() != null, "t2.print_label", bizEquipWarehouseRecord.getPrintLabel());
        // 图片上传
        queryWrapper.eq(bizEquipWarehouseRecord.getUploadPic() != null, "t2.upload_pic", bizEquipWarehouseRecord.getUploadPic());
        // 采购时间
        queryWrapper.between(bizEquipWarehouseRecord.getStartTime() != null && bizEquipWarehouseRecord.getEndTime() != null, "t1.purchase_date", bizEquipWarehouseRecord.getStartTime(), bizEquipWarehouseRecord.getEndTime());
        List<BizEquipWarehouseRecord> bizEquipWarehouseRecords = bizEquipWarehouseRecordMapper.selectList(queryWrapper);
        if (CollUtil.isNotEmpty(bizEquipWarehouseRecords)) {
            for (BizEquipWarehouseRecord bizEquipWarehouseRecord1 : bizEquipWarehouseRecords) {
                Long deptId = bizEquipWarehouseRecord1.getDeptId();
                SysDept sysDept = remoteDeptService.selectSysDeptByDeptId(deptId);
                bizEquipWarehouseRecord1.setDeptName(sysDept.getDeptName());
                Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(deptId);
                bizEquipWarehouseRecord1.setCompanyName(belongCompany2.get("companyName").toString());
                bizEquipWarehouseRecord1.setAssetTypeName("仪器设备");
                bizEquipWarehouseRecord1.setCategoryName("仪器设备");
                SysUser sysUser = remoteUserService.selectSysUserByUserId(bizEquipWarehouseRecord1.getKeeperId());
                bizEquipWarehouseRecord1.setKeeper(sysUser.getUserName());
            }
        }
        return bizEquipWarehouseRecords;
    }

    /**
     * 详情
     *
     * @param id 主键ID
     */
    @Override
    public BizEquipWarehouseRecord getInfo(String id) {
        return bizEquipWarehouseRecordMapper.selectById(id);
    }
}
