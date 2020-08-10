/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jdbc.spi;

import io.nuun.kernel.api.annotations.Facet;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

/**
 * The JDBC provider is an SPI for accessing configured JDBC data sources.
 */
@Facet
public interface JdbcProvider {
    /**
     * Provides a configured data source by its name.
     *
     * @param name the data source name.
     * @return the datasource.
     */
    DataSource getDataSource(String name);
    
    /**
     * Provides all configured data source names.
     *
     * @return All the dataSource names.
     */
    List<String> getDataSourceNames();
}
