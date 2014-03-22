package com.regaltec.asip.transport.jdbc;

import javax.sql.DataSource;

/**
 * 
 * <p>SQL构造器</p>
 * <p>创建日期：2010-10-12 下午04:24:06</p>
 *
 * @author 封加华
 */
public abstract class SQLBuilder {
    /**
     * 数据源-有时候sql通过存储过程构造，提供数据源
     */
    private DataSource dataSource;
    /**
     * 
     * <p>生成sql</p>
     * @param params 参数
     * @return 返回完整的sql
     * @throws Exception 异常
     */
    public abstract String buildSQL(Object[] params)throws Exception;

    /**
     * @return the dataSource
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * @param dataSource the dataSource to set
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

}
