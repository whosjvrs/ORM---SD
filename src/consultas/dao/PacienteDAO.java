package consultas.dao;

import consultas.modelo.Paciente;

/** DAO específico de Paciente: unicidade do CPF e regra de exclusão. */
public class PacienteDAO extends GenericDAO<Paciente> {

    public PacienteDAO() {
        super(Paciente.class);
    }

    /** Regra 10: CPF é único. */
    public boolean existeCpf(String cpf, Long ignorarId) {
        return existeOutro("cpf", cpf, ignorarId);
    }

    /** Regra 9: não exclui paciente com consultas (inclusive canceladas, que ficam como histórico). */
    @Override
    public boolean remover(Long id) {
        impedirSeExistir("SELECT COUNT(c) FROM Consulta c WHERE c.paciente.id = :id", id,
                "O paciente tem %d consulta(s) registradas e não pode ser excluído.");
        return super.remover(id);
    }
}
