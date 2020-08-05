package com.niewj.demo.juc.producer_consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;

public class Producer implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(Producer.class);
    Producer(ArrayBlockingQueue<Order> queue) {
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
        int i = 0;
        while (true) {
            Order order = new Order(++i);
            try {
                queue.put(order);
                log.info("{} 生产一个订单(休息片刻):{} 当前queue中的存量:", Thread.currentThread().getName(), order, queue.size());
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // members
    private ArrayBlockingQueue<Order> queue;
}
