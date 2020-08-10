/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jdbc.internal;

import org.seedstack.jdbc.JdbcExceptionHandler;
import org.seedstack.jdbc.spi.DataSourceProvider;

import javax.sql.DataSource;

class DataSourceDefinition {
    private final String name;
    private final DataSource dataSource;
    private Class<? extends JdbcExceptionHandler> jdbcExceptionHandler;
    private DataSourceProvider dataSourceProvider;

    DataSourceDefinition(String name, DataSource dataSource) {
        this.name = name;
        this.dataSource = dataSource;
    }

    String getName() {
        return name;
    }

    Class<? extends JdbcExceptionHandler> getJdbcExceptionHandler() {
        return jdbcExceptionHandler;
    }

    void setJdbcExceptionHandler(Class<? extends JdbcExceptionHandler> jdbcExceptionHandler) {
        this.jdbcExceptionHandler = jdbcExceptionHandler;
    }

    DataSourceProvider getDataSourceProvider() {
        return dataSourceProvider;
    }

    void setDataSourceProvider(DataSourceProvider dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
    }

    DataSource getDataSource() {
        return dataSource;
    }
}
