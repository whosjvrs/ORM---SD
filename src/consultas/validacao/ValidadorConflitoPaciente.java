package consultas.validacao;

import consultas.dao.ConsultaDAO;
import consultas.modelo.AgendamentoException;
import consultas.modelo.HorarioAtendimento;
import consultas.modelo.Paciente;

/**
 * Chain of Responsibility — elo 4 (novo no RA3): o paciente não pode ter outra consulta
 * AGENDADA no mesmo horário, mesmo com outro psicólogo ou em outra clínica.
 * Resolve a limitação de horários sobrepostos que ficou aberta no RA2.
 */
public class ValidadorConflitoPaciente extends ValidadorAgendamento {

    private final ConsultaDAO consultaDAO = new ConsultaDAO();

    @Override
    protected void verificar(Paciente paciente, HorarioAtendimento horario) {
        if (consultaDAO.pacienteTemConsultaNoHorario(paciente, horario.getInicio())) {
            throw new AgendamentoException("O paciente já tem uma consulta agendada neste horário.");
        }
        System.out.println("[Validação] Agenda do paciente OK");
    }
}
