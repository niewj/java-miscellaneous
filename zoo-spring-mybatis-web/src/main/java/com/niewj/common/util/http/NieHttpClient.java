package com.niewj.common.util.http;

import com.google.common.collect.Lists;
import com.niewj.common.util.CollectionUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.auth.AUTH;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.BufferedHeader;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * HttpClient 工具类
 */
public class NieHttpClient {
    public final static String UTF8 = "UTF-8";
    public final static int CONNECTION_REQUEST_TIMEOUT = 5 * 1000;
    public final static int CONNECTION_TIMEOUT = 5 * 1000;
    public final static int SOCKET_TIME_OUT = 5 * 1000;
    public final static String CONTENT_TYPE_JSON = "application/json;charset=utf-8";

    //从连接池获取连接、连接建立、读取数据超时时间都设置为5s,且不会重试,默认编码UTF-8
    private static HttpClient httpClient = HttpClientFactory.create();


    /**
     * 执行post请求,默认编码为UTF-8;
     * connTimeout、socketTimeout如果小于等于0,则默认为5s
     *
     * @param serviceUrl
     * @param connTimeout
     * @param socketTimeout
     * @return
     * @throws Exception
     */
    public static NieHttpResponse post(String serviceUrl, int connTimeout, int socketTimeout) throws Exception {
        return post(serviceUrl, connTimeout, socketTimeout, UTF8);
    }

    /**
     * 执行post请求;
     * connTimeout、socketTimeout如果小于等于0,则默认为5s
     *
     * @param serviceUrl
     * @param connTimeout
     * @param socketTimeout
     * @param charset
     * @return
     * @throws Exception
     */
    public static NieHttpResponse post(String serviceUrl, int connTimeout, int socketTimeout, String charset) throws Exception {
        HttpResponse response = null;
        try {
            //构造请求配置
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                    .setConnectTimeout(connTimeout > 0 ? connTimeout : CONNECTION_TIMEOUT)
                    .setSocketTimeout(socketTimeout > 0 ? socketTimeout : SOCKET_TIME_OUT)
                    .build();

            HttpUriRequest request = RequestBuilder.post(serviceUrl)
                    .setCharset(Charset.forName(charset))
                    .setConfig(requestConfig)
                    .build();

            //执行请求
            response = httpClient.execute(request);

            //判断请求返回码
            int statusCode = response.getStatusLine().getStatusCode();
            //会主动关闭流
            String result = EntityUtils.toString(response.getEntity(), charset);

            return new NieHttpResponse(statusCode, result);
        } catch (Exception e) {
            throw e;
        } finally {
            if (response != null) {
                try {
                    ((CloseableHttpResponse) response).close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 执行post请求,默认编码为UTF-8;
     * connTimeout、socketTimeout如果小于等于0,则默认为5s
     *
     * @param serviceUrl
     * @param paramMap
     * @param connTimeout
     * @param socketTimeout
     * @return
     * @throws Exception
     */
    public static NieHttpResponse post(String serviceUrl, Map<String, String> paramMap, int connTimeout, int socketTimeout) throws Exception {
        return post(serviceUrl, paramMap, connTimeout, socketTimeout, UTF8);
    }

    public static NieHttpResponse post(String serviceUrl, Map<String, String> paramMap, int connTimeout, int socketTimeout, String charset) throws Exception {
        return post(serviceUrl, paramMap, null, connTimeout, socketTimeout, charset);
    }

    /**
     * 执行post请求;
     * connTimeout、socketTimeout如果小于等于0,则默认为5s
     *
     * @param serviceUrl
     * @param paramMap
     * @param connTimeout
     * @param socketTimeout
     * @param charset
     * @return
     * @throws Exception
     */
    public static NieHttpResponse post(String serviceUrl, Map<String, String> paramMap, Map<String, String> headers, int connTimeout, int socketTimeout, String charset) throws Exception {
        if (paramMap == null || paramMap.isEmpty()) {
            throw new Exception("paramMap is null or empty");
        }

        List<NameValuePair> param = Lists.newArrayListWithExpectedSize(paramMap.size());
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            param.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        HttpResponse response = null;
        try {
            //构造请求参数
            HttpEntity entity = new UrlEncodedFormEntity(param, UTF8);

            //构造请求配置
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                    .setConnectTimeout(connTimeout > 0 ? connTimeout : CONNECTION_TIMEOUT)
                    .setSocketTimeout(socketTimeout > 0 ? socketTimeout : SOCKET_TIME_OUT)
                    .build();

            //构造请求命令
            HttpUriRequest request = RequestBuilder.post(serviceUrl)
                    .setCharset(Charset.forName(charset))
                    .setConfig(requestConfig)
                    .setEntity(entity)
                    .build();

            //设置请求头
            if (CollectionUtil.notEmpty(headers)) {
                for (String key : headers.keySet()) {
                    request.setHeader(key, headers.get(key));
                }
            }

            //执行请求
            response = httpClient.execute(request);

            //判断请求返回码
            int statusCode = response.getStatusLine().getStatusCode();
            //会主动关闭流
            String result = EntityUtils.toString(response.getEntity(), charset);

            return new NieHttpResponse(statusCode, result);
        } catch (Exception e) {
            throw e;
        } finally {
            if (response != null) {
                try {
                    ((CloseableHttpResponse) response).close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 执行post请求,数据以json形式提交,默认编码为UTF-8;
     * connTimeout、socketTimeout如果小于等于0,则默认为5s
     *
     * @param serviceUrl
     * @param param
     * @param connTimeout
     * @param socketTimeout
     * @return
     * @throws Exception
     */
    public static NieHttpResponse postToJson(String serviceUrl, String param, int connTimeout, int socketTimeout) throws Exception {
        return postToJson(serviceUrl, param, null, connTimeout, socketTimeout, UTF8);
    }

    /**
     * 执行post请求,数据以json形式提交,默认编码为UTF-8;
     * connTimeout、socketTimeout如果小于等于0,则默认为5s
     *
     * @param serviceUrl    请求url
     * @param param         请求参数,json格式字符串
     * @param headers       请求头
     * @param connTimeout
     * @param socketTimeout
     * @return
     * @throws Exception
     */
    public static NieHttpResponse postToJson(String serviceUrl, String param, Map<String, String> headers, int connTimeout, int socketTimeout) throws Exception {
        return postToJson(serviceUrl, param, headers, connTimeout, socketTimeout, UTF8);
    }

    /**
     * 执行post请求,数据以application/json形式提交
     * connTimeout、socketTimeout如果小于等于0,则默认为5s
     *
     * @param serviceUrl    请求url
     * @param param         请求参数,json格式字符串
     * @param headers       请求头
     * @param connTimeout
     * @param socketTimeout
     * @param charset
     * @return
     * @throws Exception
     */
    public static NieHttpResponse postToJson(String serviceUrl, String param, Map<String, String> headers, int connTimeout, int socketTimeout, String charset) throws Exception {
        if (param == null || param.trim().isEmpty()) {
            throw new Exception("param is null or empty");
        }

        HttpResponse response = null;
        try {
            //构造请求参数
            StringEntity entity = new StringEntity(param, charset);
            entity.setContentType(CONTENT_TYPE_JSON);

            //构造请求配置
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                    .setConnectTimeout(connTimeout > 0 ? connTimeout : CONNECTION_TIMEOUT)
                    .setSocketTimeout(socketTimeout > 0 ? socketTimeout : SOCKET_TIME_OUT)
                    .build();

            //构造请求命令
            HttpUriRequest request = RequestBuilder.post(serviceUrl)
                    .setCharset(Charset.forName(charset))
                    .setConfig(requestConfig)
                    .setEntity(entity)
                    .build();

            //设置请求头
            if (CollectionUtil.notEmpty(headers)) {
                for (String key : headers.keySet()) {
                    request.setHeader(key, headers.get(key));
                }
            }

            response = httpClient.execute(request);

            //获取响应状态码和响应主机IP
            int statusCode = response.getStatusLine().getStatusCode();

            Header header = response.getFirstHeader(NieHttpContext.RESPONSE_HOST);
            String host = (header != null) ? header.getValue() : null;

            //获取响应数据,会主动关闭流
            String result = EntityUtils.toString(response.getEntity(), charset);

            return new NieHttpResponse(statusCode, result, host);
        } catch (Exception e) {
            throw e;
        } finally {
            if (response != null) {
                try {
                    ((CloseableHttpResponse) response).close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static NieHttpResponse postToString(String serviceUrl, String param, Map<String, String> headers, int connTimeout, int socketTimeout) throws Exception {
        if (param == null || param.trim().isEmpty()) {
            throw new Exception("param is null or empty");
        }

        HttpResponse response = null;
        try {
            //构造请求参数
            StringEntity entity = new StringEntity(param, UTF8);

            //构造请求配置
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                    .setConnectTimeout(connTimeout > 0 ? connTimeout : CONNECTION_TIMEOUT)
                    .setSocketTimeout(socketTimeout > 0 ? socketTimeout : SOCKET_TIME_OUT)
                    .build();

            //构造请求命令
            HttpUriRequest request = RequestBuilder.post(serviceUrl)
                    .setCharset(Charset.forName(UTF8))
                    .setConfig(requestConfig)
                    .setEntity(entity)
                    .build();

            //设置请求头
            if (CollectionUtil.notEmpty(headers)) {
                for (String key : headers.keySet()) {
                    request.setHeader(key, headers.get(key));
                }
            }

            response = httpClient.execute(request);

            //获取响应状态码和响应主机IP
            int statusCode = response.getStatusLine().getStatusCode();

            Header header = response.getFirstHeader(NieHttpContext.RESPONSE_HOST);
            String host = (header != null) ? header.getValue() : null;

            //获取响应数据,会主动关闭流
            String result = EntityUtils.toString(response.getEntity(), UTF8);

            return new NieHttpResponse(statusCode, result, host);
        } catch (Exception e) {
            throw e;
        } finally {
            if (response != null) {
                try {
                    ((CloseableHttpResponse) response).close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 执行post请求,数据以byte array形式提交,默认编码为UTF-8
     * connTimeout、socketTimeout如果小于等于0,则默认为5s
     *
     * @param serviceUrl
     * @param param
     * @param connTimeout
     * @param socketTimeout
     * @return
     * @throws Exception
     */
    public static NieHttpResponse postToByteArray(String serviceUrl, String param, int connTimeout, int socketTimeout) throws Exception {
        return postToByteArray(serviceUrl, param, connTimeout, socketTimeout, UTF8);
    }

    /**
     * 执行post请求,数据以byte array形式提交
     * connTimeout、socketTimeout如果小于等于0,则默认为5s
     *
     * @param serviceUrl
     * @param param
     * @param connTimeout
     * @param socketTimeout
     * @param charset
     * @return
     * @throws Exception
     */
    public static NieHttpResponse postToByteArray(String serviceUrl, String param, int connTimeout, int socketTimeout, String charset) throws Exception {
        if (param == null || param.trim().isEmpty()) {
            throw new Exception("param is null or empty");
        }

        HttpResponse response = null;
        try {
            //构造请求配置
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                    .setConnectTimeout(connTimeout > 0 ? connTimeout : CONNECTION_TIMEOUT)
                    .setSocketTimeout(socketTimeout > 0 ? socketTimeout : SOCKET_TIME_OUT)
                    .build();

            //构造请求命令
            HttpUriRequest request = RequestBuilder.post(serviceUrl)
                    .setCharset(Charset.forName(charset))
                    .setConfig(requestConfig)
                    .setHeader("Content-Type", "application/json")
                    .setEntity(new ByteArrayEntity(param.getBytes(UTF8)))
                    .build();

            //执行请求
            response = httpClient.execute(request);

            //判断请求返回码
            int statusCode = response.getStatusLine().getStatusCode();
            //会主动关闭流
            String result = EntityUtils.toString(response.getEntity(), charset);

            return new NieHttpResponse(statusCode, result);
        } catch (Exception e) {
            throw e;
        } finally {
            if (response != null) {
                try {
                    ((CloseableHttpResponse) response).close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 需要身份验证的Post请求,支持请求header设置,默认编码UTF-8
     * 数据以application/json形式提交
     * headers可以不设置值
     *
     * @param serviceUrl
     * @param param
     * @param headers
     * @param userName      身份验证需要的用户名
     * @param password      身份验证需要的密码
     * @param connTimeout
     * @param socketTimeout
     * @return
     * @throws Exception
     */
    public static NieHttpResponse post(String serviceUrl, String param, Map<String, String> headers, String userName, String password, int connTimeout, int socketTimeout) throws Exception {
        return post(serviceUrl, param, headers, userName, password, connTimeout, socketTimeout, UTF8);
    }

    /**
     * 需要身份验证的Post请求,支持请求header设置
     * 数据以application/json形式提交
     * headers可以不设置值
     *
     * @param serviceUrl
     * @param param
     * @param headers
     * @param userName      身份验证需要的用户名
     * @param password      身份验证需要的密码
     * @param connTimeout
     * @param socketTimeout
     * @param charset
     * @return
     * @throws Exception
     */
    public static NieHttpResponse post(String serviceUrl, String param, Map<String, String> headers, String userName, String password, int connTimeout, int socketTimeout, String charset) throws Exception {
        if (param == null || param.trim().isEmpty()) {
            throw new Exception("param is null or empty");
        }
        if (userName == null || userName.isEmpty() || password == null || password.isEmpty()) {
            throw new Exception("userName or password is null or empty");
        }

        HttpResponse response = null;
        try {
            //构造请求参数
            StringEntity entity = new StringEntity(param, UTF8);
            entity.setContentType("application/json");

            //构造请求配置
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                    .setConnectTimeout(connTimeout > 0 ? connTimeout : CONNECTION_TIMEOUT)
                    .setSocketTimeout(socketTimeout > 0 ? socketTimeout : SOCKET_TIME_OUT)
                    .build();

            //构造请求命令
            HttpUriRequest request = RequestBuilder.post(serviceUrl)
                    .setCharset(Charset.forName(charset))
                    .setConfig(requestConfig)
                    .setEntity(entity)
                    .build();

            request.addHeader(authenticate(userName, password, charset));
            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    request.addHeader(entry.getKey(), entry.getValue());
                }
            }

            //执行请求
            response = httpClient.execute(request);

            //判断请求返回码
            int statusCode = response.getStatusLine().getStatusCode();
            //会主动关闭流
            String result = EntityUtils.toString(response.getEntity(), charset);

            return new NieHttpResponse(statusCode, result);
        } catch (Exception e) {
            throw e;
        } finally {
            if (response != null) {
                try {
                    ((CloseableHttpResponse) response).close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 执行get请求;默认编码为UTF-8
     * connTimeout、socketTimeout如果小于等于0,则默认为5s
     *
     * @param serviceUrl
     * @param connTimeout
     * @param socketTimeout
     * @return
     * @throws Exception
     */
    public static NieHttpResponse get(String serviceUrl, int connTimeout, int socketTimeout) throws Exception {
        return get(serviceUrl, connTimeout, socketTimeout, UTF8);
    }

    /**
     * 执行get请求;
     * connTimeout、socketTimeout如果小于等于0,则默认为5s
     *
     * @param serviceUrl
     * @param connTimeout
     * @param socketTimeout
     * @return
     * @throws Exception
     */
    public static NieHttpResponse get(String serviceUrl, int connTimeout, int socketTimeout, String charset) throws Exception {
        HttpResponse response = null;
        try {
            //构造请求配置
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                    .setConnectTimeout(connTimeout > 0 ? connTimeout : CONNECTION_TIMEOUT)
                    .setSocketTimeout(socketTimeout > 0 ? socketTimeout : SOCKET_TIME_OUT)
                    .build();

            //构造请求命令
            HttpUriRequest request = RequestBuilder.get(serviceUrl)
                    .setCharset(Charset.forName(charset))
                    .setConfig(requestConfig)
                    .build();

            //执行请求
            response = httpClient.execute(request);

            //判断请求返回码
            int statusCode = response.getStatusLine().getStatusCode();
            //会主动关闭流
            String result = EntityUtils.toString(response.getEntity(), charset);

            return new NieHttpResponse(statusCode, result);
        } catch (Exception e) {
            throw e;
        } finally {
            if (response != null) {
                try {
                    ((CloseableHttpResponse) response).close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 执行get请求;默认编码为UTF-8
     * connTimeout、socketTimeout如果小于等于0,则默认为5s
     *
     * @param serviceUrl
     * @param params
     * @param connTimeout
     * @param socketTimeout
     * @return
     * @throws Exception
     */
    public static NieHttpResponse get(String serviceUrl, Map<String, String> params, int connTimeout, int socketTimeout) throws Exception {
        return get(serviceUrl, params, connTimeout, socketTimeout, UTF8);
    }

    public static NieHttpResponse get(String serviceUrl, Map<String, String> params, int connTimeout, int socketTimeout, String charset) throws Exception {
        return get(serviceUrl, params, null, connTimeout, socketTimeout, charset);
    }

    /**
     * 执行get请求;
     * connTimeout、socketTimeout如果小于等于0,则默认为5s
     *
     * @param serviceUrl
     * @param params
     * @return
     * @throws Exception
     */
    public static NieHttpResponse get(String serviceUrl, Map<String, String> params, Map<String, String> headers, int connTimeout, int socketTimeout, String charset) throws Exception {
        HttpResponse response = null;
        try {
            //构造请求配置
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                    .setConnectTimeout(connTimeout > 0 ? connTimeout : CONNECTION_TIMEOUT)
                    .setSocketTimeout(socketTimeout > 0 ? socketTimeout : SOCKET_TIME_OUT)
                    .build();

            //构造请求命令
            RequestBuilder requestBuilder = RequestBuilder.get(serviceUrl)
                    .setCharset(Charset.forName(charset))
                    .setConfig(requestConfig);

            //设置请求参数
            if (CollectionUtil.notEmpty(params)) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    requestBuilder.addParameter(entry.getKey(), entry.getValue());
                }
            }

            HttpUriRequest request = requestBuilder.build();
            //设置请求头
            if (CollectionUtil.notEmpty(headers)) {
                for (String key : headers.keySet()) {
                    request.setHeader(key, headers.get(key));
                }
            }
            //执行请求
            response = httpClient.execute(request);
            //判断请求返回码
            int statusCode = response.getStatusLine().getStatusCode();
            //会主动关闭流
            String result = EntityUtils.toString(response.getEntity(), charset);

            return new NieHttpResponse(statusCode, result);
        } catch (Exception e) {
            throw e;
        } finally {
            if (response != null) {
                try {
                    ((CloseableHttpResponse) response).close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 需要身份验证的get请求,默认编码UTF-8
     *
     * @param serviceUrl
     * @param userName      身份验证的用户名
     * @param password      身份验证的密码
     * @param connTimeout
     * @param socketTimeout
     * @return
     * @throws Exception
     */
    public static NieHttpResponse get(String serviceUrl, String userName, String password, int connTimeout, int socketTimeout) throws Exception {
        return get(serviceUrl, userName, password, connTimeout, socketTimeout, UTF8);
    }

    /**
     * 需要身份验证的get请求,默认编码UTF-8
     *
     * @param serviceUrl
     * @param userName      身份验证的用户名
     * @param password      身份验证的密码
     * @param connTimeout
     * @param socketTimeout
     * @param charset
     * @return
     * @throws Exception
     */
    public static NieHttpResponse get(String serviceUrl, String userName, String password, int connTimeout, int socketTimeout, String charset) throws Exception {
        if (userName == null || userName.isEmpty() || password == null || password.isEmpty()) {
            throw new Exception("userName or password is null or empty");
        }

        HttpResponse response = null;
        try {
            //构造请求配置
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                    .setConnectTimeout(connTimeout > 0 ? connTimeout : CONNECTION_TIMEOUT)
                    .setSocketTimeout(socketTimeout > 0 ? socketTimeout : SOCKET_TIME_OUT)
                    .build();

            //构造请求命令
            HttpUriRequest request = RequestBuilder.get(serviceUrl)
                    .setCharset(Charset.forName(charset))
                    .setConfig(requestConfig)
                    .build();

            request.addHeader(authenticate(userName, password, charset));

            //执行请求
            response = httpClient.execute(request);

            //判断请求返回码
            int statusCode = response.getStatusLine().getStatusCode();
            String result = EntityUtils.toString(response.getEntity(), charset);

            return new NieHttpResponse(statusCode, result);
        } catch (Exception e) {
            throw e;
        } finally {
            if (response != null) {
                try {
                    ((CloseableHttpResponse) response).close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static Header authenticate(final String name, final String password, final String charset) throws AuthenticationException {
        Args.notNull(name, "credentials name");
        Args.notNull(password, "credentials password");
        Args.notNull(charset, "credentials charset");

        final StringBuilder tmp = new StringBuilder();
        tmp.append(name).append(":").append(password);

        final Base64 base64codec = new Base64();
        final byte[] base64password = base64codec.encode(
                EncodingUtils.getBytes(tmp.toString(), charset));

        final CharArrayBuffer buffer = new CharArrayBuffer(32);

        buffer.append(AUTH.WWW_AUTH_RESP);
        buffer.append(": Basic ");
        buffer.append(base64password, 0, base64password.length);

        return new BufferedHeader(buffer);
    }

    public static NieHttpResponse fileUpload(File file, Map<String, String> otherParam, String serviceUrl) throws IOException {
        return fileUpload(file, otherParam, serviceUrl, CONNECTION_TIMEOUT, SOCKET_TIME_OUT);
    }


    public static NieHttpResponse fileUpload(File file, Map<String, String> otherParam, String serviceUrl, int connTimeout, int socketTimeout) throws IOException {
        //构造请求配置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                .setConnectTimeout(connTimeout > 0 ? connTimeout : CONNECTION_TIMEOUT)
                .setSocketTimeout(socketTimeout > 0 ? socketTimeout : SOCKET_TIME_OUT)
                .build();

        FileBody fileBody = new FileBody(file, ContentType.create("application/octet-stream", Consts.UTF_8));
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        multipartEntityBuilder.addPart("fileData", fileBody);

        if (!CollectionUtils.isEmpty(otherParam)) {
            for (String key : otherParam.keySet()) {
                if (StringUtils.isBlank(key)) continue;

                multipartEntityBuilder.addPart(key, new StringBody(otherParam.get(key), ContentType.create("text/plain", Consts.UTF_8)));
            }
        }

        HttpEntity entity = multipartEntityBuilder.build();

        HttpUriRequest request = RequestBuilder.post(serviceUrl)
                .setCharset(Consts.UTF_8)
                .setConfig(requestConfig)
                .setEntity(entity)
                .build();

        HttpResponse response = httpClient.execute(request);

        //判断请求返回码
        int statusCode = response.getStatusLine().getStatusCode();
        String result = EntityUtils.toString(response.getEntity(), Consts.UTF_8);

        return new NieHttpResponse(statusCode, result);
    }

    public static NieHttpResponse fileDownload(File file, Map<String, String> otherParam, String serviceUrl, int connTimeout, int socketTimeout) throws IOException {
        //构造请求配置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                .setConnectTimeout(connTimeout > 0 ? connTimeout : CONNECTION_TIMEOUT)
                .setSocketTimeout(socketTimeout > 0 ? socketTimeout : SOCKET_TIME_OUT)
                .build();

        FileBody fileBody = new FileBody(file, ContentType.create("application/octet-stream", Consts.UTF_8));
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        multipartEntityBuilder.addPart("fileData", fileBody);

        if (!CollectionUtils.isEmpty(otherParam)) {
            for (String key : otherParam.keySet()) {
                if (StringUtils.isBlank(key)) continue;

                multipartEntityBuilder.addPart(key, new StringBody(otherParam.get(key), ContentType.create("text/plain", Consts.UTF_8)));
            }
        }

        HttpEntity entity = multipartEntityBuilder.build();

        HttpUriRequest request = RequestBuilder.post(serviceUrl)
                .setCharset(Consts.UTF_8)
                .setConfig(requestConfig)
                .setEntity(entity)
                .build();

        HttpResponse response = httpClient.execute(request);

        //判断请求返回码
        int statusCode = response.getStatusLine().getStatusCode();
        String result = EntityUtils.toString(response.getEntity(), Consts.UTF_8);

        return new NieHttpResponse(statusCode, result);
    }

    /**
     * 上传二进制文件和文本参数
     *
     * @param fileParam
     * @param otherParam
     * @param serviceUrl
     * @param connTimeout
     * @param socketTimeout
     * @return
     * @throws IOException
     */
    public static NieHttpResponse fileUpload(Map<String, File> fileParam, Map<String, String> otherParam, String serviceUrl, int connTimeout, int socketTimeout) throws IOException {
        //构造请求配置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                .setConnectTimeout(connTimeout > 0 ? connTimeout : CONNECTION_TIMEOUT)
                .setSocketTimeout(socketTimeout > 0 ? socketTimeout : SOCKET_TIME_OUT)
                .build();

        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        // 二进制
        if (!CollectionUtils.isEmpty(fileParam)) {
            for (String key : fileParam.keySet()) {
                if (StringUtils.isBlank(key)) continue;
                multipartEntityBuilder.addPart(key, new FileBody(fileParam.get(key), ContentType.create("application/octet-stream", Consts.UTF_8)));
            }
        }

        // 文本
        if (!CollectionUtils.isEmpty(otherParam)) {
            for (String key : otherParam.keySet()) {
                if (StringUtils.isBlank(key)) continue;

                multipartEntityBuilder.addPart(key, new StringBody(otherParam.get(key), ContentType.create("text/plain", Consts.UTF_8)));
            }
        }

        HttpEntity entity = multipartEntityBuilder.build();

        HttpUriRequest request = RequestBuilder.post(serviceUrl)
                .setCharset(Consts.UTF_8)
                .setConfig(requestConfig)
                .setEntity(entity)
                .build();

        HttpResponse response = httpClient.execute(request);

        //判断请求返回码
        int statusCode = response.getStatusLine().getStatusCode();
        String result = EntityUtils.toString(response.getEntity(), Consts.UTF_8);

        return new NieHttpResponse(statusCode, result);
    }

    /**
     * 执行Put请求
     */
    public static NieHttpResponse put(String serviceUrl, int connTimeout, int socketTimeout) throws Exception {
        return put(serviceUrl, null, null, connTimeout, socketTimeout, UTF8);
    }


    public static NieHttpResponse put(String serviceUrl, String param, int connTimeout, int socketTimeout) throws Exception {
        return put(serviceUrl, param, null, connTimeout, socketTimeout, UTF8);
    }

    public static NieHttpResponse put(String serviceUrl, Map<String, String> headers, int connTimeout, int socketTimeout) throws Exception {
        return put(serviceUrl, null, headers, connTimeout, socketTimeout, UTF8);
    }

    public static NieHttpResponse put(String serviceUrl, String param, Map<String, String> headers, int connTimeout, int socketTimeout) throws Exception {
        return put(serviceUrl, param, headers, connTimeout, socketTimeout, UTF8);
    }

    public static NieHttpResponse put(String serviceUrl, String param, Map<String, String> headers, int connTimeout, int socketTimeout, String charset) throws Exception {

        HttpResponse response = null;
        try {
            //构造请求参数
            StringEntity entity = null;
            if (param != null && param.trim().length() > 0) {
                entity = new StringEntity(param, charset);
                entity.setContentType(CONTENT_TYPE_JSON);
            }

            //构造请求配置
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                    .setConnectTimeout(connTimeout > 0 ? connTimeout : CONNECTION_TIMEOUT)
                    .setSocketTimeout(socketTimeout > 0 ? socketTimeout : SOCKET_TIME_OUT)
                    .build();

            //构造请求
            HttpUriRequest request = RequestBuilder.put(serviceUrl)
                    .setCharset(Charset.forName(charset))
                    .setEntity(entity)
                    .setConfig(requestConfig)
                    .build();

            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    request.addHeader(entry.getKey(), entry.getValue());
                }
            }

            //执行请求
            response = httpClient.execute(request);
            //判断请求返回码
            int statusCode = response.getStatusLine().getStatusCode();
            //会主动关闭流
            String result = EntityUtils.toString(response.getEntity(), charset);
            return new NieHttpResponse(statusCode, result);
        } catch (Exception e) {
            throw e;
        } finally {
            if (response != null) {
                try {
                    ((CloseableHttpResponse) response).close();
                } catch (IOException e) {
                }
            }
        }
    }

}
