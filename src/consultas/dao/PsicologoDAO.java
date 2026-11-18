package consultas.dao;

import consultas.modelo.Clinica;
import consultas.modelo.Especialidade;
import consultas.modelo.Psicologo;

import java.util.List;

/** DAO específico de Psicologo: consultas pelos relacionamentos N:N e regras de exclusão. */
public class PsicologoDAO extends GenericDAO<Psicologo> {

    public PsicologoDAO() {
        super(Psicologo.class);
    }

    /** Psicólogos que atendem na clínica. JOIN na coleção N:N vira JOIN com psicologo_clinica. */
    public List<Psicologo> listarPorClinica(Clinica clinica) {
        return JPAUtil.consultar(em -> em.createQuery(
                        "SELECT p FROM Psicologo p JOIN p.clinicas c WHERE c.id = :id ORDER BY p.nome", Psicologo.class)
                .setParameter("id", clinica.getId())
                .getResultList());
    }

    public List<Psicologo> listarPorEspecialidade(Especialidade especialidade) {
        return JPAUtil.consultar(em -> em.createQuery(
                        "SELECT p FROM Psicologo p JOIN p.especialidades e WHERE e.id = :id ORDER BY p.nome", Psicologo.class)
                .setParameter("id", especialidade.getId())
                .getResultList());
    }

    /** Regra 10: CRP é único. */
    public boolean existeCrp(String crp, Long ignorarId) {
        return existeOutro("crp", crp, ignorarId);
    }

    /**
     * Troca as clínicas e especialidades do psicólogo em uma transação.
     * Tudo é buscado de novo com em.find, para trabalhar com objetos gerenciados:
     * as mudanças na lista do lado dono (Psicologo) viram INSERT/DELETE nas tabelas de junção.
     */
    public void definirVinculos(Long psicologoId, List<Clinica> clinicas, List<Especialidade> especialidades) {
        JPAUtil.emTransacao(em -> {
            Psicologo psicologo = em.find(Psicologo.class, psicologoId);
            limparVinculos(psicologo);
            for (Clinica c : clinicas) {
                psicologo.vincular(em.find(Clinica.class, c.getId()));
            }
            for (Especialidade e : especialidades) {
                psicologo.adicionarEspecialidade(em.find(Especialidade.class, e.getId()));
            }
            return null;
        });
    }

    /** Regra 9: não exclui psicólogo com horários. Os vínculos N:N são desfeitos na mesma transação. */
    @Override
    public boolean remover(Long id) {
        impedirSeExistir("SELECT COUNT(h) FROM HorarioAtendimento h WHERE h.psicologo.id = :id", id,
                "O psicólogo tem %d horário(s) cadastrados. Remova-os antes.");
        return JPAUtil.emTransacao(em -> {
            Psicologo psicologo = em.find(Psicologo.class, id);
            if (psicologo == null) {
                return false;
            }
            limparVinculos(psicologo);
            em.remove(psicologo);
            return true;
        });
    }

    /** Desfaz os dois N:N pelos métodos da entidade, que mantêm os dois lados coerentes. */
    private static void limparVinculos(Psicologo psicologo) {
        psicologo.getClinicas().forEach(psicologo::desvincular);
        psicologo.getEspecialidades().forEach(psicologo::removerEspecialidade);
    }
}
