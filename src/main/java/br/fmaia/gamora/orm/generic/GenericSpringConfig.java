package br.fmaia.gamora.orm.generic;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

public abstract class GenericSpringConfig {

  public abstract Configuracoes configuracoes();
  
  private String gerarNomeComSufixo(String sufixo) {
    final String nomeClasse = this.getClass().getSimpleName();
    StringBuilder sb = new StringBuilder(nomeClasse.substring(0, nomeClasse.indexOf("SpringConfig")).toLowerCase());
    sb.append(sufixo);
    return sb.toString();
  }

  @Bean
  public DataSource dataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(this.configuracoes().getDriver());
    dataSource.setUrl(this.configuracoes().getUrl());
    dataSource.setUsername(this.configuracoes().getUsuario());
    dataSource.setPassword(this.configuracoes().getSenha());
    return dataSource;
  }

  @Bean
  public EntityManagerFactory entityManagerFactory() {
    LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
    factory.setDataSource(this.dataSource());
    factory.setPackagesToScan(this.getClass().getAnnotation(EnableJpaRepositories.class).value()[0]);
    factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
    factory.setJpaProperties(this.configuracoes().getHibernateProperties());
    factory.setPersistenceUnitName(this.gerarNomeComSufixo("PersistenceUnit"));
    factory.setPersistenceProviderClass(HibernatePersistenceProvider.class);
    factory.afterPropertiesSet();
    return factory.getObject();
  }

  @Bean
  public JpaTransactionManager transactionManager() {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(this.entityManagerFactory());
    return transactionManager;
  }

}


