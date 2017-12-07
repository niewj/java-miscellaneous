package chap2.com.ns.nie;

import chap2.HashPartitioner;
import kafka.Kafka;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafka.serializer.StringEncoder;

import java.util.Properties;

public class ProducerTest {

    public static void main(String[] args) throws Exception {

        String topic = "test1";
        // 1. Props
        Properties props = new Properties();
        props.put("metadata.broker.list", "NIE-00:9092");
        props.put("partitioner.class", HashPartitioner.class.getName());
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        // 2. producer_conf
        ProducerConfig producerConfig = new ProducerConfig(props);
        // 3. producer object
        Producer<String, String> producer = new Producer<String, String>(producerConfig);
        // 4. make msg
        KeyedMessage<String, String> msg = null;
        for (int i = 0; i < 100; i++) {
            msg = new KeyedMessage<String, String>(topic, "测试消息嘻嘻....\t" + i);
            Thread.sleep(500);
            producer.send(msg);
        }

        System.out.println("hahaha... will close producer...");
        producer.close();
    }

}
