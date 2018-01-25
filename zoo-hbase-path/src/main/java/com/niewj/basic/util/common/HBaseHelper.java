package com.niewj.basic.util.common;


import com.google.gson.Gson;
import com.niewj.basic.util.hbase.HBaseUtil;
import com.niewj.basic.util.hbase.SlowLogUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * HBase helper类
 */
public class HBaseHelper {
    private static final Logger logger = LoggerFactory.getLogger(HBaseHelper.class);
    private final static int COLUMN_MAX_VERSION = 1;
    public final static String CF_SPLIT_CHAR = String.valueOf(KeyValue.COLUMN_FAMILY_DELIMITER);// ":"列簇和列之间的分隔符

    public static Map<String, String> configMap = PropUtil.getConfigMap("config.properties");
    private static final Object CONN_LOCK = new Object();
    private static final Object ADMIN_LOCK = new Object();
    private static final Object TABLE_LOCK = new Object();

    private static Connection connection = null;
    private static Configuration configuration = null;


    private HBaseHelper() {
    }

    // ======================================== [manage util]=================================
    // ======================================== [manage util]=================================
    // ======================================== [manage util]=================================

    /**
     * 线程安全的-获取-configuration
     * hbase config
     */
    public static Configuration getConfiguration() {
        if (configuration == null) {
            synchronized (HBaseUtil.class) {
                if (configuration == null) {
                    configuration = HBaseConfiguration.create();
                    if (configMap == null || configMap.isEmpty()) {
                        logger.error("Cannot load Hbase config info from config.properties");
                        return null;
                    }
                    logger.info("--------------------------------------");
                    logger.info(new Gson().toJson(configMap));
                    logger.info("--------------------------------------");

                    configuration.set("hbase.zookeeper.quorum", configMap.get("hbase.zookeeper.quorum"));
                    configuration.set("hbase.zookeeper.property.clientPort", configMap.get("hbase.zookeeper.property.clientPort"));
                    configuration.set("zookeeper.znode.parent", configMap.get("zookeeper.znode.parent"));
                }
            }
        }

        return configuration;
    }

    /**
     * 线程安全的-获取-connection
     * hbase connection
     */
    public static Connection getConn() {
        if (connection == null) {
            synchronized (CONN_LOCK) {
                if (connection == null) {
                    try {
                        connection = ConnectionFactory.createConnection(getConfiguration());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        return connection;
    }

    /**
     * 获取Hbase操作Admin对象
     *
     * @param connection
     * @return
     */
    private static Admin getHBaseAdmin(Connection connection) {
        Admin _admin = null;

        if (_admin == null) {
            synchronized (ADMIN_LOCK) {
                if (_admin == null) {
                    try {
                        _admin = connection.getAdmin();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        return _admin;
    }

    /**
     * 获取DML操作对象table
     *
     * @param connection hbase连接
     * @param tblName    指定的hbase表名
     * @return
     */
    public static Table getHTable(Connection connection, String tblName) {
        Table _table = null;

        if (_table == null) {
            synchronized (TABLE_LOCK) {
                if (_table == null) {
                    try {
                        _table = connection.getTable(TableName.valueOf(tblName));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        return _table;
    }

    /**
     * 关闭 Table对象。
     *
     * @param table
     */
    private static void closeHTable(Table table) {
        if (table != null) {
            try {
                table.close();
            } catch (IOException e) {

                logger.warn("close table error, tblName:" + table.getName(), e);
            }
        }
    }

    // ======================================== [manage util]=================================
    // ======================================== [manage util]=================================
    // ======================================== [manage util]=================================


    // ------------------------- 封装方法- 临时用 --------------------------

    private static String fixColName(String family, String colName) {
        if (colName == null || colName.trim().length() == 0) {
            return family;
        }

        int index = family.indexOf(CF_SPLIT_CHAR);
        if (index == -1) {
            family += CF_SPLIT_CHAR + colName;
        }

        return family;
    }

    private static HColumnDescriptor createColumnDesc(String cf) {
        String tmp = fixColName(cf, null);
        byte[] colNameByte = Bytes.toBytes(tmp);
        HColumnDescriptor hColumnDescriptor = new HColumnDescriptor(colNameByte);
        hColumnDescriptor.setMaxVersions(COLUMN_MAX_VERSION);
        return hColumnDescriptor;
    }

    // =========================================insert=======================================
    // =========================================insert=======================================
    // =========================================insert=======================================
//    public static void insertAndUpdate(String tblName, String rowKey, String family, String colName, String value) {
//        long start = System.currentTimeMillis();
//        String info = "tblName:" + tblName + ",family:" + family + ",rowKey" + rowKey + ",family" + family + ",colName:" + colName + ",value:" + value;
//        Table table = null;
//        try {
//            table = getHTable(connection, tblName);
//            Put p = new Put(Bytes.toBytes(rowKey));
//
//            p.add(Bytes.toBytes(family), (colName != null ? Bytes.toBytes(colName) : null), Bytes.toBytes(value));
//            table.put(p);
//        } catch (IOException e) {
//            logger.error("insertAndUpdate error, " + info, e);
//            throw new RuntimeException("insertAndUpdate error, " + info, e);
//        } finally {
//            closeHTable(table);
//            SlowLogUtils.logHBaseResTime(tblName, "insertAndUpdate " + info, start);
//        }
//    }
//
//    public static void insertAndUpdate(String tblName, String rowKey, String family, String colName, String value, long timestamp) {
//        long start = System.currentTimeMillis();
//        String info = "tblName:" + tblName + ",family:" + family + ",rowKey" + rowKey + ",family" + family + ",colName:" + colName + ",value:" + value + ", timestamp:" + timestamp;
//
//        Table table = null;
//        try {
//            table = getHTable(connection, tblName);
//            Put p = new Put(Bytes.toBytes(rowKey), timestamp);
//            p.addColumn(Bytes.toBytes(family), (colName != null ? Bytes.toBytes(colName) : null), Bytes.toBytes(value));
//            table.put(p);
//        } catch (IOException e) {
//            throw new RuntimeException("insertAndUpdate error, " + info, e);
//        } finally {
//            closeHTable(table);
//            SlowLogUtils.logHBaseResTime(tblName, "insertAndUpdate " + info, start);
//        }
//    }

    /**
     * 插入更新操作
     *
     * @param tblName         表名
     * @param familyAndValues 列簇值map，外层key是ColumnFamily key，内层key是Column key
     *                        当ColumnFamily作为整列时，key"
     * @throws IOException
     */
    public static void insertAndUpdate(Table table, String tblName, String rowKey, Map<String, Map<String, String>> familyAndValues) {
        long start = System.currentTimeMillis();
        String info = "tblName:" + tblName + ",rowKey:" + rowKey;

        try {
            Put put = new Put(Bytes.toBytes(rowKey));
            // 1. 列族列表
            Set<String> cfSet = familyAndValues.keySet();
            for (String cf : cfSet) {
                Map<String, String> columnMap = (Map<String, String>) familyAndValues.get(cf);
                if (columnMap.containsKey("")) {
                    put.addColumn(Bytes.toBytes(cf), Bytes.toBytes(""), Bytes.toBytes((String) columnMap.get("")));
                    continue;
                } else {
                    Set<String> columnSet = columnMap.keySet();
                    for (String column : columnSet) {
                        String columnValue = (String) columnMap.get(column);
                        if (columnValue == null)
                            columnValue = "";
                        put.addColumn(Bytes.toBytes(cf), Bytes.toBytes(column), Bytes.toBytes(columnValue));
                    }
                }
            }

            table.put(put);
        } catch (IOException e) {
            logger.error("insertAndUpdate error, " + info, e);
            throw new RuntimeException(e);
        } finally {
            closeHTable(table);
            SlowLogUtils.logHBaseResTime(tblName, "insertAndUpdate " + info, start);
        }
    }

    /**
     * 创建 Hbase 表
     *
     * @param admin   DDL-操作对象
     * @param tblName 待创建表名
     * @param cfs     列族名数组
     */
    public static void createHbaseTable(Admin admin, String tblName, String[] cfs) {
        TableName tableName = TableName.valueOf(tblName);
        try {
            if (admin.tableExists(tableName)) {
                logger.error("table is exists!");
            } else {
                HTableDescriptor hTableDescriptor = new HTableDescriptor(tableName);
                HColumnDescriptor columnDescriptor = null;
                for (String cf : cfs) {
                    columnDescriptor = createColumnDesc(cf);
                    hTableDescriptor.addFamily(columnDescriptor);
                    hTableDescriptor.setReadOnly(false);
                }
                admin.createTable(hTableDescriptor);
            }
        } catch (IOException e) {
            throw new RuntimeException("createHbaseTable ERROR, tblName:" + tblName, e);
        }
    }

    public static void main(String[] args) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String tblName = "zn_zzz_test";
        String rowKey = uuid + "20171015";

        // #0. Connection
        Connection connection = getConn();
        // #1. Admin
        Admin admin = getHBaseAdmin(connection);
        logger.info("==============>connection = {}", connection);
        logger.info("==============>admin = {}", admin);
        // #2. Table
        Table table = getHTable(connection, tblName);
        logger.info("==============>table = {}", table);

        // 1. 创建表
        createHbaseTable(admin, tblName, new String[]{"cf1"});

        Map<String, Map<String, String>> dataMap = new HashMap<String, Map<String, String>>();
        Map<String, String> data = new HashMap<String, String>();
        data.put("name", "莫道初");
        data.put("idCard", "142720199505051212");
        data.put("bankCard", "62358809208567222");
        data.put("userMeta", "{'imgUrl','http://www.baidu.com/aaa.jpg'}");
        dataMap.put("cf1", data);
        insertAndUpdate(table, tblName, rowKey, dataMap);

    }

}
