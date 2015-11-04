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

import com.google.inject.PrivateModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.google.inject.util.Providers;
import org.seedstack.jdbc.JdbcExceptionHandler;
import org.seedstack.seed.transaction.spi.TransactionalProxy;

import java.sql.Connection;
import java.util.Map;

class JdbcModule extends PrivateModule {
    private static final String JDBC_REGISTERED_CLASSES = "jdbc-registered-classes";

    private final Map<String, DataSourceDefinition> dataSourceDefinitions;
    private final Map<Class<?>, String> registeredClasses;

    JdbcModule(Map<String, DataSourceDefinition> dataSourceDefinitions, Map<Class<?>, String> registeredClasses) {
        this.dataSourceDefinitions = dataSourceDefinitions;
        this.registeredClasses = registeredClasses;
    }

    @Override
    protected void configure() {
        // Connection
        JdbcConnectionLink jdbcLink = new JdbcConnectionLink();
        bind(Connection.class).toInstance(TransactionalProxy.create(Connection.class, jdbcLink));

        // Datasources
        for (DataSourceDefinition definition : dataSourceDefinitions.values()) {
            bindDataSource(definition, jdbcLink);
        }

        // Classes registered to use a specific datasource
        bind(new TypeLiteral<Map<Class<?>, String>>() {}).annotatedWith(Names.named(JDBC_REGISTERED_CLASSES)).toInstance(registeredClasses);

        expose(Connection.class);
        expose(new TypeLiteral<Map<Class<?>, String>>() {}).annotatedWith(Names.named(JDBC_REGISTERED_CLASSES));
    }

    private void bindDataSource(DataSourceDefinition definition,  JdbcConnectionLink jdbcLink) {
        Named dataSourceQualifier = Names.named(definition.getName());

        if (definition.getJdbcExceptionHandler() != null) {
            bind(JdbcExceptionHandler.class).annotatedWith(dataSourceQualifier).to(definition.getJdbcExceptionHandler());
        } else {
            bind(JdbcExceptionHandler.class).annotatedWith(dataSourceQualifier).toProvider(Providers.<JdbcExceptionHandler> of(null));
        }

        JdbcTransactionHandler transactionHandler = new JdbcTransactionHandler(jdbcLink, definition.getDataSource());
        bind(JdbcTransactionHandler.class).annotatedWith(dataSourceQualifier).toInstance(transactionHandler);

        expose(JdbcExceptionHandler.class).annotatedWith(dataSourceQualifier);
        expose(JdbcTransactionHandler.class).annotatedWith(dataSourceQualifier);
    }
}
