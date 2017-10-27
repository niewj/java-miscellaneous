package com.niewj.basic.concurrent.patterns.immutable;

/**
 * 同样的两个方法toString() 同步和不同步的性能区别测试
 * Created by niewj on 2017/10/12.
 */
public class TestSynchronizedPerformance {

    private static final long COUNT = 1000000000L;

    private static void call(String msg, long count, Object object) {
        System.out.println(msg + ": BEGIN");
        long start = System.currentTimeMillis();

        for (long i = 0; i < count; i++) {
            object.toString();
        }

        System.out.println(msg + ": END");
        long end = System.currentTimeMillis();

        System.out.println("TIME: " + (end - start) + "msec.");
    }

    public static void main(String[] args) {
        new TestSynchronizedPerformance().call("不同步", COUNT, new NotSync());
        System.out.println("==========================");
        new TestSynchronizedPerformance().call("同步", COUNT, new Sync());
//        输出如下：------------
//        不同步: BEGIN
//        不同步: END
//        TIME: 446msec.
//        ==========================
//        同步: BEGIN
//        同步: END
//        TIME: 23812msec.
    }
}

class Sync {
    private final String name = "synchronized";

    @Override
    public synchronized String toString() {
        return "[" + name + "]";
    }
}

class NotSync {
    private final String name = "not-synchronized";

    @Override
    public String toString() {
        return "[" + name + "]";
    }
}