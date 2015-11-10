/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jdbc.internal;

import io.nuun.kernel.api.annotations.Facet;

import javax.sql.DataSource;

/**
 * The JDBC registry is an internal API for accessing configured JDBC data sources.
 *
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
@Facet
public interface JdbcRegistry {

    /**
     * This method allows to automatically use a data source for the given class when it asks for the injection of a connection.
     *
     * @param dataSourceName the dataSource to use
     * @param aClass          the class requiring a connection
     */
    void registerDataSourceForClass(Class<?> aClass, String dataSourceName);

    /**
     * Provides a configured data source by its name.
     *
     * @param dataSource the data source name
     * @return the dataSource
     */
    DataSource getDataSource(String dataSource);
}
