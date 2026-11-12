package consultas.validacao;

import consultas.modelo.HorarioAtendimento;
import consultas.modelo.AgendamentoException;
import consultas.modelo.Paciente;

/** Chain of Responsibility — elo 3: o horário não pode estar reservado. */
public class ValidadorDisponibilidade extends ValidadorAgendamento {

    @Override
    protected void verificar(Paciente paciente, HorarioAtendimento horario) {
        if (horario.isReservado()) {
            throw new AgendamentoException("Horário indisponível.");
        }
        System.out.println("[Validação] Disponibilidade OK");
    }
}
