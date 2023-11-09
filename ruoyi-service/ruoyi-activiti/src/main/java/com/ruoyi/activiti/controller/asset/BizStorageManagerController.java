package com.ruoyi.activiti.controller.asset;

import com.ruoyi.activiti.domain.purchase.BizStorageManager;
import com.ruoyi.activiti.service.asset.BizStorageManagerService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 耗材库管管理
 *
 * @author zx
 * @date 2022/3/30 16:53
 */
@RestController
@RequestMapping("storage_manager")
public class BizStorageManagerController extends BaseController {
    private final BizStorageManagerService storageManagerService;

    @Autowired
    public BizStorageManagerController(BizStorageManagerService storageManagerService) {
        this.storageManagerService = storageManagerService;
    }

    /**
     * 保存
     * @param storageManager
     * @return
     */
    @PostMapping("info")
    public R save(@RequestBody BizStorageManager storageManager){
        try {
            storageManagerService.save(storageManager);
            return R.ok("保存成功");
        }catch (Exception e){
            logger.error("保存失败",e);
            return R.error("保存失败");
        }
    }

    /**
     * 查询列表
     * @return
     */
    @GetMapping("info")
    public R getList(BizStorageManager storageManager){
        try {
            pageUtil();
            return resultData(storageManagerService.getList(storageManager));
        }catch (Exception e){
            logger.error("查询失败",e);
            return R.error("查询失败");
        }
    }
}
