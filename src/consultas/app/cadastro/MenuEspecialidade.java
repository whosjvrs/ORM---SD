package consultas.app.cadastro;

import consultas.app.Console;
import consultas.dao.EspecialidadeDAO;
import consultas.dao.PsicologoDAO;
import consultas.modelo.Especialidade;

/**
 * CRUD de Especialidade (lado inverso do N:N com Psicologo).
 * Segue a estrutura de MenuClinica (modelo de referência).
 */
public class MenuEspecialidade extends MenuCrud {

    private final EspecialidadeDAO dao = new EspecialidadeDAO();
    private final PsicologoDAO psicologoDAO = new PsicologoDAO();

    public MenuEspecialidade() {
        super("Especialidades");
    }

    @Override
    protected void cadastrar() {
        String nome = Console.lerTexto("Nome: ");
        if (dao.existeNome(nome, null)) {         // regra 10
            System.out.println("Já existe uma especialidade com esse nome.");
            return;
        }
        String descricao = Console.lerTexto("Descrição: ");
        Especialidade especialidade = dao.salvar(new Especialidade(nome, descricao));
        System.out.println("Especialidade cadastrada com id " + especialidade.getId() + ".");
    }

    @Override
    protected void listar() {
        Console.imprimir(dao.listarTodos());
    }

    @Override
    protected void buscar() {
        Especialidade especialidade = dao.buscarPorId(Console.lerId("Id da especialidade: "));
        if (especialidade == null) {
            System.out.println("Especialidade não encontrada.");
            return;
        }
        System.out.println(especialidade);
        System.out.println("   Descrição: " + especialidade.getDescricao());
        System.out.println("   Psicólogos:");
        Console.imprimir(psicologoDAO.listarPorEspecialidade(especialidade)); // JOIN na tabela de junção
    }

    @Override
    protected void atualizar() {
        Especialidade especialidade = dao.buscarPorId(Console.lerId("Id da especialidade: "));
        if (especialidade == null) {
            System.out.println("Especialidade não encontrada.");
            return;
        }
        System.out.println("Enter mantém o valor atual.");
        String nome = Console.lerTextoOpcional("Nome", especialidade.getNome());
        if (dao.existeNome(nome, especialidade.getId())) {
            System.out.println("Já existe outra especialidade com esse nome.");
            return;
        }
        especialidade.setNome(nome);
        especialidade.setDescricao(Console.lerTextoOpcional("Descrição", especialidade.getDescricao()));
        dao.atualizar(especialidade);
        System.out.println("Especialidade atualizada.");
    }

    @Override
    protected void excluir() {
        Long id = Console.lerId("Id da especialidade: ");
        if (!Console.confirmar("Confirma a exclusão? Ela será retirada dos psicólogos que a têm.")) {
            return;
        }
        // EspecialidadeDAO.remover desfaz os vínculos N:N antes de apagar.
        System.out.println(dao.remover(id) ? "Especialidade excluída." : "Especialidade não encontrada.");
    }
}
