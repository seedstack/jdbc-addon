/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jdbc.internal;

import com.google.common.base.Strings;
import org.aopalliance.intercept.MethodInvocation;
import org.seedstack.jdbc.Jdbc;
import org.seedstack.jdbc.JdbcConfig;
import org.seedstack.jdbc.JdbcExceptionHandler;
import org.seedstack.seed.Application;
import org.seedstack.seed.SeedException;
import org.seedstack.seed.transaction.spi.TransactionMetadata;
import org.seedstack.seed.transaction.spi.TransactionMetadataResolver;

import javax.inject.Inject;
import java.util.Optional;

class JdbcTransactionMetadataResolver implements TransactionMetadataResolver {
    @Inject
    private Application application;

    @Override
    public TransactionMetadata resolve(MethodInvocation methodInvocation, TransactionMetadata defaults) {
        Optional<Jdbc> jdbcOptional = JdbcResolver.INSTANCE.apply(methodInvocation.getMethod());
        if (jdbcOptional.isPresent() || JdbcTransactionHandler.class.equals(defaults.getHandler())) {
            TransactionMetadata result = new TransactionMetadata();
            result.setHandler(JdbcTransactionHandler.class);
            result.setExceptionHandler(JdbcExceptionHandler.class);

            if (jdbcOptional.isPresent() && !Strings.isNullOrEmpty(jdbcOptional.get().value())) {
                result.setResource(jdbcOptional.get().value());
            } else {
                String defaultDatasource = application.getConfiguration().get(JdbcConfig.class).getDefaultDataSource();
                if (!Strings.isNullOrEmpty(defaultDatasource)) {
                    result.setResource(defaultDatasource);
                } else {
                    throw SeedException.createNew(JdbcErrorCode.NO_DATASOURCE_SPECIFIED_FOR_TRANSACTION)
                            .put("method", methodInvocation.getMethod().toString());
                }
            }
            return result;
        }
        return null;
    }
}
