/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jdbc.internal.datasource;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.seedstack.jdbc.internal.JdbcErrorCode;
import org.seedstack.jdbc.spi.DataSourceProvider;
import org.seedstack.seed.SeedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.util.Properties;

public class C3p0DataSourceProvider implements DataSourceProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(C3p0DataSourceProvider.class);

    @Override
    public DataSource provide(String driverClassName, String url, String user, String password, Properties jdbcProperties) {
        ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();

        try {
            comboPooledDataSource.setDriverClass(driverClassName);
            comboPooledDataSource.setJdbcUrl(url);
            if (jdbcProperties != null) {
                comboPooledDataSource.setProperties(jdbcProperties);
            }
            if (user != null) {
                comboPooledDataSource.setUser(user);
            }
            if (password != null) {
                comboPooledDataSource.setPassword(password);
            }
        } catch (PropertyVetoException e) {
            throw SeedException.wrap(e, JdbcErrorCode.UNABLE_TO_PROVIDE_DATASOURCE);
        }

        return comboPooledDataSource;
    }

    @Override
    public void close(DataSource dataSource) {
        try {
            if (dataSource instanceof ComboPooledDataSource) {
                ((ComboPooledDataSource) dataSource).close();
            }
        } catch (Exception e) {
            LOGGER.warn("Unable to close datasource", e);
        }
    }
}
