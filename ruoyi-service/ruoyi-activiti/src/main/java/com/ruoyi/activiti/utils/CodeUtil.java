package com.ruoyi.activiti.utils;

import cn.hutool.core.date.DateUtil;

import java.util.Date;

/**
 * @Author yrb
 * @Date 2023/6/7 10:45
 * @Version 1.0
 * @Description 工具类
 */
public class CodeUtil {
    private CodeUtil() {

    }

    /**
     * 获取格式化编码
     *
     * @param prefix 前缀
     * @param total  总长度
     * @return code
     */
    public static String getCode(String prefix, int total) {
        String code = prefix + DateUtil.format(new Date(), "yyyyMMdd") +
                String.valueOf(Math.random()).split("\\.")[1].substring(0, total - prefix.length() - 8);
        return code;
    }

    /**
     * 获取格式化编码
     *
     * @param prefix 前缀
     * @return code
     */
    public static String getCode(String prefix) {
        String code = prefix + DateUtil.format(new Date(), "yyyyMMdd") +
                String.valueOf(Math.random()).split("\\.")[1].substring(0, 23 - prefix.length() - 8);
        return code;
    }
}
