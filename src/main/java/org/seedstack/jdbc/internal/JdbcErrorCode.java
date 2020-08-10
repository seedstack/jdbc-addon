/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jdbc.internal;

import org.seedstack.shed.exception.ErrorCode;

public enum JdbcErrorCode implements ErrorCode {
    ACCESSING_JDBC_CONNECTION_OUTSIDE_TRANSACTION,
    CANNOT_CONNECT_TO_JDBC_DATASOURCE,
    ERROR_AUTO_DETECTING_JDBC_DRIVER,
    JDBC_CLOSE_EXCEPTION,
    JDBC_COMMIT_EXCEPTION,
    JDBC_ROLLBACK_EXCEPTION,
    JNDI_CONTEXT_NOT_FOUND,
    JNDI_NAME_NOT_FOUND,
    NO_DATASOURCE_SPECIFIED_FOR_TRANSACTION,
    UNABLE_TO_INSTANTIATE_DATASOURCE_PROVIDER,
    UNABLE_TO_PROVIDE_DATASOURCE
}
