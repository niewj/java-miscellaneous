
package com.niewj.basic.concurrent.coreTech.thread1_4;


import java.util.concurrent.atomic.AtomicInteger;

/**
 * @see Thread#setPriority(int) 线程优先级
 * @see Thread#setDaemon(boolean) 设置守护线程
 */
public class ThreadProperty {

    public static void main(String[] args) throws InterruptedException {

        MyBox apple = new MyBox("apple");
        MyBox pen = new MyBox("pen");

        Thread appleThread = new Thread(apple);
        Thread penThread = new Thread(pen);

//        appleThread.setPriority(Thread.MIN_PRIORITY);
//        penThread.setPriority(Thread.MAX_PRIORITY);
        appleThread.setPriority(Thread.NORM_PRIORITY);
        penThread.setPriority(Thread.NORM_PRIORITY);

        appleThread.start();
        penThread.start();

        Thread.sleep(100);
        appleThread.interrupt();
        penThread.interrupt();

        System.out.println(apple);
        System.out.println(pen);
    }

}

/**
 * pen or apple box
 */
class MyBox implements Runnable {
    private final String name;
    private final AtomicInteger count = new AtomicInteger(0);

    MyBox(String name) {
        this.name = name;
    }

    private String getName() {
        return name;
    }


    @Override
    public String toString() {
        return ">< I have [" + count.get() + "] " + this.getName();
    }


    @Override
    public void run() {
        while (true) {
            if (Thread.currentThread().isInterrupted()) {
                System.out.println("线程中断:" + this.getName());
                break;
            }
            count.incrementAndGet();
        }
    }
}
