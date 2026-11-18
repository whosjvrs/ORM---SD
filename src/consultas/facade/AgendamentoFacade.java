package consultas.facade;

import consultas.agenda.Agenda;
import consultas.dao.ConsultaDAO;
import consultas.dao.GenericDAO;
import consultas.dao.NotificacaoDAO;
import consultas.dao.PacienteDAO;
import consultas.dao.PsicologoDAO;
import consultas.estrategia.EstrategiaOrdenacao;
import consultas.modelo.Clinica;
import consultas.modelo.Consulta;
import consultas.modelo.HorarioAtendimento;
import consultas.modelo.Notificacao;
import consultas.modelo.Paciente;
import consultas.modelo.Psicologo;
import consultas.notificacao.PublicadorAgendamento;
import consultas.validacao.ValidadorAgendamento;

import java.util.List;

/**
 * Facade: único ponto de acesso do menu ao sistema.
 * Coordena a Agenda, a cadeia de validação (Chain), as estratégias de
 * ordenação (Strategy) e o publicador de eventos (Observer).
 *
 * RA3: os dados vêm do banco pelos DAOs (antes vinham de DadosExemplo e de uma lista em memória).
 */
public class AgendamentoFacade {
    private final Agenda agenda;
    private final ValidadorAgendamento validador;
    private final PublicadorAgendamento publicador;

    private final GenericDAO<Clinica> clinicaDAO = new GenericDAO<>(Clinica.class);
    private final PacienteDAO pacienteDAO = new PacienteDAO();
    private final PsicologoDAO psicologoDAO = new PsicologoDAO();
    private final ConsultaDAO consultaDAO = new ConsultaDAO();
    private final NotificacaoDAO notificacaoDAO = new NotificacaoDAO();

    public AgendamentoFacade(Agenda agenda, ValidadorAgendamento validador, PublicadorAgendamento publicador) {
        this.agenda = agenda;
        this.validador = validador;
        this.publicador = publicador;
    }

    public List<Clinica> listarClinicas() {
        return clinicaDAO.listarTodos();
    }

    public List<Psicologo> listarPsicologosDaClinica(Clinica c) {
        return psicologoDAO.listarPorClinica(c);
    }

    public List<Paciente> listarPacientes() {
        return pacienteDAO.listarTodos();
    }

    public List<Consulta> listarConsultas() {
        return consultaDAO.listarTodos();
    }

    public List<Consulta> listarConsultasAgendadas() {
        return consultaDAO.listarAtivas();
    }

    public List<Notificacao> listarNotificacoes(Consulta consulta) {
        return notificacaoDAO.listarPorConsulta(consulta);
    }

    public List<HorarioAtendimento> listarHorarios(Clinica c, Psicologo p, EstrategiaOrdenacao estrategia) {
        System.out.println("[Facade] Ordenando por: " + estrategia.getDescricao());
        return estrategia.ordenar(agenda.listarDisponiveis(c, p));   // Strategy
    }

    public Consulta agendar(Paciente paciente, HorarioAtendimento horario) {
        System.out.println("[Facade] Iniciando agendamento...");
        HorarioAtendimento atual = agenda.atualizar(horario);       // estado atual do banco
        validador.validar(paciente, atual);                          // 1. Chain: lança se falhar
        Consulta consulta = consultaDAO.registrar(paciente, atual);  // 2 e 3. reserva + registra (1 transação)
        System.out.println("[Facade] Horário " + atual.getId() + " reservado e consulta "
                + consulta.getId() + " registrada.");
        publicador.notificar(consulta);                              // 4. Observer
        return consulta;
    }

    /** Novo no RA3: cancela, libera o horário e avisa os observadores. */
    public Consulta cancelar(Consulta consulta) {
        System.out.println("[Facade] Cancelando consulta " + consulta.getId() + "...");
        Consulta cancelada = consultaDAO.cancelar(consulta.getId());
        System.out.println("[Facade] Consulta cancelada e horário " + cancelada.getHorario().getId() + " liberado.");
        publicador.notificarCancelamento(cancelada);
        return cancelada;
    }
}
