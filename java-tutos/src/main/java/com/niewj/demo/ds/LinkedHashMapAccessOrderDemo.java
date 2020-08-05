package com.niewj.demo.ds;

import java.util.LinkedHashMap;
import java.util.Map;

// arg accessOrder=true 最后访问的key, 最后遍历到;
// 可以用作 LRU算法 Latest Recently Used 简单版本
public class LinkedHashMapAccessOrderDemo {

    public static void main(String[] args) {
        testLinkedHashMap();

    }

    // 最近使用的在最后
    private static void testLinkedHashMap() {
        LinkedHashMap<String, Integer> mymap = new LinkedHashMap(10, 0.75f, true);
        mymap.put("9", 9);
        mymap.put("8", 8);
        mymap.put("7", 7);
        mymap.put("6", 6);
        mymap.put("5", 5);
        mymap.put("4", 4);
        mymap.put("3", 3);
        mymap.put("2", 2);
        mymap.put("1", 1);

        System.out.println(mymap.get("9")); // 访问 9
        System.out.println(mymap.get("5")); // 访问 5

        System.out.println("======================");
        for (Map.Entry entry : mymap.entrySet()) {
            System.out.print(entry.getKey() + " -> "); // 8 -> 7 -> 6 -> 4 -> 3 -> 2 -> 1 -> 9 -> 5 ->
        }

    }
}
