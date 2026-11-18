package consultas.app.cadastro;

import consultas.app.Console;
import consultas.dao.EspecialidadeDAO;
import consultas.dao.PsicologoDAO;
import consultas.modelo.Especialidade;

/** CRUD de Especialidade (lado inverso do N:N com Psicologo). */
public class MenuEspecialidade extends MenuCrud<Especialidade, EspecialidadeDAO> {

    private final PsicologoDAO psicologoDAO = new PsicologoDAO();

    public MenuEspecialidade() {
        super("Especialidades", "da especialidade", new EspecialidadeDAO());
    }

    @Override
    protected void cadastrar() {
        String nome = Console.lerTexto("Nome: ");
        if (dao.existeNome(nome, null)) {         // regra 10
            System.out.println("Já existe uma especialidade com esse nome.");
            return;
        }
        Especialidade especialidade = dao.salvar(new Especialidade(nome, Console.lerTexto("Descrição: ")));
        System.out.println("Especialidade cadastrada com id " + especialidade.getId() + ".");
    }

    @Override
    protected void detalhar(Especialidade especialidade) {
        System.out.println(especialidade);
        System.out.println("   Descrição: " + especialidade.getDescricao());
        System.out.println("   Psicólogos:");
        Console.imprimir(psicologoDAO.listarPorEspecialidade(especialidade)); // JOIN na tabela de junção
    }

    @Override
    protected boolean editar(Especialidade especialidade) {
        String nome = Console.lerTextoOpcional("Nome", especialidade.getNome());
        if (dao.existeNome(nome, especialidade.getId())) {
            System.out.println("Já existe outra especialidade com esse nome.");
            return false;
        }
        especialidade.setNome(nome);
        especialidade.setDescricao(Console.lerTextoOpcional("Descrição", especialidade.getDescricao()));
        return true;
    }
}
