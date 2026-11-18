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
 * Cadastrar e cancelar passam pela fachada, para que a cadeia de validação (Chain) e a
 * notificação (Observer) do RA2 também valham aqui. O excluir usa o ConsultaDAO
 * (as notificações vão junto por cascade e, se AGENDADA, o horário é liberado).
 */
public class MenuConsulta extends MenuCrud<Consulta, ConsultaDAO> {

    private final AgendamentoFacade facade;
    private final PacienteDAO pacienteDAO = new PacienteDAO();
    private final HorarioDAO horarioDAO = new HorarioDAO();
    private final NotificacaoDAO notificacaoDAO = new NotificacaoDAO();

    public MenuConsulta(AgendamentoFacade facade) {
        super("Consultas", "da consulta", new ConsultaDAO());
        this.facade = facade;
    }

    /** Versão curta do agendamento: escolhe paciente e qualquer horário livre. */
    @Override
    protected void cadastrar() {
        Paciente paciente = Console.escolher("Pacientes", pacienteDAO.listarTodos());
        HorarioAtendimento horario = paciente == null ? null
                : Console.escolher("Horários disponíveis", horarioDAO.listarTodosDisponiveis());
        if (horario != null) {
            Consulta consulta = facade.agendar(paciente, horario);
            System.out.println("Consulta cadastrada com id " + consulta.getId() + ".");
        }
    }

    @Override
    protected void listar() {
        System.out.println("1 - Todas");
        System.out.println("2 - De um paciente");
        if (Console.lerNumero("Escolha: ", 1, 2) == 1) {
            super.listar();
            return;
        }
        Paciente paciente = Console.escolher("Pacientes", pacienteDAO.listarTodos());
        if (paciente != null) {
            Console.imprimir(dao.listarPorPaciente(paciente));
        }
    }

    @Override
    protected void detalhar(Consulta consulta) {
        System.out.println(consulta);
        System.out.println("   Registrada em: " + consulta.getDataRegistro().format(Console.DATA_HORA)
                + " | Observações: " + (consulta.getObservacoes() == null ? "-" : consulta.getObservacoes()));
        System.out.println("   Notificações:");
        Console.imprimir(notificacaoDAO.listarPorConsulta(consulta));
    }

    /**
     * Só as observações são gravadas pelo merge do MenuCrud (devolve true).
     * Mudanças de status têm transação própria no DAO/fachada (devolve false: nada a gravar depois).
     */
    @Override
    protected boolean editar(Consulta consulta) {
        System.out.println(consulta);
        System.out.println("1 - Alterar observações");
        System.out.println("2 - Marcar como REALIZADA");
        System.out.println("3 - Cancelar (libera o horário e notifica o paciente)");
        switch (Console.lerNumero("Escolha: ", 1, 3)) {
            case 1 -> {
                consulta.setObservacoes(Console.lerTextoOpcional("Observações", consulta.getObservacoes()));
                return true;
            }
            case 2 -> {
                dao.marcarRealizada(consulta.getId());
                System.out.println("Consulta marcada como REALIZADA.");
            }
            default -> facade.cancelar(consulta);
        }
        return false;
    }
}
