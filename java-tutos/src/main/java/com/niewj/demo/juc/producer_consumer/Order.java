package com.niewj.demo.juc.producer_consumer;

public class Order {
    private long orderId;

    public Order(long orderId) {
        this.orderId = orderId;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "Order{" + "orderId=" + orderId + '}';
    }
}
