package com.ruoyi.activiti.service.asset;

import com.ruoyi.activiti.domain.asset.BizConsumablesInventory;

/**
 * 耗材盘点
 * @author zx
 * @date 2022/3/29 14:12
 */
public interface BizConsumablesInventoryService {
    /**
     * 新增申请
     * @param consumablesInventory
     * @return
     */
    int insert(BizConsumablesInventory consumablesInventory);

    /**
     * 获取详情
     * @param id
     * @return
     */
    BizConsumablesInventory selectById(Long id);
}
