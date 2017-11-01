package br.fmaia.gamora.orm.generic;

public enum DriverDialeto {

  H2("org.h2.Driver", "org.hibernate.dialect.H2Dialect")
  ;
  
  private String driver, dialeto;
  private DriverDialeto(String driver, String dialeto) { 
    this.driver = driver; 
    this.dialeto = dialeto; 
  }
  public String driver() { return this.driver; };
  public String dialeto() { return this.dialeto; };
  
}
