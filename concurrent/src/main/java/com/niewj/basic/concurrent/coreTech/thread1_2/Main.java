package com.niewj.basic.concurrent.coreTech.thread1_2;

import com.niewj.basic.common.PrintUtil;

/**
 * interrpt()会打破线程的wait/sleep状态，使
 */
public class Main {

    public static void main(String[] args) {
        testInterrupt(); //

    }

    /**
     *  逻辑见md
     * @see Thread#interrupt()
     * @see Thread#interrupted()
     * @see Thread#isInterrupted()
     * @see Thread#sleep(long)
     */
    public static void testInterrupt() {
        Thread loop = new Thread(
                new Runnable() {
                    @Override
                    public void run() {

                        while (true) {
                            /** 1. 监控线程状态是否为:中断
                             * #1. 打印状态；
                             * #2. 跳出死循环；
                             */
                            if (Thread.interrupted()) {
                                PrintUtil.printThreadStatus("looop线程被中断了，即将退出！");
                                break;
                            }

                            /**
                             * 2. 若无中断则-每隔半秒钟-打印下当前时间；
                             * 如果线程的sleep行为被中断的话:
                             * #1. 打印线程当前状态
                             * #2. 马上设置中断状态true! (非常重要，要让上面break可以退出！)
                             */
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                if (!Thread.currentThread().isInterrupted()) {
                                    PrintUtil.printThreadStatus("线程状态不对，设置线程中断状态为false");
                                    Thread.currentThread().interrupt(); // #
                                }
//                                e.printStackTrace();
                            }

                            // 线程做的工作：xxx
                            System.out.println(System.currentTimeMillis());
                            // Continue to do nothing
                        }

                    }
                }
        );

        // #1 启动线程
        loop.start();

        // #2. main线程休眠4秒后，发起对loop线程的interrupt()动作:
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        loop.interrupt();
    }

}
