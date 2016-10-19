/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jdbc.internal;

import org.aopalliance.intercept.MethodInvocation;
import org.seedstack.jdbc.Jdbc;
import org.seedstack.jdbc.JdbcExceptionHandler;
import org.seedstack.seed.core.utils.SeedReflectionUtils;
import org.seedstack.seed.transaction.spi.TransactionMetadata;
import org.seedstack.seed.transaction.spi.TransactionMetadataResolver;

class JdbcTransactionMetadataResolver implements TransactionMetadataResolver {
    static String defaultJdbc;

    @Override
    public TransactionMetadata resolve(MethodInvocation methodInvocation, TransactionMetadata defaults) {
        Jdbc jdbc = SeedReflectionUtils.getMethodOrAncestorMetaAnnotatedWith(methodInvocation.getMethod(), Jdbc.class);
        if (jdbc != null || JdbcTransactionHandler.class.equals(defaults.getHandler())) {
            TransactionMetadata result = new TransactionMetadata();
            result.setHandler(JdbcTransactionHandler.class);
            result.setExceptionHandler(JdbcExceptionHandler.class);
            result.setResource(resolveResourceName(jdbc));
            return result;
        }
        return null;
    }

    private String resolveResourceName(Jdbc jdbc) {
        if (jdbc != null && !"".equals(jdbc.value())) {
            return jdbc.value();
        } else {
            return defaultJdbc;
        }
    }
}
