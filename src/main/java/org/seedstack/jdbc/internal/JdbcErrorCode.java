/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/*
 * Creation : 17 févr. 2015
 */
package org.seedstack.jdbc.internal;

import org.seedstack.seed.ErrorCode;

/**
 * JDBC Error codes
 */
public enum JdbcErrorCode implements ErrorCode {

    ACCESSING_JDBC_CONNECTION_OUTSIDE_TRANSACTION,

    CANNOT_CONNECT_TO_JDBC_DATASOURCE,

    JDBC_COMMIT_EXCEPTION,

    JDBC_ROLLBACK_EXCEPTION,

    JDBC_CLOSE_EXCEPTION,

    WRONG_JDBC_DRIVER,

    WRONG_DATASOURCE_PROVIDER,

    WRONG_DATASOURCE_CONTEXT, MISSING_DATASOURCE_CONFIG;

}
