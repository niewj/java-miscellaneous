package com.niewj.basic.concurrent.coreTech.thread1_4;

import com.niewj.basic.common.exception.NieExceptionHandler;

/**
 * 未捕捉异常：
 * 1. checked exception - FileNotFoundException
 * 2. unchecked exception - ArrayIndexOutOfBoundException
 */
public class UnCaughtExceptionSample {

    public static void main(String[] args) {
        // 所有异常的处理
        Thread.setDefaultUncaughtExceptionHandler(new NieExceptionHandler());

        int[] intArray = {1, 2, 3, 4, 5};

//        // caughted不会被 handler处理
//        Thread caughtedThread = new Thread(new CaughtedExceptionThread(intArray));
//        caughtedThread.start();

        // unCaughted会被 handler处理
        Thread unCaughtedThread = new Thread(new UnCaughtedExceptionThread(intArray));
        unCaughtedThread.start();

    }
}


/**
 * 自己捕获了异常：
 */
class CaughtedExceptionThread implements Runnable {
    private int[] intArr;

    public CaughtedExceptionThread(int[] intArr) {
        this.intArr = intArr;
    }

    /**
     * 如果是一个一直执行的程序：比如kafka的一个partition, 要做消息的监听和处理，
     * 不能因为一条消息出错导致这个partition处理停止： 如下方式处理异常也是一种方法：
     */
    @Override
    public void run() {
        int i = 0;
        while (true) {
            try {
                for (; i < 10; i++) {
                    System.out.println(intArr[i]);
                }
            } catch (Exception e) {
                System.out.println("error: lines: " + i + "\n" + e);
                continue;
            }
        }
    }
}

/**
 * 自己没有捕获异常：
 */
class UnCaughtedExceptionThread implements Runnable {
    private int[] intArr;

    public UnCaughtedExceptionThread(int[] intArr) {
        this.intArr = intArr;
    }

    @Override
    public void run() {
        /**
         * 不捕获异常，可以用 UnCaughtedExceptionHandler 来记录信息等
         */
        int i = 0;
        while (true) {
            // ----------------
            for (; i < 10; i++) {
                System.out.println(intArr[i]);
            }
        }
    }
}
