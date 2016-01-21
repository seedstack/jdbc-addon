/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/*
 * Creation : 18 févr. 2015
 */
package org.seedstack.jdbc;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;

import javax.inject.Inject;

import net.jcip.annotations.NotThreadSafe;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.seed.it.SeedITRunner;
import org.seedstack.jdbc.sample.Repository;
import org.seedstack.seed.transaction.Transactional;

@RunWith(SeedITRunner.class)
@NotThreadSafe
public class JdbcPersistenceIT {

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
    public void simpleTransaction() throws Exception {
        String barfoo = "barfoo";
        assertThat(internalSimpleTransaction(barfoo)).isEqualTo(barfoo);
    }

    @Transactional
    protected String internalSimpleTransaction(String barfoo) throws Exception {
        int id = 1;
        repository.add(id, barfoo);
        return repository.getBar(id);
    }

    @Test
    public void nestedTransaction() throws Exception {
        assertThat(internalNestedTransaction()).isNull();
    }

    @Transactional
    @Jdbc("datasource1")
    protected String internalNestedTransaction() throws Exception {
        int id1 = 1;
        final String bar1 = "barfoo";
        repository.add(id1, bar1);
        int id2 = 2;
        final String bar2 = "foobar";
        try {
            repository.addFail(id2, bar2);
        } catch (Exception e) {
            // ignore exception
        }
        return repository.getBar(id2);
    }
}
