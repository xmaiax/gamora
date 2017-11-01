package br.fmaia.gamora.orm.generic;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Table;

import org.apache.log4j.Logger;

public abstract class GenericRepository<T extends GenericEntity<PK>, PK extends Serializable> {

  protected Logger logger = Logger.getLogger(GenericRepository.class);

  protected Class<T> entityClass;

  @PersistenceContext
  protected EntityManager entityManager;

  @SuppressWarnings("unchecked")
  public GenericRepository() {
    ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
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

  public long contarTodos() {
    String sql = String.format(" SELECT COUNT(t) FROM %s t ", this.entityClass.getName());
    Query query = this.entityManager.createQuery(sql);
    return (Long) query.getSingleResult();
  }

  @SuppressWarnings("unchecked")
  public List<T> listarTodos() {
    String sql = String.format(" FROM %s ", this.entityClass.getName());
    Query query = this.entityManager.createQuery(sql);
    return query.getResultList();
  }

  public Query getResultadosComPaginacao(Query query, int pagina, int registrosPorPagina) {
    return query.setFirstResult((pagina - 1) * registrosPorPagina).setMaxResults(registrosPorPagina);
  }

  public PK inserir(T t) {
    this.entityManager.persist(t);
    return t.getPrimaryKey();
  }

  public T buscarPorId(PK id) {
    return this.entityManager.find(entityClass, id);
  }

  public void atualizar(T t) {
    this.entityManager.merge(t);
  }

  public void remover(T t) {
    t = this.entityManager.merge(t);
    this.entityManager.remove(t);
  }

}
