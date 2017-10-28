package com.niewj.basic.concurrent.coreTech.thread1_1;

/**
 * 把点击"开始"后的操作写成一个线程，异步执行。
 */
class BallRunnable implements Runnable {

    private Ball ball;
    private BallComponent comp;
    public static final int STEPS = 1000;
    public static final int DELAY = 3;

    public BallRunnable(Ball ball, BallComponent comp) {
        this.ball = ball;
        this.comp = comp;
    }

    @Override
    public void run() {
        try {
            // 3. 循环1000次，每次循环让球跳动1次，也就是让球跳动1000次
            for (int i = 1; i <= STEPS; i++) {
                ball.move(comp.getBounds());
                comp.paint(comp.getGraphics());
                Thread.sleep(DELAY);
            }

        } catch (InterruptedException e) {
//            e.printStackTrace();
        }
    }

}