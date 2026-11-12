package consultas.facade;

import consultas.agenda.Agenda;
import consultas.modelo.HorarioAtendimento;
import consultas.app.DadosExemplo;
import consultas.estrategia.EstrategiaOrdenacao;
import consultas.modelo.Clinica;
import consultas.modelo.Consulta;
import consultas.modelo.Paciente;
import consultas.modelo.Psicologo;
import consultas.notificacao.PublicadorAgendamento;
import consultas.validacao.ValidadorAgendamento;

import java.util.ArrayList;
import java.util.List;

/**
 * Facade: único ponto de acesso do menu ao sistema.
 * Coordena a Agenda, a cadeia de validação (Chain), as estratégias de
 * ordenação (Strategy) e o publicador de eventos (Observer).
 */
public class AgendamentoFacade {
    private final DadosExemplo dados;
    private final Agenda agenda;
    private final ValidadorAgendamento validador;
    private final PublicadorAgendamento publicador;
    private final List<Consulta> consultas = new ArrayList<>();

    public AgendamentoFacade(DadosExemplo dados, Agenda agenda,
                             ValidadorAgendamento validador,
                             PublicadorAgendamento publicador) {
        this.dados = dados;
        this.agenda = agenda;
        this.validador = validador;
        this.publicador = publicador;
    }

    public List<Clinica> listarClinicas() {
        return dados.getClinicas();
    }

    public List<Psicologo> listarPsicologosDaClinica(Clinica c) {
        List<Psicologo> resultado = new ArrayList<>();
        for (Psicologo p : dados.getPsicologos()) {
            if (p.atendeEm(c)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public List<Paciente> listarPacientes() {
        return dados.getPacientes();
    }

    public List<Consulta> listarConsultas() {
        return new ArrayList<>(consultas);
    }

    public List<HorarioAtendimento> listarHorarios(Clinica c, Psicologo p, EstrategiaOrdenacao estrategia) {
        System.out.println("[Facade] Ordenando por: " + estrategia.getDescricao());
        return estrategia.ordenar(agenda.listarDisponiveis(c, p));   // Strategy
    }

    public Consulta agendar(Paciente paciente, HorarioAtendimento horario) {
        System.out.println("[Facade] Iniciando agendamento...");
        validador.validar(paciente, horario);                        // 1. Chain: lança se falhar
        agenda.reservar(horario);                                    // 2. reserva
        Consulta consulta = new Consulta(paciente, horario);
        consultas.add(consulta);                                     // 3. registra
        System.out.println("[Facade] Consulta registrada.");
        publicador.notificar(consulta);                              // 4. Observer
        return consulta;
    }
}
