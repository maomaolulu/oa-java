package com.ruoyi.activiti.controller.asset;

import com.ruoyi.activiti.domain.asset.BizEquipWarehouseRecord;
import com.ruoyi.activiti.service.asset.BizEquipWarehouseRecordService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author yrb
 * @Date 2023/8/8 17:40
 * @Version 1.0
 * @Description 仪器设备入库记录
 */
@RestController
@RequestMapping("/equip")
public class BizEquipWarehouseRecordController extends BaseController {
    private final BizEquipWarehouseRecordService bizEquipWarehouseRecordService;

    public BizEquipWarehouseRecordController(BizEquipWarehouseRecordService bizEquipWarehouseRecordService){
        this.bizEquipWarehouseRecordService = bizEquipWarehouseRecordService;
    }

    /**
     * 查询仪器设备入库列表
     *
     * @param bizEquipPurchaseRecord 仪器设备信息
     * @return 集合
     */
    @ApiOperation("查询列表")
    @GetMapping("/list")
    public R list(BizEquipWarehouseRecord bizEquipPurchaseRecord){
        try {
            startPage();
            return result(bizEquipWarehouseRecordService.selectBizEquipWarehouseRecordList(bizEquipPurchaseRecord));
        } catch (Exception e) {
            logger.error("获取仪器设备入库列表失败=======", e);
            return R.error("获取仪器设备入库列表失败");
        }
    }

    /**
     * 详情
     *
     * @param bizEquipPurchaseRecord 仪器设备信息
     * @return result
     */
    @ApiOperation("详情")
    @GetMapping("/getInfo")
    public R getInfo(BizEquipWarehouseRecord bizEquipPurchaseRecord){
        return R.data(bizEquipWarehouseRecordService.getInfo(String.valueOf(bizEquipPurchaseRecord.getId())));
    }
}
