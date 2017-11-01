package teste.job;

import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.fmaia.gamora.jobmanager.GenericJob;
import teste.model.Tarefa;
import teste.service.TarefaService;

@Component
public class TarefaJob extends GenericJob {

  @Autowired
  private TarefaService service;
  
  @Override
  protected void executar() throws JobExecutionException {
    Tarefa tarefa = new Tarefa();
    tarefa.setTitulo(String.format("Teste %02d inserida manualmente", this.service.contarTodos() + 1));
    tarefa.setDescricao("huehuehue");
    this.service.salvar(tarefa);
    this.logger.info(tarefa.toJson());
  }

  @Override
  protected int intervaloDeExecucaoEmSegundos() {
    return 2;
  }

}
