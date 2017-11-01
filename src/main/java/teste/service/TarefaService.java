package teste.service;

import br.fmaia.gamora.orm.generic.GenericService;
import teste.dao.TarefaDAO;
import teste.model.Tarefa;

public interface TarefaService extends GenericService<Tarefa, Long, TarefaDAO> { }
