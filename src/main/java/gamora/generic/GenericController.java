package gamora.generic;

@SuppressWarnings("rawtypes")
public abstract class GenericController<T extends GenericEntity<PK>, PK extends java.io.Serializable, SRVC extends GenericService> {

  protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(GenericController.class);

  protected Class<T> entityClass;
  protected Class<PK> pkClass;

  @SuppressWarnings("unchecked")
  public GenericController() {
    java.lang.reflect.ParameterizedType genericSuperclass = (java.lang.reflect.ParameterizedType) this.getClass().getGenericSuperclass();
    this.entityClass = (Class<T>) genericSuperclass.getActualTypeArguments()[0];
    this.pkClass = (Class<PK>) genericSuperclass.getActualTypeArguments()[1];
    this.logger.debug(String.format("Instanciando '%s'", this.getClass().getSimpleName()));
  }

  @org.springframework.beans.factory.annotation.Autowired
  protected SRVC service;

  @SuppressWarnings("unchecked")
  @org.springframework.web.bind.annotation.RequestMapping(value = "/buscar/{chavePrimaria}", method = org.springframework.web.bind.annotation.RequestMethod.GET, produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
  public @org.springframework.web.bind.annotation.ResponseBody String buscar(@org.springframework.web.bind.annotation.PathVariable(value = "chavePrimaria") String chavePrimaria) {
    try {
      return this.service.buscarPorId((java.io.Serializable) this.gerarObjetoDaString(chavePrimaria, this.pkClass)).toJson();
    }
    catch(NullPointerException e) {
      return gamora.utils.GSonUtils.getInstance().obj2Json(null);
    }
  }

  @org.springframework.web.bind.annotation.RequestMapping(value = "/listar/{registros}/pagina/{pagina}", method = org.springframework.web.bind.annotation.RequestMethod.GET, produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
  public @org.springframework.web.bind.annotation.ResponseBody String listarPaginacao(@org.springframework.web.bind.annotation.PathVariable("pagina") Integer pagina, @org.springframework.web.bind.annotation.PathVariable("registros") Integer registros) {
    return gamora.utils.GSonUtils.getInstance().obj2Json(this.service.listarTodosComPaginacao(pagina, registros));
  }
  
  @org.springframework.web.bind.annotation.RequestMapping(value = "/listar/tudo", method = org.springframework.web.bind.annotation.RequestMethod.GET, produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
  public @org.springframework.web.bind.annotation.ResponseBody String listarTudo() {
    return gamora.utils.GSonUtils.getInstance().obj2Json(this.service.listarTodos());
  }
  
  @SuppressWarnings("unchecked")
  @org.springframework.web.bind.annotation.RequestMapping(value = "/salvar", method = org.springframework.web.bind.annotation.RequestMethod.POST, produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
  public @org.springframework.web.bind.annotation.ResponseBody String salvar(@org.springframework.web.bind.annotation.RequestParam java.util.Map<String,String> parametros) throws Exception {
    T t = this.gerarObjetoDoMap(parametros);
    java.util.Map<String, Object> saida = new java.util.HashMap<String, Object>();
    if(t.getPrimaryKey() == null) saida.put("metodo", "inserir");
    else saida.put("metodo", "atualizar");
    try {
      this.service.salvar(t);
      saida.put("status", "ok");
      saida.put("objeto", t);
    }
    catch(Exception e) {
      saida.put("status", "nok");
      saida.put("error", e.getMessage());
      if(t.getPrimaryKey() == null) saida.put("mensagem", String.format("N\u00e3o foi poss\u00edvel inserir o objeto %s", this.entityClass.getSimpleName()));
      else saida.put("mensagem", String.format("N\u00e3o foi poss\u00edvel atualizar o objeto %s", this.entityClass.getSimpleName()));
    }
    return gamora.utils.GSonUtils.getInstance().obj2Json(saida);
  }
  
  @SuppressWarnings("unchecked")
  @org.springframework.web.bind.annotation.RequestMapping(value = "/apagar/{chavePrimaria}", method = org.springframework.web.bind.annotation.RequestMethod.GET, produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
  public @org.springframework.web.bind.annotation.ResponseBody String apagar(@org.springframework.web.bind.annotation.PathVariable(value = "chavePrimaria") String chavePrimaria) throws Exception {
    java.util.Map<String, Object> saida = new java.util.HashMap<String, Object>();
    T t = this.entityClass.newInstance();
    for(java.lang.reflect.Field campo : this.entityClass.getDeclaredFields()) {
      if(campo.isAnnotationPresent(javax.persistence.Id.class)) {
        java.lang.reflect.Method metodo = this.entityClass.getMethod(this.getSetter(campo.getName()), this.pkClass);
        metodo.invoke(t, this.gerarObjetoDaString(chavePrimaria, this.pkClass));
        break;
      }
    }
    saida.put("metodo", "apagar");
    try {
      this.service.remover(t);
      saida.put("status", "ok");
      saida.put("objeto", t.getPrimaryKey());
    }
    catch(Exception e) {
      saida.put("status", "nok");
      saida.put("error", e.getMessage());
      saida.put("mensagem", String.format("N\u00e3o foi poss\u00edvel apagar o objeto %s", this.entityClass.getSimpleName()));
    }
    return gamora.utils.GSonUtils.getInstance().obj2Json(saida);
  }
  
  //----
  
  private T gerarObjetoDoMap(java.util.Map<String,String> map) {
    try {
      T t = this.entityClass.newInstance();
      for(java.util.Map.Entry<String, String> par : map.entrySet()) {
        String setter = this.getSetter(par.getKey());
        if(par.getValue().length() > 0) 
          for(java.lang.reflect.Method m : this.entityClass.getMethods()) {
            if(m.getName().equals(setter)) {
              Class tipoParametro = m.getParameterTypes()[0];
              m.invoke(t, this.gerarObjetoDaString(par.getValue(), tipoParametro));
              break;
            }
          }
      }
      return t;
    }
    catch(Exception e) {
      return null;
    }
  }
  
  private Object gerarObjetoDaString(String string, Class classe) {
    if(Byte.class.equals(classe)) { return Byte.parseByte(string); }
    else if(Short.class.equals(classe)) { return Short.parseShort(string); }
    else if(Integer.class.equals(classe)) { return Integer.parseInt(string); }
    else if(Long.class.equals(classe)) { return Long.parseLong(string); }
    else if(java.math.BigInteger.class.equals(classe)) { return java.math.BigInteger.valueOf(Long.parseLong(string)); }
    else if(Float.class.equals(classe)) { return Float.parseFloat(string); }
    else if(Double.class.equals(classe)) { return Double.parseDouble(string); }
    else if(java.math.BigDecimal.class.equals(classe)) { return java.math.BigDecimal.valueOf(Double.parseDouble(string)); }
    else if(String.class.equals(classe)) { return string; }
    return null;
  }
  
  private String getSetter(String nomeAtributo) {
    StringBuilder sb = new StringBuilder();
    sb.append("set");
    char[] array = nomeAtributo.toCharArray();
    array[0] = Character.toUpperCase(array[0]);
    sb.append(new String(array));
    return sb.toString();
  }
  
}
