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
        long total = JPAUtil.consultar(em -> em.createQuery(
                        "SELECT COUNT(e) FROM Especialidade e WHERE LOWER(e.nome) = LOWER(:nome)"
                                + " AND (:ignorar IS NULL OR e.id <> :ignorar)", Long.class)
                .setParameter("nome", nome)
                .setParameter("ignorar", ignorarId)
                .getSingleResult());
        return total > 0;
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
