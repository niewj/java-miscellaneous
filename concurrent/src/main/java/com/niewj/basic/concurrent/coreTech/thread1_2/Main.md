##### 1.2 线程中断: thread1_2
###### Main#testInterrupt
- Thread#interrupt()
>interrupt() 调用线程t的方法，它将进入中断状态，也就是说此时获取t.isInterrupted()--true

- Thread#interrupted()
> 两个操作：
1-获取线程的中断状态，如果是中断，则true(反之false)
2-若线程的中断状态为true，还要将之中断状态clear掉(也就是非中断状态)
apidoc里有一句话： 如果线程执行了中断操作，调用interrupted()两次后得到的将会是false--从这两点可以推得的。

- Thread#isInterrupted()
> 单纯的获取线程的中断状态
其实源码中：interrupted()和isInterrupted()内部都调用的同一个方法(这个方法有个boolean
参数): 
private native boolean isInterrupted(boolean ClearInterrupted);
区别就在于： interrupted()给传了true,而isInterrupted()传了false;这样的话，传了true还要处理下中断标志位。

- Thread#sleep(long) throws InterruptedException
> sleep的休眠就不说了，之前不解的就是，何时就抛出中断异常？
通过这个例子的运行，不难发现：原来是t.interrupt()被调用了，如果正在sleep然后娘希匹的被中断了，这个时候就会throws中断异常了。
那么问题又来了： 异常要不要处理呢？怎么个处理法？---这里我的理解是：如果线程像这样一直死循环在做某事，你要觉得线程打断是个严重的事，我必须要stop，那就在catch里 手动t.interrupt()一下，然后可以判断中断状态给break掉像例子中似的；

---

#####  核心技术多线程一章第二节说了，sleep的中断异常--不要在catch里什么也不做：
要么你interrupt()了，在程序里处理它，或者更好的方式是，你throws抛给调用者，让他处理。
哈哈！ 

OK
