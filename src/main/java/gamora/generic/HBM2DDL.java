package gamora.generic;

public enum HBM2DDL {
  NADA("none"),
  VALIDAR("validate"), 
  ATUALIZAR("update"), 
  DROPAR_CRIAR("create"), 
  CRIAR_DROPAR("create-drop");
  private String valor;
  private HBM2DDL(String valor) { this.valor = valor; }
  public String valor() { return this.valor; }
}
