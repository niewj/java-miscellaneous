//package com.niewj.common.util.hbase;
//
//import org.apache.hadoop.hbase.client.HTableInterface;
//
//import java.io.IOException;
//
//
//public interface HBaseExec {
//    public Object exec(HTableInterface table) throws IOException;
//
//    /**
//     * @return 当出现Exception或者执行时间较长时，会把该方法返回的内容记入日志，用于查错，比如，rowKey，colFamilyName等信息
//     */
//    public String logInfo();
//}
