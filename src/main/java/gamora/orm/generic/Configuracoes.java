package gamora.orm.generic;

public class Configuracoes {

  private String url;
  public String getUrl() { return this.url; }
  public void setUrl(String url) { this.url = url; }

  private String usuario;
  public String getUsuario() { return this.usuario; }
  public void setUsuario(String usuario) { this.usuario = usuario; }

  private String senha;
  public String getSenha() { return this.senha; }
  public void setSenha(String senha) { this.senha = senha; }

  private DriverDialeto driverDialeto;
  public DriverDialeto getDriverDialeto() { return this.driverDialeto; }
  public void setDriverDialeto(DriverDialeto driverDialeto) { this.driverDialeto = driverDialeto; }
  public String getDriver() { return this.driverDialeto.driver(); }
  public String getDialeto() { return this.driverDialeto.dialeto(); }

  private boolean exibirSQL;
  public boolean isExibirSQL() { return this.exibirSQL; }
  public void setExibirSQL(boolean exibirSQL) { this.exibirSQL = exibirSQL; }

  private boolean formatarSQL;
  public boolean isFormatarSQL() { return this.formatarSQL; }
  public void setFormatarSQL(boolean formatarSQL) { this.formatarSQL = formatarSQL; }

  private HBM2DDL hbm2ddl;
  public HBM2DDL getHbm2ddl() { return this.hbm2ddl; }
  public void setHbm2ddl(HBM2DDL hbm2ddl) { this.hbm2ddl = hbm2ddl; }

  private boolean otimizarReflections;
  public boolean isOtimizarReflections() { return this.otimizarReflections; }
  public void setOtimizarReflections(boolean otimizarReflections) { this.otimizarReflections = otimizarReflections; }

  public Configuracoes() {
    this.url = "jdbc:h2:./arquivos/bancoDados;CIPHER=AES;FILE_LOCK=SOCKET;";
    this.usuario = "admin";
    this.senha = "bc0259c5c27fea4a09afb897d581c970 cabd7e3571d4cccb57f130c6fa919a0a";
    this.driverDialeto = DriverDialeto.H2;
    this.exibirSQL = Boolean.FALSE;
    this.formatarSQL = Boolean.TRUE;
    this.hbm2ddl = HBM2DDL.ATUALIZAR;
    this.otimizarReflections = Boolean.TRUE;
  }

  public Configuracoes(
    String url,
    String usuario,
    String senha,
    DriverDialeto driverDialeto,
    boolean exibirSQL,
    boolean formatarSQL,
    HBM2DDL hbm2ddl,
    boolean otimizarReflections
  ) {
    this.url = url;
    this.usuario = usuario;
    this.senha = senha;
    this.driverDialeto = driverDialeto;
    this.exibirSQL = exibirSQL;
    this.formatarSQL = formatarSQL;
    this.hbm2ddl = hbm2ddl;
    this.otimizarReflections = otimizarReflections;
  }

  public Configuracoes(DriverDialeto driverDialeto, HBM2DDL hbm2ddl, String url, String usuario, String senha) {
    this.url = url;
    this.usuario = usuario;
    this.senha = senha;
    this.driverDialeto = driverDialeto;
    this.exibirSQL = Boolean.TRUE;
    this.formatarSQL = Boolean.TRUE;
    this.hbm2ddl = hbm2ddl;
    this.otimizarReflections = Boolean.TRUE;
  }

  public java.util.Properties getHibernateProperties() {
    java.util.Properties properties = new java.util.Properties();
    properties.put("hibernate.dialect", this.driverDialeto.dialeto());
    properties.put("hibernate.show_sql", this.exibirSQL ? "true" : "false");
    properties.put("hibernate.format_sql", this.formatarSQL ? "true" : "false");
    properties.put("hibernate.hbm2ddl.auto", this.hbm2ddl.valor());
    properties.put("hibernate.enable_lazy_load_no_trans", "true");
    properties.put("hibernate.cglib.use_reflection_optimizer", this.otimizarReflections ? "true" : "false");
    return properties;
  }

}
