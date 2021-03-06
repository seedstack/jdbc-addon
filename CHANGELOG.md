# Version 3.0.3 (2020-08-10)

* [chg] Update for Seed 3.10+ 

# Version 3.0.2 (2017-07-31)

* [chg] Improved error message when no data source is specified.

# Version 3.0.1 (2017-02-26)

* [fix] Fix transitive dependency to poms SNAPSHOT.

# Version 3.0.0 (2016-12-13)

* [brk] Update to SeedStack 16.11 new configuration system.
* [brk] Remove the possibility of NOT specifying the datasource in `@Jdbc` if only one is present. Use the `defaultDatasource` configuration property instead. 
* [new] The Hikari provider automatically picks the `hikari.properties` file if present at the classpath root.

# Version 2.1.3 (2016-04-26)

* [chg] Update for SeedStack 16.4
* [fix] Correctly cleanup `ThreadLocal` in `JdbcConnectionLink`

# Version 2.1.2 (2016-01-21)

* [fix] Errors message were referencing outdated information.

# Version 2.1.1 (2015-11-25)

* [chg] Uses JDBC add-on 2.1.1

# Version 2.1.0 (2015-11-15)

* [chg] Refactored as an add-on and updated to work with Seed 2.1.0+

# Version 2.0.0 (2015-07-30)

* [new] Initial Open-Source release.
