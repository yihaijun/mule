/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.jdbc.test;

import com.mockobjects.dynamic.Mock;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

public class TestDataSource implements DataSource
{
    public Connection getConnection() throws SQLException
    {
        Mock mockConnection = new Mock(Connection.class);
        mockConnection.expectAndReturn("getAutoCommit", false);
        mockConnection.expect("commit");
        mockConnection.expect("close");

        return (Connection) mockConnection.proxy();
    }

    public Connection getConnection(String username, String password) throws SQLException
    {
        return getConnection();
    }

    public int getLoginTimeout() throws SQLException
    {
        return 0;
    }

    public PrintWriter getLogWriter() throws SQLException
    {
        return null;
    }

    public void setLoginTimeout(int seconds) throws SQLException
    {
        // nop
    }

    public void setLogWriter(PrintWriter out) throws SQLException
    {
        // nop
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException
    {
        return false;
    }

    public <T> T unwrap(Class<T> iface) throws SQLException
    {
        return null;
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException
    {
        return null;
    }
}


