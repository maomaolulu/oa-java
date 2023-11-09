package com.ruoyi.daily.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.daily.domain.BizDaily;
import com.ruoyi.daily.domain.vw.ViewDailyList;
import com.ruoyi.daily.service.DailyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 日报管理
 *
 * @author zx
 * @date 2022/1/8 17:50
 * @menu 日报管理
 */
@RestController
@RequestMapping("daily")
public class DailyController extends BaseController {
    private final DailyService dailyService;

    @Autowired
    public DailyController(DailyService dailyService) {
        this.dailyService = dailyService;
    }


    /**
     * 查询接收人列表
     */
    @GetMapping("receiver_daily")
    public R getReceiverDaily(ViewDailyList viewDailyList) {
        try {
            return R.data(dailyService.getVwList(viewDailyList));
        } catch (Exception e) {
            logger.error("查询接收人列表", e);
            return R.error("查询接收人列表失败");
        }
    }

    /**
     * 查询日报
     */
    @GetMapping("info")
    public R getById(Long id) {
        try {
            return R.data(dailyService.getById(id));
        } catch (Exception e) {
            logger.error("查询日报", e);
            return R.error("查询日报失败");
        }
    }

    /**
     * 已读标记
     */
    @GetMapping("read")
    public R updateRead(Long id) {
        try {
            dailyService.updateRead(id);
            return R.ok();
        } catch (Exception e) {
            logger.error("已读标记", e);
            return R.error("已读标记失败");
        }
    }

    /**
     * 编辑日报
     */
    @PutMapping("info")
    public R update(@RequestBody BizDaily daily) {
        try {
            dailyService.update(daily);
            return R.ok();
        } catch (Exception e) {
            logger.error("编辑日报", e);
            return R.error("编辑日报失败");
        }
    }

    /**
     * 提交拜访日报
     */
    @PostMapping("save")
    public R save(@RequestBody BizDaily daily) {
        try {
            if (daily.getDailyTime() == null) {
                return R.error("未填写日报所属日期");
            }
            return dailyService.save(daily);
        } catch (Exception e) {
            logger.error("提交日报", e);
            return R.error("提交日报失败");
        }
    }

    /**
     * 查询拜访日报
     */
    @GetMapping("list")
    public R get(BizDaily daily) {
        try {
            pageUtil();
            return R.data(dailyService.getDailyRecordList(daily));
        } catch (Exception e) {
            logger.error("查询拜访日报", e);
            return R.error("查询拜访日报出现错误");
        }
    }

    /**
     * 按日期查询单个拜访日报
     */
    @GetMapping("getDailyOne")
    public R getDailyOne(BizDaily daily) {
        try {
            if (daily.getDailyTime() == null) {
                return R.error("日报提交日期为空");
            }
            BizDaily dailyOne = dailyService.getDailyOne(daily);
            if (dailyOne == null) {
                return R.error(201, "未提交日报");
            }
            return R.data(dailyOne);
        } catch (Exception e) {
            logger.error("查询拜访日报", e);
            return R.error("查询拜访日报出现错误");
        }
    }
}
