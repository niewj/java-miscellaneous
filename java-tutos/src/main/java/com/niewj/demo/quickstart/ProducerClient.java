package com.niewj.demo.quickstart;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class ProducerClient {

    public static void main(String[] args) {
        // 2. 创建channel
        Connection conn = MqConnUtil.getConn();
        Channel channel = null;
        try {
            channel = conn.createChannel();

            String exchange = "";
            for (int i = 1020; i < 1100; i++) {
                // 每发一次休眠 1000-3000毫秒
                long millis = ThreadLocalRandom.current().nextInt(200, 1000);
                Thread.sleep(millis);
                // publish
                byte[] body = ("[Hello: this is my No." + i + " message ] ~").getBytes();
                channel.basicPublish(exchange, MqConnUtil.QUEUE_NAME, null, body);
                System.out.println("published : " + i + " in millis :" + millis);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            MqConnUtil.releaseConn(conn);
            MqConnUtil.releaseChannel(channel);
        }

    }
}
