package consultas.app.cadastro;

/**
 * CRUD de Especialidade.
 *
 * Responsável: Geovana Vincensi
 * Siga a estrutura de MenuClinica (modelo de referência).
 * - Nome é único (regra 10).
 * - Buscar: mostrar também os psicólogos da especialidade.
 * - Excluir: desfazer os vínculos com psicólogos antes de remover.
 */
public class MenuEspecialidade extends MenuCrud {

    public MenuEspecialidade() {
        super("Especialidades");
    }

    @Override
    protected void cadastrar() {
        emConstrucao(); // TODO Geovana Vincensi
    }

    @Override
    protected void listar() {
        emConstrucao(); // TODO Geovana Vincensi
    }

    @Override
    protected void buscar() {
        emConstrucao(); // TODO Geovana Vincensi
    }

    @Override
    protected void atualizar() {
        emConstrucao(); // TODO Geovana Vincensi
    }

    @Override
    protected void excluir() {
        emConstrucao(); // TODO Geovana Vincensi
    }
}
