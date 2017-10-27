package com.niewj.basic.concurrent;

import java.util.concurrent.TimeUnit;

/**
 * # 1. ERROR-不能监控还 // TODO
 * Created by weijun.nie on 2017/10/3.
 */
public class TestJStack {
    public static void main(String[] args) {

        Thread.currentThread().setName("main_t_hello");

        // 1. task1 -run
        final Task1 target1 = new Task1();
        final Thread t1 = new Thread(target1, "thread_1");
        t1.start();

        // 1. t2 monitor
        Thread t2 = new Thread(new Runnable() {
            public void run() {
                if (target1.getData() == 100) {
                    t1.interrupt();
                    System.out.println(t1.getName() + " is Interrupted!!!");
                }
            }
        }, "monitor_t2");
        t2.start();


    }

}

class Task1 implements Runnable {
    private int data = 0;

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public void run() {
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(1);
                data++;
                if (data % 5 == 0) {
                    System.out.println("hhhhhhhhhhhhhhhhaaaaaaaa\t" + data);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}