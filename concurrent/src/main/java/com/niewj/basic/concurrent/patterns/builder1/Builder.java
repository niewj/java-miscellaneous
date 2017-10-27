package com.niewj.basic.concurrent.patterns.builder1;

/**
 * Created by weijun.nie on 2017/10/19.
 */
public abstract class Builder {

    public abstract void buildTitle(String title); // 创建报文title

    public abstract void buildContent(String content); // 创建报文content

    public abstract Product getProduct();  // 获取产品
}
