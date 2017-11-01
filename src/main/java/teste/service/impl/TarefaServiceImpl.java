package teste.service.impl;

import org.springframework.stereotype.Service;

import br.fmaia.gamora.orm.generic.GenericServiceImpl;
import teste.dao.TarefaDAO;
import teste.model.Tarefa;
import teste.service.TarefaService;

@Service
public final class TarefaServiceImpl extends GenericServiceImpl<Tarefa, Long, TarefaDAO> implements TarefaService {

}
