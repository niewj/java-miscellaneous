package com.niewj.basic.concurrent.bruceEckel.constructorSafe;// StaticIDField.java

public class StaticIDField implements HasID {
    private static int counter = 0;
    private int id = counter++;

    public int getID() {
        return id;
    }
}