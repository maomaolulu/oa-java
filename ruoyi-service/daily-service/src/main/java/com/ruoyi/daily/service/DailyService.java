package com.ruoyi.daily.service;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.daily.domain.BizDaily;
import com.ruoyi.daily.domain.vw.ViewDailyList;

import java.util.List;

/**
 * 日报
 * @author zx
 * @date 2022/1/8 16:35
 */
public interface DailyService {
    /**
     * 查询接收人日报列表
     * @return
     */
    List<ViewDailyList> getVwList( ViewDailyList viewDailyList);

    /**
     * 查询我发出的
     * @param daily
     * @return
     */
    List<BizDaily> getDailyRecordList(BizDaily daily);

    /**
     * 提交日报
     * @param daily
     * @return
     */
    R save(BizDaily daily);

    /**
     * 查询日报
     * @param id
     * @return
     */
    BizDaily getById(Long id);

    /**
     * 编辑日报
     * @param daily
     */
    void update(BizDaily daily);

    /**
     * 已读标记
     * @param id
     */
    void updateRead(Long id);

    /**
     * 查询选中的日期是否提交过日报
     * @param bizDaily 按日期查
     * @return 日报记录
     */
    BizDaily getDailyOne(BizDaily bizDaily);
}
