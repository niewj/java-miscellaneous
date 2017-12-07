package chap2;

import kafka.producer.Partitioner;

/**
 * 随机达到某一个节点
 * Created by niewj on 2016/11/30.
 */
public class RandomPartitioner implements Partitioner {
    @Override
    public int partition(Object key, int numPartitions) {
        /**
         * 获得一个0到1000之间的随机数
         */
        int randomNum = new Double(Math.random() * 1000).intValue();

        return randomNum % numPartitions;
    }

}
