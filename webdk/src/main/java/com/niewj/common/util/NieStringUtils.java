package com.niewj.common.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by niewj on 2017/9/13.
 */
public class NieStringUtils {

    /**
     * 如果给定的字符串 targetString 大于 len 位, 则截取 len 位返回; 不大于，直接返回.
     *
     * @param targetString 给定的字符串
     * @param len          指定的长度
     * @return
     */
    public static String subLen(String targetString, int len) {
        if (StringUtils.isBlank(targetString) || targetString.length() <= len) {
            return targetString;
        }
        return targetString.substring(0, len);
    }

    /**
     * 检查是否字符串可以转化成数字
     *
     * @param number
     * @return
     */
    public static boolean isNumber(String number) {
        // 1. 为空，false
        if (StringUtils.isBlank(number))
            return false;

        // 2. 如果字符串是正负号开头，去掉在判断
        if (number.startsWith("-") || number.startsWith("+")) {
            number = number.substring(1);
        }

        /**
         * 3. 取小数点：
         * 如果没有，判断字符串是否是数字；
         * 如果有，判断两端的数字是否都是数字
         */
        int index = number.indexOf(".");
        if (index < 0) {
            return StringUtils.isNumeric(number);
        } else {
            String num1 = number.substring(0, index);
            String num2 = number.substring(index + 1);

            return StringUtils.isNumeric(num1) && StringUtils.isNumeric(num2);
        }
    }


    public static void main(String[] args) {

    }
}
