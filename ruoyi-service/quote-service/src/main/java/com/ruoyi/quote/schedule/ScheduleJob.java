package com.ruoyi.quote.schedule;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.ruoyi.quote.domain.entity.QuoteExpenseDetails;
import com.ruoyi.quote.domain.entity.QuotePointInfo;
import com.ruoyi.quote.domain.entity.QuoteSheetItems;
import com.ruoyi.quote.domain.entity.QuoteTestItem;
import com.ruoyi.quote.mapper.QuoteExpenseDetailsMapper;
import com.ruoyi.quote.mapper.QuotePointInfoMapper;
import com.ruoyi.quote.mapper.QuoteSheetItemsMapper;
import com.ruoyi.quote.mapper.QuoteTestItemMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Author yrb
 * @Date 2022/8/4 15:56
 * @Version 1.0
 * @Description 定时任务
 */
@Component
@EnableScheduling
@Slf4j
public class ScheduleJob {
    private final QuoteTestItemMapper quoteTestItemMapper;
    private final QuoteSheetItemsMapper quoteSheetItemsMapper;
    private final QuoteExpenseDetailsMapper quoteExpenseDetailsMapper;
    private final QuotePointInfoMapper quotePointInfoMapper;

    public ScheduleJob(QuoteTestItemMapper quoteTestItemMapper,
                       QuoteSheetItemsMapper quoteSheetItemsMapper,
                       QuoteExpenseDetailsMapper quoteExpenseDetailsMapper,
                       QuotePointInfoMapper quotePointInfoMapper) {
        this.quoteTestItemMapper = quoteTestItemMapper;
        this.quoteSheetItemsMapper = quoteSheetItemsMapper;
        this.quoteExpenseDetailsMapper = quoteExpenseDetailsMapper;
        this.quotePointInfoMapper = quotePointInfoMapper;
    }

    /**
     * 删除报价产生的临时数据
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void deleteQuoteTempData() {

        log.info("-------------------------开始执行报价定时任务-------------------------");

        log.info("-------------------------正在删除环境、公卫临时数据-------------------------");

        QuoteTestItem quoteTestItem = new QuoteTestItem();
        DateTime dateTime = DateUtil.offsetDay(new Date(), -2);
        quoteTestItem.setTempFlag(1L);
        quoteTestItem.setCreateTime(dateTime);
        quoteTestItemMapper.deleteTempTestItem(quoteTestItem);

        log.info("-------------------------环境、公卫临时数据删除完成-------------------------");

        log.info("-------------------------正在删除职卫临时数据-------------------------");

        QuoteSheetItems quoteSheetItems = new QuoteSheetItems();
        quoteSheetItems.setTempFlag(1L);
        quoteSheetItems.setCreateTime(dateTime);
        quoteSheetItemsMapper.deleteTempSheetItem(quoteSheetItems);

        log.info("-------------------------职卫临时数据删除完成-------------------------");

        log.info("-------------------------正在删除检测明细临时数据-------------------------");

        QuoteExpenseDetails quoteExpenseDetails = new QuoteExpenseDetails();
        quoteExpenseDetails.setTempFlag(1L);
        quoteExpenseDetails.setCreateTime(dateTime);
        quoteExpenseDetailsMapper.deleteTempExpenseDetails(quoteExpenseDetails);

        log.info("-------------------------检测明细临时数据删除完成-------------------------");

        log.info("-------------------------正在删除点位信息临时数据-------------------------");

        QuotePointInfo quotePointInfo = new QuotePointInfo();
        quotePointInfo.setCreateTime(dateTime);
        quotePointInfoMapper.deleteTempPointInfo(quotePointInfo);

        log.info("-------------------------点位信息临时数据删除完成-------------------------");

        log.info("-------------------------报价定时任务执行完成-------------------------");
    }
}
