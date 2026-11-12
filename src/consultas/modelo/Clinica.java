package consultas.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "clinica")
public class Clinica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 200)
    private String endereco;

    @Column(length = 20)
    private String telefone;

    /** Lado inverso do N:N com Psicologo (quem grava é Psicologo.clinicas). */
    @ManyToMany(mappedBy = "clinicas")
    private List<Psicologo> psicologos = new ArrayList<>();

    /** Lado inverso de HorarioAtendimento.clinica (1:N). */
    @OneToMany(mappedBy = "clinica")
    private List<HorarioAtendimento> horarios = new ArrayList<>();

    protected Clinica() {
    }

    public Clinica(String nome, String endereco, String telefone) {
        this.nome = nome;
        this.endereco = endereco;
        this.telefone = telefone;
    }

    public Long getId() { return id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public List<Psicologo> getPsicologos() { return new ArrayList<>(psicologos); }
    public List<HorarioAtendimento> getHorarios() { return new ArrayList<>(horarios); }

    // Usados por Psicologo.vincular/desvincular para manter os dois lados coerentes.
    void adicionarPsicologo(Psicologo psicologo) {
        if (!psicologos.contains(psicologo)) {
            psicologos.add(psicologo);
        }
    }

    void removerPsicologo(Psicologo psicologo) {
        psicologos.remove(psicologo);
    }

    void adicionarHorario(HorarioAtendimento horario) {
        if (!horarios.contains(horario)) {
            horarios.add(horario);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Clinica outra)) return false;
        return id != null && id.equals(outra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(Clinica.class);
    }

    @Override
    public String toString() {
        return "[" + id + "] " + nome;
    }
}
