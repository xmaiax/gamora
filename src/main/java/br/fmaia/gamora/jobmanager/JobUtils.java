package br.fmaia.gamora.jobmanager;

import org.springframework.scheduling.quartz.JobDetailBean;
import org.springframework.scheduling.quartz.SimpleTriggerBean;

@org.springframework.stereotype.Component
public final class JobUtils<J extends GenericJob> {

  private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());

  @org.springframework.beans.factory.annotation.Autowired
  protected org.springframework.context.ApplicationContext applicationContext;

  private String gerarNomeDoJob(J job, String sufixo) {
    return job == null ? null : job.getClass().getSimpleName().concat(sufixo == null ? "" : sufixo);
  }

  private JobDetailBean criarJobDetailBean(J job) {
    try {
      this.logger.debug(String.format("Criando o JobDetailBean '%s'...", this.gerarNomeDoJob(job, null)));
      JobDetailBean jobDetailBean = new JobDetailBean();
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

  private org.springframework.scheduling.quartz.SimpleTriggerBean criarTrigger(J job, JobDetailBean jobDetailBean) {
    try {
      this.logger.debug(String.format("Criando a Trigger '%s'...", this.gerarNomeDoJob(job, null)));
      org.springframework.scheduling.quartz.SimpleTriggerBean simpleTrigger = new SimpleTriggerBean();
      simpleTrigger.setJobDetail(jobDetailBean);
      simpleTrigger.setJobName(this.gerarNomeDoJob(job, null));
      simpleTrigger.setName(this.gerarNomeDoJob(job, "Trigger"));
      simpleTrigger.setStartTime(java.util.Calendar.getInstance().getTime());
      simpleTrigger.setRepeatInterval(job.intervaloDeExecucaoEmSegundos() * 1000);
      simpleTrigger.setPriority(job.prioridade().getIntValue());
      this.logger.debug(String.format("Trigger '%s' criada com sucesso!", this.gerarNomeDoJob(job, null)));
      return simpleTrigger;
    }
    catch(Exception e) {
      e.printStackTrace();
      this.logger.fatal(String.format("Ocorreu um erro ao criar a Trigger do job '%s': %s", jobDetailBean.getName(), e.getMessage()));
      System.exit(-1);
    }
    return null;
  }

  private void schedularJob(org.quartz.Scheduler scheduler, JobDetailBean jobDetailBean, org.quartz.Trigger trigger) throws org.quartz.SchedulerException {
    try {
      this.logger.debug(String.format("Schedulando Job '%s'...", jobDetailBean.getName()));
      scheduler.addJob(jobDetailBean, Boolean.TRUE);
      scheduler.scheduleJob(trigger);
      this.logger.debug(String.format("Job '%s' schedulado com sucesso!", jobDetailBean.getName()));
    }
    catch(Exception e) {
      e.printStackTrace();
      this.logger.fatal(String.format("Ocorreu um erro ao schedular o job '%s': %s", jobDetailBean.getName(), e.getMessage()));
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
          JobDetailBean jdb = this.criarJobDetailBean(job);
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
