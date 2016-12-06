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
import org.seedstack.seed.transaction.spi.TransactionMetadata;
import org.seedstack.seed.transaction.spi.TransactionMetadataResolver;

import java.util.Optional;

class JdbcTransactionMetadataResolver implements TransactionMetadataResolver {
    static String defaultJdbc;

    @Override
    public TransactionMetadata resolve(MethodInvocation methodInvocation, TransactionMetadata defaults) {
        Optional<Jdbc> jdbc = JdbcResolver.INSTANCE.apply(methodInvocation.getMethod());
        if (jdbc.isPresent() || JdbcTransactionHandler.class.equals(defaults.getHandler())) {
            TransactionMetadata result = new TransactionMetadata();
            result.setHandler(JdbcTransactionHandler.class);
            result.setExceptionHandler(JdbcExceptionHandler.class);
            result.setResource(jdbc.isPresent() ? resolveDatasource(jdbc.get()) : defaultJdbc);
            return result;
        }
        return null;
    }

    private String resolveDatasource(Jdbc jdbc) {
        String value = jdbc.value();
        if (value.isEmpty()) {
            return defaultJdbc;
        } else {
            return value;
        }
    }
}
