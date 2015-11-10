/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/*
 * Creation : 17 févr. 2015
 */
package org.seedstack.jdbc.internal;

import com.google.common.collect.Lists;
import io.nuun.kernel.api.plugin.InitState;
import io.nuun.kernel.api.plugin.PluginException;
import io.nuun.kernel.api.plugin.context.InitContext;
import io.nuun.kernel.api.plugin.request.ClasspathScanRequest;
import io.nuun.kernel.core.AbstractPlugin;
import org.apache.commons.configuration.Configuration;
import org.seedstack.jdbc.spi.DataSourceProvider;
import org.seedstack.seed.core.internal.jndi.JndiPlugin;
import org.seedstack.seed.core.spi.configuration.ConfigurationProvider;
import org.seedstack.seed.transaction.internal.TransactionPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * JDBC support plugin
 */
public class JdbcPlugin extends AbstractPlugin implements JdbcRegistry {
    public static final String JDBC_PLUGIN_CONFIGURATION_PREFIX = "org.seedstack.jdbc";
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcPlugin.class);

    private Map<String, DataSourceDefinition> dataSourceDefinitions;

    private final Map<Class<?>, String> registeredClasses = new HashMap<Class<?>, String>();

    @Override
    public String name() {
        return "jdbc";
    }

    @SuppressWarnings("unchecked")
    @Override
    public InitState init(InitContext initContext) {
        Configuration jdbcConfiguration = initContext.dependency(ConfigurationProvider.class)
                .getConfiguration().subset(JDBC_PLUGIN_CONFIGURATION_PREFIX);
        TransactionPlugin transactionPlugin = initContext.dependency(TransactionPlugin.class);
        JndiPlugin jndiPlugin = initContext.dependency(JndiPlugin.class);

        Collection<Class<?>> dataSourceProviderClasses = initContext.scannedSubTypesByParentClass()
                .get(DataSourceProvider.class);

        dataSourceDefinitions = new DataSourceDefinitionFactory(jdbcConfiguration)
                .createDataSourceDefinitions(jndiPlugin.getJndiContexts(), dataSourceProviderClasses);

        // If there is only one dataSource set it as the default
        if (dataSourceDefinitions.size() == 1) {
            JdbcTransactionMetadataResolver.defaultJdbc = dataSourceDefinitions.keySet().iterator().next();
        }

        // If dataSources are configured enable the JdbcTransactionHandler
        if (!dataSourceDefinitions.isEmpty()) {
            transactionPlugin.registerTransactionHandler(JdbcTransactionHandler.class);
        }

        return InitState.INITIALIZED;
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
    public Collection<Class<?>> requiredPlugins() {
        return Lists.<Class<?>>newArrayList(ConfigurationProvider.class, TransactionPlugin.class, JndiPlugin.class);
    }

    @Override
    public Object nativeUnitModule() {
        return new JdbcModule(dataSourceDefinitions, registeredClasses);
    }

    @Override
    public Collection<ClasspathScanRequest> classpathScanRequests() {
        return classpathScanRequestBuilder().subtypeOf(DataSourceProvider.class).build();
    }

    @Override
    public void registerDataSourceForClass(Class<?> clazz, String dataSourceName) {
        if (!dataSourceDefinitions.containsKey(dataSourceName)) {
            throw new PluginException("DataSource [" + dataSourceName
                    + "] Does not exist. Make sure it corresponds to a DataSource declared under configuration " + JDBC_PLUGIN_CONFIGURATION_PREFIX
                    + ".datasources");
        }
        registeredClasses.put(clazz, dataSourceName);
    }

    @Override
    public DataSource getDataSource(String dataSource) {
        DataSourceDefinition definition = dataSourceDefinitions.get(dataSource);
        if (definition != null) {
            return definition.getDataSource();
        } else {
            return null;
        }
    }
}
