package consultas.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/** Registro de um aviso enviado ao paciente pelo Observer. Nova no RA3. */
@Entity
@Table(name = "notificacao")
public class Notificacao {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** N:1 — dono da associação: grava a coluna consulta_id. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "consulta_id", nullable = false)
    private Consulta consulta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoNotificacao tipo;

    @Column(nullable = false, length = 500)
    private String mensagem;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Column(nullable = false)
    private boolean lida;

    protected Notificacao() {
    }

    public Notificacao(Consulta consulta, TipoNotificacao tipo, String mensagem) {
        this.consulta = consulta;
        this.tipo = tipo;
        this.mensagem = mensagem;
        this.dataHora = LocalDateTime.now();
        this.lida = false;
        consulta.adicionarNotificacao(this);
    }

    public Long getId() { return id; }
    public Consulta getConsulta() { return consulta; }
    public TipoNotificacao getTipo() { return tipo; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public LocalDateTime getDataHora() { return dataHora; }

    public boolean isLida() { return lida; }
    public void marcarComoLida() { this.lida = true; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notificacao outra)) return false;
        return id != null && id.equals(outra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(Notificacao.class);
    }

    @Override
    public String toString() {
        return "[" + id + "] " + dataHora.format(FORMATO) + " " + tipo + (lida ? " (lida)" : "") + " - " + mensagem;
    }
}
