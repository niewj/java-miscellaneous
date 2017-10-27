package com.niewj.basic.concurrent.patterns.immutable;

/**
 * Created by niewj on 2017/10/12.
 */
public final class Person {
    private final String name;
    private final String addr;

    public Person(String name, String addr) {
        this.name = name;
        this.addr = addr;
    }

    public String getName() {
        return name;
    }

    public String getAddr() {
        return addr;
    }

    @Override
    public String toString() {
        return "[ Person >> name: " + this.name + "; addr:" + addr + "]";
    }

    public static void main(String[] args) {
        Person alice = new Person("alice", "Tianjin");
        new PrintPersonThread(alice).start();
        new PrintPersonThread(alice).start();
        new PrintPersonThread(alice).start();
    }
}

class PrintPersonThread extends Thread {
    private Person person;

    public PrintPersonThread(Person person) {
        this.person = person;
    }

    @Override
    public void run() {
        while (true) {
            System.out.println(Thread.currentThread().getName() + " prints " + person);
        }
    }
}
