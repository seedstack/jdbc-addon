/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jdbc.internal.datasource;

import org.seedstack.jdbc.internal.JdbcErrorCode;
import org.seedstack.jdbc.spi.DataSourceProvider;
import org.seedstack.seed.SeedException;

import javax.sql.DataSource;
import java.sql.Driver;
import java.util.Properties;

/**
 * Data source provider for the {@link PlainDataSource}.
 */
public class PlainDataSourceProvider implements DataSourceProvider {
    @Override
    public DataSource provide(String driverClassName, String url, String user, String password, Properties jdbcProperties) {
        try {
            return new PlainDataSource(Class.forName(driverClassName).asSubclass(Driver.class).newInstance(), url, jdbcProperties, user, password);
        } catch (Exception e) {
            throw SeedException.wrap(e, JdbcErrorCode.UNABLE_TO_PROVIDE_DATASOURCE)
                    .put("driverClass", driverClassName)
                    .put("url", url);
        }
    }

    @Override
    public void close(DataSource dataSource) {
        // not supported
    }
}
