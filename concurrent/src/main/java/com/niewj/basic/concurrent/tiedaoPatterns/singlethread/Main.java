package com.niewj.basic.concurrent.tiedaoPatterns.singlethread;

/**
 * Created by niewj on 2017/10/13.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Testing Gate ...");
        Gate gate = new Gate(); // 3个线程共享的对象, 有name/address
        new UserThread(gate, "Alice", "Alaska").start();
        new UserThread(gate, "Bobby", "Boston").start();
        new UserThread(gate, "Chris", "Canada").start();

    }
}
