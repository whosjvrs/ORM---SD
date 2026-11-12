package consultas.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "psicologo")
@DiscriminatorValue("PSICOLOGO")
public class Psicologo extends Pessoa {

    @Column(nullable = false, unique = true, length = 20)
    private String crp;

    /** N:N com Clinica. Psicologo é o lado dono: grava a tabela psicologo_clinica. */
    @ManyToMany
    @JoinTable(name = "psicologo_clinica",
            joinColumns = @JoinColumn(name = "psicologo_id"),
            inverseJoinColumns = @JoinColumn(name = "clinica_id"))
    private List<Clinica> clinicas = new ArrayList<>();

    /** N:N com Especialidade. Psicologo é o lado dono: grava psicologo_especialidade. */
    @ManyToMany
    @JoinTable(name = "psicologo_especialidade",
            joinColumns = @JoinColumn(name = "psicologo_id"),
            inverseJoinColumns = @JoinColumn(name = "especialidade_id"))
    private List<Especialidade> especialidades = new ArrayList<>();

    /** Lado inverso de HorarioAtendimento.psicologo (1:N). */
    @OneToMany(mappedBy = "psicologo")
    private List<HorarioAtendimento> horarios = new ArrayList<>();

    protected Psicologo() {
    }

    public Psicologo(String nome, String crp, String email, String telefone) {
        super(nome, email, telefone);
        this.crp = crp;
    }

    public String getCrp() { return crp; }
    public void setCrp(String crp) { this.crp = crp; }

    public List<Clinica> getClinicas() { return new ArrayList<>(clinicas); }
    public List<Especialidade> getEspecialidades() { return new ArrayList<>(especialidades); }
    public List<HorarioAtendimento> getHorarios() { return new ArrayList<>(horarios); }

    /** Liga o psicólogo à clínica nos dois lados da associação. */
    public void vincular(Clinica clinica) {
        if (!clinicas.contains(clinica)) {
            clinicas.add(clinica);
            clinica.adicionarPsicologo(this);
        }
    }

    public void desvincular(Clinica clinica) {
        clinicas.remove(clinica);
        clinica.removerPsicologo(this);
    }

    public boolean atendeEm(Clinica clinica) {
        return clinicas.contains(clinica);
    }

    public void adicionarEspecialidade(Especialidade especialidade) {
        if (!especialidades.contains(especialidade)) {
            especialidades.add(especialidade);
            especialidade.adicionarPsicologo(this);
        }
    }

    public void removerEspecialidade(Especialidade especialidade) {
        especialidades.remove(especialidade);
        especialidade.removerPsicologo(this);
    }

    /** Chamado pelo HorarioAtendimento para manter os dois lados coerentes. */
    void adicionarHorario(HorarioAtendimento horario) {
        if (!horarios.contains(horario)) {
            horarios.add(horario);
        }
    }

    @Override
    public String toString() {
        return "[" + getId() + "] " + getNome() + " (CRP " + crp + ")";
    }
}
