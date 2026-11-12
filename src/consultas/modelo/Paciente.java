package consultas.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "paciente")
@DiscriminatorValue("PACIENTE")
public class Paciente extends Pessoa {

    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    private LocalDate dataNascimento;

    /** Lado inverso de Consulta.paciente (1:N). Quem grava a chave é a Consulta. */
    @OneToMany(mappedBy = "paciente")
    private List<Consulta> consultas = new ArrayList<>();

    protected Paciente() {
    }

    public Paciente(String nome, String cpf, String email, String telefone, LocalDate dataNascimento) {
        super(nome, email, telefone);
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
    }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public List<Consulta> getConsultas() { return new ArrayList<>(consultas); }

    /** Chamado pela Consulta para manter os dois lados da associação coerentes. */
    void adicionarConsulta(Consulta consulta) {
        if (!consultas.contains(consulta)) {
            consultas.add(consulta);
        }
    }

    @Override
    public String toString() {
        return "[" + getId() + "] " + getNome() + " (CPF " + cpf + ")";
    }
}
