package chap2;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * Created by niewj on 2016/12/4.
 */
public class T {

    /**
     * 虚拟一个多线程查询ExecuterService案例，
     *
     * @param args
     */
    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "NIE-00:9092");
        properties.put("metadata.broker.list", "NIE-00:9092");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("serializer.class", "kafka.serializer.StringEncoder");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("request.required.acks", "1");

        KafkaProducer<Integer, String> producer = new KafkaProducer<Integer, String>(properties);
        for (int i = 0; i < 100; i++) {
            String message = "测试消息： " + i;
            ProducerRecord<Integer, String> record = new ProducerRecord<Integer, String>("test1", message);
            producer.send(record);
        }
        producer.close();
    }


}
