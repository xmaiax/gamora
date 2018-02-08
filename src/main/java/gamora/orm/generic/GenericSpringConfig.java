package gamora.orm.generic;

public abstract class GenericSpringConfig {

  public abstract Configuracoes configuracoes();

  private String gerarNomeComSufixo(String sufixo) {
    final String nomeClasse = this.getClass().getSimpleName();
    StringBuilder sb = new StringBuilder(nomeClasse.substring(java.math.BigInteger.ZERO.intValue(), nomeClasse.indexOf("SpringConfig")).toLowerCase());
    sb.append(sufixo);
    return sb.toString();
  }

  @org.springframework.context.annotation.Bean
  public javax.sql.DataSource dataSource() {
    org.springframework.jdbc.datasource.DriverManagerDataSource dataSource = new org.springframework.jdbc.datasource.DriverManagerDataSource();
    dataSource.setDriverClassName(this.configuracoes().getDriver());
    dataSource.setUrl(this.configuracoes().getUrl());
    dataSource.setUsername(this.configuracoes().getUsuario());
    dataSource.setPassword(this.configuracoes().getSenha());
    return dataSource;
  }

  @org.springframework.context.annotation.Bean
  public javax.persistence.EntityManagerFactory entityManagerFactory() {
    org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean factory = new org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean();
    factory.setDataSource(this.dataSource());
    factory.setPackagesToScan(this.getClass().getAnnotation(org.springframework.data.jpa.repository.config.EnableJpaRepositories.class).value()[java.math.BigInteger.ZERO.intValue()]);
    factory.setJpaVendorAdapter(new org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter());
    factory.setJpaProperties(this.configuracoes().getHibernateProperties());
    factory.setPersistenceUnitName(this.gerarNomeComSufixo("PersistenceUnit"));
    factory.setPersistenceProviderClass(org.hibernate.jpa.HibernatePersistenceProvider.class);
    factory.afterPropertiesSet();
    return factory.getObject();
  }

  @org.springframework.context.annotation.Bean
  public org.springframework.orm.jpa.JpaTransactionManager transactionManager() {
    org.springframework.orm.jpa.JpaTransactionManager transactionManager = new org.springframework.orm.jpa.JpaTransactionManager();
    transactionManager.setEntityManagerFactory(this.entityManagerFactory());
    return transactionManager;
  }

}
