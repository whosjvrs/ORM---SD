package consultas.validacao;

import consultas.modelo.HorarioAtendimento;
import consultas.modelo.Paciente;

/**
 * Chain of Responsibility — Handler.
 *
 * Cada validador concreto confere UMA regra em verificar().
 * Se a regra passar, validar() repassa ao próximo elo da corrente.
 * Se falhar, o elo lança AgendamentoException e a corrente para ali.
 */
public abstract class ValidadorAgendamento {

    private ValidadorAgendamento proximo;

    /** Liga o próximo elo e o devolve, para encadear: a.setProximo(b).setProximo(c); */
    public ValidadorAgendamento setProximo(ValidadorAgendamento proximo) {
        this.proximo = proximo;
        return proximo;
    }

    /** final: a ordem "verifica e repassa" é a mesma para todos os elos. */
    public final void validar(Paciente paciente, HorarioAtendimento horario) {
        verificar(paciente, horario);
        if (proximo != null) {
            proximo.validar(paciente, horario);
        }
    }

    protected abstract void verificar(Paciente paciente, HorarioAtendimento horario);
}
