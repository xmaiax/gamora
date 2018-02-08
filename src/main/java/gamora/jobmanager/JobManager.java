package gamora.jobmanager;

@org.springframework.context.annotation.Configuration
@org.springframework.context.annotation.ComponentScan("gamora.jobmanager")
public class JobManager {

  private final String CONST_MENSAGEM_INICIALIZACAO = "*** Inicializando os Job's ***",
                       CONST_MENSAGEM_ERRO          = "Ocorreu um erro ao inicializar a aplica\u00e7\u00e3o: %s";

  private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());

  @org.springframework.context.annotation.Bean
  public org.springframework.scheduling.quartz.SchedulerFactoryBean schedulerFactoryBean() {
    this.logger.info(this.CONST_MENSAGEM_INICIALIZACAO);
    try {
      org.springframework.scheduling.quartz.SchedulerFactoryBean schedulerFactoryBean = new org.springframework.scheduling.quartz.SchedulerFactoryBean();
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
