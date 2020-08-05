package com.niewj.demo.quickstart;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MqConnUtil {

    public static final String QUEUE_NAME = "dstest_mq";

    private static ConnectionFactory factory = new ConnectionFactory();



    static {
        factory.setHost("10.16.30.125");
        factory.setPort(5672);
        factory.setUsername("dstest");
        factory.setPassword("dstestmq");
        factory.setVirtualHost("/");
    }

    public static Connection getConn() {
        Connection conn = null;

        try {
            // 1. 获取连接
            conn = factory.newConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static void releaseConn(Connection conn) {
        if (conn != null && conn.isOpen()) {
            try {
                conn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void releaseChannel(Channel channel) {
        if (channel != null && channel.isOpen()) {
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
    }

}
