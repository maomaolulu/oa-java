package com.ruoyi.activiti.utils;

import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * 集合工具
 * @author zx
 * @date 2022/1/12 14:57
 */
public class CollectUtil {
    private CollectUtil() {
    }

    /**
     * 字符串数组去重（有序）
     * @param arr
     * @return
     */
    public static LinkedHashSet<String> twoClear(String[] arr) {
        return new LinkedHashSet<>(Arrays.asList(arr));
    }

    public static void main(String[] args) {
        String a = "33,33,44,55,55,44,77,77";
        System.out.println(String.join(",",twoClear(a.split(","))));
    }
}
