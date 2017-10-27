package com.niewj.basic.concurrent.patterns.singleton;

/**
 * 懒汉式单例
 * Created by weijun.nie on 2017/10/12.
 */
public class Singleton2 {
    public static int STATUS = 1;
    private static Singleton2 instance = new Singleton2(); //只会被初始化一次

    private Singleton2() {
        System.out.println("实例初始化...");
    }

    public static Singleton2 getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        System.out.println(Singleton2.STATUS);
//        // TODO -多线程时会有可能有多个实例
//        for (int i = 0; i < 10; i++) {
//            new Thread(new Runnable() {
//                public void run() {
//                    System.out.println(Singleton2.getInstance());
//                }
//            }, "" + i).start();
//
//        }
    }
}
