package com.niewj.demo.model;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 *
 * @author niewj
 *
 */
public class User implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 7077246948860107883L;
    private int id;
    private String userName;
    private int age;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}