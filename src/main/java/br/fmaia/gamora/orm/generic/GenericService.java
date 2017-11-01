package br.fmaia.gamora.orm.generic;

public interface GenericService<
                   T extends GenericEntity<PK>,
                   PK extends java.io.Serializable,
                   REP extends GenericRepository<T, PK>
                               > {

  public java.util.List<T> listarTodos();
  public java.util.List<T> listarTodosComPaginacao(int pagina, int registros);

  public java.util.List<T> listarPorExemplo(T t);
  public java.util.List<T> listarPorExemploComPaginacao(T t, int pagina, int registros);

  public T buscarPorId(PK id);
  public long contarTodos();

  public T salvar(T t);
  public T remover(T t);

  public void setREP(REP rep);

}
