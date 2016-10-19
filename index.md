---
title: "Basics"
name: "JDBC"
repo: "https://github.com/seedstack/jdbc-addon"
date: 2016-01-21
author: Yves DAUTREMAY
description: "Provides configuration, injection and transactions for JDBC datasources."
backend: true
weight: -1
tags:
    - "persistence"
    - "jdbc"
    - "data"
    - "database"
zones:
    - Addons
menu:
    AddonJDBC:
        weight: 10
---

Seed JDBC persistence add-on enables your application to interface with any relational database through the JDBC API. 

{{< dependency g="org.seedstack.addons.jdbc" a="jdbc" >}}

# Configuration

You can configure the add-on in one or more of your \*.props files. Declare you list of data source names you will be 
configuring later:

    org.seedstack.jdbc.datasources = datasource1, datasource2, ...
    
Configure each data source separately. Notice the use of the keyword *property* to specify any property that will be 
used by the datasource as specific configuration.

    [org.seedstack.jdbc.datasource.datasource1]
    provider = HikariDataSourceProvider
    driver = org.hsqldb.jdbcDriver
    url = jdbc:hsqldb:mem:testdb1
    user = sa
    password =
    property.specific.jdbc.prop = value
    property.prop.for.datasource = value

Alternatively, if you want to lookup the data source through JNDI you can use this configuration:

    [org.seedstack.jdbc.datasource.datasource1]
    jndi-name = java:comp/env/jdbc/my-datasource
    context = ...
    
The `context` property is optional and can be used to specify a particular context name configured in 
[core support](/docs/seed/manual/core/jndi) to make the lookup. Otherwise the default context (named `default`) will be used.
    
# Usage

The following examples show how to get a JDBC connection. 
    
    public class MyRepository {

        @Inject
        private Connection connection;

        @Transactional
        @Jdbc("datasource1")
        public void updateStuff(int id, String bar){
            try{
                String sql = "INSERT INTO FOO VALUES(?, ?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, id);
                statement.setString(2, bar);
                statement.executeUpdate();
            } catch(SqlException e){
                throw new SomeRuntimeException(e, "message");
            }
        }
    }
    
{{% callout info %}}
As seen in the example above, any interaction with this connection have to be done inside a **transaction**. Refer to the [transaction support documentation](/docs/seed/manual/transactions) for more detail.
{{% /callout %}}

# Data source providers

## Built-in providers

When using a non JNDI data source, we recommend the use of pooled datasource through a DataSourceProvider defined in the 
configuration. Four data source providers can be specified in the `provider` property:

* [HikariCP](http://brettwooldridge.github.io/HikariCP/) with `HikariDataSourceProvider`
* [Commons DBCP](http://commons.apache.org/proper/commons-dbcp/) with `DbcpDataSourceProvider`
* [C3P0](http://www.mchange.com/projects/c3p0/) with `C3p0DataSourceProvider`
* A test-only plain data source provider with `PlainDataSourceProvider`. **Do not use in production**.

## Custom providers

In the case you want to use another data source provider, you can create your own `DataSourceProvider` by implementing the {{< java "org.seedstack.jdbc.spi.DataSourceProvider" >}} interface:

    public class SomeDataSourceProvider implements DataSourceProvider {
    
        @Override
        public DataSource provideDataSource(String driverClass, String
                url, String user, String password, Properties jdbcProperties) {
            SomeDataSource sds = new SomeDataSource();
            sds.setDriverClass(driverClass);
            sds.setJdbcUrl(url);
            sds.setUser(url);
            sds.setPassword(user);
            sds.setProperties(jdbcProperties);
            return sds;
        }
    
    }
    
You will be able to declare it in your configuration as `SomeDataSourceProvider` (the simple name of your class). Note 
that if you want to use one of the three datasource providers described above, you will have to add the corresponding 
dependency to your project.
