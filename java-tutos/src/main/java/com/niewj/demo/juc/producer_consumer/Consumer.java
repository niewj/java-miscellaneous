package com.niewj.demo.juc.producer_consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;

public class Consumer implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(Consumer.class);
    Consumer(ArrayBlockingQueue<Order> queue) {
        this.queue = queue;
    }


    public ArrayBlockingQueue getQueue() {
        return queue;
    }

    public void setQueue(ArrayBlockingQueue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            Order order = null;
            try {
                order = queue.take();
                log.info("{} 消费一个: {} 当前queue中的存量:{}", Thread.currentThread().getName(), order, queue.size());
                log.info("-----------------------------------------------------------------");
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // members
    private ArrayBlockingQueue<Order> queue;
}
