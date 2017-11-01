package teste.dao;

import org.springframework.stereotype.Repository;

import br.fmaia.gamora.orm.generic.GenericRepository;
import teste.model.Tarefa;

@Repository
public final class TarefaDAO extends GenericRepository<Tarefa, Long> { }
