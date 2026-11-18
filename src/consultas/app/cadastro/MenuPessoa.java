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
 * O listar do MenuCrud já mostra a consulta polimórfica: "SELECT e FROM Pessoa e" traz pacientes
 * e psicólogos juntos, cada um como objeto da sua classe. Pessoa não pode ser instanciada, então o
 * cadastro pergunta o tipo e reaproveita os menus das subclasses.
 */
public class MenuPessoa extends MenuCrud<Pessoa, GenericDAO<Pessoa>> {

    private final MenuPaciente menuPaciente;
    private final MenuPsicologo menuPsicologo;

    public MenuPessoa(MenuPaciente menuPaciente, MenuPsicologo menuPsicologo) {
        super("Pessoas (pacientes e psicólogos)", "da pessoa", new GenericDAO<>(Pessoa.class));
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

    /** Mostra a classe real de cada objeto: o JPA devolve Paciente ou Psicologo, nunca "Pessoa". */
    @Override
    protected void listar() {
        for (Pessoa p : dao.listarTodos()) {
            System.out.println(p.getClass().getSimpleName() + " " + p);
        }
    }

    @Override
    protected void detalhar(Pessoa pessoa) {
        System.out.println(pessoa);
        System.out.println("   E-mail: " + pessoa.getEmail() + " | Telefone: " + pessoa.getTelefone());
    }

    /** Só os campos comuns, definidos em Pessoa. */
    @Override
    protected boolean editar(Pessoa pessoa) {
        pessoa.setNome(Console.lerTextoOpcional("Nome", pessoa.getNome()));
        pessoa.setEmail(Console.lerTextoOpcional("E-mail", pessoa.getEmail()));
        pessoa.setTelefone(Console.lerTextoOpcional("Telefone", pessoa.getTelefone()));
        return true;
    }

    /** Exclui com a regra da subclasse (paciente com consultas, psicólogo com horários). */
    @Override
    protected void excluir() {
        Pessoa pessoa = lerExistente();
        if (pessoa != null && Console.confirmar("Excluir " + pessoa + "?")) {
            if (pessoa instanceof Paciente) {
                new PacienteDAO().remover(pessoa.getId());
            } else {
                new PsicologoDAO().remover(pessoa.getId());
            }
            System.out.println("Registro excluído.");
        }
    }
}
