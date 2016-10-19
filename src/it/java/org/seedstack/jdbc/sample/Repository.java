/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jdbc.sample;

import org.seedstack.jdbc.Jdbc;
import org.seedstack.seed.it.ITBind;
import org.seedstack.seed.transaction.Propagation;
import org.seedstack.seed.transaction.Transactional;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@ITBind
public class Repository {
    @Inject
    private Connection connection;

    public void drop() throws SQLException {
        Statement stmnt = connection.createStatement();
        String sql = "DROP TABLE FOO";
        stmnt.executeUpdate(sql);
    }

    public void init() throws SQLException {
        Statement stmnt = connection.createStatement();
        String sql = "CREATE TABLE FOO (id INTEGER not NULL, bar varchar(255), PRIMARY KEY (id))";
        stmnt.executeUpdate(sql);
    }

    public void add(int id, String bar) throws SQLException {
        String sql = "INSERT INTO FOO VALUES(?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        statement.setString(2, bar);
        statement.executeUpdate();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Jdbc("datasource1")
    public void addFail(int id, String bar) throws Exception {
        add(id, bar);
        throw new Exception();
    }

    public String getBar(int id) throws SQLException {
        String sql = "SELECT * FROM FOO WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next())
            return resultSet.getString("bar");
        return null;
    }
}
