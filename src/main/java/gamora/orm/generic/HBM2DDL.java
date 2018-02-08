package gamora.orm.generic;

public enum HBM2DDL {
  VALIDAR("validate"), 
  ATUALIZAR("update"), 
  DROPAR_CRIAR("create"), 
  CRIAR_DROPAR("create-drop");
  private String valor;
  private HBM2DDL(String valor) { this.valor = valor; }
  public String valor() { return this.valor; }
}
