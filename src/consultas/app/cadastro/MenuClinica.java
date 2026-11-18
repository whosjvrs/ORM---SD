package consultas.app.cadastro;

import consultas.app.Console;
import consultas.dao.ClinicaDAO;
import consultas.modelo.Clinica;

/** CRUD de Clinica. Listar, buscar, atualizar e excluir vêm prontos do MenuCrud. */
public class MenuClinica extends MenuCrud<Clinica, ClinicaDAO> {

    public MenuClinica() {
        super("Clínicas", "da clínica", new ClinicaDAO());
    }

    @Override
    protected void cadastrar() {
        Clinica clinica = dao.salvar(new Clinica(
                Console.lerTexto("Nome: "),
                Console.lerTexto("Endereço: "),
                Console.lerTexto("Telefone: ")));
        System.out.println("Clínica cadastrada com id " + clinica.getId() + ".");
    }

    @Override
    protected void detalhar(Clinica clinica) {
        System.out.println(clinica);
        System.out.println("   Endereço: " + clinica.getEndereco() + " | Telefone: " + clinica.getTelefone());
        System.out.println("   Psicólogos: " + clinica.getPsicologos().size()
                + " | Horários: " + clinica.getHorarios().size()); // listas LAZY: buscadas aqui
    }

    @Override
    protected boolean editar(Clinica clinica) {
        clinica.setNome(Console.lerTextoOpcional("Nome", clinica.getNome()));
        clinica.setEndereco(Console.lerTextoOpcional("Endereço", clinica.getEndereco()));
        clinica.setTelefone(Console.lerTextoOpcional("Telefone", clinica.getTelefone()));
        return true;
    }
}
