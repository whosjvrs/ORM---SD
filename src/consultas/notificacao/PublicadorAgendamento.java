package consultas.notificacao;

import consultas.modelo.Consulta;

import java.util.ArrayList;
import java.util.List;

/** Observer — Subject: guarda os observadores e repassa os eventos. */
public class PublicadorAgendamento {
    private final List<ObservadorAgendamento> observadores = new ArrayList<>();

    public void inscrever(ObservadorAgendamento o) {
        observadores.add(o);
        System.out.println("[Eventos] Observador inscrito");
    }

    public void remover(ObservadorAgendamento o) {
        observadores.remove(o);
        System.out.println("[Eventos] Observador removido");
    }

    public boolean estaInscrito(ObservadorAgendamento o) {
        return observadores.contains(o);
    }

    public void notificar(Consulta consulta) {
        for (ObservadorAgendamento o : observadores) {
            o.aoAgendar(consulta);
        }
    }

    public void notificarCancelamento(Consulta consulta) {
        for (ObservadorAgendamento o : observadores) {
            o.aoCancelar(consulta);
        }
    }
}
