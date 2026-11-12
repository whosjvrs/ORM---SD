package consultas.dao;

import consultas.modelo.AgendamentoException;
import consultas.modelo.Clinica;

/**
 * DAO específico da Clinica. Herda o CRUD do GenericDAO e acrescenta a regra de exclusão.
 *
 * Convenção do projeto: regras que impedem uma exclusão (regra 9) ficam no DAO específico,
 * sobrescrevendo remover() e lançando AgendamentoException com uma mensagem para o usuário.
 */
public class ClinicaDAO extends GenericDAO<Clinica> {

    public ClinicaDAO() {
        super(Clinica.class);
    }

    /** Regra 9: não exclui clínica que tenha horários ou psicólogos vinculados. */
    @Override
    public boolean remover(Long id) {
        long horarios = JPAUtil.consultar(em -> em.createQuery(
                        "SELECT COUNT(h) FROM HorarioAtendimento h WHERE h.clinica.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult());
        long psicologos = JPAUtil.consultar(em -> em.createQuery(
                        "SELECT COUNT(p) FROM Psicologo p JOIN p.clinicas c WHERE c.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult());
        if (horarios > 0 || psicologos > 0) {
            throw new AgendamentoException("A clínica tem " + horarios + " horário(s) e "
                    + psicologos + " psicólogo(s) vinculados. Remova-os antes.");
        }
        return super.remover(id);
    }
}
