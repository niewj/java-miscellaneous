package com.niewj.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by weijun.nie on 2018/6/1.
 */
public class DbUtil {

    private static Logger logger = LoggerFactory.getLogger(DbUtil.class);

    /**
     * 查询表中所有字段
     *
     * @param connection
     * @return
     */
    public List<DbKeyValue> getConfig(Connection connection) {
        List<DbKeyValue> resultList = new ArrayList<>();
        if (connection == null) {
            return null;
        }
        String sql = "select prop_name, prop_value from gthb where code_valid = 1";

        PreparedStatement pst = null;
        ResultSet rst = null;
        try {
            pst = connection.prepareStatement(sql);
            rst = pst.executeQuery();
            while (rst.next()) {
                String k = rst.getString(1);
                String v = rst.getString(2);
                resultList.add(new DbKeyValue(k, v));
            }

        } catch (SQLException e) {
            logger.error("SQLE: {}", e);
        } finally {
            try {
                if (rst != null && !rst.isClosed()) {
                    rst.close();
                }
                if (pst != null && !pst.isClosed()) {
                    pst.close();
                }
                if (connection != null && !connection.isClosed()) {
                    // ConnectionUtil是单例； 释放连接
                    ConnectionUtil.DbUtilHolder.getInstance().freeConnection(connection);
                }
            } catch (SQLException e) {
                logger.error("close resource: {}", e);
            }
        }

        return resultList;
    }

    /**
     * 表gthb
     */
    public static class DbKeyValue {
        private String prop_name; // k
        private String prop_value; // v

        public DbKeyValue(String prop_name, String prop_value) {
            this.prop_name = prop_name;
            this.prop_value = prop_value;
        }

        public String getProp_name() {
            return prop_name;
        }

        public String getProp_value() {
            return prop_value;
        }
    }


}
