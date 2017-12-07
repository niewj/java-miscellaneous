package chap2;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafka.serializer.StringEncoder;

import java.util.Properties;

public class ProducerDemo {

    //    static private final String ZOOKEEPER = "NIE-00:2181";
    static private final String BROKER_LIST = "NIE-00:9092";
    static private final String TOPIC = "test1";
    //  static private final int PARTITIONS = TopicAdmin.partitionNum(ZOOKEEPER, TOPIC);
    static private final int PARTITIONS = 2;


    public static void main(String[] args) throws Exception {
        Producer<String, String> producer = initProducer();
        sendOne(producer, TOPIC);
    }

    private static Producer<String, String> initProducer() {
        Properties props = new Properties();
        props.put("metadata.broker.list", BROKER_LIST);
        // props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("serializer.class", StringEncoder.class.getName());
        props.put("partitioner.class", HashPartitioner.class.getName());
        // props.put("partitioner.class", "kafka.producer.DefaultPartitioner");
//    props.put("compression.codec", "0");
        props.put("producer.type", "async");
        props.put("batch.num.messages", "3");
        props.put("queue.buffer.max.ms", "10000000");
        props.put("queue.buffering.max.messages", "1000000");
        props.put("queue.enqueue.timeout.ms", "20000000");

        ProducerConfig config = new ProducerConfig(props);
        Producer<String, String> producer = new Producer<String, String>(config);
        return producer;
    }

    public static void sendOne(Producer<String, String> producer, String topic) throws InterruptedException {
        KeyedMessage<String, String> message1 = new KeyedMessage<String, String>(topic, "33", "测试消息嘻嘻");
        producer.send(message1);
        producer.close();
    }

}
