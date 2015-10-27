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
package org.seedstack.seed.persistence.jdbc.api;

import org.seedstack.seed.transaction.spi.ExceptionHandler;
import org.seedstack.seed.transaction.spi.TransactionMetadata;

/**
 * Transaction exception handler for JDBC transactions
 */
public interface JdbcExceptionHandler extends ExceptionHandler<JdbcTransaction> {

    @Override
    boolean handleException(Exception exception, TransactionMetadata associatedTransactionMetadata, JdbcTransaction associatedTransaction);

}
