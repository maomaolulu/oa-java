package com.ruoyi.activiti.service.impl.asset;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.activiti.domain.purchase.BizStorageManager;
import com.ruoyi.activiti.mapper.asset.BizStorageManagerMapper;
import com.ruoyi.activiti.service.asset.BizStorageManagerService;
import com.ruoyi.system.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 耗材库管管理
 *
 * @author zx
 * @date 2022/3/30 15:58
 */
@Service
public class BizStorageManagerServiceImpl implements BizStorageManagerService {
    private final BizStorageManagerMapper storageManagerMapper;

    @Autowired
    public BizStorageManagerServiceImpl(BizStorageManagerMapper storageManagerMapper) {
        this.storageManagerMapper = storageManagerMapper;
    }

    /**
     * 保存
     *
     * @param storageManager
     */
    @Override
    public void save(BizStorageManager storageManager) {
        // 新增
        if (storageManager.getId() == null) {
            storageManager.setDelFlag("0");
            storageManager.setCreateTime(new Date());
            storageManager.setCreateBy(SystemUtil.getUserName());
            storageManagerMapper.insert(storageManager);
        } else {
            // 修改
            storageManager.setUpdateBy(SystemUtil.getUserName());
            storageManager.setUpdateTime(new Date());
            storageManagerMapper.updateById(storageManager);
        }
    }

    /**
     * 查询列表
     *
     * @param storageManager
     * @return
     */
    @Override
    public List<BizStorageManager> getList(BizStorageManager storageManager) {
        QueryWrapper<BizStorageManager> wrapper = new QueryWrapper<>();
        wrapper.eq(storageManager.getCompanyId()!=null,"company_id",storageManager.getCompanyId());
        wrapper.eq(StrUtil.isNotBlank(storageManager.getTypes()),"types",storageManager.getTypes());
        wrapper.like(StrUtil.isNotBlank(storageManager.getUserName()),"user_name",storageManager.getUserName());
        wrapper.orderByDesc("create_time");
        List<BizStorageManager> storageManagers = storageManagerMapper.selectList(wrapper);
        return storageManagers;
    }
}
