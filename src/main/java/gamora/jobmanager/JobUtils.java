package gamora.jobmanager;

@org.springframework.stereotype.Component
public final class JobUtils<J extends GenericJob> {

  private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());

  @org.springframework.beans.factory.annotation.Autowired
  protected org.springframework.context.ApplicationContext applicationContext;

  private String gerarNomeDoJob(J job, String sufixo) {
    return job == null ? null : job.getClass().getSimpleName().concat(sufixo == null ? "" : sufixo);
  }

  private org.springframework.scheduling.quartz.JobDetailBean criarJobDetailBean(J job) {
    try {
      this.logger.debug(String.format("Criando o JobDetailBean '%s'...", this.gerarNomeDoJob(job, null)));
      org.springframework.scheduling.quartz.JobDetailBean jobDetailBean = new org.springframework.scheduling.quartz.JobDetailBean();
      jobDetailBean.setJobClass(job.getClass());
      jobDetailBean.setName(this.gerarNomeDoJob(job, null));
      java.util.Map<String, Object> map = new java.util.HashMap<String, Object>();
      map.put("applicationContext", this.applicationContext);
      jobDetailBean.setJobDataAsMap(map);
      this.logger.debug(String.format("JobDetailBean '%s' criado com sucesso!", this.gerarNomeDoJob(job, null)));
      return jobDetailBean;
    }
    catch(Exception e) {
      e.printStackTrace();
      this.logger.fatal(String.format("Ocorreu um erro ao criar o Job Detail do '%s': %s", this.gerarNomeDoJob(job, null), e.getMessage()));
      System.exit(-1);
    }
    return null;
  }

  private org.springframework.scheduling.quartz.CronTriggerBean criarTrigger(J job, org.springframework.scheduling.quartz.JobDetailBean jobDetailBean) {
    try {
      this.logger.debug(String.format("Criando a Trigger '%s'...", this.gerarNomeDoJob(job, null)));
      org.springframework.scheduling.quartz.CronTriggerBean trigger = new org.springframework.scheduling.quartz.CronTriggerBean();
      trigger.setJobDetail(jobDetailBean);
      trigger.setJobName(this.gerarNomeDoJob(job, null));
      trigger.setName(this.gerarNomeDoJob(job, "Trigger"));
      trigger.setPriority(job.prioridade().getIntValue());
      trigger.setStartTime(java.util.Calendar.getInstance().getTime());
      trigger.setCronExpression(job.expressaoCronFrequenciaExecucao());
      this.logger.debug(String.format("Trigger '%s' criada com sucesso!", this.gerarNomeDoJob(job, null)));
      return trigger;
    }
    catch(Exception e) {
      e.printStackTrace();
      this.logger.fatal(String.format("Ocorreu um erro ao criar a Trigger do job '%s': %s", jobDetailBean.getName(), e.getMessage()));
      System.exit(-1);
    }

    return null;
  }

  private void schedularJob(org.quartz.Scheduler scheduler, org.springframework.scheduling.quartz.JobDetailBean jobDetailBean, org.quartz.Trigger trigger) throws org.quartz.SchedulerException {
    try {
      this.logger.debug(String.format("Agendando Job '%s'...", jobDetailBean.getName()));
      scheduler.addJob(jobDetailBean, Boolean.TRUE);
      scheduler.scheduleJob(trigger);
      this.logger.debug(String.format("Job '%s' schedulado com sucesso!", jobDetailBean.getName()));
    }
    catch(Exception e) {
      e.printStackTrace();
      this.logger.fatal(String.format("Ocorreu um erro ao agendar o job '%s': %s", jobDetailBean.getName(), e.getMessage()));
      System.exit(-1);
    }
  }

  @javax.annotation.PostConstruct
  public void configurarJobs() {
    org.quartz.Scheduler scheduler = this.applicationContext.getBean(org.springframework.scheduling.quartz.SchedulerFactoryBean.class).getObject();
    for(String beanName : this.applicationContext.getBeanDefinitionNames()) {
      Object bean = this.applicationContext.getBean(beanName);
      if(bean.getClass().getSuperclass().equals(GenericJob.class)) {
        try {
          @SuppressWarnings("unchecked")
          J job = (J) bean;
          this.logger.debug(String.format("Job '%s' encontrado, configurando...", this.gerarNomeDoJob(job, null)));
          org.springframework.scheduling.quartz.JobDetailBean jdb = this.criarJobDetailBean(job);
          this.schedularJob(scheduler, jdb, this.criarTrigger(job, jdb));
        }
        catch (org.quartz.SchedulerException e) {
          this.logger.fatal(e.getMessage());
          System.exit(-1);
        }
      }
    }
    this.logger.debug("Todos os Job's foram configurados com sucesso. Iniciando aplica\u00e7\u00e3o...");
  }

}
