package com.niewj.common.util.hbase;

import java.util.Map;

/**
 * 
 * 多行 多列批量插入
 */

public class PutInfo {
	private String rowKey;
	/**
	 * <f:<column:value>>
	 */
	private Map<String, Map<String, String>> familyAndValues;

	public String getRowKey() {
		return rowKey;
	}

	public void setRowKey(String rowKey) {
		this.rowKey = rowKey;
	}

	public Map<String, Map<String, String>> getFamilyAndValues() {
		return familyAndValues;
	}

	public void setFamilyAndValues(Map<String, Map<String, String>> familyAndValues) {
		this.familyAndValues = familyAndValues;
	}
}
