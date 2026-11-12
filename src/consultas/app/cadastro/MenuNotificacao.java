package consultas.app.cadastro;

/**
 * CRUD de Notificacao.
 *
 * Responsável: Tabatha Aita
 * Siga a estrutura de MenuClinica (modelo de referência).
 * - Cadastrar: normalmente quem cria é o Observer; aqui, permitir criar um aviso manual para uma consulta.
 * - Listar: todas ou por consulta (NotificacaoDAO.listarPorConsulta).
 * - Atualizar: marcar como lida / alterar mensagem.
 * - Excluir: remover pela consulta (consulta.removerNotificacao) para respeitar o orphanRemoval.
 */
public class MenuNotificacao extends MenuCrud {

    public MenuNotificacao() {
        super("Notificações");
    }

    @Override
    protected void cadastrar() {
        emConstrucao(); // TODO Tabatha Aita
    }

    @Override
    protected void listar() {
        emConstrucao(); // TODO Tabatha Aita
    }

    @Override
    protected void buscar() {
        emConstrucao(); // TODO Tabatha Aita
    }

    @Override
    protected void atualizar() {
        emConstrucao(); // TODO Tabatha Aita
    }

    @Override
    protected void excluir() {
        emConstrucao(); // TODO Tabatha Aita
    }
}
