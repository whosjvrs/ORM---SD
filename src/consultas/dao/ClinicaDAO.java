package consultas.dao;

import consultas.modelo.Clinica;

/**
 * DAO específico da Clinica. Herda o CRUD do GenericDAO e acrescenta a regra de exclusão.
 *
 * Convenção do projeto: regras que impedem uma exclusão (regra 9) ficam no DAO específico,
 * sobrescrevendo remover() antes de chamar o remover() do GenericDAO.
 */
public class ClinicaDAO extends GenericDAO<Clinica> {

    public ClinicaDAO() {
        super(Clinica.class);
    }

    /** Regra 9: não exclui clínica que tenha horários ou psicólogos vinculados. */
    @Override
    public boolean remover(Long id) {
        impedirSeExistir("SELECT COUNT(h) FROM HorarioAtendimento h WHERE h.clinica.id = :id", id,
                "A clínica tem %d horário(s) cadastrados. Remova-os antes.");
        impedirSeExistir("SELECT COUNT(p) FROM Psicologo p JOIN p.clinicas c WHERE c.id = :id", id,
                "A clínica tem %d psicólogo(s) vinculados. Desvincule-os antes.");
        return super.remover(id);
    }
}
