package com.ruoyi.training.utils;

import com.ruoyi.system.domain.SysUser;
import io.swagger.annotations.ApiModelProperty;

import java.util.*;

/**
 * @Author yrb
 * @Date 2022/6/7 10:58
 * @Version 1.0
 * @Description 培训工具类
 */
public class TrainingUtils {
    @ApiModelProperty(value = "宁波优维公司ID")
    public static final Long NBYW_COMPANY_ID = 131L;

    @ApiModelProperty(value = "培训-考试类型-自定义考试")
    public static final Integer TRAINING_CUSTOMIZE_EXAM_TYPE_CUSTOMIZE = 1;

    public static String getRegexpAncestor(String ancestor) {
        String[] ancestors = ancestor.split(",");
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : ancestors) {
            stringBuilder.append(s).append("|");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }


    /**
     * 判断员工类型
     *
     * @param user 员工信息
     * @return 判断结果
     * @author hjy
     * @date 2022/6/16 17:11
     * 需求：
     * 1.自然年1-9月入职员工，记为本年度新员工；下一自然年记为老员工
     * 2.本自然年10-12月入职员工，本年度及下一自然年均记为新员工，下下自然年记为老员工
     * 解决方案：
     * 1.新员工1-9月份入职  学时只计算当年
     * 2.新员工10月到12月入职，学时计算范围是当年10月到下一自然年12月底（解决办法：把期间加入的课程加入到下一自然年）
     * 3.老员工，学时计算自然年即可
     */
    public static Map<String, Integer> judgeStaffType(SysUser user) {
        Calendar calendar = Calendar.getInstance();
        //入职时间放入日历中
        calendar.setTime(user.getCreateTime());
        //入职月份
        int month = calendar.get(Calendar.MONTH);
        //工龄（当前年份-入职年份）
        int temp = (Calendar.getInstance().get(Calendar.YEAR) - calendar.get(Calendar.YEAR));
        Map<String, Integer> map = new HashMap<>();
        //类型   1 新员工  2：老员工（默认）
        int type = 2;
        //状态 新员工学时计算跨年状态 1：需要跨年计算  2：不需要跨年
        int status = 2;
        //判断新员工-条件1
        if ((temp == 1 && month > 9)) {
            type = 1;
        }
        //判断新员工-条件2(当前年份，满足条件为新员工) 学时跨年计算
        if (temp == 0) {
            type = 1;
            if (month > 9) {
                status = 1;
            }
        }
        //新老员工状态
        map.put("type", type);
        //新员工是否需要跨年计算学时   改变课程状态，如果是新员工，满足条件的话，学时完成度计算到下一年
        map.put("status", status);
        return map;
    }

    /**
     * 获取指定格式字符串
     *
     * @param collection 集合
     * @param format  分隔符
     * @return 字符串
     */
    public static String getFormatString(Collection<?> collection, String format) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object sub : collection) {
            stringBuilder.append(sub).append(format);
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }
}
