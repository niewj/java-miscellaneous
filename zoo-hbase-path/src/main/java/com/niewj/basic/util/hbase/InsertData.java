package com.niewj.basic.util.hbase;

import org.apache.hadoop.hbase.client.Put;

/**
 * To change this template use File | Settings | File Templates.
 */
public interface InsertData {
    void addData(Put p);
}
