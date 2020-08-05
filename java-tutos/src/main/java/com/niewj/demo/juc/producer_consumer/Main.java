package com.niewj.demo.juc.producer_consumer;

import java.util.concurrent.ArrayBlockingQueue;

public class Main {
    public static void main(String[] args) {
        ArrayBlockingQueue<Order> blockingQueue = new ArrayBlockingQueue<>(100);
        Producer producer = new Producer(blockingQueue);
        Consumer consumer = new Consumer(blockingQueue);
        Thread p1 = new Thread(producer);
        p1.start();

        for (int i = 0; i < 10; i++) {
            new Thread(consumer, "T-" + i).start();
        }
    }
}
