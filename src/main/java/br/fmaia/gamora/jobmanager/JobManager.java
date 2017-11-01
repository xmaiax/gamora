package br.fmaia.gamora.jobmanager;

import org.apache.log4j.Logger;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@org.springframework.context.annotation.Configuration
@org.springframework.context.annotation.ComponentScan("br.fmaia.gamora.jobmanager") //FIXME Kill me, please
public class JobManager {

  private final String CONST_MENSAGEM_INICIALIZACAO = "*** Inicializando os JOB's ***",
                       CONST_MENSAGEM_ERRO          = "Ocorreu um erro ao inicializar a aplica\u00e7\u00e3o: %s";

  private final Logger logger = Logger.getLogger(this.getClass());

  @org.springframework.context.annotation.Bean
  public SchedulerFactoryBean schedulerFactoryBean() {
    logger.info(this.CONST_MENSAGEM_INICIALIZACAO);
    try {
      SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
      return schedulerFactoryBean;
    }
    catch(Exception e) {
      e.printStackTrace();
      this.logger.fatal(String.format(this.CONST_MENSAGEM_ERRO, e.getMessage()));
      System.exit(-1);
    }
    return null;
  }

}
