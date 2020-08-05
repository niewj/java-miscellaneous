package com.niewj.demo.juc.countdownlatch_cyclicbarrier;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class CountDownLatchDemo {

    public static void testCountDownLatch() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(100);
        final AtomicInteger count = new AtomicInteger(0);
        final ConcurrentHashMap<String, Integer> concurrentHashMap = new ConcurrentHashMap();

        long start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 100000; j++) {
                        int k = count.incrementAndGet();
                        concurrentHashMap.put("C" + k, k);
                    }
                    countDownLatch.countDown();
                }
            }).start();
        }
        countDownLatch.await();
        long end = System.currentTimeMillis();
        System.out.println(end - start);
        System.out.println(concurrentHashMap.size());
    }

    public static void main(String[] args) {
        try {
            testCountDownLatch();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
