package mianshi.concurrent;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 题目：
 * 子线程打印50次，主线程打印20次；
 * 然后子线程打印50次，主线程打印20次；
 * 重复50次，然后停止。
 * Created by niewj on 2016/12/14.
 */
public class TestTimer {


    public static void main(String[] args) {
        testTimer1();
    }

    private static void testTimer1() {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName());
            }
        }, 1000, 2000);
    }
}

//class TimerTaskTest extends TimerTask{
//
//}