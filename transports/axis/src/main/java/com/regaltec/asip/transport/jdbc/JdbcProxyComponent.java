package com.regaltec.asip.transport.jdbc;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;

/**
 * 
 * <p>Jdbc代理组件，负责执行jdbc任务。</p>
 * <p>创建日期：2010-10-12 下午02:43:37</p>
 *
 * @author 封加华
 */
public class JdbcProxyComponent implements Callable {
    private Log logger = LogFactory.getLog(getClass());
    /**
     * 数据源
     */
    private DataSource dataSource;
    /**
     * jdbcTask
     */
    private JdbcTask jdbcTask;

    /**
     * 
     * {@inheritDoc}
     */
    public Object onCall(MuleEventContext eventContext) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("onCall() begin...");
        }
        jdbcTask.setConnection(dataSource.getConnection());
        Object payload = eventContext.getMessage().getPayload();
        Object result = null;
        if (payload instanceof Object[]) {
            result = jdbcTask.execute((Object[]) payload);
        } else {
            result = jdbcTask.execute(new Object[] { payload });
        }
        if (logger.isDebugEnabled()) {
            logger.debug("onCall() return.");
        }
        return result;
    }

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

    /**
     * @return the jdbcTask
     */
    public JdbcTask getJdbcTask() {
        return jdbcTask;
    }

    /**
     * @param jdbcTask the jdbcTask to set
     */
    public void setJdbcTask(JdbcTask jdbcTask) {
        this.jdbcTask = jdbcTask;
    }
}
