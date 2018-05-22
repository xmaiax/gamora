package gamora.generic;

public abstract class GenericEntity<PK extends java.io.Serializable> {

  private static final Character QUEBRA_ATRIBUTO           = Character.MAX_VALUE;

  private static transient gamora.json.PropriedadeAutoForm autoForm;
  private static transient String TO_STRING_FIELDS;
  
  public GenericEntity() {
    super();
    if(TO_STRING_FIELDS == null || TO_STRING_FIELDS.length() < 1) {
      StringBuilder sb = new StringBuilder();
      sb.append(this.getClass().getSimpleName());
      sb.append(" [");
      java.lang.reflect.Field[] atributos = this.getClass().getDeclaredFields();
      for(int i = 0; i < atributos.length; i++) {
        sb.append(atributos[i].getName());
        sb.append("=".concat(QUEBRA_ATRIBUTO.toString()));
        if(i != atributos.length - 1) sb.append(", ");
      }
      TO_STRING_FIELDS = sb.toString();
    }
    if(autoForm == null) {
      autoForm = new gamora.json.PropriedadeAutoForm();
      autoForm.setType("object");
      autoForm.setTitle(this.getClass().getSimpleName());
      autoForm.setProperties(new java.util.HashMap<String, gamora.json.PropriedadeAutoForm>());
      for(java.lang.reflect.Field campo : this.getClass().getDeclaredFields()) {
        if(campo.isAnnotationPresent(javax.persistence.Column.class)) {
          gamora.json.PropriedadeAutoForm propriedade = new gamora.json.PropriedadeAutoForm(); 
          propriedade.setName(campo.getName());
          propriedade.setTitle(campo.getName().substring(0, 1).toUpperCase().concat(campo.getName().substring(1, campo.getName().length())));
          propriedade.setType(
            String.class.equals(campo.getType())  ? "string" : 
            Integer.class.equals(campo.getType()) ? "integer" :
            Long.class.equals(campo.getType())    ? "number" :
            Boolean.class.equals(campo.getType()) ? "boolean" :
            "object"
          );
          autoForm.getProperties().put(campo.getName(), propriedade);
        }
      }
    }
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
    sb.append("get");
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
    String[] pattern = TO_STRING_FIELDS.split(String.format("[%s]", QUEBRA_ATRIBUTO.toString()));
    int i = 0;
    StringBuilder sb = new StringBuilder();
    while (!getters.isEmpty()) sb.append(pattern[i++]).append(getters.remove(0));
    sb.append("]");
    return sb.toString();
  }
  public String getAutoForm() { return gamora.utils.GSonUtils.getInstance().obj2Json(autoForm); };
  public String toJson() { return gamora.utils.GSonUtils.getInstance().obj2Json(this); }

}
