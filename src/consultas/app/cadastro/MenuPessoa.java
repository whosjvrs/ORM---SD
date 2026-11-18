package consultas.app.cadastro;

import consultas.app.Console;
import consultas.dao.GenericDAO;
import consultas.dao.PacienteDAO;
import consultas.dao.PsicologoDAO;
import consultas.modelo.Paciente;
import consultas.modelo.Pessoa;

/**
 * CRUD de Pessoa, a superclasse abstrata (herança JOINED).
 *
 * Mostra a consulta polimórfica: "SELECT e FROM Pessoa e" traz pacientes e psicólogos juntos,
 * cada um como objeto da sua própria classe. Pessoa não pode ser instanciada, então o
 * cadastro pergunta o tipo e reaproveita os menus das subclasses.
 */
public class MenuPessoa extends MenuCrud {

    private final GenericDAO<Pessoa> dao = new GenericDAO<>(Pessoa.class);
    private final MenuPaciente menuPaciente;
    private final MenuPsicologo menuPsicologo;

    public MenuPessoa(MenuPaciente menuPaciente, MenuPsicologo menuPsicologo) {
        super("Pessoas (pacientes e psicólogos)");
        this.menuPaciente = menuPaciente;
        this.menuPsicologo = menuPsicologo;
    }

    @Override
    protected void cadastrar() {
        System.out.println("1 - Paciente");
        System.out.println("2 - Psicólogo");
        if (Console.lerNumero("Tipo: ", 1, 2) == 1) {
            menuPaciente.cadastrar();
        } else {
            menuPsicologo.cadastrar();
        }
    }

    @Override
    protected void listar() {
        for (Pessoa p : dao.listarTodos()) {
            System.out.println(tipo(p) + " " + p);
        }
    }

    @Override
    protected void buscar() {
        Pessoa pessoa = dao.buscarPorId(Console.lerId("Id da pessoa: "));
        if (pessoa == null) {
            System.out.println("Pessoa não encontrada.");
            return;
        }
        System.out.println(tipo(pessoa) + " " + pessoa);
        System.out.println("   E-mail: " + pessoa.getEmail() + " | Telefone: " + pessoa.getTelefone());
    }

    /** Atualiza só os campos comuns, definidos em Pessoa. */
    @Override
    protected void atualizar() {
        Pessoa pessoa = dao.buscarPorId(Console.lerId("Id da pessoa: "));
        if (pessoa == null) {
            System.out.println("Pessoa não encontrada.");
            return;
        }
        System.out.println(tipo(pessoa) + " " + pessoa + " — Enter mantém o valor atual.");
        pessoa.setNome(Console.lerTextoOpcional("Nome", pessoa.getNome()));
        pessoa.setEmail(Console.lerTextoOpcional("E-mail", pessoa.getEmail()));
        pessoa.setTelefone(Console.lerTextoOpcional("Telefone", pessoa.getTelefone()));
        dao.atualizar(pessoa); // o merge atualiza a tabela pessoa
        System.out.println("Pessoa atualizada.");
    }

    /** Exclui usando a regra da subclasse (paciente com consultas, psicólogo com horários). */
    @Override
    protected void excluir() {
        Pessoa pessoa = dao.buscarPorId(Console.lerId("Id da pessoa: "));
        if (pessoa == null) {
            System.out.println("Pessoa não encontrada.");
            return;
        }
        if (!Console.confirmar("Excluir " + tipo(pessoa) + " " + pessoa.getNome() + "?")) {
            return;
        }
        boolean removida = pessoa instanceof Paciente
                ? new PacienteDAO().remover(pessoa.getId())
                : new PsicologoDAO().remover(pessoa.getId());
        System.out.println(removida ? "Pessoa excluída." : "Pessoa não encontrada.");
    }

    private static String tipo(Pessoa pessoa) {
        return pessoa instanceof Paciente ? "(Paciente)" : "(Psicólogo)";
    }
}
