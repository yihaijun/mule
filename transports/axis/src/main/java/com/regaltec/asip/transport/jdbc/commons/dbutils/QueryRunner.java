package com.regaltec.asip.transport.jdbc.commons.dbutils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import javax.sql.DataSource;

/**
 * 
 * <p>使用apache的QueryRunner时，
 * 由于有些数据库驱动并未实现PreparedStatement的getParameterMetaData方法所以在42行会报NullPointException
 * 重写QueryRunner在43行加了if判断
 * </p>
 * <p>创建日期：2010-10-15 下午05:10:12</p>
 *
 * @author 易海军
 */
public class QueryRunner extends org.apache.commons.dbutils.QueryRunner {
    protected final DataSource ds;
    /**
     * 
     * 构造函数
     */
    public QueryRunner() {
        super();
        ds = null;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void fillStatement(PreparedStatement stmt, Object[] params) throws SQLException {

        if (params == null) {
            return;
        }

        /*ParameterMetaData pmd = stmt.getParameterMetaData();
        if (pmd != null) {
            if (pmd.getParameterCount() < params.length) {
                throw new SQLException("Too many parameters: expected " + pmd.getParameterCount() + ", was given "
                        + params.length);
            }
        }*/
        for (int i = 0; i < params.length; i++) {
            if (params[i] != null) {
                stmt.setObject(i + 1, params[i]);
            } else {
                // VARCHAR works with many drivers regardless
                // of the actual column type. Oddly, NULL and
                // OTHER don't work with Oracle's drivers.
                int sqlType = Types.VARCHAR;
                /*if (!pmdKnownBroken) {
                    try {
                        sqlType = pmd.getParameterType(i + 1);
                    } catch (SQLException e) {
                        pmdKnownBroken = true;
                    }
                }*/
                stmt.setNull(i + 1, sqlType);
            }
        }
    }
}
