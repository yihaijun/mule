package com.regaltec.asip.utils;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * 
 * <p>数据库映射</p>
 * <p>创建日期：2010-10-20 下午12:41:22</p>
 *
 * @author 封加华
 */
public class DBMapping {
    private static Logger logger = LoggerFactory.getLogger(DBMapping.class);
    private static ApplicationContext context;
    static {
        // 取虚拟机系统属性，因为mule启动时会设置mule.home系统属性
        String muleHome = System.getProperty("mule.home");
        if (muleHome == null || "".equals(muleHome)) {
            muleHome = System.getenv("MULE_HOME");// 取系统环境变量
        }
        String basePath = new File(muleHome, "asipconf").getAbsolutePath();// muleHome + "/" + "asipconf";
        File f = new File(basePath);
        if (!f.exists()) {
            f.mkdirs();
        }
        String configFile = new File(basePath, "application-datasource.xml").getAbsolutePath();
        String configFileStr = "file:" + configFile;
        context = new FileSystemXmlApplicationContext(configFileStr);
    }
    private DataSource dataSource;
    private Connection conn = null;
    private ResultSet rs = null;
    private PreparedStatement pstmt = null;

    /**
     * 
     * 构造函数
     * @param dataSourceId spring配置中数据源的id
     */
    public DBMapping(String dataSourceId) {
        dataSource = (DataSource) context.getBean(dataSourceId);
    }

    /**
     * 
     * <p>释放数据库相关句柄</p>
     */
    private void close() {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        try {
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * 
     * <p>返回第一行第一列</p>
     * @param querySql select sql
     * @param params 参数
     * @return 第一列数据
     */
    public String getFirstColumn(String querySql, Object[] params) {
        querySql = querySql.trim();
        rs = executeQuery(querySql, params);
        String result = null;
        if (rs != null) {
            try {
                if (rs.next()) {
                    result = rs.getString(1);
                }
            } catch (Exception e) {
                logger.error("", e);
                return null;
            } finally {
                close();
            }
        }
        return result;
    }

    /**
     * 
     * <p>返回第一行第一列(不带参数)</p>
     * @param querySql select sql
     * @return 列数据
     */
    public String getFirstColumn(String querySql) {
        return getFirstColumn(querySql, null);
    }

    /**
     * 
     * <p>返回第一行</p>
     * @param querySql select sql
     * @param params 参数 
     * @return Map<String,String> 返回第一行记录，key以列名小写形式存在
     */
    public Map<String, String> getFirstRow(String querySql, Object[] params) {
        querySql = querySql.trim();
        rs = executeQuery(querySql, params);
        Map<String, String> row = null;
        if (rs != null) {
            try {
                if (rs.next()) {
                    row = new HashMap<String, String>();
                    ResultSetMetaData rsm = rs.getMetaData();
                    for (int i = 1; i <= rsm.getColumnCount(); i++) {
                        row.put(rsm.getColumnLabel(i).toLowerCase(), rs.getString(i));
                    }
                }
            } catch (Exception e) {
                logger.error("", e);
                return null;
            } finally {
                close();
            }
        }
        return row;
    }

    /**
     * 
     * <p>返回第一行(不带参数)</p>
     * @param querySql select sql
     * @return Map<String,String> 返回第一行记录，key以列名小写形式存在
     */
    public Map<String, String> getFirstRow(String querySql) {
        return getFirstRow(querySql, null);
    }

    /**
     * 
     * <p>返回整个结果集</p>
     * @param querySql select sql
     * @param params 参数
     * @return List<Map<String,String>>每一行作为一个Map，key以列名消协形式存在
     */
    public List<Map<String, String>> getRowSet(String querySql, Object[] params) {
        rs = executeQuery(querySql, params);
        List<Map<String, String>> listOfMaps = new ArrayList<Map<String, String>>();
        Map<String, String> row = null;
        if (rs != null) {
            try {
                while (rs.next()) {
                    row = new HashMap<String, String>();
                    ResultSetMetaData rsm = rs.getMetaData();
                    for (int i = 1; i <= rsm.getColumnCount(); i++) {
                        row.put(rsm.getColumnLabel(i).toLowerCase(), rs.getString(i));
                    }
                    listOfMaps.add(row);
                }
            } catch (Exception e) {
                logger.error("", e);
                return null;
            } finally {
                close();
            }
            if(listOfMaps.size() >= 100){
                logger.warn("Do not query so much records!(" + listOfMaps.size() +">=100)");
            }
        }
        return listOfMaps;
    }

    /**
     * 
     * <p>返回整个结果集(不带参数)</p>
     * @param querySql select sql
     * @return List<Map<String,String>>每一行作为一个Map，key以列名消协形式存在
     */
    public List<Map<String, String>> getRowSet(String querySql) {
        return getRowSet(querySql, null);
    }

    /**
     * 
     * <p>功能的简单描述，参数、返回值及异常必须注明。</p>
     * @param querySql 查询sql，只允许select语句
     * @param params 参数
     * @return java.sql.ResultSet
     */
    private ResultSet executeQuery(String querySql, Object[] params) {
        querySql = querySql.replaceAll("\n\r", "").replaceAll("\n", "");
        if (logger.isDebugEnabled()) {
            logger.debug("DBMapping开始执行sql转换,sql=" + querySql + ",参数=" + Arrays.toString(params));
        }
        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(querySql);
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
            }
            return pstmt.executeQuery();
        } catch (Exception e) {
            logger.error("", e);
            //异常情况关闭句柄
            if(pstmt!=null){
                try {
                    pstmt.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                } 
            }
            if(conn!=null){
                try {
                    conn.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            
            return null;
        }
    }
}
