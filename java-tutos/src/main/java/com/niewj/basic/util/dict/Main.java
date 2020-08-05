package com.niewj.basic.util.dict;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        goSplit();
    }

    private static void goSplit() {
        Map<String, Integer> map;
        List<String> list;
        File textFile = new File("D:\\Docs\\java_en_lang\\TCP_part.txt");
        // 1. 读取文章文件
        // 2. 去除各种分割字符，生成 Set<word, count>单词词组
        try {
            map = readBookText(textFile);
            list = new ArrayList<>(map.keySet());
            Collections.sort(list);
            FileUtils.writeLines(new File("d:/tcpip.txt"), list, false);
        } catch (IOException e) {
            logger.error("IOException: {}", e);
        }
        printWord();
    }

    private static void printWord() {

    }

    private static Map<String, Integer> readBookText(File textFile) throws IOException {
//        Set<Word> set = new HashSet<>(); \()-.
        Map<String, Integer> map = new HashMap<>();

        List<String> lines = FileUtils.readLines(textFile);
        for (String line : lines) {
            String[] wordSegments = line.replaceAll("[\'\"#@$%^&\\*\\(\\)\\-\\.\\,\\>\\<\\+\\:\\;\\=\\]\\[\\{\\}\\?\\~\\`\\!\\_\\|]", " ").split("[\\s*\t\n\r/]");
            for (String seg : wordSegments) {
                // 数字跳过
                if (NieStringUtils.isNumber(seg) || !NieStringUtils.isAllLetters(seg) || NumberUtils.isNumber(seg) || seg.length() < 3) {
                    System.out.println(seg);
                    continue;
                }

                if (map.containsKey(seg)) {
                    map.put(seg, map.get(seg) + 1);
                } else {
                    map.put(seg, 1);
                }
            }
        }


        System.out.println(map.size());
        return map;
    }
}
//
//class Word {
//    private String word; // word
//    private String sentence; //
//    private int count;
//
//    public Word() {
//    }
//
//
//}
