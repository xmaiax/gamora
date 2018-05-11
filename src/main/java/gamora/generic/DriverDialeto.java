package gamora.generic;

public enum DriverDialeto {

   H2("org.h2.Driver", "org.hibernate.dialect.H2Dialect")
  ,DERBY_EMBEDDED("org.apache.derby.jdbc.EmbeddedDriver", "org.hibernate.dialect.DerbyDialect")
  ,POSTGRESQL("org.postgresql.Driver", "org.hibernate.dialect.PostgreSQLDialect")
  ,POSTGRESPLUS("org.postgresql.Driver", "org.hibernate.dialect.PostgresPlusDialect")
  ,MYSQL("com.mysql.jdbc.Driver", "org.hibernate.dialect.MySQLDialect")
  ,MYSQL5("com.mysql.jdbc.Driver", "org.hibernate.dialect.MySQL5Dialect")
  ,FIREBIRD("org.firebirdsql.jdbc.FBDriver", "org.hibernate.dialect.FirebirdDialect")
  ,HSQL("org.hsqldb.jdbcDriver", "org.hibernate.dialect.HSQLDialect")
  ,ORACLE10g_11g("oracle.jdbc.driver.OracleDriver", "org.hibernate.dialect.Oracle10gDialect")
  ,ORACLE8i("oracle.jdbc.driver.OracleDriver", "org.hibernate.dialect.Oracle8iDialect")
  ,ORACLE9("oracle.jdbc.driver.OracleDriver", "org.hibernate.dialect.Oracle9Dialect")
  ,ORACLE9i("oracle.jdbc.driver.OracleDriver", "org.hibernate.dialect.Oracle9iDialect")
  ,ORACLE("oracle.jdbc.driver.OracleDriver", "org.hibernate.dialect.OracleDialect")
  ,SQLSERVER2005("com.microsoft.sqlserver.jdbc.SQLServerDriver", "org.hibernate.dialect.SQLServer2005Dialect")
  ,SQLSERVER2008("com.microsoft.sqlserver.jdbc.SQLServerDriver", "org.hibernate.dialect.SQLServer2008Dialect")
  ,SQLSERVER2012("com.microsoft.sqlserver.jdbc.SQLServerDriver", "org.hibernate.dialect.SQLServer2012Dialect")
  ,SQLSERVER("com.microsoft.sqlserver.jdbc.SQLServerDriver", "org.hibernate.dialect.SQLServerDialect")
  ,SYBASE11("com.sybase.jdbc2.jdbc.SybDriver", "org.hibernate.dialect.Sybase11Dialect")
  ,SYBASEANYWHERE("com.sybase.jdbc3.jdbc.SybDriver", "org.hibernate.dialect.SybaseAnywhereDialect")
  ,SYBASE("com.sybase.jdbc2.jdbc.SybDriver", "org.hibernate.dialect.SybaseDialect")
  ,TERADATA("com.teradata.jdbc.TeraDriver", "org.hibernate.dialect.TeradataDialect")
  ;

  private String driver, dialeto;
  private DriverDialeto(String driver, String dialeto) {
    this.driver = driver;
    this.dialeto = dialeto;
  }
  public String driver() { return this.driver; };
  public String dialeto() { return this.dialeto; };

}
