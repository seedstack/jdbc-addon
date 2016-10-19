/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jdbc.internal.datasource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

class PlainDataSource implements DataSource {
    private final Driver driver;
    private final String url;
    private final Properties properties;
    private PrintWriter writer = new PrintWriter(System.out);
    private int timeout;

    PlainDataSource(Driver driver, String url, Properties properties, String user, String password) {
        this.driver = driver;
        this.url = url;
        this.properties = properties;
        if (user != null) {
            properties.put("user", user);
        }
        if (password != null) {
            properties.put("password", password);
        }
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return writer;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.writer = out;
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        this.timeout = seconds;
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return timeout;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        // not using JUL
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return (T) this;
        }
        throw new SQLException("Wrapped DataSource is not an instance of " + iface);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return driver.connect(this.url, this.properties);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        Properties properties = new Properties(this.properties);
        properties.put("user", username);
        properties.put("password", password);
        return driver.connect(this.url, properties);
    }
}
