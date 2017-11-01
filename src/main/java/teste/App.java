package teste;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import teste.config.TarefaSpringConfig;
import teste.model.Tarefa;
import teste.service.TarefaService;

public class App {

  @SuppressWarnings("resource")
  public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(TarefaSpringConfig.class);

    TarefaService service = ctx.getBean(TarefaService.class);
    Tarefa tarefa = new Tarefa();
    tarefa.setTitulo("Teste 01");
    tarefa.setDescricao("teste");
    service.salvar(tarefa);

    System.out.println(tarefa);
  }

}
