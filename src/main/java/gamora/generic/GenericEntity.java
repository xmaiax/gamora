package gamora.generic;

public abstract class GenericEntity<PK extends java.io.Serializable> {

  private static final String    GET                       = "get";
  private static final Character QUEBRA_ATRIBUTO           = Character.MAX_VALUE;
  private static final Character SEPARADOR                 = ',';
  private static final Character MARCADOR_INICIO_ATRIBUTOS = '[';
  private static final Character MARCADOR_FIM_ATRIBUTOS    = ']';

  private transient String TO_STRING_FIELDS;
  public GenericEntity() {
    super();
    StringBuilder sb = new StringBuilder();
    sb.append(this.getClass().getSimpleName());
    sb.append(" ".concat(MARCADOR_INICIO_ATRIBUTOS.toString()));
    java.lang.reflect.Field[] atributos = this.getClass().getDeclaredFields();
    for(int i = 0; i < atributos.length; i++) {
      sb.append(atributos[i].getName());
      sb.append("=".concat(QUEBRA_ATRIBUTO.toString()));
      if(i != atributos.length - 1) sb.append(SEPARADOR.toString().concat(" "));
    }
    this.TO_STRING_FIELDS = sb.toString();
  }
  
  @SuppressWarnings("unchecked")
  public PK getPrimaryKey() {
    for (java.lang.reflect.Field field : this.getClass().getDeclaredFields()) {
      java.lang.annotation.Annotation[] annotations = field.getDeclaredAnnotations();
      for (java.lang.annotation.Annotation annotation : annotations)
        if (javax.persistence.Id.class.equals(annotation.annotationType()) ||
            javax.persistence.EmbeddedId.class.equals(annotation.annotationType()))
          try { return (PK) this.getClass().getMethod(getGetter(field.getName())).invoke(this); }
          catch (Exception ignore) { }
    }
    return null;
  }

  private String getGetter(String nomeAtributo) {
    StringBuilder sb = new StringBuilder();
    sb.append(GET);
    char[] array = nomeAtributo.toCharArray();
    array[0] = Character.toUpperCase(array[0]);
    sb.append(new String(array));
    return sb.toString();
  }

  @Override
  public String toString() {
    java.util.List<Object> getters = new java.util.ArrayList<Object>();
    for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
      try { getters.add(this.getClass().getMethod(getGetter(this.getClass().getDeclaredFields()[i].getName())).invoke(this)); }
      catch (Exception ignore) { getters.add(null); }
    }
    String[] pattern = this.TO_STRING_FIELDS.split(String.format("[%s]", QUEBRA_ATRIBUTO.toString()));
    int i = 0;
    StringBuilder sb = new StringBuilder();
    while (!getters.isEmpty()) sb.append(pattern[i++]).append(getters.remove(0));
    sb.append(MARCADOR_FIM_ATRIBUTOS);
    return sb.toString();
  }

  public String toJson() { return gamora.utils.GSonUtils.getInstance().obj2Json(this); }

}
