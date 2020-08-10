/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jdbc;

import net.jcip.annotations.NotThreadSafe;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.jdbc.sample.Repository;
import org.seedstack.seed.testing.junit4.SeedITRunner;
import org.seedstack.seed.transaction.Transactional;

import javax.inject.Inject;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SeedITRunner.class)
@NotThreadSafe
public class JdbcPersistenceIT {

    private static final String BARFOO = "barfoo";
    private static final String FOOBAR = "foobar";
    @Inject
    private Repository repository;

    private static boolean alreadyInitialized;

    @Transactional
    @Jdbc
    @Before
    public void init() throws SQLException {
        if (alreadyInitialized) {
            repository.drop();
        } else {
            alreadyInitialized = true;
        }
        repository.init();
    }

    @Test
    @Transactional
    public void simpleTransaction() throws Exception {
        repository.add(1, BARFOO);
        assertThat(repository.getBar(1)).isEqualTo(BARFOO);
    }

    @Test
    @Transactional
    @Jdbc("datasource1")
    public void nestedTransaction() throws Exception {
        repository.add(1, BARFOO);
        try {
            repository.addFail(2, FOOBAR);
        } catch (Exception e) {
            // ignore exception
        }
        assertThat(repository.getBar(2)).isNull();
    }
}
