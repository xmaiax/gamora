package gamora.generic;

public abstract class GenericSpringConfig {

  private static final String CONST_NOME_PADRAO_PU = "gamoraPersistenceUnit";
  
  public abstract Configuracoes configuracoes();

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
    factory.setPackagesToScan(this.getClass().getAnnotation(org.springframework.data.jpa.repository.config.EnableJpaRepositories.class).value()[0]);
    factory.setJpaVendorAdapter(new org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter());
    factory.setJpaProperties(this.configuracoes().getHibernateProperties());
    factory.setPersistenceUnitName(CONST_NOME_PADRAO_PU);
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
