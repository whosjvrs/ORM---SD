package consultas.dao;

import consultas.modelo.Especialidade;
import consultas.modelo.Psicologo;

/** DAO específico de Especialidade: unicidade do nome e exclusão desfazendo o N:N. */
public class EspecialidadeDAO extends GenericDAO<Especialidade> {

    public EspecialidadeDAO() {
        super(Especialidade.class);
    }

    /** Regra 10: o nome da especialidade é único. */
    public boolean existeNome(String nome, Long ignorarId) {
        return existeOutro("nome", nome, ignorarId);
    }

    /**
     * Antes de apagar, tira a especialidade de todos os psicólogos (lado dono do N:N),
     * senão a chave estrangeira em psicologo_especialidade impediria o DELETE.
     */
    @Override
    public boolean remover(Long id) {
        return JPAUtil.emTransacao(em -> {
            Especialidade especialidade = em.find(Especialidade.class, id);
            if (especialidade == null) {
                return false;
            }
            for (Psicologo p : especialidade.getPsicologos()) {
                p.removerEspecialidade(especialidade);
            }
            em.remove(especialidade);
            return true;
        });
    }
}
