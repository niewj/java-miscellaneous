package com.niewj.basic.util.jar;

import java.util.concurrent.atomic.AtomicInteger;

public class UnCheckedExceptionTest {

    public static void main(String[] args) {
        Thread t = new Thread(new MyBox2("萝卜"));
        t.start();
    }
}


/**
 * pen or apple box
 */
class MyBox2 implements Runnable {
    private final int[] intArr = {1, 2, 3, 4, 5};
    private final String name;
    private final AtomicInteger count = new AtomicInteger(0);

    public MyBox2(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    @Override
    public String toString() {
        return ">< I have [" + count.get() + "] " + this.getName();
    }


    @Override
    public void run(){
        while (true) {
            if (Thread.currentThread().isInterrupted()) {
                System.out.println("线程中断:" + this.getName());
                break;
            }
            count.incrementAndGet();
            int i = 0;
            for (; i < 10; i++) {
                try{

                    System.out.println(intArr[i]);
                }catch (ArrayIndexOutOfBoundsException e){
                    System.out.println(e);
                    continue;
                }
            }
            System.out.println("HHHHHHHHHHHHHHHHH" + i);
        }
    }
}
