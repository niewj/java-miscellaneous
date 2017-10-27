package com.niewj.basic.concurrent.patterns.builder1;

/**
 * Created by weijun.nie on 2017/10/25.
 */
public class TextBuilder extends Builder {

    StringBuffer sbuff = new StringBuffer();

    @Override
    public void buildTitle(String title) {
        sbuff.append(title).append("\n");
    }

    @Override
    public void buildContent(String content) {
        sbuff.append(content);
    }

    @Override
    public Product getProduct() {
        return new Product(sbuff.toString());
    }
}
