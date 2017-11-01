package com.niewj.common.util.http;

public class NieHttpResponse {
    //http状态码
    private int httpStatus;
    //http返回的数据
    private String content;
    //http 响应头里面返回的主机IP
    private String host;

    public NieHttpResponse() {
    }

    public NieHttpResponse(int httpStatus, String content) {
        this.httpStatus = httpStatus;
        this.content = content;
    }

    public NieHttpResponse(int httpStatus, String content, String host) {
        this.httpStatus = httpStatus;
        this.content = content;
        this.host = host;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isOK() {
        return httpStatus / 100 == 2;
    }

    public boolean isNotResource() {
        return httpStatus == 404;
    }
}
