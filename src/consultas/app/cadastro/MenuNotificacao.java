package consultas.app.cadastro;

import consultas.app.Console;
import consultas.dao.ConsultaDAO;
import consultas.dao.NotificacaoDAO;
import consultas.modelo.Consulta;
import consultas.modelo.Notificacao;
import consultas.modelo.TipoNotificacao;

import java.util.List;

/**
 * CRUD de Notificacao. Normalmente quem cria notificações é o Observer (NotificadorPaciente);
 * aqui também é possível registrar um aviso manual para uma consulta.
 * O excluir do MenuCrud chama NotificacaoDAO.remover, que respeita o orphanRemoval.
 */
public class MenuNotificacao extends MenuCrud<Notificacao, NotificacaoDAO> {

    private final ConsultaDAO consultaDAO = new ConsultaDAO();

    public MenuNotificacao() {
        super("Notificações", "da notificação", new NotificacaoDAO());
    }

    @Override
    protected void cadastrar() {
        Consulta consulta = Console.escolher("Consultas", consultaDAO.listarTodos());
        if (consulta == null) {
            return;
        }
        TipoNotificacao tipo = Console.escolher("Tipo", List.of(TipoNotificacao.values()));
        Notificacao notificacao = dao.registrar(consulta.getId(), tipo, Console.lerTexto("Mensagem: "));
        System.out.println("Notificação cadastrada com id " + notificacao.getId() + ".");
    }

    @Override
    protected void listar() {
        System.out.println("1 - Todas");
        System.out.println("2 - De uma consulta");
        if (Console.lerNumero("Escolha: ", 1, 2) == 1) {
            super.listar();
            return;
        }
        Consulta consulta = Console.escolher("Consultas", consultaDAO.listarTodos());
        if (consulta != null) {
            Console.imprimir(dao.listarPorConsulta(consulta));
        }
    }

    @Override
    protected void detalhar(Notificacao notificacao) {
        System.out.println(notificacao);
        System.out.println("   Paciente: " + notificacao.getConsulta().getPaciente().getNome());
    }

    @Override
    protected boolean editar(Notificacao notificacao) {
        notificacao.setMensagem(Console.lerTextoOpcional("Mensagem", notificacao.getMensagem()));
        if (!notificacao.isLida() && Console.confirmar("Marcar como lida?")) {
            notificacao.marcarComoLida();
        }
        return true;
    }
}
