package com.niewj.basic.util.hbase;

import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * 记录慢日志
 */
public class SlowLogUtils {
	//private static final Logger LOG = LoggerFactory.getLogger(LogConstants.LOG_TYPE_SLOW);//打印到单独的文件
	private static final Logger LOG = LoggerFactory.getLogger(SlowLogUtils.class);
    
    public static final int DEFAULT_HTTP_RESPONSE_TIME = 300;//ms
	public static final int DEFAULT_HBASE_RESPONSE_TIME = 300;//ms
	public static final int DEFAULT_NEO4J_RESPONSE_TIME = 200;//ms
	public static final int DEFAULT_REDIS_RESPONSE_TIME = 20;//ms

	private static final int LOG_URL_MAX_LENGTH = 300;
	private static final int LOG_INFO_MAX_LENGTH = 300;
	private static final String RESPONSE_TIME_STR = "response time >";
	private static final String OVER_LENGTH_SIGN = "...";
	private static final Gson gson=new Gson();;

	private SlowLogUtils() {
	}

    
    /**
	 * 日志记录超过@{LOG_URL_MAX_LENGTH} ms的http请求
	 * @param url，超过300字节长度会被截取
	 */
	public static void logHttpResTime(String url, long startTime) {
		long resTime = System.currentTimeMillis() - startTime;
		if ( resTime > DEFAULT_HTTP_RESPONSE_TIME ) {
			if ( !StringUtils.isBlank(url) && url.length() > LOG_URL_MAX_LENGTH ) {
				url = url.substring(0, LOG_URL_MAX_LENGTH)+OVER_LENGTH_SIGN;
			}
			LOG.warn("*url {}  "+RESPONSE_TIME_STR+" " + DEFAULT_HTTP_RESPONSE_TIME + " ms , {} ms", url, resTime);
		}else{
            LOG.debug("*url {}  "+RESPONSE_TIME_STR+" " + DEFAULT_HTTP_RESPONSE_TIME + " ms , {} ms", url, resTime);
        }
	}
	
	/**
	 * 日志记录超过@{DEFAULT_REDIS_RESPONSE_TIME}ms的redis请求
	 * @param ip
	 * @param port
	 * @param db
	 * @param info，通常包含redis命令和key信息，超过300字节长度会被截取
	 */
	public static void logRedisResTime(String ip, int port, int db, String info, long startTime) {
		long resTime = System.currentTimeMillis() - startTime;
		if ( resTime > DEFAULT_REDIS_RESPONSE_TIME ) {
			LOG.warn("*redis {} {} {} {} " + RESPONSE_TIME_STR + " " +
					DEFAULT_REDIS_RESPONSE_TIME + " ms , {} ms", ip, port, db, cutString(info), resTime);
		}
	}

	/**
	 * 日志记录超过@{DEFAULT_REDIS_RESPONSE_TIME}ms的redis请求
	 * @param info，通常包含redis命令和key信息，超过300字节长度会被截取
	 */
	public static void logClusterRedisResTime(String info, long startTime) {
		long resTime = System.currentTimeMillis() - startTime;
		if ( resTime > DEFAULT_REDIS_RESPONSE_TIME ) {
			LOG.warn("*redis {} "+RESPONSE_TIME_STR+" " +
					DEFAULT_REDIS_RESPONSE_TIME + " ms , {} ms", cutString(info), resTime);
		}
	}
	
	/**
	 * 日志记录超过${DEFAULT_HBASE_RESPONSE_TIME}ms的hbase请求
	 * @param table hbase表名称
	 * @param info hbase表操作的命令信息，比如：get row， scan startrow endrow等，，超过300字节长度会被截取
	 */
	public static void logHBaseResTime(String table, String info, long startTime) {
		long resTime = System.currentTimeMillis() - startTime;
		if ( resTime > DEFAULT_HBASE_RESPONSE_TIME ) {
			LOG.warn("*hbase table {} {} "+RESPONSE_TIME_STR+" " + DEFAULT_HBASE_RESPONSE_TIME + " ms , {} ms", table, cutString(info), resTime);
		}
	}

	/**
	 * 日志记录超过${DEFAULT_NEO4J_RESPONSE_TIME}ms的neo4j请求方法
	 * @param sql
	 * @param args 参数
	 * @param startTime  args超过300字节长度会被截取
	 */
	public static void logNeo4jResTime(String sql, Object[] args, long startTime) {
		long resTime = System.currentTimeMillis() - startTime;
		if ( resTime > DEFAULT_NEO4J_RESPONSE_TIME ) {
			LOG.warn("*neo4j sql:{},args:{}  "+RESPONSE_TIME_STR+" " + DEFAULT_NEO4J_RESPONSE_TIME + " ms , {} ms", sql,cutObjects(args),resTime);
		}
	}
	private static String cutObjects(Object[] source) {
		if (source==null || source.length==0) {
			return null;
		}
		StringBuilder sb=new StringBuilder();
		for (int i = 0; i <source.length ; i++) {
			sb.append(gson.toJson(source[i])+",");
		}
		if ( sb.toString().length() > LOG_INFO_MAX_LENGTH ) {
			return sb.toString().substring(0, LOG_INFO_MAX_LENGTH) + OVER_LENGTH_SIGN+"info length:"+sb.toString().length();
		}
		return sb.toString();
	}
	private static String cutString(String source) {
		if ( StringUtils.isBlank(source) ) {
			return source;
		}
		
		if ( source.length() > LOG_INFO_MAX_LENGTH ) {
			source = source.substring(0, LOG_INFO_MAX_LENGTH)+OVER_LENGTH_SIGN+"info length:"+source.length();
		}
		
		return source;
	}

	   public static String getLocalIP() {
	        InetAddress addr = null;
	        try {
	            addr = InetAddress.getLocalHost();
	        } catch (UnknownHostException e) {
	            e.printStackTrace();
	            return "x.x";
	        }
	        return suffix(addr.getHostAddress());
	    }


	    // 获取本机ip最后一位(双网卡内网ip)
	    public static String getIpSuffix() {
	        String ip = null;
	        String suffix = "";
	        NetworkInterface ni;
	        try {
	            ni = NetworkInterface.getByName("eth0");
	            if (ni == null) {
	                ni = NetworkInterface.getByName("eth1");
	            }

	            Enumeration<NetworkInterface> nis = null;
	            nis = NetworkInterface.getNetworkInterfaces();
	            for (; nis.hasMoreElements(); ) {
	                NetworkInterface ni0 = nis.nextElement();
	                System.out.println(ni0);

	            }

	            Enumeration<InetAddress> ias = ni.getInetAddresses();
	            for (; ias.hasMoreElements(); ) {
	                InetAddress ia = ias.nextElement();
	                System.out.println(ia);
	                if (ia instanceof InetAddress) {
	                    ip = ia.getHostAddress();
	                }
	            }
	            if (ip == null) {
	                InetAddress addr = InetAddress.getLocalHost();
	                ip = addr.getHostAddress();
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        suffix = suffix(ip);
	        return suffix;
	    }

	    private static String suffix(String ip) {
	        String suffix = "";
	        if (ip != null) {
	            int index = ip.indexOf('.', ip.indexOf('.')) + 1;
	            suffix = ip.substring(index);

	            index = suffix.indexOf('.', suffix.indexOf('.')) + 1;
	            suffix = suffix.substring(index);
	        }
	        return suffix;
	    }
	    
	    
    public static void main(String[] args) {
//        System.out.println(SlowLogUtils.getLocalIP());
		Object[] args2=new Object[3];
		args2[0]="AAAA";
		args2[1]="BBBB";
		args2[2]="CCCC";

    }

}
