package com.regaltec.asip.transport.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 
 * <p>Statement执行查询的jdbc任务默认实现</p>
 * <p>创建日期：2010-10-14 下午02:53:19</p>
 *
 * @author 封加华
 */
public class StatementExecuteQueryJdbcTask extends JdbcTask {
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public Object execute(Object[] params) throws Exception {
        List<Map<String, Object>> listOfMaps = new ArrayList<Map<String, Object>>();
        ResultSet rs = null;
        Statement stmt = null;
        Connection conn = getConnection();
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(getSqlBuilder().buildSQL(params));
            listOfMaps = resultSetToListOfMaps(rs);
            // Add by yihaijun at 2012-05-18
            if (listOfMaps.size() >= 100) {
                logger.warn("Do not query so much records!(" + listOfMaps.size() + ">=100)");
            }
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return listOfMaps;
    }
}
