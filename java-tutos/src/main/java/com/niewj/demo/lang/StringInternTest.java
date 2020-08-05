package com.niewj.demo.lang;

public class StringInternTest {
    public static void main(String[] args) {

        String s1 = "javaHelloW";
        String s2 = "javaHelloW";
        String s3 = new String("javaHelloW");

        // intern 会发布字符串内容到 字符串常量池中；
        String s4 = s3.intern();
        System.out.println(s1 == s2);
        System.out.println(s1 == s3);
        System.out.println(s1 == s4);
    }
}