package consultas.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Um horário oferecido por um psicólogo em uma clínica.
 * Um psicólogo não pode ter dois horários começando no mesmo instante.
 */
@Entity
@Table(name = "horario_atendimento",
        uniqueConstraints = @UniqueConstraint(columnNames = {"psicologo_id", "inicio"}))
public class HorarioAtendimento {

    public static final int DURACAO_MINUTOS = 50;
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** N:1 — vários horários na mesma clínica. Grava a coluna clinica_id. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "clinica_id", nullable = false)
    private Clinica clinica;

    /** N:1 — vários horários do mesmo psicólogo. Grava a coluna psicologo_id. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "psicologo_id", nullable = false)
    private Psicologo psicologo;

    @Column(nullable = false)
    private LocalDateTime inicio;

    @Column(nullable = false)
    private boolean reservado;

    protected HorarioAtendimento() {
    }

    public HorarioAtendimento(Clinica clinica, Psicologo psicologo, LocalDateTime inicio) {
        this.clinica = clinica;
        this.psicologo = psicologo;
        this.inicio = inicio;
        this.reservado = false;
        clinica.adicionarHorario(this);
        psicologo.adicionarHorario(this);
    }

    public Long getId() { return id; }
    public Clinica getClinica() { return clinica; }
    public Psicologo getPsicologo() { return psicologo; }

    public LocalDateTime getInicio() { return inicio; }

    /** Regra 8: horário reservado não pode ter a data alterada. */
    public void setInicio(LocalDateTime inicio) {
        if (reservado) {
            throw new AgendamentoException("Horário reservado não pode ser alterado.");
        }
        this.inicio = inicio;
    }

    public boolean isReservado() { return reservado; }

    /** Chamado pela Agenda ao agendar. */
    public void reservar() {
        if (reservado) {
            throw new AgendamentoException("Horário " + id + " já está reservado.");
        }
        this.reservado = true;
    }

    /** Chamado ao cancelar uma consulta: o horário volta a ficar disponível. */
    public void liberar() {
        this.reservado = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HorarioAtendimento outro)) return false;
        return id != null && id.equals(outro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(HorarioAtendimento.class);
    }

    @Override
    public String toString() {
        return "[" + id + "] " + inicio.format(FORMATO) + " (" + DURACAO_MINUTOS + " min) - "
                + clinica.getNome() + " - " + psicologo.getNome();
    }
}
