package com.niewj.basic.common.exception;

/**
 * 自定义异常的父类：NieException：
 * 为每一个子类不用处理，如果没有捕获，可以用默认的未捕获异常处理器做记录日志等操作
 */
public class NieException extends RuntimeException {

    static {
        Thread.setDefaultUncaughtExceptionHandler(new NieExceptionHandler());
    }

}
