package gamora.generic;

public abstract class GenericSpringConfig extends org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter implements org.springframework.web.WebApplicationInitializer {

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
  
  @Override
  public void addResourceHandlers(org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry registry) {
    super.addResourceHandlers(registry);
    registry.addResourceHandler("/resources/**")
            .addResourceLocations("/resources/");
  }

  @Override
  public void configureContentNegotiation(org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer configurer) {
    configurer.favorPathExtension(true).useJaf(false).ignoreAcceptHeader(true).mediaType("json", org.springframework.http.MediaType.ALL);
  }

  @org.springframework.context.annotation.Bean
  public org.springframework.web.servlet.ViewResolver restfulViewResolver() {
    return new org.springframework.web.servlet.view.BeanNameViewResolver();
  }
  
  @org.springframework.context.annotation.Bean
  public org.springframework.web.servlet.ViewResolver tiles3ViewResolver() {
    return new org.springframework.web.servlet.view.tiles3.TilesViewResolver();
  }
  
  @org.springframework.context.annotation.Bean
  public org.springframework.web.servlet.view.tiles3.TilesConfigurer tilesConfigurer(){
    org.springframework.web.servlet.view.tiles3.TilesConfigurer tilesConfigurer = new org.springframework.web.servlet.view.tiles3.TilesConfigurer();
    tilesConfigurer.setDefinitions(new String[] { "/WEB-INF/**/tiles.xml", "classpath:**/tiles.xml" });
    tilesConfigurer.setCheckRefresh(true);
    return tilesConfigurer;
  }
  
  @Override
  public void onStartup(javax.servlet.ServletContext servletContext) throws javax.servlet.ServletException {
    org.springframework.web.context.WebApplicationContext servletAppContext = new org.springframework.web.context.support.AnnotationConfigWebApplicationContext();
    ((org.springframework.web.context.support.AnnotationConfigWebApplicationContext) servletAppContext).register(this.getClass());
    org.springframework.web.servlet.DispatcherServlet dispatcherServlet = new org.springframework.web.servlet.DispatcherServlet(servletAppContext);
    javax.servlet.ServletRegistration.Dynamic registration = servletContext.addServlet("dispatcher", dispatcherServlet);
    registration.setLoadOnStartup(1);
    registration.addMapping("/");
    registration.setAsyncSupported(Boolean.TRUE);
  }

}
