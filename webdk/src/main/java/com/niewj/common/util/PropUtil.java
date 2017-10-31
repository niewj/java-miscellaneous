package com.niewj.common.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


/**
 * 读取配置文件工具 create in 2012.02.06
 */
public class PropUtil {

	/**
	 * 获取指定值
	 * 
	 * @param key
	 * @return
	 */
	public static String getValue(String fileName, String key) {
		String resource = "/" + fileName;
		Properties prop = new Properties();
		try {
			prop.load(PropUtil.class.getResourceAsStream(resource));
		} catch (Exception e) {
			System.err.println("Exception when prop.load(" + resource + ")." +  e);
		}
		String value = prop.getProperty(key);
		if (value == null) {
			System.err.println("prop.getProperty(" + key + ") return is null.");
		}
		return value == null ? "" : value;
	}

	/**
	 * 获取配置文件中所有的配置的一个Map对象。
	 * 
	 * @param fileName
	 * @return
	 */
	public static Map<String, String> getConfigMap(String fileName) {
		Map<String, String> entryMap = new HashMap<String, String>();
		String resource = "/" + fileName;
		Properties prop = new Properties();
		try {
			prop.load(PropUtil.class.getResourceAsStream(resource));
		} catch (IOException e) {
			System.err.println("Exception when prop.load(" + resource + ").");
		}
		Set<String> sets = prop.stringPropertyNames();
		for (final String s : sets) {
			entryMap.put(s, prop.getProperty(s));
		}
		return entryMap;
	}
}
