package teste.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.fmaia.gamora.orm.generic.GenericEntity;

@Entity
@Table(name = "TAREFA", uniqueConstraints = { @UniqueConstraint(name = "UNQ_CNST_TITULO_TAREFA", columnNames= { "TITULO" }) })
public class Tarefa extends GenericEntity<Long> {

  @Id
  @GeneratedValue
  @Column(name = "ID_TAREFA")
  private Long idTarefa;
  public Long getIdTarefa() { return this.idTarefa; }
  public void setIdTarefa(Long idTarefa) { this.idTarefa = idTarefa; }

  @Column(name = "TITULO", length = 128, /*unique = true,*/ nullable = false)
  private String titulo;
  public String getTitulo() { return this.titulo; }
  public void setTitulo(String titulo) { this.titulo = titulo; }

  @Lob
  @Column(name = "DESCRICAO", length = 16384, nullable = false)
  private String descricao;
  public String getDescricao() { return this.descricao; }
  public void setDescricao(String descricao) { this.descricao = descricao; }

  @Column(name = "URGENTE", nullable = true)
  private Boolean urgente;
  public Boolean getUrgente() { return this.urgente; }
  public void setUrgente(Boolean urgente) { this.urgente = urgente; }

  @Column(name = "CRIADA_EM", insertable = false, updatable = false)
  private Date criadaEm;
  public Date getCriadaEm() { return criadaEm; }
  public void setCriadaEm(Date criadaEm) { this.criadaEm = criadaEm; }

  @PrePersist
  protected void aoCriar() {
    this.urgente = Boolean.FALSE;
    this.criadaEm = new Date();
  }

}
