package com.ruoyi.daily.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.daily.domain.DailyVisitRecord;
import com.ruoyi.daily.domain.dto.DailyVisitDto;

import java.util.List;

/**
 * 拜访打卡
 * @author zh
 * @date 2023-03-30
 */
public interface DailyVisitRecordService extends IService<DailyVisitRecord> {

    /**
     * 查询当前人当日打卡
     *
     * @param dailyVisitRecord
     * @return
     */
     DailyVisitRecord getDailyVisitRecord(DailyVisitRecord dailyVisitRecord);

      /**
     * 新增拜访打卡
     *
     * @param dailyVisitRecord
     * @return
     */
     DailyVisitRecord saveDailyVisitRecord(DailyVisitRecord dailyVisitRecord);

    /**
     * 获取拜访记录列表
     * @return 集合
     */
    List<DailyVisitDto> getDailyVisitRecordList(DailyVisitRecord dailyVisitRecord);
}
