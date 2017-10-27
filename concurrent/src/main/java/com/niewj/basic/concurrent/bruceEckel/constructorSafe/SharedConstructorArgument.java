package com.niewj.basic.concurrent.bruceEckel.constructorSafe;// SharedConstructorArgument.java

import java.util.concurrent.atomic.AtomicInteger;

interface SharedArg {
    int get();
}

class Unsafe implements SharedArg {
    private int i = 0;

    public int get() {
        return i++;
    }
}

class Safe implements SharedArg {
    private static AtomicInteger counter = new AtomicInteger();

    public int get() {
        return counter.getAndAdd(1);
    }
}

class SharedUser implements HasID {
    private final int id;

    public SharedUser(SharedArg sa) {
        id = sa.get();
    }

    @Override
    public int getID() {
        return id;
    }
}

public class SharedConstructorArgument {
    public static void main(String[] args) {
        Unsafe unsafe = new Unsafe();
        IDChecker.test(() -> new SharedUser(unsafe));
        Safe safe = new Safe();
        IDChecker.test(() -> new SharedUser(safe));
    }
}
/* Output:
47747
0
*/