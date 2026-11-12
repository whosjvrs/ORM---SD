package consultas.app.cadastro;

/**
 * CRUD de Paciente.
 *
 * Responsável: Geovana Vincensi
 * Siga a estrutura de MenuClinica (modelo de referência).
 * - CPF é único (regra 10): trate o erro de CPF repetido com uma mensagem clara.
 * - Excluir: não permitir se o paciente tiver consultas (regra 9) — PacienteDAO.remover.
 */
public class MenuPaciente extends MenuCrud {

    public MenuPaciente() {
        super("Pacientes");
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
