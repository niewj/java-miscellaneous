package com.niewj.common.util.http;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.net.ssl.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * 获取HttpClient.
 */
public class HttpClientFactory {
    public final static int CONNECTION_REQUEST_TIMEOUT = 5 * 1000;
    public final static int CONNECTION_TIMEOUT = 5 * 1000;
    public final static int SOCKET_TIME_OUT = 5 * 1000;
    public final static int MAX_CONN_TOTAL = 256;
    public final static int MAX_CONN_PER_ROUTE = 32;

    /**
     * 该方法创建的HttpClient不会重试,并且所有的超时设置都为默认值
     *
     * @return
     */
    public static HttpClient create() {
        return create(0, 0, 0, 0, 0);
    }


    /**
     * 该方法创建的HttpClient可以设置重试策略;如果maxRetry设置为0,则不会进行重试
     *
     * @param connRequestTimeout
     * @param connTimeout
     * @param socketTimeout
     * @param maxConn
     * @param maxConnPerRoute
     * @return
     */
    public static HttpClient create(int connRequestTimeout, int connTimeout, int socketTimeout, int maxConn, int maxConnPerRoute) {
        int connRequestTimeoutCopy = connRequestTimeout > 0 ? connRequestTimeout : CONNECTION_REQUEST_TIMEOUT;
        int connTimeoutCopy = connTimeout > 0 ? connTimeout : CONNECTION_TIMEOUT;
        int socketTimeoutCopy = socketTimeout > 0 ? socketTimeout : SOCKET_TIME_OUT;
        int maxConnCopy = maxConn > 0 ? maxConn : MAX_CONN_TOTAL;
        int maxConnPerRouteCopy = maxConnPerRoute > 0 ? maxConnPerRoute : MAX_CONN_PER_ROUTE;


        /**
         * ConnectionRequestTimeout 从连接池获取连接的超时时间
         * ConnectTimeout 建立连接的超时时间
         * SocketTimeout 读取数据时的超时时间
         */
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(connRequestTimeoutCopy)
                .setConnectTimeout(connTimeoutCopy)
                .setSocketTimeout(socketTimeoutCopy)
                .build();

        /**
         * MaxConnTotal 连接池最大连接数
         * MaxConnPerRoute 每个路由最大的连接数
         * RetryHandler 重试处理器
         * ServiceUnavailableRetryStrategy 服务不可用时的重试策略
         */
        return HttpClientBuilder.create()
                .setUserAgent("NIE HTTP Client")
                .setMaxConnTotal(maxConnCopy)
                .setMaxConnPerRoute(maxConnPerRouteCopy)
                .setDefaultRequestConfig(requestConfig)
                .setSSLSocketFactory(createSSLFactory())
                .build();
    }


    private static SSLConnectionSocketFactory createSSLFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[]{};
                }
            }}, new java.security.SecureRandom());

            return new SSLConnectionSocketFactory(sslContext, new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("get SSLFactory is failed,exception: ", e);
        }
    }
}