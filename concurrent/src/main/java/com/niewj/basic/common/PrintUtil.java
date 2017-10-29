package com.niewj.basic.common;

/**
 * 打印工具，用于输出
 */
public class PrintUtil {

    /**
     * 打印状态描述信息
     *
     * @param description
     */
    public static void printThreadStatus(String description) {
        System.out.println("#线程当前的状态为： " + description);
    }
}
