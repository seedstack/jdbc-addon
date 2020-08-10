/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jdbc.internal;

import com.google.common.collect.Lists;
import io.nuun.kernel.api.plugin.InitState;
import io.nuun.kernel.api.plugin.context.InitContext;
import org.seedstack.jdbc.JdbcConfig;
import org.seedstack.jdbc.spi.DataSourceProvider;
import org.seedstack.jdbc.spi.JdbcProvider;
import org.seedstack.seed.core.internal.AbstractSeedPlugin;
import org.seedstack.seed.core.internal.jndi.JndiPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This plugins configures, starts and stops JDBC datasources.
 */
public class JdbcPlugin extends AbstractSeedPlugin implements JdbcProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcPlugin.class);
    private final Map<String, DataSourceDefinition> dataSourceDefinitions = new HashMap<>();
    private final Map<Class<?>, String> registeredClasses = new HashMap<>();

    @Override
    public String name() {
        return "jdbc";
    }

    @Override
    public Collection<Class<?>> dependencies() {
        return Lists.newArrayList(JndiPlugin.class);
    }

    @Override
    public InitState initialize(InitContext initContext) {
        JdbcConfig jdbcConfig = getConfiguration(JdbcConfig.class);
        DataSourceDefinitionFactory dataSourceDefinitionFactory = new DataSourceDefinitionFactory(initContext.dependency(JndiPlugin.class).getJndiContexts());

        if (jdbcConfig.getDataSources().isEmpty()) {
            LOGGER.info("No datasource configured, JDBC support disabled");
        } else {
            jdbcConfig.getDataSources().entrySet().stream()
                    .map((entry) -> dataSourceDefinitionFactory.createDataSourceDefinition(entry.getKey(), entry.getValue()))
                    .forEach(dataSourceDefinition -> dataSourceDefinitions.put(dataSourceDefinition.getName(), dataSourceDefinition));
        }

        return InitState.INITIALIZED;
    }

    @Override
    public Object nativeUnitModule() {
        return new JdbcModule(dataSourceDefinitions, registeredClasses);
    }

    @Override
    public void stop() {
        for (DataSourceDefinition dataSourceDefinition : dataSourceDefinitions.values()) {
            DataSourceProvider dataSourceProvider = dataSourceDefinition.getDataSourceProvider();
            if (dataSourceProvider != null) {
                LOGGER.info("Closing JDBC datasource {}", dataSourceDefinition.getName());
                try {
                    dataSourceProvider.close(dataSourceDefinition.getDataSource());
                } catch (Exception e) {
                    LOGGER.error(String.format("Unable to properly close JDBC datasource %s", dataSourceDefinition.getName()), e);
                }
            }
        }
    }

    @Override
    public DataSource getDataSource(String name) {
        DataSourceDefinition definition = dataSourceDefinitions.get(name);
        if (definition != null) {
            return definition.getDataSource();
        } else {
            return null;
        }
    }

    @Override
    public List<String> getDataSourceNames() {
        List<String> dataSourceNames = new ArrayList<>();
        dataSourceDefinitions.forEach((datasourceName, datasource) -> {
            dataSourceNames.add(datasourceName);
        });
        return dataSourceNames;
    }
}
