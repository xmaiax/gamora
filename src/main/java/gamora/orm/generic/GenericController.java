package gamora.orm.generic;

@SuppressWarnings("rawtypes")
public abstract class GenericController<T extends GenericEntity<PK>, PK extends java.io.Serializable, SRVC extends GenericService> {

  protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(GenericController.class);

  protected Class<T> entityClass;
  protected Class<PK> pkClass;

  @SuppressWarnings("unchecked")
  public GenericController() {
    java.lang.reflect.ParameterizedType genericSuperclass = (java.lang.reflect.ParameterizedType) this.getClass().getGenericSuperclass();
    this.entityClass = (Class<T>) genericSuperclass.getActualTypeArguments()[java.math.BigInteger.ZERO.intValue()];
    this.pkClass = (Class<PK>) genericSuperclass.getActualTypeArguments()[java.math.BigInteger.ONE.intValue()];
    this.logger.debug(String.format("Instanciando '%s'",this.getClass().getSimpleName()));
  }

  @org.springframework.beans.factory.annotation.Autowired
  protected SRVC service;

  public Object gerarChavePrimariaDaString(String string) {
    if(string == null || string.length() < 1) {
      final String mensagem = "A chave prim\u00e1ria n\u00e3o foi informada.";
      this.logger.error(mensagem);
      throw new IllegalAccessError(mensagem);
    }
    if(Byte.class.equals(this.pkClass)) { return Byte.parseByte(string); }
    else if(Short.class.equals(this.pkClass)) { return Short.parseShort(string); }
    else if(Integer.class.equals(this.pkClass)) { return Integer.parseInt(string); }
    else if(Long.class.equals(this.pkClass)) { return Long.parseLong(string); }
    else if(java.math.BigInteger.class.equals(this.pkClass)) { return java.math.BigInteger.valueOf(Long.parseLong(string)); }
    else if(Float.class.equals(this.pkClass)) { return Float.parseFloat(string); }
    else if(Double.class.equals(this.pkClass)) { return Double.parseDouble(string); }
    else if(java.math.BigDecimal.class.equals(this.pkClass)) { return java.math.BigDecimal.valueOf(Double.parseDouble(string)); }
    else if(String.class.equals(this.pkClass)) { return string; }
    else {
      final String mensagem = "N\u00e3o foi poss\u00edvel converter a chave prim\u00e1ria a partir de uma String, favor sobreescrever (@Override) o m\u00e9todo 'public Object gerarChavePrimariaDaString(String string)'.";
      this.logger.error(mensagem);
      throw new IllegalStateException(mensagem);
    }
  }

  @SuppressWarnings("unchecked")
  @org.springframework.web.bind.annotation.RequestMapping(value = "/buscar/{chavePrimaria}", method = org.springframework.web.bind.annotation.RequestMethod.GET, produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
  public @org.springframework.web.bind.annotation.ResponseBody String buscar(@org.springframework.web.bind.annotation.PathVariable(value = "chavePrimaria") String chavePrimaria) {
    return this.service.buscarPorId((java.io.Serializable) this.gerarChavePrimariaDaString(chavePrimaria)).toJson();
  }

  @org.springframework.web.bind.annotation.RequestMapping(value = "/listar/registros/{registros}/pagina/{pagina}", method = org.springframework.web.bind.annotation.RequestMethod.GET, produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
  public @org.springframework.web.bind.annotation.ResponseBody String listarPaginacao(@org.springframework.web.bind.annotation.PathVariable("pagina") Integer pagina, @org.springframework.web.bind.annotation.PathVariable("registros") Integer registros) {
    return gamora.utils.GSonUtils.getInstance().obj2Json(this.service.listarTodosComPaginacao(pagina, registros));
  }

}
