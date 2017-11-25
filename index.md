---
title: "JDBC"
repo: "https://github.com/seedstack/jdbc-addon"
author: Yves DAUTREMAY
description: "Provides configuration, injection and transactions for JDBC datasources."
tags:
    - persistence
    - transactions
zones:
    - Addons
menu:
    AddonJDBC:
        weight: 10
---

SeedStack JDBC add-on provides support for connection to any relational database through the JDBC API.<!--more--> 

# Dependency

{{< dependency g="org.seedstack.addons.jdbc" a="jdbc" >}}

{{% callout info %}}
A JDBC driver is also required in the classpath and depends upon the chosen database.
{{% /callout %}}

# Configuration

Configuration is done by declaring one or more data-sources:

{{% config p="jdbc" %}}
```yaml
jdbc:
  # Configured data-sources with the name of the data-source as key
  datasources:
    datasource1:
      # The fully qualified class name of the data-source provider (see below, defaults to 'org.seedstack.jdbc.internal.datasource.PlainDataSourceProvider')
      provider: (Class<? extends DataSourceProvider>)

      # The fully qualified class name of the JDBC driver (automatically detected from url if not specified)
      driver: (Class<? extends Driver>)
      
      # The URL of the data-source
      url: (String)
      
      # The properties of the data-source (dependent on the driver) 
      properties:
        property1: value1
      
      # The username used to connect to the data-source (optional) 
      user: (String)
      
      # The password used to connect to the data-source (optional)
      password: (String)
      
      # The fully qualified class name of the exception handler (optional)
      exceptionHandler: (Class<? extends JdbcExceptionHandler>)
      
      # When looking up the datasource through JNDI, the name of the data-source.
      jndiName: (String)
      
      # When looking up the datasource through JNDI, the context to do the lookup (use the default context if not specified)
      jndiContext: (String)      
    
  # The name of the configured data-source to use if nothing is specified in the '@Jdbc' annotation    
  defaultDatasource: (String)
```
{{% /config %}}    

## Examples

### With Hikari pooling

The following YAML configures a data-source named `datasource1`, using the [Hikari connection pool](https://brettwooldridge.github.io/HikariCP/)
which is a very fast and reliable connection pool.

```yaml
jdbc:
  datasources:
    datasource1:
      provider: org.seedstack.jdbc.internal.datasource.HikariDataSourceProvider
      url: jdbc:hsqldb:mem:testdb1
```

Note that the driver class name is automatically detected according to the URL. The Hikari dependency will be needed: 

{{< dependency g="com.zaxxer" a="HikariCP" v="2.5.1" >}}

### JNDI lookup
    
When a JNDI name is specified, the `provider`, `url`, `properties`, `user` and `password` configuration options
are ignored.      
    
```yaml
jdbc:
  datasources:
    datasource1:
      jndiName: java:comp/env/jdbc/my-datasource
```    

{{% callout info %}}
The `jndiContext` configuration option is needed only when you want to do the lookup in a non-default JNDI context.
{{% /callout %}}
    
# Usage

The following examples show how to get a JDBC connection. 
    
```java
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
```
    
{{% callout info %}}
Any interaction with this connection have to be done inside a **transaction**. Refer to the 
[transaction documentation]({{< ref "docs/core/transactions.md" >}}) for more detail.
{{% /callout %}}

# Data source providers

## Built-in

When using a non JNDI data-source, we recommend the use of a connection pool. This is done by specifying a class 
implementing the {{< java "org.seedstack.jdbc.spi.DataSourceProvider" >}} interface. The built-in providers are:

* [HikariCP](http://brettwooldridge.github.io/HikariCP/) with `HikariDataSourceProvider`
* [Commons DBCP](http://commons.apache.org/proper/commons-dbcp/) with `DbcpDataSourceProvider`
* [C3P0](http://www.mchange.com/projects/c3p0/) with `C3p0DataSourceProvider`
* A test-only, do-nothing, plain data-source provider with `PlainDataSourceProvider`. **Do not use in production**.

{{% callout info %}}
To use a connection pool, add its dependency on the classpath. Each connection pool must be configured according to its 
documentation.
{{% /callout %}}

## Custom

In the case you want to use another data source provider, you can create your own `DataSourceProvider` by implementing 
the {{< java "org.seedstack.jdbc.spi.DataSourceProvider" >}} interface:

```java
public class SomeDataSourceProvider implements DataSourceProvider {
    @Override
    public DataSource provideDataSource(String driverClass, String url, String user, String password, Properties jdbcProperties) {
        // TODO: build the data-source and return it
    }
}
```

To use it, just specify the fully qualified name of the class in the `provider` configuration option.
