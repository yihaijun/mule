package com.regaltec.asip.transport.jdbc;
/**
 * 
 * <p>执行存储过程的jdbc任务默认实现</p>
 * <p>创建日期：2010-10-14 下午02:52:34</p>
 *
 * @author 封加华
 */
public class ExecuteProcedureJdbcTask extends JdbcTask {
    /**
     * @return 如果只有一个出参数，则返回一个Object，多个出参返回参数顺序的Object数组，如果出参类型为ResultSet已经转换为 List of Map object，没出参返回Object[0]
     * {@inheritDoc}
     */
    @Override
    public Object execute(Object[] params) throws Exception {

        return this.executeProcedure(params);
    }
}
