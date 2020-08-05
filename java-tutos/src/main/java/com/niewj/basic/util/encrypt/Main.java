package com.niewj.basic.util.encrypt;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        // 1. 普通字符串-base64加密
        String pureString = Base64Util.encryptBase64("绝尘");
        // 2. 文件转成base64字符串-比如图片
        String fileBase64String = Base64Util.encryptFileBase64("E:\\Media\\IMG\\BGimg\\captain.jpg");

        System.out.println(pureString);
        System.out.println(fileBase64String);

        // 1. 普通字符串-base64解密
        System.out.println(Base64Util.decryptBase64(pureString));
        // 2. base64加密的文件字符串解密为文件-比如图片
        System.out.println(Base64Util.decryptFileBase64(fileBase64String, "d:/chuanzhang", "jpg").getAbsolutePath());
    }
}
