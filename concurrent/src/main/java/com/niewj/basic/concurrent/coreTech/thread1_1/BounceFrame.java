package com.niewj.basic.concurrent.coreTech.thread1_1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 滚动装置的界面
 */
class BounceFrame extends JFrame {
    private BallComponent comp;

    public BounceFrame() {
        // # ================[1. name]=================
        setTitle("跳跳球");

        // # ================[2. ball装置]=================
        comp = new BallComponent(); // 2. Frame 上加入一个ball装置-加于便捷布局的中间
        add(comp, BorderLayout.CENTER);

        // # ================[3. panel和两个按钮]=================
        // 3. Frame 上加入 两个按钮
        JPanel btnPanel = new JPanel();
        // 1: 开始-加一个球;
        addButton(btnPanel, "开始", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBall();
            }
        });
        // 2: 停止-System.exit(0);
        addButton(btnPanel, "停止", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        add(btnPanel, BorderLayout.SOUTH);
        pack();
    }

    /**
     * 新建一个button 加入到给定的Container。
     *
     * @param c        容器
     * @param title    button名称
     * @param listener 按钮的监听器
     */
    public void addButton(Container c, String title, ActionListener listener) {
        JButton button = new JButton(title);
        c.add(button);
        button.addActionListener(listener);
    }

    /**
     * 每点击一次调用的方法，改为每次点击发起一个线程。
     */
    public void addBall() {
        Ball ball = new Ball(); // 1. new个球
        comp.add(ball); // 2. 球add到面板里
        // 3. 发起一个线程并启动
        BallRunnable runnable = new BallRunnable(ball, comp);
        new Thread(runnable).start();
    }

}
