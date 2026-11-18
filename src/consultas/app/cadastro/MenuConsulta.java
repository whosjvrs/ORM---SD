package consultas.app.cadastro;

import consultas.app.Console;
import consultas.dao.ConsultaDAO;
import consultas.dao.HorarioDAO;
import consultas.dao.NotificacaoDAO;
import consultas.dao.PacienteDAO;
import consultas.facade.AgendamentoFacade;
import consultas.modelo.Consulta;
import consultas.modelo.HorarioAtendimento;
import consultas.modelo.Paciente;

/**
 * CRUD de Consulta.
 *
 * Cadastrar e cancelar passam pela fachada, para que a cadeia de validação (Chain) e a
 * notificação (Observer) do RA2 também valham aqui. As demais operações usam o ConsultaDAO.
 */
public class MenuConsulta extends MenuCrud {

    private final AgendamentoFacade facade;
    private final ConsultaDAO dao = new ConsultaDAO();
    private final PacienteDAO pacienteDAO = new PacienteDAO();
    private final HorarioDAO horarioDAO = new HorarioDAO();
    private final NotificacaoDAO notificacaoDAO = new NotificacaoDAO();

    public MenuConsulta(AgendamentoFacade facade) {
        super("Consultas");
        this.facade = facade;
    }

    /** Versão curta do agendamento: escolhe paciente e qualquer horário livre. */
    @Override
    protected void cadastrar() {
        Paciente paciente = Console.escolher("Pacientes", pacienteDAO.listarTodos());
        if (paciente == null) {
            return;
        }
        HorarioAtendimento horario = Console.escolher("Horários disponíveis", horarioDAO.listarTodosDisponiveis());
        if (horario == null) {
            return;
        }
        Consulta consulta = facade.agendar(paciente, horario);
        System.out.println("Consulta cadastrada com id " + consulta.getId() + ".");
    }

    @Override
    protected void listar() {
        System.out.println("1 - Todas");
        System.out.println("2 - De um paciente");
        if (Console.lerNumero("Escolha: ", 1, 2) == 1) {
            Console.imprimir(dao.listarTodos());
            return;
        }
        Paciente paciente = Console.escolher("Pacientes", pacienteDAO.listarTodos());
        if (paciente != null) {
            Console.imprimir(dao.listarPorPaciente(paciente));
        }
    }

    @Override
    protected void buscar() {
        Consulta consulta = dao.buscarPorId(Console.lerId("Id da consulta: "));
        if (consulta == null) {
            System.out.println("Consulta não encontrada.");
            return;
        }
        System.out.println(consulta);
        System.out.println("   Registrada em: " + consulta.getDataRegistro().format(Console.DATA_HORA)
                + " | Observações: " + (consulta.getObservacoes() == null ? "-" : consulta.getObservacoes()));
        System.out.println("   Notificações:");
        Console.imprimir(notificacaoDAO.listarPorConsulta(consulta));
    }

    @Override
    protected void atualizar() {
        Consulta consulta = dao.buscarPorId(Console.lerId("Id da consulta: "));
        if (consulta == null) {
            System.out.println("Consulta não encontrada.");
            return;
        }
        System.out.println(consulta);
        System.out.println("1 - Alterar observações");
        System.out.println("2 - Marcar como REALIZADA");
        System.out.println("3 - Cancelar (libera o horário e notifica o paciente)");
        switch (Console.lerNumero("Escolha: ", 1, 3)) {
            case 1 -> {
                consulta.setObservacoes(Console.lerTextoOpcional("Observações", consulta.getObservacoes()));
                dao.atualizar(consulta);
                System.out.println("Observações atualizadas.");
            }
            case 2 -> {
                dao.marcarRealizada(consulta.getId());
                System.out.println("Consulta marcada como REALIZADA.");
            }
            default -> facade.cancelar(consulta);
        }
    }

    @Override
    protected void excluir() {
        Long id = Console.lerId("Id da consulta: ");
        if (!Console.confirmar("Confirma a exclusão? As notificações dela também serão apagadas.")) {
            return;
        }
        // ConsultaDAO.remover: cascade apaga as notificações e, se AGENDADA, libera o horário.
        System.out.println(dao.remover(id) ? "Consulta excluída." : "Consulta não encontrada.");
    }
}
