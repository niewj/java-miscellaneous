package com.niewj.basic.common.exception;

/**
 * 未捕获的异常，可以用它来做处理
 */
public class NieExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.println("==========MyExceptionHandler==========");
        System.out.println("Nie-异常信息： " + e.getMessage());
        System.out.println("Nie-线程信息： " + t.getName());
        System.out.println("==========MyExceptionHandler==========");
    }
}
