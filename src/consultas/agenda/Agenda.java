package consultas.agenda;

import consultas.modelo.Clinica;
import consultas.modelo.HorarioAtendimento;
import consultas.modelo.Psicologo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Guarda os horários de atendimento e controla as reservas.
 * É um dos subsistemas coordenados pela AgendamentoFacade.
 */
public class Agenda {

    private final List<HorarioAtendimento> horarios = new ArrayList<>();

    public void adicionar(HorarioAtendimento horario) {
        horarios.add(horario);
    }

    /** Horários livres e futuros daquela clínica e daquele psicólogo. Devolve sempre uma lista nova. */
    public List<HorarioAtendimento> listarDisponiveis(Clinica clinica, Psicologo psicologo) {
        LocalDateTime agora = LocalDateTime.now();
        List<HorarioAtendimento> disponiveis = new ArrayList<>();
        for (HorarioAtendimento h : horarios) {
            if (h.getClinica() == clinica && h.getPsicologo() == psicologo
                    && !h.isReservado() && h.getInicio().isAfter(agora)) {
                disponiveis.add(h);
            }
        }
        return disponiveis;
    }

    /** Marca o horário como reservado. Protege contra reservar duas vezes. */
    public void reservar(HorarioAtendimento horario) {
        horario.reservar(); // lança AgendamentoException se já estiver reservado
        System.out.println("[Agenda] Horário " + horario.getId() + " reservado.");
    }
}
