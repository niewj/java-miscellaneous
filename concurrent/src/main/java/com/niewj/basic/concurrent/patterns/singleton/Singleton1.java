package com.niewj.basic.concurrent.patterns.singleton;

/**
 * 懒汉式单例
 * Created by niewj on 2017/10/12.
 */
public class Singleton1 {
    private static Singleton1 instance;

    private Singleton1() {
        System.out.println("实例初始化...");
    }

    public static Singleton1 getInstance() {
//    public static synchronized Singleton1 getInstance() {
        if (instance == null) {
            instance = new Singleton1();
        }
        return instance;
    }

    public static void main(String[] args) {

        // TODO -多线程时会有可能有多个实例
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                public void run() {
                    System.out.println(Singleton1.getInstance());
                }
            }, "" + i).start();

        }
    }
}
