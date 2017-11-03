package com.niewj.basic.concurrent.patterns.singleton;

/**
 * 懒汉式-安全延迟
 * Created by niewj on 2017/10/12.
 */
public class SingletonBetter {
    private SingletonBetter() {
        System.out.println("实例初始化...");
    }

    public static SingletonBetter getInstance() {
        return InnerInitUtil.instance;
    }

    /**
     * 静态内部类：
     */
    private static class InnerInitUtil {
        private static SingletonBetter instance = new SingletonBetter(); //只会被调用一次
    }

    public static void main(String[] args) {
        // 无论取多少次，都是初始化好的。
        for (int i = 0; i < 100; i++) {
            new Thread(new Runnable() {
                public void run() {
                    System.out.println(SingletonBetter.getInstance());
                }
            }, "" + i).start();
        }
    }
}
