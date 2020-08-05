package com.niewj.demo.juc;


import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadLocalRandom;

public class CyclicBarrierDemo {
    public static void main(String[] args) {

        CyclicBarrier cb = new CyclicBarrier(5, new Runnable() {
            @Override
            public void run() {
                System.out.println("=======[O.V.E.R.]=======" + Thread.currentThread().getName());
            }
        });

        Task task = new Task(cb);

        for (int i = 0; i < 5; i++) {
            Thread t=new Thread(task);
            t.start();
        }
    }

    private static class Task implements Runnable{
        private CyclicBarrier cyclicBarrier;

        public Task(CyclicBarrier cyclicBarrier) {
            this.cyclicBarrier = cyclicBarrier;
        }

        @Override
        public void run() {
            try {
                int timeSleep = ThreadLocalRandom.current().nextInt(100, 10000);
                System.out.println("Run-" + Thread.currentThread().getName() + timeSleep);
                Thread.sleep(timeSleep);
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }
}
