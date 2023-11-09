package com.ruoyi.activiti.service.asset;


import com.ruoyi.activiti.domain.asset.BizEquipWarehouseRecord;

import java.util.List;

/**
 * @Author yrb
 * @Date 2023/8/8 16:49
 * @Version 1.0
 * @Description
 */
public interface BizEquipWarehouseRecordService {

    /**
     * 仪器设备入库记录
     *
     * @param bizEquipWarehouseRecord 入库信息
     * @return 集合
     */
    List<BizEquipWarehouseRecord> selectBizEquipWarehouseRecordList(BizEquipWarehouseRecord bizEquipWarehouseRecord);

    /**
     * 详情
     *
     * @param id 主键ID
     */
    BizEquipWarehouseRecord getInfo(String id);
}
