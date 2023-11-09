package com.ruoyi.activiti.service.asset;

import com.ruoyi.activiti.domain.purchase.BizStorageManager;

import java.util.List;

/**
 * 耗材库管管理
 * @author zx
 * @date 2022/3/30 15:58
 */
public interface BizStorageManagerService {
    /**
     * 保存
     * @param storageManager
     */
    void save(BizStorageManager storageManager);

    /**
     * 查询列表
     * @param storageManager
     * @return
     */
    List<BizStorageManager> getList(BizStorageManager storageManager);
}
