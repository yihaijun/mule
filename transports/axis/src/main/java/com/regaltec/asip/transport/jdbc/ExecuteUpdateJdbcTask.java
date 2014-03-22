package com.regaltec.asip.transport.jdbc;
/**
 * 
 * <p>执行修改或删除的jdbc任务默认实现</p>
 * <p>创建日期：2010-10-14 下午02:53:47</p>
 *
 * @author 封加华
 */
public class ExecuteUpdateJdbcTask extends JdbcTask {
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public Object execute(Object[] params) throws Exception {

        return this.executeUpdate(getSqlBuilder().buildSQL(params), params);
    }
}
