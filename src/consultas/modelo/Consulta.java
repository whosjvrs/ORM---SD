package consultas.modelo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Consulta agendada por um paciente em um horário.
 *
 * Consulta -> HorarioAtendimento é N:1 (e não 1:1): uma consulta cancelada fica
 * guardada como histórico e o horário pode receber outra consulta depois.
 * "No máximo uma consulta AGENDADA por horário" é garantido pela validação.
 */
@Entity
@Table(name = "consulta")
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "horario_id", nullable = false)
    private HorarioAtendimento horario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusConsulta status;

    @Column(nullable = false)
    private LocalDateTime dataRegistro;

    @Column(length = 500)
    private String observacoes;

    /** Composição: as notificações não existem sem a consulta e são apagadas com ela. */
    @OneToMany(mappedBy = "consulta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notificacao> notificacoes = new ArrayList<>();

    protected Consulta() {
    }

    public Consulta(Paciente paciente, HorarioAtendimento horario) {
        this.paciente = paciente;
        this.horario = horario;
        this.status = StatusConsulta.AGENDADA;
        this.dataRegistro = LocalDateTime.now();
        paciente.adicionarConsulta(this);
    }

    public Long getId() { return id; }
    public Paciente getPaciente() { return paciente; }
    public HorarioAtendimento getHorario() { return horario; }

    // Clínica e psicólogo vêm do horário, sem duplicar dados.
    public Clinica getClinica() { return horario.getClinica(); }
    public Psicologo getPsicologo() { return horario.getPsicologo(); }

    public StatusConsulta getStatus() { return status; }
    public void setStatus(StatusConsulta status) { this.status = status; }

    public LocalDateTime getDataRegistro() { return dataRegistro; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public boolean isAtiva() { return status == StatusConsulta.AGENDADA; }

    /** Regra 7: cancela a consulta e libera o horário. */
    public void cancelar() {
        if (status != StatusConsulta.AGENDADA) {
            throw new AgendamentoException("Só consultas AGENDADAS podem ser canceladas.");
        }
        status = StatusConsulta.CANCELADA;
        horario.liberar();
    }

    public List<Notificacao> getNotificacoes() { return new ArrayList<>(notificacoes); }

    /** Chamado pela Notificacao para manter os dois lados coerentes. */
    void adicionarNotificacao(Notificacao notificacao) {
        if (!notificacoes.contains(notificacao)) {
            notificacoes.add(notificacao);
        }
    }

    public void removerNotificacao(Notificacao notificacao) {
        notificacoes.remove(notificacao);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Consulta outra)) return false;
        return id != null && id.equals(outra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(Consulta.class);
    }

    @Override
    public String toString() {
        return "Consulta " + id + " | " + status + " | Paciente: " + paciente.getNome() + " | " + horario;
    }
}
