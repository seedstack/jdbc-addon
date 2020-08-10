/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jdbc.spi;

import javax.annotation.Nullable;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * Interface for data source providers. The role of a datasource provider is to create a datasource the JDBC add-on
 * will be able to use.
 */
public interface DataSourceProvider {

    /**
     * Provides a datasource
     *
     * @param driverClassName the qualified name of the driver class to use.
     * @param url             configured url
     * @param user            configured user
     * @param password        configured password
     * @param jdbcProperties  Additional configured properties
     * @return the datasource
     */
    DataSource provide(String driverClassName, String url, @Nullable String user, @Nullable String password, @Nullable Properties jdbcProperties);

    /**
     * This method is called upon shutdown to allow the provider to close a datasource in an implementation-specific
     * way.
     *
     * @param dataSource the datasource that may be closed.
     */
    void close(DataSource dataSource);
}
