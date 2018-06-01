package com.niewj.db;

import com.niewj.PropUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Vector;

/**
 * Created by weijun.nie on 2018/6/1.
 */
public class ConnectionUtil {
    private Vector<Connection> pool = new Vector<>();
    private static final String DB_DRIVER; // 驱动
    private static final String DB_URL; // 连接地址
    private static final String DB_USER; // 用户名
    private static final String DB_PWD; // 密码
    private static final int POOL_SIZE; // 连接池大小
    private static final Map<String, String> configMap = PropUtil.getConfigMap("config/config.properties");

    static {
        DB_DRIVER = configMap.get("db_driver");
        DB_URL = configMap.get("db_url");
        DB_USER = configMap.get("db_user");
        DB_PWD = configMap.get("db_pwd");
        POOL_SIZE = Integer.parseInt(configMap.get("pool_size"));
    }

    private ConnectionUtil() {
        initConnectionPool();
    }

    /**
     * 从数据库连接池中获取数据库连接
     *
     * @return
     */
    public Connection getConnection() {
        if (pool == null || pool.isEmpty()) {
            return null;
        }
        return pool.remove(0);
    }

    /**
     * 将用完的数据库连接放回连接池poo中
     *
     * @param connection
     */
    public void freeConnection(Connection connection) {
        if (connection != null) {
            pool.add(connection);
        }
    }

    /**
     * 初始化数据库连接池
     */
    private void initConnectionPool() {
        try {
            Class.forName(DB_DRIVER); // 1.加载驱动程序
            for (int i = 1; i <= POOL_SIZE; i++) {
                Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PWD); // 2.连接
                pool.add(connection);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 单例的内部类方式
     */
    public static class DbUtilHolder {
        private static ConnectionUtil instance = new ConnectionUtil(); // 单例

        public static ConnectionUtil getInstance() {
            return DbUtilHolder.instance;
        }
    }

}
