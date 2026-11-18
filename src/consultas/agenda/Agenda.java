package consultas.agenda;

import consultas.dao.HorarioDAO;
import consultas.modelo.AgendamentoException;
import consultas.modelo.Clinica;
import consultas.modelo.HorarioAtendimento;
import consultas.modelo.Psicologo;

import java.util.List;

/**
 * Subsistema de horários coordenado pela AgendamentoFacade.
 *
 * RA3: deixou de guardar uma lista em memória e passou a consultar o banco pelo HorarioDAO.
 * A reserva em si acontece dentro de ConsultaDAO.registrar, na mesma transação da consulta (regra 4).
 */
public class Agenda {

    private final HorarioDAO horarioDAO = new HorarioDAO();

    /** Horários livres e futuros daquela clínica e daquele psicólogo (consulta JPQL). */
    public List<HorarioAtendimento> listarDisponiveis(Clinica clinica, Psicologo psicologo) {
        return horarioDAO.listarDisponiveis(clinica, psicologo);
    }

    /**
     * Busca o horário de novo no banco. O objeto que veio do menu pode estar desatualizado
     * (ex.: outra opção do menu reservou o horário depois que ele foi listado); a validação
     * precisa olhar o estado atual.
     */
    public HorarioAtendimento atualizar(HorarioAtendimento horario) {
        HorarioAtendimento atual = horarioDAO.buscarPorId(horario.getId());
        if (atual == null) {
            throw new AgendamentoException("Horário " + horario.getId() + " não existe mais.");
        }
        return atual;
    }
}
