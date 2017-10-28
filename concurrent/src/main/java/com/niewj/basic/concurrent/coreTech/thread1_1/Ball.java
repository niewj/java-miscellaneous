package com.niewj.basic.concurrent.coreTech.thread1_1;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class Ball {

    private static final int XSIZE = 15;
    private static final int YSIZE = 15;

    private double x = 0; //
    private double y = 0;
    private double dx = 1; // 距离
    private double dy = 1;

    /**
     * 在长方形区域内移动，碰壁则掉转方向
     *
     * @param bounds 长方形区域
     */
    public void move(Rectangle2D bounds) {
        // 每次移动横向纵向都移动15px
        x += dx;
        y += dy;

        // 坐标移动向左边超出, 移动方向调换-反向
        // 触碰到右边边界：移动方向调换-反向
        if (x < bounds.getMinX()) {
            x = bounds.getMinX();
            dx = -dx;
        }
        if (x + XSIZE >= bounds.getMaxX()) {
            x = bounds.getMaxX() - XSIZE;
            dx = -dx;
        }

        if (y < bounds.getMinY()) {
            y = bounds.getMinY();
            dy = -dy;
        }
        if (y + YSIZE >= bounds.getMaxY()) {
            x = bounds.getMaxY() - YSIZE;
            dy = -dy;
        }
    }

    /**
     * get球的当前椭圆图形
     *
     * @return
     */
    public Ellipse2D getShape() {
        return new Ellipse2D.Double(x, y, XSIZE, YSIZE);
    }
}
