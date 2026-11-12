package consultas.validacao;

import consultas.modelo.HorarioAtendimento;
import consultas.modelo.AgendamentoException;
import consultas.modelo.Paciente;

/** Chain of Responsibility — elo 1: o psicólogo precisa atender na clínica do horário. */
public class ValidadorVinculo extends ValidadorAgendamento {

    @Override
    protected void verificar(Paciente paciente, HorarioAtendimento horario) {
        if (!horario.getPsicologo().atendeEm(horario.getClinica())) {
            throw new AgendamentoException("Psicólogo não atende nesta clínica.");
        }
        System.out.println("[Validação] Vínculo psicólogo-clínica OK");
    }
}
