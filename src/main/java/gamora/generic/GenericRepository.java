package gamora.generic;

public abstract class GenericRepository<T extends GenericEntity<PK>, PK extends java.io.Serializable> {

  protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(GenericRepository.class);

  protected Class<T> entityClass;

  @javax.persistence.PersistenceContext
  protected javax.persistence.EntityManager entityManager;

  @SuppressWarnings("unchecked")
  public GenericRepository() {
    java.lang.reflect.ParameterizedType genericSuperclass = (java.lang.reflect.ParameterizedType) this.getClass().getGenericSuperclass();
    this.entityClass = (Class<T>) genericSuperclass.getActualTypeArguments()[0];
    this.logger.info(
      String.format("Instanciando '%s' (Tabela: %s)",
        this.getClass().getSimpleName(),
        this.entityClass.getAnnotation(javax.persistence.Table.class).schema() == null || this.entityClass.getAnnotation(javax.persistence.Table.class).schema().length() < 1 ?
          this.entityClass.getAnnotation(javax.persistence.Table.class).name() :
          this.entityClass.getAnnotation(javax.persistence.Table.class).schema().concat(".").concat(this.entityClass.getAnnotation(javax.persistence.Table.class).name())
      )
    );
  }
  public long contarTodos() { String sql = String.format(" SELECT COUNT(t) FROM %s t ", this.entityClass.getName()); javax.persistence.Query query = this.entityManager.createQuery(sql); return (Long) query.getSingleResult(); }
  @SuppressWarnings("unchecked") public java.util.List<T> listarTodos() { String sql = String.format(" FROM %s ", this.entityClass.getName()); javax.persistence.Query query = this.entityManager.createQuery(sql); return query.getResultList(); }
  public javax.persistence.Query getResultadosComPaginacao(javax.persistence.Query query, int pagina, int registrosPorPagina) { return query.setFirstResult((pagina - 1) * registrosPorPagina).setMaxResults(registrosPorPagina); }
  public PK inserir(T t) { this.entityManager.persist(t); return t.getPrimaryKey(); }
  public T buscarPorId(PK id) { return this.entityManager.find(this.entityClass, id); }
  public void atualizar(T t) { this.entityManager.merge(t); }
  public void remover(T t) { t = this.entityManager.merge(t); this.entityManager.remove(t); }

}
