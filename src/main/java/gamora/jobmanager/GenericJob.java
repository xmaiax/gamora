package gamora.jobmanager;

public abstract class GenericJob extends org.springframework.scheduling.quartz.QuartzJobBean {

  protected org.apache.log4j.Logger logger;
  public org.apache.log4j.Logger getLogger() { return this.logger; }

  private String idExecucao;
  public String getIdExecucao() { return this.idExecucao; }

  private java.util.Calendar inicioExecucao;
  public java.util.Date getInicioExecucao() { return this.inicioExecucao != null ? this.inicioExecucao.getTime() : null; }

  public GenericJob() {
    super();
    this.logger = org.apache.log4j.Logger.getLogger(this.getClass());
    this.inicioExecucao = java.util.Calendar.getInstance();
    this.idExecucao = gamora.utils.SecurityUtils.getInstance().string2MD5(
      String.valueOf(this.inicioExecucao.getTimeInMillis())
    );
  }

  protected abstract void executar() throws org.quartz.JobExecutionException;
  protected abstract String expressaoCronFrequenciaExecucao();
  public gamora.jobmanager.Prioridade prioridade() { return gamora.jobmanager.Prioridade.NORMAL; }

  protected void executeInternal(org.quartz.JobExecutionContext context) throws org.quartz.JobExecutionException {

    try {
      this.logger.debug(
        String.format(
          "In\u00edcio da execu\u00e7\u00e3o do Job '%s' (%s)",
          this.getClass().getSimpleName(),
          this.idExecucao)
      );

      org.springframework.context.ApplicationContext applicationContext = (org.springframework.context.ApplicationContext) context.getJobDetail().getJobDataMap().get("applicationContext");
      org.springframework.beans.factory.config.AutowireCapableBeanFactory factory = applicationContext.getAutowireCapableBeanFactory();
      factory.autowireBean(this);

      this.executar();
      double tempoExecucao = (java.util.Calendar.getInstance().getTimeInMillis() - this.inicioExecucao.getTimeInMillis()) / 1000.0f;
      this.logger.debug(
        String.format(
          "Fim da execu\u00e7\u00e3o do Job '%s' (%s) em %,.3f segundos.",
          this.getClass().getSimpleName(),
          this.idExecucao,
          tempoExecucao
        )
      );
    }
    catch(Exception e) {
      this.logger.error(
        String.format(
          "Ocorreu um erro ao executar o Job '%s' (%s): %s",
          this.getClass().getSimpleName(),
          this.idExecucao,
          e.getMessage()
        )
      );
    }

  }

}
