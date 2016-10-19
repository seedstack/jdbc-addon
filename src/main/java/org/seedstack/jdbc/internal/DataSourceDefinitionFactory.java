/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jdbc.internal;

import com.google.common.base.Strings;
import org.seedstack.jdbc.JdbcConfig;
import org.seedstack.jdbc.spi.DataSourceProvider;
import org.seedstack.seed.SeedException;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Map;

class DataSourceDefinitionFactory {
    private final Map<String, Context> jndiContexts;

    DataSourceDefinitionFactory(Map<String, Context> jndiContexts) {
        this.jndiContexts = jndiContexts;
    }

    DataSourceDefinition createDataSourceDefinition(String name, JdbcConfig.DataSourceConfig dataSourceConfig) {
        DataSourceDefinition dataSourceDefinition;

        if (Strings.isNullOrEmpty(dataSourceConfig.getJndiName())) {
            dataSourceDefinition = createLocalDataSource(name, dataSourceConfig);
        } else {
            dataSourceDefinition = createJndiDataSource(name, dataSourceConfig);
        }
        dataSourceDefinition.setJdbcExceptionHandler(dataSourceConfig.getExceptionHandler());

        return dataSourceDefinition;
    }

    private DataSourceDefinition createLocalDataSource(String name, JdbcConfig.DataSourceConfig dataSourceConfig) {
        DataSourceProvider dataSourceProvider;
        try {
            dataSourceProvider = dataSourceConfig.getProvider().newInstance();
        } catch (Exception e) {
            throw SeedException.wrap(e, JdbcErrorCode.UNABLE_TO_INSTANTIATE_DATASOURCE_PROVIDER)
                    .put("providerClass", dataSourceConfig.getProvider().getName());
        }

        DataSourceDefinition dataSourceDefinition = new DataSourceDefinition(name, dataSourceProvider.provide(
                dataSourceConfig.getDriver().getName(),
                dataSourceConfig.getUrl(),
                dataSourceConfig.getUser(),
                dataSourceConfig.getPassword(),
                dataSourceConfig.getProperties()
        ));
        dataSourceDefinition.setDataSourceProvider(dataSourceProvider);
        return dataSourceDefinition;
    }

    private DataSourceDefinition createJndiDataSource(String name, JdbcConfig.DataSourceConfig dataSourceConfig) {
        Context context;
        if (Strings.isNullOrEmpty(dataSourceConfig.getJndiContext())) {
            context = jndiContexts.get("default");
        } else {
            context = jndiContexts.get(dataSourceConfig.getJndiContext());
        }

        if (context == null) {
            throw SeedException.createNew(JdbcErrorCode.JNDI_CONTEXT_NOT_FOUND)
                    .put("jndiContext", dataSourceConfig.getJndiContext())
                    .put("dataSource", name);
        }

        try {
            return new DataSourceDefinition(name, (DataSource) context.lookup(dataSourceConfig.getJndiName()));
        } catch (NamingException e) {
            throw SeedException.createNew(JdbcErrorCode.JNDI_NAME_NOT_FOUND)
                    .put("jndiName", dataSourceConfig.getJndiName())
                    .put("jndiContext", dataSourceConfig.getJndiContext())
                    .put("dataSource", name);
        }
    }
}
