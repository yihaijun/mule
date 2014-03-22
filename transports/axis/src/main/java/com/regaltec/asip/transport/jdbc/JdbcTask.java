package com.regaltec.asip.transport.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * <p>描述一个Jdbc任务</p>
 * <p>创建日期：2010-10-12 下午02:44:30</p>
 *
 * @author 封加华
 */
public abstract class JdbcTask {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * jdbc 连接
     */
    private Connection connection;
    /**
     * sql生成器
     */
    private SQLBuilder sqlBuilder;

    /**
     * sql statement
     */
    private String statement;

    /**
     * 输出参数
     */
    private String outParameter;

    /**
     * 
     * <p>执行函数,默认行为</p>
     * @param params 参数类型为object array
     * @return 由实现者决定其具体类型
     * @exception Exception 异常
     */
    public abstract Object execute(Object[] params) throws Exception;

    /**
     * 
     * <p>执行jdbc查询，默认将返回listOfMaps格式</p>
     * @param sql sql statement
     * @param params 参数
     * @return List<Map<String, Object>> 每行用一个Map描述作为List中的element
     * @throws Exception 异常
     */
    protected List<Map<String, Object>> executeQuery(String sql, Object... params) throws Exception {
        List<Map<String, Object>> listOfMaps = new ArrayList<Map<String, Object>>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection conn = getConnection();
        try {
            pstmt = conn.prepareStatement(sql);
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
            }
            rs = pstmt.executeQuery();
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
            if (pstmt != null) {
                try {
                    pstmt.close();
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

    /**
     * 
     * <p>将jdbc ResultSet转换成List<Map<String, Object>>格式</p>
     * @param rs ResultSet
     * @return List<Map<String, Object>>
     * @throws SQLException 异常
     */
    protected List<Map<String, Object>> resultSetToListOfMaps(ResultSet rs) throws SQLException {
        List<Map<String, Object>> listOfMaps = new ArrayList<Map<String, Object>>();
        Map<String, Object> rowMap = null;
        if (rs != null) {
            while (rs.next()) {
                ResultSetMetaData metaData = rs.getMetaData();
                rowMap = new HashMap<String, Object>();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    rowMap.put(metaData.getColumnLabel(i).toLowerCase(), rs.getObject(i));
                }
                listOfMaps.add(rowMap);
            }
        }
        return listOfMaps;
    }

    /**
     * <p>执行select之外的语句，例如：insert delete</p>
     * @param sql sql statement
     * @param params 参数
     * @return 影响行
     * @throws Exception 异常
     */
    protected int executeUpdate(String sql, Object... params) throws Exception {
        Connection conn = getConnection();
        PreparedStatement pstmt = null;
        pstmt = conn.prepareStatement(sql);
        int rows = 0;
        try {
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
            }
            rows = pstmt.executeUpdate();
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
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
        return rows;
    }

    /**
     * 
     * <p>执行存储过程，返回类型不明确</p>
     * @param params 参数
     * @return 返回值
     * @throws Exception 异常
     */
    protected Object executeProcedure(Object... params) throws Exception {
        Object[] objArray = null;
        boolean success = false;
        CallableStatement proc = null;
        Connection conn = getConnection();
        try {
            // "{call cp_tru_get_lognew(?,?,?,?,?,?)}"
            if (logger.isDebugEnabled()) {
                logger.debug("executeProcedure：" + statement);
            }
            proc = conn.prepareCall(statement);
            // 处理入参
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    proc.setObject((i + 1), params[i]);
                }
            }
            // 处理出参
            if (null != outParameter && !"".equals(outParameter)) {
                String[] arr = outParameter.split("#");
                objArray = new Object[arr.length];
                if (logger.isDebugEnabled()) {
                    logger.debug("outParameter:" + outParameter + "(size=" + objArray.length + ")");
                }
                for (int i = 0; i < arr.length; i++) {
                    String p = arr[i];
                    String[] ps = p.split(",");
                    int index = Integer.parseInt(ps[0].substring(1));
                    int type = Integer.parseInt(ps[1].substring(0, ps[1].length() - 1));
                    if (logger.isDebugEnabled()) {
                        logger.debug("ps[" + index + "].type=" + type);
                    }
                    proc.registerOutParameter(index, type);
                    objArray[i] = index;
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("proc.execute() begin....");
                }
            } else {
                objArray = new Object[0];
            }
            // 执行存储过程
            success = proc.execute();
            logger.debug("proc.execute() return.");
            for (int i = 0; i < objArray.length; i++) {
                Object o = proc.getObject(Integer.parseInt(objArray[i].toString()));
                if (o instanceof ResultSet) {
                    ResultSet rs = null;
                    try {
                        rs = (ResultSet) o;
                        objArray[i] = resultSetToListOfMaps(rs);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (rs != null) {
                            try {
                                rs.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    // Add by yihaijun at 2012-05-18
                    if (((List<Map<String, Object>>) objArray[i]).size() >= 100) {
                        logger.warn("Do not query so much records!(" + ((List<Map<String, Object>>) objArray[i]).size()
                                + ">=100)");
                    }
                } else {
                    objArray[i] = o;
                }
            }
        } finally {
            if (proc != null) {
                try {
                    proc.close();
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
        // 如果只有一个出参数，则返回一个Object，多个出参返回参数顺序的Object数组，如果出参类型为ResultSet已经转换为 List of Map object
        if (objArray != null & objArray.length == 1) {
            return objArray[0];// 一个出参时，返回Object
        } else if (objArray != null & objArray.length == 0) {
            return success; // 没有出参，返回true false
        } else {
            return objArray;// 多个出参时，返回数组
        }
    }

    /**
     * @return the connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * @param connection the connection to set
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * @return the sqlBuilder
     */
    public SQLBuilder getSqlBuilder() {
        return sqlBuilder;
    }

    /**
     * @param sqlBuilder the sqlBuilder to set
     */
    public void setSqlBuilder(SQLBuilder sqlBuilder) {
        this.sqlBuilder = sqlBuilder;
    }

    /**
     * @return the statement
     */
    public String getStatement() {
        return statement;
    }

    /**
     * @param statement the statement to set
     */
    public void setStatement(String statement) {
        this.statement = statement;
    }

    /**
     * @return the outParameter
     */
    public String getOutParameter() {
        return outParameter;
    }

    /**
     * @param outParameter the outParameter to set
     */
    public void setOutParameter(String outParameter) {
        this.outParameter = outParameter;
    }
}
