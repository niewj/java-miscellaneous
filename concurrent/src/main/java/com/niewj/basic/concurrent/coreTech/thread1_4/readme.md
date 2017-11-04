## 1.4 线程属性&未捕捉异常: thread1_4
### 1.  线程属性 @see ThreadProperty
Demo
- 线程优先级和执行次数是正相关关系
```java
appleThread.setPriority(Thread.MIN_PRIORITY);
penThread.setPriority(Thread.MAX_PRIORITY);
```
>线程中断:pen    
线程中断:apple    
>< I have [6704689] apple    
>< I have [7652479] pen    

```java
appleThread.setPriority(Thread.NORM_PRIORITY);
penThread.setPriority(Thread.NORM_PRIORITY);
```
>优先级相同，但不保证执行一样多次。  
- setDaemon(boolean) 守护线程：
>如果JVM总只剩守护线程，就会推出执行。

### 2.  未捕捉异常UnCaughtException
异常如果自己try-catch了，可以自己处理；
如果自己没有try-catch, 异常爆发后，可以通过 UnCaughtedExceptionHandler来做处理：比如 记录日志等；
可以自定义 
OK
