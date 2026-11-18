package consultas.dao;

import consultas.modelo.AgendamentoException;
import consultas.modelo.Paciente;

/** DAO específico de Paciente: unicidade do CPF e regra de exclusão. */
public class PacienteDAO extends GenericDAO<Paciente> {

    public PacienteDAO() {
        super(Paciente.class);
    }

    /** Regra 10: CPF é único. ignorarId serve para o "atualizar" não acusar o próprio registro. */
    public boolean existeCpf(String cpf, Long ignorarId) {
        long total = JPAUtil.consultar(em -> em.createQuery(
                        "SELECT COUNT(p) FROM Paciente p WHERE p.cpf = :cpf AND (:ignorar IS NULL OR p.id <> :ignorar)",
                        Long.class)
                .setParameter("cpf", cpf)
                .setParameter("ignorar", ignorarId)
                .getSingleResult());
        return total > 0;
    }

    /** Regra 9: não exclui paciente com consultas (inclusive canceladas, que ficam como histórico). */
    @Override
    public boolean remover(Long id) {
        long consultas = JPAUtil.consultar(em -> em.createQuery(
                        "SELECT COUNT(c) FROM Consulta c WHERE c.paciente.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult());
        if (consultas > 0) {
            throw new AgendamentoException("O paciente tem " + consultas + " consulta(s) registradas e não pode ser excluído.");
        }
        return super.remover(id);
    }
}
