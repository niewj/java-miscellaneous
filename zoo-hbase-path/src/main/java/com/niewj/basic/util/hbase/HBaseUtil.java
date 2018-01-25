package com.niewj.basic.util.hbase;


import com.google.gson.Gson;
import com.niewj.basic.util.common.PropUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * HBase helper类
 */
public class HBaseUtil {
    private static final Logger logger = LoggerFactory.getLogger(HBaseUtil.class);

    public static Map<String, String> configMap = PropUtil.getConfigMap("config.properties");

    public final static String COLENDCHAR = String.valueOf(KeyValue.COLUMN_FAMILY_DELIMITER);// ":"列簇和列之间的分隔符
    private final static int COLUMN_MAX_VERSION = 1;
    private static Connection connection = null;
    private static final Object CONN_LOCK = new Object();
    //    private static HBaseAdmin admin = null;
//    private static Admin admin = null;
    private static final Object ADMIN_LOCK = new Object();
    private static Configuration configuration = null;

    private HBaseUtil() {
    }

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
        Admin admin = null;
        synchronized (ADMIN_LOCK) {
            if (admin == null) {
                try {
                    admin = connection.getAdmin();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return admin;
    }

    /**
     * 获取DML操作对象table
     *
     * @param connection hbase连接
     * @param tblName    指定的hbase表名
     * @return
     */
    public static Table getHTable(Connection connection, String tblName) {
        Table table = null;
        try {
            table = connection.getTable(TableName.valueOf(tblName));
        } catch (IOException e) {
            try {
                table = connection.getTable(TableName.valueOf(tblName));
            } catch (IOException e1) {
                throw new RuntimeException("getHTable error, tblName:" + tblName, e1);
            }
        }

        return table;
    }


    /**
     * 创建 Hbase 表
     *
     * @param admin   DDL-操作对象
     * @param tblName 待创建表名
     * @param cfs     列族名数组
     */
    public static void createHbaseTable(Admin admin, String tblName, String[] cfs) {
        try {
            if (admin.tableExists(TableName.valueOf(tblName)))
                return;// 判断表是否已经存
            HTableDescriptor htdesc = new HTableDescriptor(tblName);

            for (int i = 0; i < cfs.length; i++) {
                String fml = cfs[i];
                addFamily(htdesc, fml, false);
            }
            admin.createTable(htdesc);
        } catch (IOException e) {
            throw new RuntimeException("createHbaseTable ERROR, tblName:" + tblName, e);
        }
    }

    /**
     * 方法描述：删除一条记录的
     * 如果colName＝null，则删除列簇
     */
    public static void deleteColumn(String tblName, String rowKey, String family, String colName) {
        long start = System.currentTimeMillis();
        String info = "tblName:" + tblName + ",rowId:" + rowKey + ",fml:" + family + ",colName:" + colName;

        Table table = null;
        try {
            Delete del = new Delete(rowKey.getBytes());

            if (colName == null || "".equals(colName))
                del.deleteFamily(family.getBytes());
            else {
                del.deleteColumns(family.getBytes(), colName.getBytes());
            }

            table = getHTable(connection, tblName);
            table.delete(del);
//            table.flushCommits();
        } catch (IOException e) {
            throw new RuntimeException("deleteColumn error, " + info, e);
        } finally {
            closeHTable(table);
            SlowLogUtils.logHBaseResTime(tblName, "deleteColumn " + info, start);
        }
    }

    public static void deleteColumns(String tblName, String rowKey, String family, List<String> columns) {
        long start = System.currentTimeMillis();
        String info = "tblName:" + tblName + ",family:" + family + ",rowKey:" + rowKey;

        Table table = null;
        try {
            table = getHTable(connection, tblName);
            List<Delete> list = new ArrayList<Delete>();
            for (String column : columns) {
                Delete delete = new Delete(Bytes.toBytes(rowKey));
//                delete.deleteColumns(Bytes.toBytes(family), Bytes.toBytes(column));
                delete.addColumns(Bytes.toBytes(family), Bytes.toBytes(column));
                list.add(delete);
            }
            table.delete(list);
        } catch (Exception e) {
            throw new RuntimeException("deleteColumns error, " + info, e);
        } finally {
            closeHTable(table);
            SlowLogUtils.logHBaseResTime(tblName, "deleteColumns " + info, start);
        }
    }

    public static void exec(String tblName, HBaseExec hbExec) {
        long start = System.currentTimeMillis();
        Table table = null;
        try {
            table = getHTable(connection, tblName);
            hbExec.exec(table);
        } catch (Exception e) {
            throw new RuntimeException("exec error, tblName:" + tblName + ",exec:" + hbExec.logInfo(), e);
        } finally {
            closeHTable(table);
            SlowLogUtils.logHBaseResTime(tblName, "exec:" + hbExec.logInfo(), start);
        }
    }

    /**
     * 方法描述：删除一行记
     *
     * @param tblName
     * @param rowKey
     * @throws IOException 返回值类void
     * @Exception 异常对象
     */
    public static void deleteRow(String tblName, String rowKey) {
        long start = System.currentTimeMillis();
        String info = "tblName:" + tblName + ",rowKey:" + rowKey;

        Table table = null;
        try {
            table = getHTable(connection, tblName);
            List list = new ArrayList();
            Delete del = new Delete(rowKey.getBytes());
            list.add(del);
            table.delete(list);
//            table.flushCommits();
        } catch (IOException e) {
            throw new RuntimeException("deleteRow error, " + info, e);
        } finally {
            closeHTable(table);
            SlowLogUtils.logHBaseResTime(tblName, "deleteRow " + info, start);
        }
    }

    public static boolean isExist(String tblName, String rowKey) {
        long start = System.currentTimeMillis();
        String info = "tblName:" + tblName;

        Table table = null;
        try {
            table = getHTable(connection, tblName);
            Get get = new Get(rowKey.getBytes());
            return table.exists(get);
        } catch (IOException e) {
            throw new RuntimeException("isExist error, " + info, e);
        } finally {
            closeHTable(table);
            SlowLogUtils.logHBaseResTime(tblName, "isExist " + info, start);
        }
    }

    public static Map<String, String> getValue(String tblName, String rowKey, String family, List<String> colNames) {
        long start = System.currentTimeMillis();
        String info = "tblName:" + tblName + ",rowKey:" + rowKey + ",family:" + family;

        Table table = null;
        Map<String, String> columnValueMap = null;
        try {
            table = getHTable(connection, tblName);
            Get get = new Get(rowKey.getBytes());
            Result result = table.get(get);
            if (result != null && !result.isEmpty() && colNames != null && colNames.size() > 0) {
                columnValueMap = new HashMap<String, String>();
                byte[] bytesValue = null;
                String value = null;
                for (String colName : colNames) {
                    bytesValue = result.getValue(family.getBytes(), colName.getBytes());
                    if (bytesValue != null) {
                        value = Bytes.toString(bytesValue);
                    } else {
                        value = "";
                    }
                    columnValueMap.put(colName, value);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("getColumnValue error, " + info, e);
        } finally {
            closeHTable(table);
            long end = System.currentTimeMillis();
            SlowLogUtils.logHBaseResTime(tblName, "getColumnValue " + info, end - start);
        }
        return columnValueMap;
    }

    public static String getValue(String tblName, String rowKey, String family, String colName) {
        long start = System.currentTimeMillis();
        String info = "tblName:" + tblName + ",rowKey:" + rowKey + ",family:" + family + ",colName:" + colName;

        Table table = null;
        try {
            table = getHTable(connection, tblName);
            Get get = new Get(rowKey.getBytes());
            Result result = table.get(get);
            byte[] b = result.getValue(family.getBytes(), colName.getBytes());
            if (b == null)
                return "";
            else
                return Bytes.toString(b);
        } catch (IOException e) {
            throw new RuntimeException("getValue error, " + info, e);
        } finally {
            closeHTable(table);
            SlowLogUtils.logHBaseResTime(tblName, "getValue " + info, start);
        }
    }

    public static byte[] getByteValue(String tblName, String rowKey, String family, String colName) {
        long start = System.currentTimeMillis();
        String info = "tblName:" + tblName + ",rowKey:" + rowKey + ",family:" + family + ",colName:" + colName;

        Table table = null;
        try {
            table = getHTable(connection, tblName);
            Get get = new Get(rowKey.getBytes());
            Result result = table.get(get);
            return result.getValue(family.getBytes(), colName.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("getByteValue error, " + info, e);
        } finally {
            closeHTable(table);
            SlowLogUtils.logHBaseResTime(tblName, "getByteValue " + info, start);
        }
    }

    /**
     * 方法描述：获得行记录
     *
     * @param tblName
     * @return
     * @throws IOException 返回值类Result
     * @Exception 异常对象
     */
    public static Result getResult(String tblName, String rowKey) {
        long start = System.currentTimeMillis();
        String info = "tblName:" + tblName + ",rowKey:" + rowKey;

        Result result = null;
        Table table = null;
        try {
            table = getHTable(connection, tblName);
            Get get = new Get(rowKey.getBytes());
            result = table.get(get);
        } catch (IOException e) {
            logger.error("getResult error, " + info, e);
            throw new RuntimeException("getResult error, " + info, e);
        } finally {
            closeHTable(table);
            SlowLogUtils.logHBaseResTime(tblName, "getResult " + info, start);
        }
        return result;
    }

    public static Result getResult(String tblName, String family, String rowKey) {
        long start = System.currentTimeMillis();
        String info = "tblName:" + tblName + ",family" + family + ",rowKey:" + rowKey;

        Result result = null;
        Table table = null;
        try {
            table = getHTable(connection, tblName);
            Get get = new Get(rowKey.getBytes());
            get.addFamily(Bytes.toBytes(family));
            result = table.get(get);
        } catch (IOException e) {
            logger.error("getResult error, " + info, e);
            throw new RuntimeException("getResult error, " + info, e);
        } finally {
            closeHTable(table);
            SlowLogUtils.logHBaseResTime(tblName, "getResult " + info, start);
        }
        return result;
    }

    public static Result getResultInTwoFml(String tblName, String family1, String family2, String rowKey) {
        long start = System.currentTimeMillis();
        String info = "tblName:" + tblName + ",family1" + family1 + ",family2" + family2 + "rowKey:" + rowKey;

        Result result = null;
        Table table = null;
        try {
            table = getHTable(connection, tblName);
            Get get = new Get(rowKey.getBytes());
            get.addFamily(Bytes.toBytes(family1));
            get.addFamily(Bytes.toBytes(family2));
            result = table.get(get);
        } catch (IOException e) {
            throw new RuntimeException("getResult error, " + info, e);
        } finally {
            closeHTable(table);
            SlowLogUtils.logHBaseResTime(tblName, "getResult " + info, start);
        }
        return result;
    }

    public static List<Result> getResult(String tblName, String rowkeyStartPrefix, int maxResultCount, String family) throws Exception {
        List<Result> resultList = new ArrayList<Result>();
        Table table = null;
        ResultScanner resultScanner = null;
        int count = 0;
        try {
            table = getHTable(connection, tblName);
            Scan scan = new Scan();
            scan.setRowPrefixFilter(rowkeyStartPrefix.getBytes());
            if (StringUtils.isNotEmpty(family)) {
                scan.addFamily(Bytes.toBytes(family));
            }
            resultScanner = table.getScanner(scan);
            for (Result result : resultScanner) {
                if (result.isEmpty()) {
                    break;
                }
                count++;
                if (count > maxResultCount) {
                    break;
                }
                resultList.add(result);
            }
        } finally {
            try {
                if (resultScanner != null) {
                    resultScanner.close();
                }
            } catch (Exception e) {
                logger.error("", e);
            }
            closeHTable(table);
        }
        return resultList;
    }

    public static List<Result> getResult(String tblName, String rowkeyStart, String rowkeyEnd, int maxResultCount, String family) throws Exception {
        List<Result> resultList = new ArrayList<Result>();
        Table table = null;
        ResultScanner resultScanner = null;
        int count = 0;
        try {
            table = getHTable(connection, tblName);
            Scan scan = new Scan();
            if (StringUtils.isNotEmpty(rowkeyStart)) {
                scan.setStartRow(Bytes.toBytes(rowkeyStart));
            }
            if (StringUtils.isNotEmpty(rowkeyEnd)) {
                scan.setStopRow(Bytes.toBytes(rowkeyEnd));
            }
            if (StringUtils.isNotEmpty(family)) {
                scan.addFamily(Bytes.toBytes(family));
            }
            resultScanner = table.getScanner(scan);
            for (Result result : resultScanner) {
                if (result.isEmpty()) {
                    break;
                }
                count++;
                if (count > maxResultCount) {
                    break;
                }
                resultList.add(result);
            }
        } finally {
            try {
                if (resultScanner != null) {
                    resultScanner.close();
                }
            } catch (Exception e) {
                logger.error("", e);
            }
            closeHTable(table);
        }
        return resultList;
    }

    /**
     * 根据rowkey起始字符串
     *
     * @param tblName
     * @param rowkeyStart
     * @param rowkeyEnd
     * @param maxResultCount
     * @return
     */
    public static List<Result> getResultsByRowkeyRange(String tblName, String rowkeyStart, String rowkeyEnd, int maxResultCount,
                                                       String family, String timestamp, String versionNum) {
        long start = System.currentTimeMillis();
        String info = "tblName:" + tblName + ",rowkeyStart:" + rowkeyStart + ",rowkeyEnd:" + rowkeyEnd + ",maxResultCount:" + maxResultCount
                + ",family:" + family + ",timestamp:" + timestamp + ",versionNum:" + versionNum;

        List<Result> resultList = new ArrayList<Result>();
        Table table = null;
        ResultScanner resultScanner = null;
        int count = 0;
        try {
            table = getHTable(connection, tblName);
            Scan scan = new Scan();
            if (StringUtils.isNotEmpty(rowkeyStart)) {
                scan.setStartRow(Bytes.toBytes(rowkeyStart));
            }
            if (StringUtils.isNotEmpty(rowkeyEnd)) {
                scan.setStopRow(Bytes.toBytes(rowkeyEnd));
            }
            if (StringUtils.isNotEmpty(family)) {
                scan.addFamily(Bytes.toBytes(family));
            }
            if (StringUtils.isNotEmpty(timestamp)) {
                scan.setTimeStamp(Long.parseLong(timestamp));
            }
            if (StringUtils.isNotEmpty(versionNum)) {
                scan.setMaxVersions(Integer.parseInt(versionNum));
            }
            resultScanner = table.getScanner(scan);
            for (Result result : resultScanner) {
                if (result.isEmpty()) {
                    break;
                }
                count++;
                if (count > maxResultCount) {
                    break;
                }
                resultList.add(result);
            }
        } catch (IOException e) {
            logger.error("getRwoResultsByRowkeyRange error, " + info, e);
            throw new RuntimeException("getRwoResultsByRowkeyRange error, " + info, e);
        } finally {
            try {
                if (resultScanner != null) {
                    resultScanner.close();
                }
            } catch (Exception e) {
                logger.error("", e);
            }
            closeHTable(table);
            SlowLogUtils.logHBaseResTime(tblName, "getRwoResultsByRowkeyRange " + info, start);
        }
        return resultList;
    }

    /**
     * 根据rowKeys批量获取查询数据
     *
     * @param tblName
     * @return
     */
    public static Result[] getResult(String tblName, List<String> rowKeys) {
        return getResult(tblName, rowKeys, null, null);
    }

    /**
     * 根据rowId批量获取查询数据，可指定查询的列族
     *
     * @param tblName
     * @param family  查询的列族
     * @return
     */
    public static Result[] getResult(String tblName, List<String> rowKeys, String family) {
        return getResult(tblName, rowKeys, family, null);
    }

    /**
     * 根据rowId批量获取查询数据，可指定一个列族和该列族下的部分列，查询结果中只包含指定列的数据
     *
     * @param tblName
     * @param family  查询的列族
     * @param columns 参数family下的列，如果指定了列，则必须指定列族family
     * @return
     */
    public static Result[] getResult(String tblName, List<String> rowKeys, String family, List<String> columns) {
        long start = System.currentTimeMillis();
        String info = "tblName:" + tblName + ",family" + family;

        if (rowKeys == null || rowKeys.size() == 0) {
            return null;
        }

        if (columns != null && columns.size() > 0) {
            if (family == null || family.equals("")) {
                throw new RuntimeException("参数异常，指定查询列的同时需指定列族名称！");
            }
        }

        Table table = null;
        try {
            table = getHTable(connection, tblName);
            List<Get> gets = new ArrayList<Get>();
            byte[] f = (family == null || family.equals("")) ? null : family.getBytes();
            for (String row : rowKeys) {
                if (row == null) {
                    throw new RuntimeException("参数rowIDs中包含有null值");
                }

                Get get = new Get(row.getBytes());
                if (columns != null && columns.size() > 0) {
                    for (String col : columns) {
                        get.addColumn(f, col.getBytes());
                    }
                } else if (f != null) {
                    get.addFamily(f);
                }

                gets.add(get);
            }

            return table.get(gets);
        } catch (IOException e) {
            logger.error("getResult error, " + info, e);
            throw new RuntimeException("getResult error, " + info, e);
        } finally {
            closeHTable(table);
            SlowLogUtils.logHBaseResTime(tblName, "getResult rowKeys count:" +
                    (rowKeys == null ? 0 : rowKeys.size()) + " " + family + " columns count:" + (columns == null ? 0 : columns.size()), start);
        }
    }

    /**
     * 根据组合条件获得结果集列表
     *
     * @param tblName
     * @param compareOpEntityList
     * @return
     */
    public static List<Result> getResultsByCondition(String tblName, List<CompareOpEntity> compareOpEntityList) {
        return getResultsByCondition(tblName, compareOpEntityList, Integer.MAX_VALUE);
    }

    /**
     * 根据组合条件获得结果集列表
     *
     * @param tblName
     * @param compareOpEntityList
     * @param maxResultCount      获得结果列表数量的最大值
     * @return
     */
    public static List<Result> getResultsByCondition(String tblName, List<CompareOpEntity> compareOpEntityList, int maxResultCount) {
        long start = System.currentTimeMillis();
        String info = "tblName:" + tblName + ",compareOpEntityList count:" + (compareOpEntityList == null ? 0 : compareOpEntityList.size())
                + ",maxResultCount:" + maxResultCount;

        List<Result> resultList = new ArrayList<Result>();
        Table table = null;
        ResultScanner resultScanner = null;
        int count = 0;
        try {
            table = getHTable(connection, tblName);
            List<Filter> filters = new ArrayList<Filter>();
            for (CompareOpEntity compareOpEntity : compareOpEntityList) {
                Filter filter = new SingleColumnValueFilter(Bytes.toBytes(compareOpEntity.getFamilyName()), Bytes.toBytes(compareOpEntity
                        .getColumnName()), compareOpEntity.getCompareOp(), Bytes.toBytes(compareOpEntity.getCompareValue()));
                filters.add(filter);
            }
            FilterList filterList1 = new FilterList(filters);
            Scan scan = new Scan();
            scan.setFilter(filterList1);
            resultScanner = table.getScanner(scan);
            for (Result result : resultScanner) {
                resultList.add(result);
                count++;
                if (count > maxResultCount) {
                    break;
                }
            }
        } catch (IOException e) {
            logger.error("getResultsByCondition error, " + info, e);
            throw new RuntimeException("getResultsByCondition error, " + info, e);
        } finally {
            try {
                if (resultScanner != null) {
                    resultScanner.close();
                }
            } catch (Exception e) {
                logger.error("", e);
            }
            closeHTable(table);
            SlowLogUtils.logHBaseResTime(tblName, "getResultsByCondition " + info, start);
        }
        return resultList;
    }

    /**
     * 根据rowkey范围只返回其对应的rowkey(最大查询记录条数100000)
     *
     * @param tblName
     * @param rowkeyStart
     * @param rowkeyEnd
     * @return
     */
    public static List<String> getTableRowkeysByRowkeyRange(String tblName, String rowkeyStart, String rowkeyEnd, boolean isCache) {
        long start = System.currentTimeMillis();
        String info = "tblName:" + tblName + ",rowkeyStart:" + rowkeyStart + ",rowkeyEnd:" + rowkeyEnd + ",isCache:" + isCache;

        if (StringUtils.isEmpty(rowkeyStart) || StringUtils.isEmpty(rowkeyEnd)) {
            throw new IllegalArgumentException("rowkeyStart or rowkeyEnd is null");
        }

        List<String> resultList = new ArrayList<String>();
        Table table = null;
        ResultScanner resultScanner = null;
        int count = 0;
        try {
            table = getHTable(connection, tblName);
            Scan scan = new Scan();
            scan.setCacheBlocks(isCache);
            scan.setStartRow(Bytes.toBytes(rowkeyStart));
            scan.setStopRow(Bytes.toBytes(rowkeyEnd));
            Filter filter = new FirstKeyOnlyFilter();
            scan.setFilter(filter);
            resultScanner = table.getScanner(scan);
            for (Result result : resultScanner) {
                String rowKey = Bytes.toString(result.getRow());
                logger.debug("rowKey = {}", rowKey);
                resultList.add(rowKey);
                count++;
                if (count > 100000) {
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("getTableRowkeysByRowkeyRange error, " + info, e);
        } finally {
            try {
                if (resultScanner != null) {
                    resultScanner.close();
                }
            } catch (Exception e) {
                logger.error("", e);
            }
            closeHTable(table);
            SlowLogUtils.logHBaseResTime(tblName, "getTableRowkeysByRowkeyRange " + info, start);
        }
        return resultList;
    }

    /**
     * 根据rowkey起始字符串
     *
     * @param tblName
     * @param rowkeyStart
     * @param rowkeyEnd
     * @param maxResultCount
     * @return
     */
    public static List<Result> getResultsByRowkeyRange(String tblName, String rowkeyStart, String rowkeyEnd, int maxResultCount) {
        long start = System.currentTimeMillis();
        String info = "tblName:" + tblName + ",rowkeyStart:" + rowkeyStart + ",rowkeyEnd:" + rowkeyEnd + ",maxResultCount:" + maxResultCount;

        List<Result> resultList = new ArrayList<Result>();
        Table table = null;
        ResultScanner resultScanner = null;
        int count = 0;
        try {
            table = getHTable(connection, tblName);
            Scan scan = new Scan();
            if (StringUtils.isNotEmpty(rowkeyStart)) {
                scan.setStartRow(Bytes.toBytes(rowkeyStart));
            }
            if (StringUtils.isNotEmpty(rowkeyEnd)) {
                scan.setStopRow(Bytes.toBytes(rowkeyEnd));
            }
            resultScanner = table.getScanner(scan);
            for (Result result : resultScanner) {
                count++;
                if (count > maxResultCount) {
                    break;
                }
                resultList.add(result);
            }
        } catch (IOException e) {
            throw new RuntimeException("getRwoResultsByRowkeyRange error, " + info, e);
        } finally {
            try {
                if (resultScanner != null) {
                    resultScanner.close();
                }
            } catch (Exception e) {
                logger.error("", e);
            }
            closeHTable(table);
            SlowLogUtils.logHBaseResTime(tblName, "getRwoResultsByRowkeyRange " + info, start);
        }
        return resultList;
    }

    public static Result getResultByRowkeyRangeTime(String tblName, String family, String rowKey, long startTime, long endTime) {
        long start = System.currentTimeMillis();
        String info = "tblName:" + tblName + ",family:" + family + ",rowKey:" + rowKey + ",startTime:" + startTime + ",endTime" + endTime;

        Result result = null;
        Table table = null;
        int count = 0;
        try {
            table = getHTable(connection, tblName);

            Get g = new Get(Bytes.toBytes(rowKey));
            g.addFamily(Bytes.toBytes(family));

            if (startTime > 0 && endTime > 0) {
                g.setTimeRange(startTime, endTime);
            }
            result = table.get(g);

            return result;
        } catch (IOException e) {
            throw new RuntimeException("getResultByRowkeyRangeTime error, " + info, e);
        } finally {
            closeHTable(table);
            SlowLogUtils.logHBaseResTime(tblName, "getResultByRowkeyRangeTime " + info, start);
        }
    }

    public static List<Result> getResultByCondition(String tblName, String family, FilterList filterList, int maxResultCount) {
        long start = System.currentTimeMillis();
        String info = "tblName:" + tblName + ",family:" + family + ",filterList count:" + filterList == null ? "null" : filterList.toString()
                + ",maxResultCount:" + maxResultCount;

        List<Result> resultList = null;
        Table table = null;
        ResultScanner resultScanner = null;
        int count = 0;
        try {
            table = getHTable(connection, tblName);
            Scan scan = new Scan();
            scan.setFilter(filterList);
            resultScanner = table.getScanner(scan);

            for (Result result : resultScanner) {
                count++;
                if (count > maxResultCount) {
                    break;
                }
                resultList.add(result);
            }
        } catch (IOException e) {
            throw new RuntimeException("getResultByCondition error," + info, e);
        } finally {
            try {
                if (resultScanner != null) {
                    resultScanner.close();
                }
            } catch (Exception e) {
                logger.error("", e);
            }
            closeHTable(table);
            SlowLogUtils.logHBaseResTime(tblName, "getResultByCondition " + info, start);
        }
        return resultList;
    }

    /**
     * 获取确定行的特定列的所有版本值
     *
     * @param tblName
     * @return
     * @throws IOException
     */
    public static List<KeyValue> getAllValues(String tblName, String rowKey, String family, String colName) {
        long start = System.currentTimeMillis();
        String info = "tblName:" + tblName + ",family:" + family + ",rowKey" + rowKey + ",colName:" + colName;

        Result result = null;
        Table table = null;
        List<KeyValue> keyValues = null;
        try {
            table = getHTable(connection, tblName);
            Get get = new Get(rowKey.getBytes());
            get.setMaxVersions(COLUMN_MAX_VERSION);
            result = table.get(get);
            keyValues = result.getColumn(Bytes.toBytes(family), Bytes.toBytes(colName));
        } catch (IOException e) {
            throw new RuntimeException("getColumnAllValues error, " + info, e);
        } finally {
            closeHTable(table);
            SlowLogUtils.logHBaseResTime(tblName, "getColumnAllValues " + info, start);
        }
        return keyValues;
    }

    public static Result getResult(String tblName, String family, String rowKey, String... colNames) {
        long start = System.currentTimeMillis();
        String info = "tblName:" + tblName + ",family:" + family + ",rowKey" + rowKey + ",colNames count:" + (colNames == null ? 0 : colNames.length);

        Result result = null;
        Table table = null;
        try {
            table = getHTable(connection, tblName);
            Get get = new Get(rowKey.getBytes());
            for (String col : colNames) {
                get.addColumn(Bytes.toBytes(family), Bytes.toBytes(col));
            }
            result = table.get(get);

        } catch (IOException e) {
            throw new RuntimeException("getColumnsResult error, " + info, e);
        } finally {
            closeHTable(table);
            SlowLogUtils.logHBaseResTime(tblName, "getColumnsResult " + info, start);
        }
        return result;
    }

    public static Result getResultByColumnPrefixs(String tblName, String family, String rowKey, String... colPrefixs) {
        long start = System.currentTimeMillis();
        String info = "tblName:" + tblName + ",family:" + family + ",rowKey" + rowKey + ",colPrefixs count:" + (colPrefixs == null ? 0 : colPrefixs.length);

        Result result = null;
        Table table = null;
        try {
            table = getHTable(connection, tblName);
            byte[][] prefixes = Bytes.toByteArrays(colPrefixs);
            MultipleColumnPrefixFilter filter = new MultipleColumnPrefixFilter(prefixes);
            Get get = new Get(Bytes.toBytes(rowKey));// 根据rowkey查询
            get.setFilter(filter);
            result = table.get(get);
        } catch (Exception ex) {
            throw new RuntimeException("getResultByColumnPrefixs error," + info, ex);
        } finally {
            closeHTable(table);
            SlowLogUtils.logHBaseResTime(tblName, "getResultByColumnPrefixs " + info, start);
        }
        return result;

    }

    public static Result getResult(String tblName, String family, String rowKey, String colName) {
        long start = System.currentTimeMillis();
        String info = "tblName:" + tblName + ",family:" + family + ",rowKey" + rowKey + ",colName" + colName;

        Result result = null;
        Table table = null;
        try {
            table = getHTable(connection, tblName);
            Get get = new Get(rowKey.getBytes());
            get.addColumn(Bytes.toBytes(family), Bytes.toBytes(colName));
            result = table.get(get);

        } catch (IOException e) {
            throw new RuntimeException("getColumnResult error, " + info, e);
        } finally {
            closeHTable(table);
            long end = System.currentTimeMillis();
            SlowLogUtils.logHBaseResTime(tblName, "getColumnResult " + info, start);
        }
        return result;
    }

    /**
     * 根据表明，rowKey，列簇，获取该列簇下所有值
     *
     * @param tblName
     * @param family
     * @return
     */
    public static List<KeyValue> getValues(String tblName, String rowKey, String family) {
        long start = System.currentTimeMillis();
        String info = "tblName:" + tblName + ",family:" + family + ",rowKey" + rowKey + ",family" + family;

        Result result = null;
        Table table = null;
        List<KeyValue> keyValues = null;
        try {
            table = getHTable(connection, tblName);
            Get get = new Get(rowKey.getBytes());
            // old.setMaxVersions(COLUMN_MAX_VERSION);
            get.addFamily(Bytes.toBytes(family));
            result = table.get(get);
            KeyValue[] kvs = result.raw();
            if (kvs.length > 0) {
                keyValues = new ArrayList<KeyValue>();
                Collections.addAll(keyValues, kvs);
            }
        } catch (IOException e) {
            throw new RuntimeException("getColumnValues error, " + info, e);
        } finally {
            closeHTable(table);
            SlowLogUtils.logHBaseResTime(tblName, "getColumnValues " + info, start);
        }
        return keyValues;
    }

    /**
     * 字符串改为时间
     *
     * @param date
     * @return
     */
    private static Date convertStr2Date(String date) {
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date time = null;
        try {
            time = formatDate.parse(date.trim());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static List<KeyValue> getValues(String tblName, String rowKey, String family, String startTime, String endTime) {
        long start = System.currentTimeMillis();
        String info = "tblName:" + tblName + ",family:" + family + ",rowKey" + rowKey + ",family" + family + ",startTime:" + startTime + ",endTime:" + endTime;

        Result result = null;
        Table table = null;
        List<KeyValue> keyValues = null;
        try {
            table = getHTable(connection, tblName);
            Get get = new Get(rowKey.getBytes());
            get.addFamily(Bytes.toBytes(family));
            get.setTimeRange(convertStr2Date(startTime).getTime(), convertStr2Date(endTime).getTime());
            result = table.get(get);
            KeyValue[] kvs = result.raw();
            if (kvs.length > 0) {
                keyValues = new ArrayList<KeyValue>();
                Collections.addAll(keyValues, kvs);
            }
        } catch (Exception e) {
            throw new RuntimeException("getColumnValues error," + info, e);
        } finally {
            closeHTable(table);
            SlowLogUtils.logHBaseResTime(tblName, "getColumnValues " + info, start);
        }
        return keyValues;
    }

    public static void insertAndUpdate(String tblName, String rowKey, String family, String colName, String value) {
        long start = System.currentTimeMillis();
        String info = "tblName:" + tblName + ",family:" + family + ",rowKey" + rowKey + ",family" + family + ",colName:" + colName + ",value:" + value;
        Table table = null;
        try {
            table = getHTable(connection, tblName);
            Put p = new Put(Bytes.toBytes(rowKey));

            p.add(Bytes.toBytes(family), (colName != null ? Bytes.toBytes(colName) : null), Bytes.toBytes(value));
            table.put(p);
        } catch (IOException e) {
            logger.error("insertAndUpdate error, " + info, e);
            throw new RuntimeException("insertAndUpdate error, " + info, e);
        } finally {
            closeHTable(table);
            SlowLogUtils.logHBaseResTime(tblName, "insertAndUpdate " + info, start);
        }
    }

    public static void insertAndUpdate(String tblName, String rowKey, String family, String colName, String value, long timestamp) {
        long start = System.currentTimeMillis();
        String info = "tblName:" + tblName + ",family:" + family + ",rowKey" + rowKey + ",family" + family + ",colName:" + colName + ",value:" + value + ", timestamp:" + timestamp;

        Table table = null;
        try {
            table = getHTable(connection, tblName);
            Put p = new Put(Bytes.toBytes(rowKey), timestamp);
            p.add(Bytes.toBytes(family), (colName != null ? Bytes.toBytes(colName) : null), Bytes.toBytes(value));
            table.put(p);
        } catch (IOException e) {
            throw new RuntimeException("insertAndUpdate error, " + info, e);
        } finally {
            closeHTable(table);
            SlowLogUtils.logHBaseResTime(tblName, "insertAndUpdate " + info, start);
        }
    }

    public static void deleteRows(String tblName, final List<String> keys) {
        final List<Delete> ks = new ArrayList<Delete>();
        for (String key : keys) {
            ks.add(new Delete(key.getBytes()));
        }

        exec(tblName, new HBaseExec() {
            @Override
            public Object exec(Table table) throws IOException {
                table.delete(ks);
                return null;
            }

            @Override
            public String logInfo() {
                return "deleteRows, keys count:" + (keys == null ? 0 : keys.size());
            }
        });
    }

    public static void insertAndUpdate(String tblName, String rowKey, InsertData insertData) {
        long start = System.currentTimeMillis();
        String info = "tblName:" + tblName + ",rowKey:" + rowKey + ",insertData:" + insertData.toString();

        Table table = null;
        try {
            table = getHTable(connection, tblName);
            Put p = new Put(Bytes.toBytes(rowKey));
            insertData.addData(p);
            table.put(p);
        } catch (IOException e) {
            throw new RuntimeException("insertAndUpdate error," + info, e);
        } finally {
            closeHTable(table);
            SlowLogUtils.logHBaseResTime(tblName, "insertAndUpdate " + info, start);
        }
    }

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

    public static void insertAndUpdate(String tblName, List<PutInfo> infos) {
        if (infos == null) {
            return;
        }

        long start = System.currentTimeMillis();
        Table table = null;
        try {
            table = getHTable(connection, tblName);
            List<Put> puts = new ArrayList<Put>(infos.size());
            for (PutInfo putInfo : infos) {
                Put p = new Put(Bytes.toBytes(putInfo.getRowKey()));
                puts.add(p);
                Map<String, Map<String, String>> familyAndValues = putInfo.getFamilyAndValues();
                Set<String> keySet = familyAndValues.keySet();
                for (String family : keySet) {
                    Map<String, String> colunmMap = (Map<String, String>) familyAndValues.get(family);
                    if (colunmMap.containsKey("")) {
                        p.add(Bytes.toBytes(family), Bytes.toBytes(""), Bytes.toBytes((String) colunmMap.get("")));
                        continue;
                    } else {
                        Set<String> keySet2 = colunmMap.keySet();
                        for (String qualifier : keySet2) {
                            String value = (String) colunmMap.get(qualifier);
                            if (value == null)
                                value = "";
                            p.add(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
                        }
                    }
                }

            }
            table.put(puts);
        } catch (IOException e) {
            throw new RuntimeException("insertAndUpdate error, tblName:" + tblName, e);
        } finally {
            closeHTable(table);
            SlowLogUtils.logHBaseResTime(tblName, "insertAndUpdate", start);
        }
    }

    /**
     * 删除表的列族
     */
    public static void removeFamily(String tblName, String family) {
        long start = System.currentTimeMillis();

        try {
            String tmp = fixColName(family);
            if (getHBaseAdmin(connection).isTableAvailable(TableName.valueOf(tblName))) {
                getHBaseAdmin(connection).disableTable(TableName.valueOf(tblName));
            }

            getHBaseAdmin(connection).deleteColumn(TableName.valueOf(tblName), tmp.getBytes());
            getHBaseAdmin(connection).enableTable(TableName.valueOf(tblName));
        } catch (IOException e) {
            throw new RuntimeException("removeFamily error, tblName:" + tblName + ",family:" + family, e);
        } finally {
            SlowLogUtils.logHBaseResTime(tblName, "removeFamily,family:" + family, start);
        }
    }

    private static void addFamily(HTableDescriptor htdesc, String family, final boolean readonly) {
        htdesc.addFamily(createHCDesc(family));
        htdesc.setReadOnly(readonly);
    }

    private static HColumnDescriptor createHCDesc(String family) {
        String tmp = fixColName(family);
        byte[] colNameByte = Bytes.toBytes(tmp);
        HColumnDescriptor hColumnDescriptor = new HColumnDescriptor(colNameByte);
        hColumnDescriptor.setMaxVersions(COLUMN_MAX_VERSION);
        return hColumnDescriptor;
    }

    private static String fixColName(String family, String colName) {
        if (colName == null || colName.trim().length() == 0) {
            return family;
        }

        int index = family.indexOf(COLENDCHAR);
        if (index == -1) {
            family += COLENDCHAR + colName;
        }

        return family;
    }

    private static String fixColName(String family) {
        return fixColName(family, null);
    }

//    /**
//     * 创建表的描述
//     *
//     * @param tblName
//     * @return
//     * @throws Exception
//     */
//    private static HTableDescriptor createHTDesc(final String tblName) {
//        return new HTableDescriptor(tblName);
//    }

    /**
     * 判断一张表中是否存在某个列族，如果没有则创建该列族
     *
     * @param tbName
     * @param family
     */
    public static void addColumn(String tbName, String family) {
        long start = System.currentTimeMillis();
        try {
            HTableDescriptor desc = getHBaseAdmin(connection).getTableDescriptor(TableName.valueOf(tbName));
            HColumnDescriptor[] cdesc = desc.getColumnFamilies();
            for (HColumnDescriptor hd : cdesc) {
                String fml = hd.getNameAsString();
                if (family.equals(fml)) {
                    return;
                }
            }
            HColumnDescriptor col = new HColumnDescriptor(family);
            getHBaseAdmin(connection).disableTable(TableName.valueOf(tbName));
            getHBaseAdmin(connection).addColumn(TableName.valueOf(tbName), col);
            getHBaseAdmin(connection).enableTable(TableName.valueOf(tbName));
        } catch (Exception e) {
            logger.error("addColumn error, tblName:" + tbName + ",family:" + family, e);
        } finally {
            SlowLogUtils.logHBaseResTime(tbName, "addColumn, family:" + family, start);
        }
    }

    /**
     * 根据rowkey起始字符串 和 列组合条件获得结果集列表
     *
     * @param tblName
     * @param rowkeyStart
     * @param rowkeyEnd
     * @param compareOpEntityList
     * @param maxResultCount
     * @return
     */
    public static List<Result> getResultsByRowkeyRangeAndCondition(String tblName, String rowkeyStart, String rowkeyEnd,
                                                                   List<CompareOpEntity> compareOpEntityList, long maxResultCount) {
        return getResultsByRowkeyRangeAndCondition(tblName, rowkeyStart, rowkeyEnd, compareOpEntityList, FilterList.Operator.MUST_PASS_ALL,
                maxResultCount);
    }

    /**
     * 根据rowkey起始字符串 和 列组合条件获得结果集列表
     *
     * @param tblName
     * @param rowkeyStart
     * @param rowkeyEnd
     * @param compareOpEntityList
     * @param operator
     * @param maxResultCount
     * @return
     */
    public static List<Result> getResultsByRowkeyRangeAndCondition(String tblName, String rowkeyStart, String rowkeyEnd,
                                                                   List<CompareOpEntity> compareOpEntityList, FilterList.Operator operator, long maxResultCount) {
        long start = System.currentTimeMillis();
        String info = "tblName:" + tblName + ",rowkeyStart:" + rowkeyStart + ",rowkeyEnd:" + rowkeyEnd + ",maxResultCount:" + maxResultCount;

        List<Result> resultList = new ArrayList<Result>();
        Table table = null;
        ResultScanner resultScanner = null;
        int count = 0;
        try {
            table = getHTable(connection, tblName);
            List<Filter> filters = new ArrayList<Filter>();
            for (CompareOpEntity compareOpEntity : compareOpEntityList) {
                Filter filter = new SingleColumnValueFilter(Bytes.toBytes(compareOpEntity.getFamilyName()), Bytes.toBytes(compareOpEntity
                        .getColumnName()), compareOpEntity.getCompareOp(), Bytes.toBytes(compareOpEntity.getCompareValue()));
                filters.add(filter);
            }
            FilterList filterList1 = new FilterList(operator, filters);

            Scan scan = new Scan();
            if (StringUtils.isNotEmpty(rowkeyStart)) {
                scan.setStartRow(Bytes.toBytes(rowkeyStart));
            }
            if (StringUtils.isNotEmpty(rowkeyEnd)) {
                scan.setStopRow(Bytes.toBytes(rowkeyEnd));
            }
            if (filterList1 != null) {
                scan.setFilter(filterList1);
            }
            resultScanner = table.getScanner(scan);
            for (Result result : resultScanner) {
                count++;
                if (count > maxResultCount) {
                    break;
                }
                resultList.add(result);
            }
        } catch (IOException e) {
            logger.error("getResultsByRowkeyRangeAndCondition error, " + info, e);
            throw new RuntimeException("getResultsByRowkeyRangeAndCondition erorr, " + info, e);
        } finally {
            if (resultScanner != null) {
                resultScanner.close();
            }
            closeHTable(table);
            SlowLogUtils.logHBaseResTime(tblName, "getResultsByRowkeyRangeAndCondition " + info, start);
        }
        return resultList;
    }

    public static List<Result> getResultsByRowkeyRangeAndColumnNames(String tblName, String rowkeyStart, String rowkeyEnd, List<String> columnNames,
                                                                     long maxResultCount) {
        long start = System.currentTimeMillis();
        String info = "tblName:" + tblName + ",rowkeyStart:" + rowkeyStart + ",rowkeyEnd:" + rowkeyEnd + ",maxResultCount:" + maxResultCount;

        List<Result> resultList = new ArrayList<Result>();
        Table table = null;
        ResultScanner resultScanner = null;
        int count = 0;
        try {
            table = getHTable(connection, tblName);
            String[] columnArray = new String[columnNames.size()];
            columnNames.toArray(columnArray);

            byte[][] prefixes = Bytes.toByteArrays(columnArray);
            MultipleColumnPrefixFilter filter = new MultipleColumnPrefixFilter(prefixes);

            Scan scan = new Scan();
            scan.setFilter(filter);

            scan.setStartRow(Bytes.toBytes(rowkeyStart));
            scan.setStopRow(Bytes.toBytes(rowkeyEnd));
            if (logger.isDebugEnabled()) {
                logger.debug("rowkeyStart:{},rowkeyEnd:{},columnNames:{},columnArray:{}", rowkeyStart, rowkeyEnd, new Gson().toJson(columnNames), new Gson().toJson(columnArray));
            }

            resultScanner = table.getScanner(scan);
            for (Result result : resultScanner) {
                logger.debug("count:" + count);
                count++;
                if (count > maxResultCount) {
                    break;
                }
                resultList.add(result);
            }
        } catch (IOException e) {
            logger.error("getResultsByRowkeyRangeAndColumnNames error, " + info, e);
            throw new RuntimeException("getResultsByRowkeyRangeAndColumnNames error, " + info, e);
        } finally {
            if (resultScanner != null) {
                resultScanner.close();
            }
            closeHTable(table);
            SlowLogUtils.logHBaseResTime(tblName, "getResultsByRowkeyRangeAndColumnNames " + info, start);
        }
        return resultList;

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

    public static void main(String[] args) throws UnsupportedEncodingException {
        String uuid = "6a4fddcfa5ea4f439ede256db4dfbc00";
        String tblName = "zn_biz_basic";
        String rowKey = uuid + "20171015";

        // #0. Connection
        Connection connection = getConn();
        // #1. Admin
        Admin admin = getHBaseAdmin(connection);
        logger.info("==============>{}", connection);
        logger.info("==============>{}", admin);
        // #2. Table
        Table table = getHTable(connection, tblName);
        logger.info("==============>{}", table);

        // 1. 创建表
        createHbaseTable(admin, tblName, new String[]{"cf1"});

        Map<String, Map<String, String>> dataMap = new HashMap<String, Map<String, String>>();
        Map<String, String> data = new HashMap<String, String>();
        data.put("name", "莫道初");
        data.put("idCard", "142720199505051212");
        data.put("bankCard", "62358809208567222");
        data.put("data", "{'key','value'}");
        dataMap.put("cf1", data);
        insertAndUpdate(table, tblName, rowKey, dataMap);


//        List<Result> result = HBaseUtil.getResultsByRowkeyRange("upa_data", "6a4fddcfa5ea4f439ede256db4dfbc0020171000", "6a4fddcfa5ea4f439ede256db4dfbc0020171016", Integer.MAX_VALUE, "cf1", null, null);
//        System.out.println(result.size());
//        for (Result r : result) {
//            System.out.println(new String(r.getValue("cf1".getBytes(), "bankCard".getBytes())) + "##########" + new String(r.getRow()));
//
//
//        }


//		insertAndUpdate("test_hbase_helper", "1", "f", "col", "testV");
//		System.out.println("get result:"+getValue("test_hbase_helper", "1", "f", "col"));
//		List<Result> rs = getResultsByRowkeyRange("test_hbase_helper","0","z",100);
//		for(Result r : rs) {
//			System.out.println("scan result:"+Bytes.toString(r.getValue("f".getBytes(), "col".getBytes())));
//		}
//		deleteColumn("test_hbase_helper", "1", "f", "col");
//		System.out.println("delete after,"+getValue("test_hbase_helper", "1", "f", "col"));
    }

}
