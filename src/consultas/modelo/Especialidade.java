package consultas.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Área de atuação do psicólogo (ex.: TCC, infantil, casal). Nova no RA3. */
@Entity
@Table(name = "especialidade")
public class Especialidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String nome;

    @Column(length = 255)
    private String descricao;

    /** Lado inverso do N:N com Psicologo (quem grava é Psicologo.especialidades). */
    @ManyToMany(mappedBy = "especialidades")
    private List<Psicologo> psicologos = new ArrayList<>();

    protected Especialidade() {
    }

    public Especialidade(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    public Long getId() { return id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public List<Psicologo> getPsicologos() { return new ArrayList<>(psicologos); }

    void adicionarPsicologo(Psicologo psicologo) {
        if (!psicologos.contains(psicologo)) {
            psicologos.add(psicologo);
        }
    }

    void removerPsicologo(Psicologo psicologo) {
        psicologos.remove(psicologo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Especialidade outra)) return false;
        return id != null && id.equals(outra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(Especialidade.class);
    }

    @Override
    public String toString() {
        return "[" + id + "] " + nome;
    }
}
