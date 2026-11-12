package consultas.validacao;

import consultas.modelo.HorarioAtendimento;
import consultas.modelo.AgendamentoException;
import consultas.modelo.Paciente;

import java.time.LocalDateTime;

/** Chain of Responsibility — elo 2: só horários futuros podem ser agendados. */
public class ValidadorDataFutura extends ValidadorAgendamento {

    @Override
    protected void verificar(Paciente paciente, HorarioAtendimento horario) {
        if (!horario.getInicio().isAfter(LocalDateTime.now())) {
            throw new AgendamentoException("Horário no passado.");
        }
        System.out.println("[Validação] Data futura OK");
    }
}
