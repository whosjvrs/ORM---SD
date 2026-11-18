package consultas.app.cadastro;

import consultas.app.Console;
import consultas.dao.ConsultaDAO;
import consultas.dao.PacienteDAO;
import consultas.modelo.Paciente;

/** CRUD de Paciente (subclasse de Pessoa: grava nas tabelas "pessoa" e "paciente"). */
public class MenuPaciente extends MenuCrud<Paciente, PacienteDAO> {

    private final ConsultaDAO consultaDAO = new ConsultaDAO();

    public MenuPaciente() {
        super("Pacientes", "do paciente", new PacienteDAO());
    }

    @Override
    protected void cadastrar() {
        String nome = Console.lerTexto("Nome: ");
        String cpf = Console.lerTexto("CPF: ");
        if (dao.existeCpf(cpf, null)) {           // regra 10
            System.out.println("Já existe um paciente com o CPF " + cpf + ".");
            return;
        }
        Paciente paciente = dao.salvar(new Paciente(nome, cpf,
                Console.lerTexto("E-mail: "),
                Console.lerTexto("Telefone: "),
                Console.lerData("Data de nascimento")));
        System.out.println("Paciente cadastrado com id " + paciente.getId() + ".");
    }

    @Override
    protected void detalhar(Paciente paciente) {
        System.out.println(paciente);
        System.out.println("   E-mail: " + paciente.getEmail() + " | Telefone: " + paciente.getTelefone()
                + " | Nascimento: " + (paciente.getDataNascimento() == null ? "-"
                : paciente.getDataNascimento().format(Console.DATA)));
        System.out.println("   Consultas:");
        Console.imprimir(consultaDAO.listarPorPaciente(paciente));
    }

    @Override
    protected boolean editar(Paciente paciente) {
        String cpf = Console.lerTextoOpcional("CPF", paciente.getCpf());
        if (dao.existeCpf(cpf, paciente.getId())) {
            System.out.println("Já existe outro paciente com o CPF " + cpf + ".");
            return false;
        }
        paciente.setCpf(cpf);
        paciente.setNome(Console.lerTextoOpcional("Nome", paciente.getNome()));
        paciente.setEmail(Console.lerTextoOpcional("E-mail", paciente.getEmail()));
        paciente.setTelefone(Console.lerTextoOpcional("Telefone", paciente.getTelefone()));
        paciente.setDataNascimento(Console.lerDataOpcional("Data de nascimento", paciente.getDataNascimento()));
        return true;
    }
}
