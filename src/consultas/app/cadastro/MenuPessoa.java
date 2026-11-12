package consultas.app.cadastro;

/**
 * CRUD de Pessoa (classe abstrata).
 *
 * Responsável: Geovana Vincensi
 * Siga a estrutura de MenuClinica (modelo de referência).
 * - Listar: new GenericDAO<>(Pessoa.class).listarTodos() traz pacientes e psicólogos juntos
 *   (consulta polimórfica — mostrar o tipo de cada um).
 * - Cadastrar: perguntar o tipo e reaproveitar MenuPaciente/MenuPsicologo.
 * - Atualizar: só os campos comuns (nome, e-mail, telefone).
 * - Excluir: reaproveitar as regras de PacienteDAO/PsicologoDAO.
 */
public class MenuPessoa extends MenuCrud {

    public MenuPessoa() {
        super("Pessoas (pacientes e psicólogos)");
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
