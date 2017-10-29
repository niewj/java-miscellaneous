##### 1.3 线程状态: thread1_3
Thread类中有个枚举类：Thread.State
```java
public enum State {
    /**
     * Thread state for a thread which has not yet started.
     * 
     * 一个还未start()的线程状态。
     */
    NEW,
    
    /**
     * Thread state for a runnable thread.  
     * A thread in the runnable state is executing in the Java virtual machine but it may be waiting for other resources from the operating system such as processor.
     * 
     * 将要在jvm里执行中了，但或许它在等待操作系统的资源例如进程。
     */
    RUNNABLE,
    
    /**
     * Thread state for a thread blocked waiting for a monitor lock.
     * A thread in the blocked state is waiting for a monitor lock to enter a synchronized block/method or reenter a synchronized block/method after calling Object#wait().
     * 
     * 在等待 monitor lock(即: 监视器锁) 的线程的状态。
     * 或者： 线程正在等待拿到锁，能让自己进入到synchronized代码块里的状态；
     * 或者： 线程正在等待拿到锁，能让自己在调用了Object.wait之后重新被notify/notifyAll();
     */
    BLOCKED,
    
    /**
     * Thread state for a waiting thread.
     * A thread is in the waiting state due to calling one of the following methods:
     * <ul>
     *   <li>Object.wait with no timeout</li>
     *   <li>Thread.join with no timeout</li>
     *   <li>LockSupport.park</li>
     * </ul>
     *
     * <p>A thread in the waiting state is waiting for another thread to perform a particular action.
     *
     * For example, a thread that has called  Object.wait() on an object is waiting for another thread to call  Object.notify()/Object.notifyAll() on that object. 
     * A thread that has called  Thread.join()  is waiting for a specified thread to terminate.
     * 
     * 等待状态，是由于执行了一下3个方法：
     * 1. Object.wait()
     * 2. Thread.join()
     * 3. LockSupport.park()
     * 4. JUC中的Lock或Condition
     * 这个状态的线程，是在等待别的线程以使自己可以继续执行自己的动作：
     * 线程a被调用了Object.wait()之后等待别的线程再调用notify()/notifyAll()来成全自己；
     * 线程a执行中调用了b.join()后在等待b执行到死-Terminated；
     */
    WAITING,
    
    /**
     * Thread state for a waiting thread with a specified waiting time.
     * A thread is in the timed waiting state due to calling one of the following methods with a specified positive waiting time:
     * <ul>
     *   <li>1. Thread.sleep</li>
     *   <li>2. Object.wait(long)</li>
     *   <li>3. t.join(long)</li>
     *   <li>4. LockSupport.parkNanos(Object , long )</li>
     *   <li>5. LockSupport.parkUntil(Object , long )</li>
     * </ul>
     * 
     * 指定时间的等待状态。线程调用以下方法会进入计时等待：
     * 1. Thread.sleep(long)
     * 2. Object.wait(long)
     * 3. t.join(long)
     * 4. LockSupport.parkNano(Object, long)
     * 5. LockSupport.parkUntil(Object, long)
     */
    TIMED_WAITING,
    
    /**
     * Thread state for a terminated thread.
     * The thread has completed execution.
     * 
     * 已终止了的线程。已经执行结束的线程的状态。
     */
    TERMINATED;
    }

```

##### 线程的六个状态:
1. New (新建)
> 一个new Thread()执行了但是还未start()的线程状态。

2. Runnable (可运行)
> 将要在jvm里执行中了，但或许它在等待操作系统的资源例如进程。   

3. Blocked (阻塞)
>在等待 monitor lock(即: 监视器锁) 的线程的状态。   
或者：正在等拿锁，能让自己进入到synchronized代码块里的状态；   
或者：正在等拿锁，能让自己在调用了Object.wait之后重新被notify/notifyAll(), 以便继续;   

4. Waiting (等待)
> 等待状态，是由于执行了一下几个方法：   
**Object.wait()**   
**Thread.join()**   
**LockSupport.park()**   
**JUC中的 Lock+Condition.await()**  -- 后面详细介绍    
这个状态的线程，是在等待别的线程以使自己可以继续执行自己的动作：    
线程a被调用了Object.wait()之后等待别的线程再调用notify()/notifyAll()来成全自己；   
线程a执行中调用了b.join()后在等待b执行到死-Terminated；   
[Java的LockSupport.park()实现分析](http://www.importnew.com/20428.html)   
[LockSupport的park和unpark的基本使用,以及对线程中断的响应性](http://blog.csdn.net/aitangyong/article/details/38373137)  

5. Timed Waiting (计时等待)
> 指定时间的等待状态。线程调用以下方法会进入计时等待：   
**Object.wait(long)**   
**t.join(long)**   
**LockSupport.parkNano(Object, long)**   
**LockSupport.parkUntil(Object, long)**   
JUC：
**Lock+Condition.await(long time, TimeUnit unit)**   
**Lock.tryLock(long time, TimeUnit unit)**   
**Thread.sleep(long)**   

6. Terminated (终止)
>已终止了的线程。已经执行结束的线程的状态. 情形有：a. 正常执行完毕; b. 没有被捕获的异常终止了run方法而意外死亡.

---
wait/notify/notifyAll -- The current thread must own this object's monitor.   
>IllegalMonitorStateException - 如果当前线程不是此对象监视器的所有者   
如果你调用wait()但是当前线程 不拥有对象监视器(当前线程没占有锁)，会报告此异常；   
如果你调用notify/notifyAll 但是当前线程 不拥有对象监视器(当前线程没占有锁)，会报告此异常；   
