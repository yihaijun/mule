package com.regaltec.asip.transport.jdbc;

/**
 * 
 * <p>执行查询的jdbc任务默认实现</p>
 * <p>创建日期：2010-10-14 下午02:53:19</p>
 *
 * @author 封加华
 */
public class PreparedStatementExecuteQueryJdbcTask extends JdbcTask {
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public Object execute(Object[] params) throws Exception {

        return this.executeQuery(getSqlBuilder().buildSQL(params), params);
    }
}
