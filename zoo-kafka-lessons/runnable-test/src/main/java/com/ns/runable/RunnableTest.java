package com.ns.runable;

/**
 * Created by niewj on 2016/12/11.
 */
public class RunnableTest {
    private boolean ready = false;
    private int num = 0;
    private int result = 0;

    public void write() {
        ready = true; //1
        num = 2; //2
    }

    public void read() {
        if (ready) {  //3
            result = num * 2 + 1; //4
        }
        System.out.println("result = " + result);
    }

    private class ThreadTest extends Thread {

        public ThreadTest(boolean flag) {
            this.flag = flag;
        }

        private boolean flag = false;

        @Override
        public void run() {
            if (flag) {
                write();
            } else {
                read();
            }

        }
    }

    public static void main(String[] args) {
        RunnableTest app = new RunnableTest();
        app.new ThreadTest(true).start();
        app.new ThreadTest(false).start();

    }
}
