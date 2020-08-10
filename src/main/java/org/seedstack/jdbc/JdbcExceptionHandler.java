/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jdbc;

import org.seedstack.seed.transaction.spi.ExceptionHandler;
import org.seedstack.seed.transaction.spi.TransactionMetadata;

/**
 * Transaction exception handler for JDBC transactions
 */
public interface JdbcExceptionHandler extends ExceptionHandler<JdbcTransaction> {

    @Override
    boolean handleException(Exception exception, TransactionMetadata associatedTransactionMetadata, JdbcTransaction associatedTransaction);

}
