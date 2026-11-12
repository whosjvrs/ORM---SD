package consultas.app.cadastro;

/**
 * CRUD de Psicologo.
 *
 * Responsável: Geovana Vincensi
 * Siga a estrutura de MenuClinica (modelo de referência).
 * - CRP é único (regra 10).
 * - Cadastrar/atualizar: escolher as clínicas (vincular/desvincular) e as especialidades
 *   (adicionarEspecialidade/removerEspecialidade) com Console.escolher.
 * - Excluir: não permitir se tiver horários (regra 9) — PsicologoDAO.remover; desfazer os N:N antes.
 */
public class MenuPsicologo extends MenuCrud {

    public MenuPsicologo() {
        super("Psicólogos");
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
