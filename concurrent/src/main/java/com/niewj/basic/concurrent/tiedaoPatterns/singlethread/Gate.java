package com.niewj.basic.concurrent.tiedaoPatterns.singlethread;

/**
 * Created by weijun.nie on 2017/10/13.
 */
public class Gate {
    private int count = 0;
    private String name;
    private String address;

    public synchronized void pass(String name, String address) {
        this.count++;
        this.name = name;
        this.address = address;
        check();
    }

    private void check() {
        if (name.charAt(0) != address.charAt(0)) {
            System.out.println("********** Broken ************ " + toString());
        }
    }

    public String toString() {
        return "No. " + count + ": " + name + ", " + address;
    }
}
