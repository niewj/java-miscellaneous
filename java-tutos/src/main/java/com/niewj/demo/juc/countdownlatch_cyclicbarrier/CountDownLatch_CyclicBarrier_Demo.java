package com.niewj.demo.juc.countdownlatch_cyclicbarrier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadLocalRandom;

// 1. CountDownLatch: 线程里 countDown, 是告诉别人自己开始了; countDown之后的动作继续执行;
// 2. CyclicBarrier: 线程里await, 是告诉别人等自己, await之后的动作: 所有线程都到await后才执行;
// TODO--有待补充
public class CountDownLatch_CyclicBarrier_Demo {
    private static Logger log = LoggerFactory.getLogger(CountDownLatch_CyclicBarrier_Demo.class);

    public static void main(String[] args) throws InterruptedException {
        testCountDownLatch();
        testCyclicBarrier();
    }

    // 每个线程 countDown
    private static void testCountDownLatch() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(3); // 3组操作
        for (int i = 0; i < 3; i++) {
            new Thread(new CountDownLatchTask(latch)).start();
        }

        long start = System.currentTimeMillis();
        log.info("count == {} 走这里", latch.getCount());
        latch.await(); // 在这里集合等待所有结束!
        long end = System.currentTimeMillis();
        log.info("count == {} 这里, 共耗时ms={}", latch.getCount(), end - start);
    }

    // 每个线程 await;
    private static void testCyclicBarrier() throws InterruptedException {
        final CyclicBarrier cyclicBarrier = new CyclicBarrier(3); // 3组操作
        for (int i = 0; i < 3; i++) {
            new Thread(new CyclicBarrierTask(cyclicBarrier)).start();
        }

        long start = System.currentTimeMillis();
        long end = System.currentTimeMillis();
//        log.info("count == {} 等在这里", cyclicBarrier.getNumberWaiting());
        log.info("这里, 等在这里共耗时ms={}", end - start);
    }

}

// countDown之后, 线程还会继续走; await之后就阻塞了;
class CountDownLatchTask implements Runnable {
    private CountDownLatch latch;

    public CountDownLatchTask(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void run() {
        int spendTime = ThreadLocalRandom.current().nextInt(1000, 5000);
        try {
            Thread.sleep(spendTime); // 跑步
            latch.countDown(); // 走完一个

            System.out.println(Thread.currentThread().getName() + " -耗时: " + spendTime + "\t 注意: [在countDown之后走到这的]");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}

// countDown之后, 线程还会继续走; await之后就阻塞了;
class CyclicBarrierTask implements Runnable {
    private CyclicBarrier cyclicBarrier;

    public CyclicBarrierTask(CyclicBarrier cyclicBarrier) {
        this.cyclicBarrier = cyclicBarrier;
    }

    @Override
    public void run() {
        int spendTime = ThreadLocalRandom.current().nextInt(500, 5000);
        try {
            Thread.sleep(spendTime); //

            System.out.println(Thread.currentThread().getName() + " -耗时: " + spendTime + "\t waiting-count= " + cyclicBarrier.getNumberWaiting());
            cyclicBarrier.await(); // 走完一个
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }

        System.out.println("注意: [" + Thread.currentThread().getName() + " 在 await 之后走到这的]");

    }
}
