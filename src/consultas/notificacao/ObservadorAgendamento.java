package consultas.notificacao;

import consultas.modelo.Consulta;

/** Observer — quem quer ser avisado dos eventos de agendamento. */
public interface ObservadorAgendamento {

    void aoAgendar(Consulta consulta);

    /** Novo no RA3: evento de cancelamento. */
    void aoCancelar(Consulta consulta);
}
