package br.fmaia.gamora.orm.generic;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.persistence.Table;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public abstract class GenericServiceImpl<
                        T extends GenericEntity<PK>,
                        PK extends Serializable,
                        REP extends GenericRepository<T, PK>
                                        >
    implements GenericService<T, PK, REP> {

  protected Logger logger = Logger.getLogger(GenericServiceImpl.class);

  protected REP rep;
  protected Class<T> entityClass;

  @SuppressWarnings("unchecked")
  public GenericServiceImpl() {
    ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
    this.entityClass = (Class<T>) genericSuperclass.getActualTypeArguments()[0];
    logger.info(
        String.format("Instanciando '%s' (Tabela: %s)",
          this.getClass().getSimpleName(),
          this.entityClass.getAnnotation(Table.class).schema() == null || this.entityClass.getAnnotation(Table.class).schema().length() < 1 ?
            this.entityClass.getAnnotation(Table.class).name() :
            this.entityClass.getAnnotation(Table.class).schema().concat(".").concat(this.entityClass.getAnnotation(Table.class).name())
        )
      );
  }

  @Override
  @Transactional(readOnly = true)
  public List<T> listarTodos() {
    return this.rep.listarTodos();
  }

  @Override
  @SuppressWarnings("unchecked")
  @Transactional(readOnly = true)
  public List<T> listarTodosComPaginacao(int pagina, int registros) {
    String sql = String.format("FROM %s", this.entityClass.getName());
    Query query = this.rep.entityManager.createQuery(sql);
    return this.rep.getResultadosComPaginacao(query, pagina, registros).getResultList();
  }

  @Override
  @Transactional(readOnly = true)
  public long contarTodos() {
    return this.rep.contarTodos();
  }

  @Override
  @Transactional(readOnly = false)
  public T salvar(T t) {
    if (t.getPrimaryKey() == null) this.rep.inserir(t);
    else this.rep.atualizar(t);
    return t;
  }

  @Override
  @Transactional(readOnly = true)
  public T buscarPorId(PK id) {
    if (id != null) return this.rep.buscarPorId(id);
    return null;
  }

  @Override
  @Transactional(readOnly = false)
  public T remover(T t) {
    if (t.getPrimaryKey() != null) {
      t = buscarPorId(t.getPrimaryKey());
      this.rep.remover(t);
      return t;
    }
    return null;
  }

  private StringBuilder incluirAND(StringBuilder sbWhere) {
    if(sbWhere.toString().length() > 1) sbWhere.append(" AND");
    return sbWhere;
  }

  private Object recuperarValorPorAtributo(T t, Field atributo) {
    try {
      StringBuilder sb = new StringBuilder();
      sb.append("get");
      char[] array = atributo.getName().toCharArray();
      array[0] = Character.toUpperCase(array[0]);
      sb.append(new String(array));
      Method m = t.getClass().getMethod(sb.toString());
      return m.invoke(t);
    }
    catch(Exception e) { return null; }
  }

  @Override
  @SuppressWarnings("unchecked")
  @Transactional(readOnly = true)
  public List<T> listarPorExemplo(T t) {
    if(t == null) return this.listarTodos();
    return this.montarQueryPorExemplo(t).getResultList();
  }

  @Override
  @SuppressWarnings("unchecked")
  @Transactional(readOnly = true)
  public List<T> listarPorExemploComPaginacao(T t, int pagina, int registros) {
    if(t == null) return this.listarTodosComPaginacao(pagina, registros);
    return this.rep.getResultadosComPaginacao(this.montarQueryPorExemplo(t), pagina, registros).getResultList();
  }

  private Query montarQueryPorExemplo(T t) {
    Field[] atributos = t.getClass().getDeclaredFields();
    Map<String, Object> parametros = new HashMap<String, Object>();
    List<String> listaParametros = new ArrayList<String>();
    StringBuilder sbWhere = new StringBuilder();
    final String prefixoQuery = ("FROM ").concat(this.entityClass.getName());
    final String packageEntidades = this.entityClass.toString().substring(0, this.entityClass.toString().lastIndexOf("."));
    for(Field atributo : atributos) {
      Boolean ehAtributoJPA = Boolean.FALSE;
      Annotation[] anotacoes = atributo.getAnnotations();
      for(Annotation anotacao : anotacoes) {
        if(anotacao.toString().startsWith("@javax.persistence")) {
          ehAtributoJPA = Boolean.TRUE;
          break;
        }
      }
      if(ehAtributoJPA) {
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
    Query query = this.rep.entityManager.createQuery(
        sbWhere.toString().length() < 1 ?
            prefixoQuery :
            prefixoQuery.concat(" WHERE ").concat(sbWhere.toString())
    );
    for(String parametro : listaParametros) {
      query.setParameter(parametro, parametros.get(parametro));
    }
    return query;
  }

  @Override @Autowired public void setREP(REP rep) { this.rep = rep; }

}
