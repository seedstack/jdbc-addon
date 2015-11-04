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

import org.seedstack.jdbc.JdbcTransaction;
import org.seedstack.seed.SeedException;
import org.seedstack.seed.transaction.spi.TransactionalLink;

import java.sql.Connection;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Link for JDBC connection
 */
class JdbcConnectionLink implements TransactionalLink<Connection> {

    private final ThreadLocal<Deque<JdbcTransaction>> perThreadObjectContainer = new ThreadLocal<Deque<JdbcTransaction>>() {
        @Override
        protected Deque<JdbcTransaction> initialValue() {
            return new ArrayDeque<JdbcTransaction>();
        }
    };

    @Override
    public Connection get() {
        JdbcTransaction transaction = this.perThreadObjectContainer.get().peek();

        if (transaction == null) {
            throw SeedException.createNew(JdbcErrorCode.ACCESSING_JDBC_CONNECTION_OUTSIDE_TRANSACTION);
        }

        return transaction.getConnection();
    }

    Connection getCurrentConnection() {
        JdbcTransaction currentTransaction = getCurrentTransaction();
        if (currentTransaction == null)
            return null;
        return currentTransaction.getConnection();
    }

    JdbcTransaction getCurrentTransaction() {
        return this.perThreadObjectContainer.get().peek();
    }

    void push(JdbcTransaction transaction) {
        perThreadObjectContainer.get().push(transaction);
    }

    JdbcTransaction pop() {
        return perThreadObjectContainer.get().pop();
    }

    boolean isLastTransaction() {
        return perThreadObjectContainer.get().size() == 1;
    }
}
