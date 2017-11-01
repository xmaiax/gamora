package teste.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import br.fmaia.gamora.jobmanager.JobManager;
import br.fmaia.gamora.orm.generic.Configuracoes;
import br.fmaia.gamora.orm.generic.GenericSpringConfig;

@Configuration
@EnableTransactionManagement
@ComponentScan("teste.*")
@EnableJpaRepositories("teste")
@Import(JobManager.class) // Opcional
public class TarefaSpringConfig extends GenericSpringConfig {

  @Override
  public Configuracoes configuracoes() {
    Configuracoes cfg = new Configuracoes();
    // (...)
    return cfg;
  }
 
}
