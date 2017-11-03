package com.niewj.basic.concurrent.patterns.builder1;

/**
 * Created by niewj on 2017/10/25.
 */
public class Product {

    private String message;

    public Product() {
    }

    public Product(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
