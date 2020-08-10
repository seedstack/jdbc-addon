/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jdbc;

import org.seedstack.coffig.Config;
import org.seedstack.jdbc.internal.JdbcErrorCode;
import org.seedstack.jdbc.internal.datasource.PlainDataSourceProvider;
import org.seedstack.jdbc.spi.DataSourceProvider;
import org.seedstack.seed.SeedException;

import javax.validation.constraints.NotNull;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Config("jdbc")
public class JdbcConfig {
    @Config("datasources")
    private Map<String, DataSourceConfig> dataSources = new HashMap<>();
    @Config("defaultDatasource")
    private String defaultDataSource;

    public Map<String, DataSourceConfig> getDataSources() {
        return Collections.unmodifiableMap(dataSources);
    }

    public void addDataSource(String name, DataSourceConfig dataSourceConfig) {
        dataSources.put(name, dataSourceConfig);
    }

    public String getDefaultDataSource() {
        return defaultDataSource;
    }

    public JdbcConfig setDefaultDataSource(String defaultDataSource) {
        this.defaultDataSource = defaultDataSource;
        return this;
    }

    public static class DataSourceConfig {
        @NotNull
        private Class<? extends DataSourceProvider> provider = PlainDataSourceProvider.class;
        private Class<? extends JdbcExceptionHandler> exceptionHandler;
        private Class<? extends Driver> driver;
        private String url;
        private Properties properties = new Properties();
        private String user;
        private String password;
        private String jndiName;
        private String jndiContext;

        public Class<? extends DataSourceProvider> getProvider() {
            return provider;
        }

        public DataSourceConfig setProvider(Class<? extends DataSourceProvider> provider) {
            this.provider = provider;
            return this;
        }

        public Class<? extends JdbcExceptionHandler> getExceptionHandler() {
            return exceptionHandler;
        }

        public DataSourceConfig setExceptionHandler(Class<? extends JdbcExceptionHandler> exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
            return this;
        }

        public Class<? extends Driver> getDriver() {
            if (driver != null) {
                return driver;
            } else {
                try {
                    return DriverManager.getDriver(url).getClass();
                } catch (SQLException e) {
                    throw SeedException.wrap(e, JdbcErrorCode.ERROR_AUTO_DETECTING_JDBC_DRIVER)
                            .put("url", url);
                }
            }
        }

        public DataSourceConfig setDriver(Class<? extends Driver> driver) {
            this.driver = driver;
            return this;
        }

        public String getUrl() {
            return url;
        }

        public DataSourceConfig setUrl(String url) {
            this.url = url;
            return this;
        }

        public Properties getProperties() {
            return properties;
        }

        public DataSourceConfig setProperties(Properties properties) {
            this.properties = properties;
            return this;
        }

        public DataSourceConfig setProperty(String name, String value) {
            this.properties.setProperty(name, value);
            return this;
        }

        public String getUser() {
            return user;
        }

        public DataSourceConfig setUser(String user) {
            this.user = user;
            return this;
        }

        public String getPassword() {
            return password;
        }

        public DataSourceConfig setPassword(String password) {
            this.password = password;
            return this;
        }

        public String getJndiName() {
            return jndiName;
        }

        public DataSourceConfig setJndiName(String jndiName) {
            this.jndiName = jndiName;
            return this;
        }

        public String getJndiContext() {
            return jndiContext;
        }

        public DataSourceConfig setJndiContext(String jndiContext) {
            this.jndiContext = jndiContext;
            return this;
        }
    }
}
