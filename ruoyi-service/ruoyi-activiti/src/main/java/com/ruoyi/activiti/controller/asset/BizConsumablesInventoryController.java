package com.ruoyi.activiti.controller.asset;

import cn.hutool.core.date.DateUtil;
import com.ruoyi.activiti.domain.asset.BizConsumablesInventory;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.service.asset.BizConsumablesInventoryService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.system.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 耗材盘点
 * @author zx
 * @date 2022/3/29 14:14
 */
@RestController
@RequestMapping("consumables")
public class BizConsumablesInventoryController extends BaseController {
    private final BizConsumablesInventoryService consumablesInventoryService;
    private final IBizBusinessService businessService;
    @Autowired
    public BizConsumablesInventoryController(BizConsumablesInventoryService consumablesInventoryService, IBizBusinessService businessService) {
        this.consumablesInventoryService = consumablesInventoryService;
        this.businessService = businessService;
    }

    /**
     * 获取耗材盘点审批编号
     *
     * @return
     */
    @GetMapping("code")
    public R getCode() {
        Date date = new Date();
        String timestamp = String.valueOf(date.getTime());
        String today = DateUtil.today();
        today = today.replace("-", "");
        return R.data("HC" + today + timestamp);
    }


    /**
     * 新增申请
     */
    @OperLog(title = "耗材盘点审批", businessType = BusinessType.UPDATE)
    @PostMapping("save")
    public R addSave(@RequestBody BizConsumablesInventory consumablesInventory) {
        try {
            consumablesInventory.setCreateTime(new Date());
            consumablesInventory.setCreateBy(SystemUtil.getUserId().toString());

            int insert = consumablesInventoryService.insert(consumablesInventory);
            if (insert == 0) {
                return R.error("提交申请失败");
            }
            if (insert == 2) {
                return R.error("请先选择物品");
            }
            return R.ok("提交申请成功");
        } catch (Exception e) {
            logger.error("提交申请失败", e);
            return R.error("提交申请失败");
        }

    }

    /**
     * 获取详情
     *
     * @param businessKey
     * @return
     */
    @GetMapping("biz/{businessKey}")
    public R biz2(@PathVariable("businessKey") String businessKey) {
        try {
            BizBusiness business = businessService.selectBizBusinessById(businessKey);
            if (null == business) {
                return R.error("获取流程信息失败");
            }
            BizConsumablesInventory consumablesInventory = consumablesInventoryService.selectById(Long.valueOf(business.getTableId()));
            consumablesInventory.setTitle(business.getTitle());
            return R.data(consumablesInventory);
        } catch (Exception e) {
            logger.error("获取详情失败", e);
            return R.error("获取详情失败");
        }

    }
}
