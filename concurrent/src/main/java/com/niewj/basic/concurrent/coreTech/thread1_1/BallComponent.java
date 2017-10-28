package com.niewj.basic.concurrent.coreTech.thread1_1;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 */
public class BallComponent extends JPanel {

    private static final int DEFAULT_WIDTH = 450;
    private static final int DEFAULT_HEIGHGT = 350;

    /**
     * 使用ArrayList会报告ConcurrentModificationException
     * BallComponent#paintComponent#for (Ball b : balls) { << 这里
     * <p>
     * 因为CopyOnWriteArrayList的操作比如：get(k) 都使用了ReentrantLock 可重入锁
     */
    private List<Ball> balls = new CopyOnWriteArrayList<>();
//    private List<Ball> balls = new ArrayList<>();

    /**
     * Ball装置里放入一个ball
     *
     * @param b
     */
    public void add(Ball b) {
        balls.add(b);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g); // 擦除背景
        Graphics2D g2 = (Graphics2D) g;
        for (Ball b : balls) {
            g2.fill(b.getShape());
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHGT);
    }
}
