package com.niewj.basic.concurrent.bruceEckel.constructorSafe;// GuardedIDField.java

import java.util.concurrent.atomic.AtomicInteger;

public class GuardedIDField implements HasID {
    private static AtomicInteger counter = new AtomicInteger();
    private int id = counter.getAndAdd(1);

    public int getID() {
        return id;
    }

    public static void main(String[] args) {
        IDChecker.test(GuardedIDField::new);
    }
}
/* Output:
0
*/