package gamora.orm.generic;

public abstract class GenericServiceImpl<
                        T extends GenericEntity<PK>,
                        PK extends java.io.Serializable,
                        REP extends GenericRepository<T, PK>
                                        >
    implements GenericService<T, PK, REP> {

  protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(GenericServiceImpl.class);

  @org.springframework.beans.factory.annotation.Autowired
  protected REP rep;

  protected Class<T> entityClass;

  @SuppressWarnings("unchecked")
  public GenericServiceImpl() {
    java.lang.reflect.ParameterizedType genericSuperclass = (java.lang.reflect.ParameterizedType) getClass().getGenericSuperclass();
    this.entityClass = (Class<T>) genericSuperclass.getActualTypeArguments()[java.math.BigInteger.ZERO.intValue()];
    this.logger.info(
        String.format("Instanciando '%s' (Tabela: %s)",
          this.getClass().getSimpleName(),
          this.entityClass.getAnnotation(javax.persistence.Table.class).schema() == null || this.entityClass.getAnnotation(javax.persistence.Table.class).schema().length() < java.math.BigInteger.ONE.intValue() ?
            this.entityClass.getAnnotation(javax.persistence.Table.class).name() :
            this.entityClass.getAnnotation(javax.persistence.Table.class).schema().concat(".").concat(this.entityClass.getAnnotation(javax.persistence.Table.class).name())
        )
      );
  }

  @Override
  @org.springframework.transaction.annotation.Transactional(readOnly = true)
  public java.util.List<T> listarTodos() { return this.rep.listarTodos(); }

  @Override
  @SuppressWarnings("unchecked")
  @org.springframework.transaction.annotation.Transactional(readOnly = true)
  public java.util.List<T> listarTodosComPaginacao(int pagina, int registros) {
    String sql = String.format("FROM %s", this.entityClass.getName());
    javax.persistence.Query query = this.rep.entityManager.createQuery(sql);
    return this.rep.getResultadosComPaginacao(query, pagina, registros).getResultList();
  }

  @Override
  @org.springframework.transaction.annotation.Transactional(readOnly = true)
  public long contarTodos() { return this.rep.contarTodos(); }

  @Override
  @org.springframework.transaction.annotation.Transactional(readOnly = false)
  public T salvar(T t) {
    if (t.getPrimaryKey() == null) this.rep.inserir(t);
    else this.rep.atualizar(t);
    return t;
  }

  @Override
  @org.springframework.transaction.annotation.Transactional(readOnly = true)
  public T buscarPorId(PK id) {
    if (id != null) return this.rep.buscarPorId(id);
    return null;
  }

  @Override
  @org.springframework.transaction.annotation.Transactional(readOnly = false)
  public T remover(T t) {
    if (t.getPrimaryKey() != null) {
      t = buscarPorId(t.getPrimaryKey());
      this.rep.remover(t);
      return t;
    }
    return null;
  }

  private StringBuilder incluirAND(StringBuilder sbWhere) {
    if(sbWhere.toString().length() > java.math.BigInteger.ONE.intValue()) sbWhere.append(" AND");
    return sbWhere;
  }

  private Object recuperarValorPorAtributo(T t, java.lang.reflect.Field atributo) {
    try {
      StringBuilder sb = new StringBuilder();
      sb.append("get");
      char[] array = atributo.getName().toCharArray();
      array[java.math.BigInteger.ZERO.intValue()] = Character.toUpperCase(array[java.math.BigInteger.ZERO.intValue()]);
      sb.append(new String(array));
      java.lang.reflect.Method m = t.getClass().getMethod(sb.toString());
      return m.invoke(t);
    }
    catch(Exception e) { return null; }
  }

  @Override
  @SuppressWarnings("unchecked")
  @org.springframework.transaction.annotation.Transactional(readOnly = true)
  public java.util.List<T> listarPorExemplo(T t) {
    if(t == null) return this.listarTodos();
    return this.montarQueryPorExemplo(t).getResultList();
  }

  @Override
  @SuppressWarnings("unchecked")
  @org.springframework.transaction.annotation.Transactional(readOnly = true)
  public java.util.List<T> listarPorExemploComPaginacao(T t, int pagina, int registros) {
    if(t == null) return this.listarTodosComPaginacao(pagina, registros);
    return this.rep.getResultadosComPaginacao(this.montarQueryPorExemplo(t), pagina, registros).getResultList();
  }

  private javax.persistence.Query montarQueryPorExemplo(T t) {
    java.lang.reflect.Field[] atributos = t.getClass().getDeclaredFields();
    java.util.Map<String, Object> parametros = new java.util.HashMap<String, Object>();
    java.util.List<String> listaParametros = new java.util.ArrayList<String>();
    StringBuilder sbWhere = new StringBuilder();
    final String prefixoQuery = ("FROM ").concat(this.entityClass.getName());
    final String packageEntidades = this.entityClass.toString().substring(java.math.BigInteger.ZERO.intValue(), this.entityClass.toString().lastIndexOf("."));
    for(java.lang.reflect.Field atributo : atributos) {
      Boolean isAtributoJPA = Boolean.FALSE;
      java.lang.annotation.Annotation[] anotacoes = atributo.getAnnotations();
      for(java.lang.annotation.Annotation anotacao : anotacoes) {
        if(anotacao.toString().startsWith("@javax.persistence")) {
          isAtributoJPA = Boolean.TRUE;
          break;
        }
      }
      if(isAtributoJPA) {
        // STRING - Inclui um LIKE e dá um LOWER CASE
        if(String.class.equals(atributo.getType())) {
          try {
            String valor = (String) this.recuperarValorPorAtributo(t, atributo);
            if(valor != null) {
              incluirAND(sbWhere);
              sbWhere.append(" LOWER(" + atributo.getName() + ") LIKE :" + atributo.getName());
              parametros.put(atributo.getName(), "%".concat(valor.toLowerCase()).concat("%"));
              listaParametros.add(atributo.getName());
            }
          }
          catch(Exception ignore) { }
        }
        // LONG, INTEGER, BOOLEAN, DOUBLE, FLOAT, NUMBER, SHORT, CHARACTER e outras entidades - Compara com '='
        if(Long.class.equals(atributo.getType()) ||
           Integer.class.equals(atributo.getType()) ||
           Boolean.class.equals(atributo.getType()) ||
           Double.class.equals(atributo.getType()) ||
           Float.class.equals(atributo.getType()) ||
           Number.class.equals(atributo.getType()) ||
           Short.class.equals(atributo.getType()) ||
           Character.class.equals(atributo.getType()) ||
           atributo.getType().toString().startsWith(packageEntidades)
            ) {
          try {
            Object valor = this.recuperarValorPorAtributo(t, atributo);
            if(valor != null) {
              incluirAND(sbWhere);
              sbWhere.append(String.format(" %s = :%s ", atributo.getName(), atributo.getName()));
              parametros.put(atributo.getName(), valor);
              listaParametros.add(atributo.getName());
            }
          }
          catch(Exception ignore) { }
        }

      }
    }
    javax.persistence.Query query = this.rep.entityManager.createQuery(
        sbWhere.toString().length() < java.math.BigInteger.ONE.intValue() ?
            prefixoQuery :
            prefixoQuery.concat(" WHERE ").concat(sbWhere.toString())
    );
    for(String parametro : listaParametros) {
      query.setParameter(parametro, parametros.get(parametro));
    }
    return query;
  }

}
