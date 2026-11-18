package consultas.app.cadastro;

import consultas.app.Console;
import consultas.dao.ConsultaDAO;
import consultas.dao.PacienteDAO;
import consultas.modelo.Paciente;

import java.time.LocalDate;

/**
 * CRUD de Paciente (subclasse de Pessoa: grava em "pessoa" e em "paciente").
 * Segue a estrutura de MenuClinica (modelo de referência).
 */
public class MenuPaciente extends MenuCrud {

    private final PacienteDAO dao = new PacienteDAO();
    private final ConsultaDAO consultaDAO = new ConsultaDAO();

    public MenuPaciente() {
        super("Pacientes");
    }

    @Override
    protected void cadastrar() {
        String nome = Console.lerTexto("Nome: ");
        String cpf = Console.lerTexto("CPF: ");
        if (dao.existeCpf(cpf, null)) {           // regra 10
            System.out.println("Já existe um paciente com o CPF " + cpf + ".");
            return;
        }
        String email = Console.lerTexto("E-mail: ");
        String telefone = Console.lerTexto("Telefone: ");
        LocalDate nascimento = Console.lerData("Data de nascimento");
        Paciente paciente = dao.salvar(new Paciente(nome, cpf, email, telefone, nascimento));
        System.out.println("Paciente cadastrado com id " + paciente.getId() + ".");
    }

    @Override
    protected void listar() {
        Console.imprimir(dao.listarTodos());
    }

    @Override
    protected void buscar() {
        Paciente paciente = dao.buscarPorId(Console.lerId("Id do paciente: "));
        if (paciente == null) {
            System.out.println("Paciente não encontrado.");
            return;
        }
        System.out.println(paciente);
        System.out.println("   E-mail: " + paciente.getEmail() + " | Telefone: " + paciente.getTelefone()
                + " | Nascimento: " + (paciente.getDataNascimento() == null ? "-"
                : paciente.getDataNascimento().format(Console.DATA)));
        System.out.println("   Consultas:");
        Console.imprimir(consultaDAO.listarPorPaciente(paciente));
    }

    @Override
    protected void atualizar() {
        Paciente paciente = dao.buscarPorId(Console.lerId("Id do paciente: "));
        if (paciente == null) {
            System.out.println("Paciente não encontrado.");
            return;
        }
        System.out.println("Enter mantém o valor atual.");
        paciente.setNome(Console.lerTextoOpcional("Nome", paciente.getNome()));
        String cpf = Console.lerTextoOpcional("CPF", paciente.getCpf());
        if (dao.existeCpf(cpf, paciente.getId())) {
            System.out.println("Já existe outro paciente com o CPF " + cpf + ".");
            return;
        }
        paciente.setCpf(cpf);
        paciente.setEmail(Console.lerTextoOpcional("E-mail", paciente.getEmail()));
        paciente.setTelefone(Console.lerTextoOpcional("Telefone", paciente.getTelefone()));
        paciente.setDataNascimento(Console.lerDataOpcional("Data de nascimento", paciente.getDataNascimento()));
        dao.atualizar(paciente); // objeto desanexado: o merge grava as mudanças
        System.out.println("Paciente atualizado.");
    }

    @Override
    protected void excluir() {
        Long id = Console.lerId("Id do paciente: ");
        if (!Console.confirmar("Confirma a exclusão?")) {
            return;
        }
        // PacienteDAO.remover aplica a regra 9 (paciente com consultas não é excluído).
        System.out.println(dao.remover(id) ? "Paciente excluído." : "Paciente não encontrado.");
    }
}
