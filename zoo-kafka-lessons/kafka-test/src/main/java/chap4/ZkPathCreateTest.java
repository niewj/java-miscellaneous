package chap4;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * countdownlatch
 * Created by niewj on 2016/12/19.
 */
public class ZkPathCreateTest {

    /**
     * 计数：当主线程创建zk连接后，如果状态符合要求，才进行创建动作；
     * 否则，等待watcher线程状态，等等等。。。等符合要求了再countDown,才创建zk目录。
     */
    CountDownLatch cdl = new CountDownLatch(1);


    private void initEphemeralSequence() throws IOException, InterruptedException, KeeperException {
        // 1. create a watcher for zk
        Watcher watcherZk = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (Event.KeeperState.SyncConnected == event.getState()) {
                    System.out.println("状态：：达到同步连接；输出path的状态： path ==> " + event.getState());
                    cdl.countDown(); // 倒计1 -- 过了一关
                }
            }
        };

        // 2. create zk Connection
        ZooKeeper zkConn = new ZooKeeper("NIE-00:2181,NIE-01:2181,NIE-02:2181", 500, watcherZk);

        cdl.await();

        String path = zkConn.create("/nieroot", "test".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
//        String path = zkConn.create("/nieroot", "test".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.in.read(); // block 一下
        System.out.println("path = " + path);


    }

    /**
     * 连接zk，并创建一个目录节点。
     *
     * @throws IOException
     * @throws KeeperException
     * @throws InterruptedException
     */
    private void initPersistent() throws IOException, KeeperException, InterruptedException {

        // 1. watcher for zk
        Watcher watcherZk = new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
                    System.out.println("状态：：达到同步连接；输出path的状态： path ==> " + watchedEvent.getState());
                    cdl.countDown();
                } else {
                    System.out.println("状态没有达到同步连接的状态，暂时不作为。。。。");
                }
            }
        };

        // 2. init zk args
        ZooKeeper zk = new ZooKeeper("NIE-00:2181,NIE-01:2181,NIE-02:2181", 6000, watcherZk);

        cdl.await();

        // 3. create path /controller ephemeral
        /**
         * 无论是ephemeral还是persistent的sequential，都生成十位数字后缀的节点，每次+1;
         * 区别就在于，persistent的sequential程序结束后在zk中是持久化了的；ephemeral的sequential只有运行时才可见，执行完毕就消失了。
         * 都生成形如/controller0000000040的节点十位数字后缀
         */
        String stat = zk.create("/controller", "1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);

//        String stat = zk.create("/controller", "1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println("created path is -->: " + stat);

    }

    public static void main(String[] args) {
        try {
            new ZkPathCreateTest().initPersistent();
//            new ZkPathCreateTest().initEphemeralSequence();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
