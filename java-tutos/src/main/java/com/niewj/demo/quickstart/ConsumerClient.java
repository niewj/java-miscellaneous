package com.niewj.demo.quickstart;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class ConsumerClient {

    public static void main(String[] args) {
        // 2. 创建channel
        Connection conn = MqConnUtil.getConn();
        Channel channel = null;
        try {
            channel = conn.createChannel();

            // 3. 声明创建一个queue; 用来consume
            AMQP.Queue.DeclareOk declareOk = channel.queueDeclare(MqConnUtil.QUEUE_NAME, true, false, false, null);
            System.out.println(declareOk.getMessageCount());

            // 4. 创建消费者
//            DefaultConsumer consumer = new DefaultConsumer(channel);
            QueueingConsumer consumer = new QueueingConsumer(channel);

            // 5. 设置消费者
            channel.basicConsume(MqConnUtil.QUEUE_NAME, true, consumer);

            // 6. 获取消息
            try {
                while (true) {

                    // 每发一次休眠 1000-3000毫秒
                    long millis = ThreadLocalRandom.current().nextInt(1000, 3000);
                    Thread.sleep(millis);

                    // consume
                    QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                    System.out.println("consumed in [" + millis + "]ms ====:>>\t " + new String(delivery.getBody()));
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            MqConnUtil.releaseConn(conn);
            MqConnUtil.releaseChannel(channel);
        }

    }
}
