package com.niewj.basic.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by weijun.nie on 2017/9/30.
 */
public class SimpleGuiTest {

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                SimpleFrame sf = new SimpleFrame();
                sf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                sf.setVisible(true);
            }
        });
    }
}

class SimpleFrame extends JFrame {

    public SimpleFrame() {
        // get screen dimension
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension dimension = kit.getScreenSize();
        int width = dimension.width;
        int height = dimension.height;

        // set frame title/size/location
        setTitle("电台app1");
        setSize(width / 2, height / 2);
        setLocationByPlatform(true);

        // set frame icon
        Image image = new ImageIcon("favicon.gif").getImage();
        setIconImage(image);

        add(new AComponent());
        pack();
    }
}

class AComponent extends JComponent {

    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 300;

    @Override
    protected void paintComponent(Graphics g) {
        g.drawRoundRect(10, 20, 200, 200, 18, 18);
        g.drawString("__________________________________________", 75, 100);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
}
