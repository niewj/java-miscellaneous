package com.niewj.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


/**
 * 线程安全工具类
 */
public class ThreadLocalUtil {

    private static ThreadLocal<Map<String, Object>> local = new ThreadLocal<Map<String, Object>>();

    public static void setValue(String key, Object value) {
        Map<String, Object> map = local.get();
        if (map == null) {
            map = new HashMap<String, Object>();
            local.set(map);
        }
        map.put(key, value);
    }

    public static Object getValue(String key) {
        Map<String, Object> map = local.get();
        if (map == null) {
            return null;
        }
        return map.get(key);

    }

    public static void clear() {
        local.set(null);
    }

    public static String REQUEST = "request";

    public static String RESPONSE = "response";

    public static String REQUEST_CONTENT = "request_content";

    public static String STAT_REQUEST_LOG = "stat_request_log";

    public static HttpServletRequest getCurrentRequest() {
        return (HttpServletRequest) ThreadLocalUtil.getValue(REQUEST);
    }

    public static HttpServletResponse getCurrentResponse() {
        return (HttpServletResponse) ThreadLocalUtil.getValue(RESPONSE);
    }

    public static void addRequest(HttpServletRequest request) {
        setValue(REQUEST, request);
    }

    public static void addResponse(HttpServletResponse response) {
        setValue(RESPONSE, response);
    }

}
