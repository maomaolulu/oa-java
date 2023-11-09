package com.ruoyi.common.utils;

/**
 * @Author yrb
 * @Date 2022/5/7 9:15
 * @Version 1.0
 * @Description 数字金额转中文
 */
public class MoneyToCNFormat {
    private static final String[] CN_NUMBERS = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
    private static final String[] CN_MONETETARY_UNIT = {"分", "角", "元", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾",
            "佰", "仟", "兆", "拾", "佰", "仟"};
    private static final String CN_FULL = "整";
    private static final String CN_NEGATIVE = "负";
    private static final String CN_ZERO = "零角零分";
    private static final long MAX_NUMBER = 10000000000000000L;// 最大16位整数
    private static final String INVALID_AMOUNT = "金额超出最大金额千兆亿(16位整数)";

    public static String formatToCN(double amount) {
        if (Math.abs(amount) >= MAX_NUMBER)
            return INVALID_AMOUNT;
        StringBuilder result = new StringBuilder();
        long value = Math.abs(Math.round(amount * 100));
        int i = 0;
        while (true) {
            int temp = (int) (value % 10);
            result.insert(0, CN_MONETETARY_UNIT[i]);
            result.insert(0, CN_NUMBERS[temp]);
            value = value / 10;
            if (value < 1)
                break;
            i++;
        }
        // "零角零分" 转换成 "整"
        int index = result.indexOf(CN_ZERO);
        if (index > -1) {
            result.replace(index, index + 4, CN_FULL);
        }
        // 负数
        if (amount < 0) {
            result.insert(0, CN_NEGATIVE);
        }
        // 去零
        while (result.indexOf("零") > -1) {
            int k = result.indexOf("零");
            int j = result.lastIndexOf("零");
            if ("元".equals(String.valueOf(result.charAt(j + 1)))) {
                result.replace(k, j + 1, "");
            } else {
                if (k != j && j != (k + 2)) {
                    result.replace(k + 1, k + 2, "");
                    result.replace(k, k + 1, "-");
                } else {
                    result.replace(k + 1, k + 4, "");
                    result.replace(k, k + 1, "-");
                }
            }
        }
        return result.toString().replace('-', '零');
    }
}
