/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jdbc.internal;

import org.seedstack.jdbc.JdbcTransaction;
import org.seedstack.seed.SeedException;
import org.seedstack.seed.transaction.spi.TransactionHandler;
import org.seedstack.seed.transaction.spi.TransactionMetadata;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * The transaction Handler for JDBC. It needs to make sure only one connection is open for each thread. For nested new transactions, the same
 * connection must be passed but a savepoint is created in case of partial rollback.
 */
class JdbcTransactionHandler implements TransactionHandler<JdbcTransaction> {
    private final String name;
    private final DataSource dataSource;
    private final JdbcConnectionLink jdbcConnectionLink;

    /**
     * Constructor
     *
     * @param jdbcConnectionLink jdbc link for the connection
     * @param dataSource         the datasource to use when needing a new connection
     */
    JdbcTransactionHandler(String name, JdbcConnectionLink jdbcConnectionLink, DataSource dataSource) {
        this.name = name;
        this.dataSource = dataSource;
        this.jdbcConnectionLink = jdbcConnectionLink;
    }

    @Override
    public void doInitialize(TransactionMetadata transactionMetadata) {
        Connection connection = jdbcConnectionLink.getCurrentConnection();
        JdbcTransaction transaction;
        try {
            if (connection == null) {
                connection = dataSource.getConnection();
                connection.setAutoCommit(false);
                transaction = new JdbcTransaction(connection);
            } else {
                transaction = new JdbcTransaction(connection, connection.setSavepoint());
            }
        } catch (SQLException e) {
            throw SeedException.wrap(e, JdbcErrorCode.CANNOT_CONNECT_TO_JDBC_DATASOURCE)
                    .put("dataSource", name);
        }
        jdbcConnectionLink.push(transaction);
    }

    @Override
    public JdbcTransaction doCreateTransaction() {
        return jdbcConnectionLink.getCurrentTransaction();
    }

    @Override
    public void doJoinGlobalTransaction() {
        throw new UnsupportedOperationException("Global transactions are not supported with JDBC");
    }

    @Override
    public void doBeginTransaction(JdbcTransaction transaction) {
        // Nothing to do
    }

    @Override
    public void doCommitTransaction(JdbcTransaction transaction) {
        try {
            transaction.commit();
        } catch (SQLException e) {
            throw SeedException.wrap(e, JdbcErrorCode.JDBC_COMMIT_EXCEPTION)
                    .put("dataSource", name);
        }
    }

    @Override
    public void doMarkTransactionAsRollbackOnly(JdbcTransaction transaction) {
        transaction.setRollbackOnly();
    }

    @Override
    public void doRollbackTransaction(JdbcTransaction transaction) {
        try {
            transaction.rollBack();
        } catch (SQLException e) {
            throw SeedException.wrap(e, JdbcErrorCode.JDBC_ROLLBACK_EXCEPTION)
                    .put("dataSource", name);
        }
    }

    @Override
    public void doReleaseTransaction(JdbcTransaction transaction) {
        if (transaction.getRollbackOnly())
            doRollbackTransaction(transaction);
    }

    @Override
    public void doCleanup() {
        try {
            jdbcConnectionLink.pop();
        } catch (SQLException e) {
            throw SeedException.wrap(e, JdbcErrorCode.JDBC_CLOSE_EXCEPTION)
                    .put("dataSource", name);
        }
    }

    @Override
    public JdbcTransaction getCurrentTransaction() {
        return jdbcConnectionLink.getCurrentTransaction();
    }

}
