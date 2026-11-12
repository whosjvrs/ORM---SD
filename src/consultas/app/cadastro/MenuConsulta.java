package consultas.app.cadastro;

/**
 * CRUD de Consulta.
 *
 * Responsável: Tabatha Aita
 * Siga a estrutura de MenuClinica (modelo de referência).
 * - Cadastrar: chamar a fachada (agendar), para passar pela Chain e pelo Observer.
 * - Listar: todas, com status; opção de filtrar por paciente (ConsultaDAO.listarPorPaciente).
 * - Atualizar: status e observações; cancelar pela fachada (libera o horário e notifica).
 * - Excluir: a remoção apaga as notificações em cascata; liberar o horário se estava AGENDADA.
 */
public class MenuConsulta extends MenuCrud {

    public MenuConsulta() {
        super("Consultas");
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
