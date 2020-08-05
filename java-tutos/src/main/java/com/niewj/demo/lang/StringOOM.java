package com.niewj.demo.lang;

import java.util.ArrayList;
import java.util.List;

/**
 * JDK6-在永久区: OutOfMemoryError: PermGen space
 * JDK7-在堆内存: OutOfMemoryError: Java heap space
 * JDK8-在堆内存: OutOfMemoryError: Java heap space
 */
public class StringOOM {
    static String base = "somethings";

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            String str = base + base;
            base = str;
            // intern 会把字符串发布到 字符串常量池中去。
            list.add(str.intern());
        }

//        List<String> list = new ArrayList<>(); 无法编译， 必须写完整： List<String> list = new ArrayList<String>();
//        JDK6-在永久区: OutOfMemoryError: PermGen space
//        PS C:\devs\java\jdk6> .\bin\java.exe StringOOM
//        Exception in thread "main" java.lang.OutOfMemoryError: PermGen space
//        at java.lang.String.intern(Native Method)
//        at StringOOM.main(StringOOM.java:12)

//        JDK7-在堆内存: OutOfMemoryError: Java heap space
//        PS C:\devs\java\jdk7\bin> .\java.exe -Xms20m -Xmx20m -XX:PermSize=8m -XX:MaxPermSize=8 StringOOM
//        Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
//        at java.util.Arrays.copyOfRange(Arrays.java:2694)
//        at java.lang.String.<init>(String.java:203)
//        at java.lang.StringBuilder.toString(StringBuilder.java:405)
//        at StringOOM.main(StringOOM.java:10)


//        JDK8-在堆内存: OutOfMemoryError: Java heap space
//        Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
//        at java.util.Arrays.copyOf(Arrays.java:3332)
//        at java.lang.AbstractStringBuilder.ensureCapacityInternal(AbstractStringBuilder.java:124)
//        at java.lang.AbstractStringBuilder.append(AbstractStringBuilder.java:448)
//        at java.lang.StringBuilder.append(StringBuilder.java:136)
//        at com.niewj.demo.lang.StringOOM.main(StringOOM.java:12)
//        Java HotSpot(TM) 64-Bit Server VM warning: ignoring option PermSize=8m; support was removed in 8.0
//        Java HotSpot(TM) 64-Bit Server VM warning: ignoring option MaxPermSize=8m; support was removed in 8.0

    }
}