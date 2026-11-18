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
 */
public class MenuNotificacao extends MenuCrud {

    private final NotificacaoDAO dao = new NotificacaoDAO();
    private final ConsultaDAO consultaDAO = new ConsultaDAO();

    public MenuNotificacao() {
        super("Notificações");
    }

    @Override
    protected void cadastrar() {
        Consulta consulta = Console.escolher("Consultas", consultaDAO.listarTodos());
        if (consulta == null) {
            return;
        }
        TipoNotificacao tipo = Console.escolher("Tipo", List.of(TipoNotificacao.values()));
        String mensagem = Console.lerTexto("Mensagem: ");
        Notificacao notificacao = dao.registrar(consulta.getId(), tipo, mensagem);
        System.out.println("Notificação cadastrada com id " + notificacao.getId() + ".");
    }

    @Override
    protected void listar() {
        System.out.println("1 - Todas");
        System.out.println("2 - De uma consulta");
        if (Console.lerNumero("Escolha: ", 1, 2) == 1) {
            Console.imprimir(dao.listarTodos());
            return;
        }
        Consulta consulta = Console.escolher("Consultas", consultaDAO.listarTodos());
        if (consulta != null) {
            Console.imprimir(dao.listarPorConsulta(consulta));
        }
    }

    @Override
    protected void buscar() {
        Notificacao notificacao = dao.buscarPorId(Console.lerId("Id da notificação: "));
        if (notificacao == null) {
            System.out.println("Notificação não encontrada.");
            return;
        }
        System.out.println(notificacao);
        System.out.println("   Paciente: " + notificacao.getConsulta().getPaciente().getNome());
    }

    @Override
    protected void atualizar() {
        Notificacao notificacao = dao.buscarPorId(Console.lerId("Id da notificação: "));
        if (notificacao == null) {
            System.out.println("Notificação não encontrada.");
            return;
        }
        System.out.println("Enter mantém o valor atual.");
        notificacao.setMensagem(Console.lerTextoOpcional("Mensagem", notificacao.getMensagem()));
        if (!notificacao.isLida() && Console.confirmar("Marcar como lida?")) {
            notificacao.marcarComoLida();
        }
        dao.atualizar(notificacao);
        System.out.println("Notificação atualizada.");
    }

    @Override
    protected void excluir() {
        Long id = Console.lerId("Id da notificação: ");
        if (!Console.confirmar("Confirma a exclusão?")) {
            return;
        }
        // NotificacaoDAO.remover tira a notificação da lista da consulta (orphanRemoval).
        System.out.println(dao.remover(id) ? "Notificação excluída." : "Notificação não encontrada.");
    }
}
