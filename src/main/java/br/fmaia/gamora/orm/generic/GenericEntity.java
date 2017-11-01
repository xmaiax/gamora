package br.fmaia.gamora.orm.generic;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import br.fmaia.gamora.utils.GSonUtils;

public abstract class GenericEntity<PK extends Serializable> {

  private static final String    GET                       = "get";
  private static final Character QUEBRA_ATRIBUTO           = Character.MAX_VALUE;
  private static final Character SEPARADOR                 = ',';
  private static final Character MARCADOR_INICIO_ATRIBUTOS = '[';
  private static final Character MARCADOR_FIM_ATRIBUTOS    = ']';

  @SuppressWarnings("unchecked")
  public PK getPrimaryKey() {
    for (Field field : this.getClass().getDeclaredFields()) {
      Annotation[] annotations = field.getDeclaredAnnotations();
      for (Annotation annotation : annotations)
        if (annotation.annotationType().equals(javax.persistence.Id.class) ||
            annotation.annotationType().equals(javax.persistence.EmbeddedId.class))
          try {
            return (PK) this.getClass().getMethod(getGetter(field.getName())).invoke(this);
          }
          catch (Exception ignore) { }
    }
    return null;
  }

  private transient String TO_STRING_FIELDS;

  public GenericEntity() {
    super();
    StringBuilder sb = new StringBuilder();
    sb.append(this.getClass().getSimpleName());
    sb.append(" ".concat(MARCADOR_INICIO_ATRIBUTOS.toString()));
    Field[] atributos = this.getClass().getDeclaredFields();
    for (int i = 0; i < atributos.length; i++) {
      sb.append(atributos[i].getName());
      sb.append("=".concat(QUEBRA_ATRIBUTO.toString()));
      if (i != atributos.length - 1) {
        sb.append(SEPARADOR.toString().concat(" "));
      }
    }
    TO_STRING_FIELDS = sb.toString();
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
    List<Object> getters = new ArrayList<Object>();
    for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
      try {
        Method m = this.getClass().getMethod(getGetter(this.getClass().getDeclaredFields()[i].getName()));
        getters.add(m.invoke(this));
      }
      catch (Exception ignore) {
        getters.add(null);
      }
    }
    String[] pattern = TO_STRING_FIELDS.split(String.format("[%s]", QUEBRA_ATRIBUTO.toString()));
    int i = 0;
    StringBuilder sb = new StringBuilder();
    while (!getters.isEmpty()) {
      sb.append(pattern[i++]);
      sb.append(getters.remove(0));
    }
    sb.append(MARCADOR_FIM_ATRIBUTOS);
    return sb.toString();
  }

  public String toJson() {
    return GSonUtils.getInstance().obj2Json(this);
  }

}
