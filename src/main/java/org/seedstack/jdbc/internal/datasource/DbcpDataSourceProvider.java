/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jdbc.internal.datasource;

import org.apache.commons.dbcp.BasicDataSource;
import org.seedstack.jdbc.spi.DataSourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Properties;

public class DbcpDataSourceProvider implements DataSourceProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(DbcpDataSourceProvider.class);

    @Override
    public DataSource provide(String driverClassName, String url, String user, String password, Properties dataSourceProperties) {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName(driverClassName);
        basicDataSource.setUrl(url);
        addConnectionProperties(basicDataSource, dataSourceProperties);
        if (user != null) {
            basicDataSource.setUsername(user);
        }
        if (password != null) {
            basicDataSource.setPassword(password);
        }
        return basicDataSource;
    }

    @Override
    public void close(DataSource dataSource) {
        try {
            ((BasicDataSource) dataSource).close();
        } catch (Exception e) {
            LOGGER.warn("Unable to close datasource", e);
        }
    }

    private void addConnectionProperties(BasicDataSource basicDataSource, Properties dataSourceProperties) {
        for (Object key : dataSourceProperties.keySet()) {
            basicDataSource.addConnectionProperty((String) key, dataSourceProperties.getProperty((String) key));
        }
    }
}
