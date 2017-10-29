package com.niewj.basic.concurrent.coreTech.thread1_3;

import java.util.concurrent.locks.LockSupport;

/**
 * @Link{http://blog.csdn.net/aitangyong/article/details/38373137}
 * LockSupport是JDK中比较底层的类，用来创建锁和其他同步工具类的基本线程阻塞原语。
 * java锁和同步器框架的核心AQS:AbstractQueuedSynchronizer，就是通过调用LockSupport.park()和LockSupport.unpark()实现线程的阻塞和唤醒的。
 * LockSupport很类似于二元信号量(只有1个许可证可供使用)，如果这个许可还没有被占用，当前线程获取许可并继续执行；
 * 如果许可已经被占用，当前线程阻塞，等待获取许可。
 *
 * @see LockSupport#park()
 */
public class Main { // TODO --learn more
    public static void main(String[] args) {
        /**
         *  --1 运行该代码，可以发现主线程一直处于阻塞状态。
         *  因为许可默认是被占用的，调用park()时获取不到许可，所以进入阻塞状态。
         */
//        LockSupport.park();
//        System.out.println("block.");

        /**
         *  --2 如下代码：先释放许可，再获取许可，主线程能够正常终止。
         * LockSupport许可的获取和释放，一般来说是对应的，如果多次unpark，只有一次park也不会出现什么问题，结果是许可处于可用状态
         */
//        Thread thread = Thread.currentThread();
//        LockSupport.unpark(thread);//释放许可
//        LockSupport.park();// 获取许可
//        System.out.println("b");

        /**
         *  --3 LockSupport是不可重入的，如果一个线程连续2次调用LockSupport.park()，那么该线程一定会一直阻塞下去。
         * 这段代码打印出a和b，不会打印c，因为第二次调用park的时候，线程无法获取许可出现死锁。
         */
//        Thread thread = Thread.currentThread();
//        LockSupport.unpark(thread);
//        System.out.println("a");
//        LockSupport.park();
//        System.out.println("b");
//        LockSupport.park();
//        System.out.println("c");

        /**
         * --4
         */
        try {
            t2();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下面我们来看下LockSupport对应中断的响应性
     * 最终线程会打印出thread over.true。
     * 这说明线程如果因为调用park而阻塞的话，能够响应中断请求(中断状态被设置成true)，但是不会抛出InterruptedException。
     * @throws Exception
     */
    public static void t2() throws Exception {
        Thread t = new Thread(new Runnable() {
            private int count = 0;

            @Override
            public void run() {
                long start = System.currentTimeMillis();
                long end = 0;

                while ((end - start) <= 1000) {
                    count++;
                    end = System.currentTimeMillis();
                }

                System.out.println("after 1 second.count=" + count);

                //等待或许许可
                LockSupport.park();
                System.out.println("thread over." + Thread.currentThread().isInterrupted());

            }
        });

        t.start();

        Thread.sleep(2000);

        // 中断线程
        t.interrupt();

        System.out.println("main over");
    }
}
