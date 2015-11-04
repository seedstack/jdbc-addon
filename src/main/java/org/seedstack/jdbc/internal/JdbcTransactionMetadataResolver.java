/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/*
 * Creation : 18 févr. 2015
 */
package org.seedstack.jdbc.internal;

import org.aopalliance.intercept.MethodInvocation;
import org.seedstack.jdbc.Jdbc;
import org.seedstack.jdbc.JdbcExceptionHandler;
import org.seedstack.seed.core.utils.SeedReflectionUtils;
import org.seedstack.seed.transaction.spi.TransactionMetadata;
import org.seedstack.seed.transaction.spi.TransactionMetadataResolver;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

/**
 * TransactionMetadataResolver for JDBC support. It reads the JDBC annotation to know which datasource should be use.
 */
class JdbcTransactionMetadataResolver implements TransactionMetadataResolver {

    static String defaultJdbc;

    @Inject
    @Named("jdbc-registered-classes")
    private Map<Class<?>, String> registeredClasses;

    @Override
    public TransactionMetadata resolve(MethodInvocation methodInvocation, TransactionMetadata defaults) {
        Jdbc jdbc = SeedReflectionUtils.getMethodOrAncestorMetaAnnotatedWith(methodInvocation.getMethod(), Jdbc.class);
        String resourceName = resolveResourceName(jdbc, methodInvocation);
        if (jdbc != null || JdbcTransactionHandler.class.equals(defaults.getHandler())) {
            TransactionMetadata result = new TransactionMetadata();
            result.setHandler(JdbcTransactionHandler.class);
            result.setExceptionHandler(JdbcExceptionHandler.class);
            result.setResource(resourceName);
            return result;
        }
        return null;
    }

    private String resolveResourceName(Jdbc jdbc, MethodInvocation methodInvocation) {
        String resourceName = defaultJdbc;
        if (jdbc != null && !"".equals(jdbc.value())) {
            resourceName = jdbc.value();
        } else if (registeredClasses.containsKey(methodInvocation.getMethod().getDeclaringClass())) {
            resourceName = registeredClasses.get(methodInvocation.getMethod().getDeclaringClass());
        }
        return resourceName;
    }
}
