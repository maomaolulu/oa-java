package com.ruoyi.activiti.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * @author zx
 * @date 2022/1/18 18:05
 */
public class DateUtil {
    private DateUtil(){}

    /**
     * 获取上周周一
     * @return
     */
    public static Date getLastTimeInterval() {
        Calendar calendar1 = Calendar.getInstance();
        int dayOfWeek = calendar1.get(Calendar.DAY_OF_WEEK) - 1;
        int offset1 = 1 - dayOfWeek;
        calendar1.add(Calendar.DATE, offset1 - 7);
        return calendar1.getTime();
    }
}
