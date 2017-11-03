package com.niewj.basic.concurrent.tiedaoPatterns.singlethread;

/**
 * Created by niewj on 2017/10/13.
 */
public class UserThread extends Thread {

    private Gate gate;
    private String myName;
    private String myAdderss;

    public UserThread(Gate gate, String name, String address) {
        this.gate = gate;
        this.myName = name;
        this.myAdderss = address;
    }

    @Override
    public void run() {
        System.out.println(myAdderss + " Begin");
        while (true) {
            gate.pass(myName, myAdderss);
        }
    }
}
