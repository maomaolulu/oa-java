package com.ruoyi.daily.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.daily.domain.DailyVisitRecord;
import com.ruoyi.daily.service.DailyVisitRecordService;
import com.ruoyi.daily.utils.TxAddressUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 拜访打卡
 *
 * @author zh
 * @date 2023-03-30
 * @menu 拜访打卡
 */
@RestController
@RequestMapping("daily_visit")
public class DailyVisitRecordController extends BaseController {
    private final DailyVisitRecordService dailyVisitRecordService;

    @Autowired
    public DailyVisitRecordController(DailyVisitRecordService dailyVisitRecordService) {
        this.dailyVisitRecordService = dailyVisitRecordService;
    }

    /**
     * 查询打卡拜访列表
     */
    @GetMapping("list_page")
    public R listPage(DailyVisitRecord dailyVisitRecord) {
        pageUtil();
        return resultData(dailyVisitRecordService.getDailyVisitRecordList(dailyVisitRecord));
    }


    /**
     * 查询当日打卡
     */
    @GetMapping("getDailyVisitRecord")
    public R getDailyVisitRecord(DailyVisitRecord dailyVisitRecord) {


        DailyVisitRecord dailyVisitRecord1 = dailyVisitRecordService.getDailyVisitRecord(dailyVisitRecord);

        return R.data(dailyVisitRecord1);

    }

    /**
     * 新增拜访打卡
     */
    @PostMapping("save")
    public R save(@RequestBody DailyVisitRecord dailyVisitRecord) {


        DailyVisitRecord dailyVisitRecord1 = dailyVisitRecordService.saveDailyVisitRecord(dailyVisitRecord);

        return R.ok("打卡成功",dailyVisitRecord1);

    }


    /**
     * 查询ip地址
     */
    @GetMapping("ip_address")
    public R ipAddress(String ip) {
        try {
            String address = TxAddressUtils.getAddress(ip);
            return R.ok(address);
        } catch (Exception e) {
            logger.error("ip查询地址失败", e);
            return R.error("ip查询地址失败");
        }
    }

}
