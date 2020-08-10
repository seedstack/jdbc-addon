/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jdbc.internal.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.seedstack.jdbc.internal.JdbcErrorCode;
import org.seedstack.jdbc.spi.DataSourceProvider;
import org.seedstack.seed.SeedException;
import org.seedstack.shed.ClassLoaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class HikariDataSourceProvider implements DataSourceProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(HikariDataSourceProvider.class);

    @Override
    public DataSource provide(String driverClassName, String url, String user, String password, Properties dataSourceProperties) {
        HikariConfig hikariConfig = getHikariConfig(driverClassName, url);
        hikariConfig.setDriverClassName(driverClassName);
        hikariConfig.setJdbcUrl(url);
        if (dataSourceProperties != null) {
            hikariConfig.setDataSourceProperties(dataSourceProperties);
        }
        if (user != null) {
            hikariConfig.setUsername(user);
        }
        if (password != null) {
            hikariConfig.setPassword(password);
        }
        return new HikariDataSource(hikariConfig);
    }

    @Override
    public void close(DataSource dataSource) {
        try {
            if (dataSource instanceof HikariDataSource) {
                ((HikariDataSource) dataSource).close();
            }
        } catch (Exception e) {
            LOGGER.warn("Unable to close datasource", e);
        }
    }

    @SuppressFBWarnings(value = "RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE", justification = "false positive due to try with resources")
    private HikariConfig getHikariConfig(String driverClassName, String url) {
        HikariConfig hikariConfig = null;

        try (InputStream inputStream = ClassLoaders.findMostCompleteClassLoader(HikariDataSourceProvider.class).getResourceAsStream("hikari.properties")) {
            if (inputStream != null) {
                Properties properties = new Properties();
                properties.load(inputStream);
                hikariConfig = new HikariConfig(properties);
                LOGGER.info("Loaded hikari.properties from classpath");
            }
        } catch (IOException e) {
            throw SeedException.wrap(e, JdbcErrorCode.UNABLE_TO_PROVIDE_DATASOURCE)
                    .put("driverClass", driverClassName)
                    .put("url", url);
        }

        if (hikariConfig == null) {
            hikariConfig = new HikariConfig();
        }

        return hikariConfig;
    }
}
