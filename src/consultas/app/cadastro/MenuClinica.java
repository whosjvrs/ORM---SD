package consultas.app.cadastro;

import consultas.app.Console;
import consultas.dao.ClinicaDAO;
import consultas.modelo.Clinica;

/**
 * CRUD de Clinica — MODELO DE REFERÊNCIA para os outros menus de cadastro.
 * Os demais menus devem seguir esta mesma estrutura.
 */
public class MenuClinica extends MenuCrud {

    private final ClinicaDAO dao = new ClinicaDAO();

    public MenuClinica() {
        super("Clínicas");
    }

    @Override
    protected void cadastrar() {
        String nome = Console.lerTexto("Nome: ");
        String endereco = Console.lerTexto("Endereço: ");
        String telefone = Console.lerTexto("Telefone: ");
        Clinica clinica = dao.salvar(new Clinica(nome, endereco, telefone));
        System.out.println("Clínica cadastrada com id " + clinica.getId() + ".");
    }

    @Override
    protected void listar() {
        Console.imprimir(dao.listarTodos());
    }

    @Override
    protected void buscar() {
        Clinica clinica = dao.buscarPorId(Console.lerId("Id da clínica: "));
        if (clinica == null) {
            System.out.println("Clínica não encontrada.");
            return;
        }
        System.out.println(clinica);
        System.out.println("   Endereço: " + clinica.getEndereco() + " | Telefone: " + clinica.getTelefone());
        System.out.println("   Psicólogos: " + clinica.getPsicologos().size()
                + " | Horários: " + clinica.getHorarios().size());
    }

    @Override
    protected void atualizar() {
        Clinica clinica = dao.buscarPorId(Console.lerId("Id da clínica: "));
        if (clinica == null) {
            System.out.println("Clínica não encontrada.");
            return;
        }
        System.out.println("Enter mantém o valor atual.");
        clinica.setNome(Console.lerTextoOpcional("Nome", clinica.getNome()));
        clinica.setEndereco(Console.lerTextoOpcional("Endereço", clinica.getEndereco()));
        clinica.setTelefone(Console.lerTextoOpcional("Telefone", clinica.getTelefone()));
        dao.atualizar(clinica);
        System.out.println("Clínica atualizada.");
    }

    @Override
    protected void excluir() {
        Long id = Console.lerId("Id da clínica: ");
        if (!Console.confirmar("Confirma a exclusão?")) {
            return;
        }
        // ClinicaDAO.remover aplica a regra 9 e lança AgendamentoException se houver vínculos.
        System.out.println(dao.remover(id) ? "Clínica excluída." : "Clínica não encontrada.");
    }
}
