package consultas.app.cadastro;

import consultas.app.Console;
import consultas.dao.GenericDAO;
import consultas.dao.PsicologoDAO;
import consultas.modelo.Clinica;
import consultas.modelo.Especialidade;
import consultas.modelo.Psicologo;

import java.util.List;
import java.util.stream.Collectors;

/** CRUD de Psicologo, incluindo os dois relacionamentos N:N (clínicas e especialidades). */
public class MenuPsicologo extends MenuCrud<Psicologo, PsicologoDAO> {

    private final GenericDAO<Clinica> clinicaDAO = new GenericDAO<>(Clinica.class);
    private final GenericDAO<Especialidade> especialidadeDAO = new GenericDAO<>(Especialidade.class);
    private boolean alterarVinculos;

    public MenuPsicologo() {
        super("Psicólogos", "do psicólogo", new PsicologoDAO());
    }

    @Override
    protected void cadastrar() {
        String nome = Console.lerTexto("Nome: ");
        String crp = Console.lerTexto("CRP: ");
        if (dao.existeCrp(crp, null)) {           // regra 10
            System.out.println("Já existe um psicólogo com o CRP " + crp + ".");
            return;
        }
        Psicologo psicologo = dao.salvar(new Psicologo(nome, crp,
                Console.lerTexto("E-mail: "),
                Console.lerTexto("Telefone: ")));
        escolherVinculos(psicologo);
        System.out.println("Psicólogo cadastrado com id " + psicologo.getId() + ".");
    }

    @Override
    protected void listar() {
        for (Psicologo p : dao.listarTodos()) {
            System.out.println(p + " | clínicas: " + nomesClinicas(p) + " | especialidades: " + nomesEspecialidades(p));
        }
    }

    @Override
    protected void detalhar(Psicologo psicologo) {
        // As listas são LAZY: cada uma é buscada no banco quando usada aqui.
        System.out.println(psicologo);
        System.out.println("   E-mail: " + psicologo.getEmail() + " | Telefone: " + psicologo.getTelefone());
        System.out.println("   Clínicas: " + nomesClinicas(psicologo));
        System.out.println("   Especialidades: " + nomesEspecialidades(psicologo));
        System.out.println("   Horários cadastrados: " + psicologo.getHorarios().size());
    }

    @Override
    protected boolean editar(Psicologo psicologo) {
        String crp = Console.lerTextoOpcional("CRP", psicologo.getCrp());
        if (dao.existeCrp(crp, psicologo.getId())) {
            System.out.println("Já existe outro psicólogo com o CRP " + crp + ".");
            return false;
        }
        psicologo.setCrp(crp);
        psicologo.setNome(Console.lerTextoOpcional("Nome", psicologo.getNome()));
        psicologo.setEmail(Console.lerTextoOpcional("E-mail", psicologo.getEmail()));
        psicologo.setTelefone(Console.lerTextoOpcional("Telefone", psicologo.getTelefone()));
        System.out.println("Atuais: clínicas " + nomesClinicas(psicologo)
                + " | especialidades " + nomesEspecialidades(psicologo));
        alterarVinculos = Console.confirmar("Alterar clínicas e especialidades?");
        return true;
    }

    /** Os N:N são gravados depois do merge, em transação própria (PsicologoDAO.definirVinculos). */
    @Override
    protected void depoisDeAtualizar(Psicologo psicologo) {
        if (alterarVinculos) {
            escolherVinculos(psicologo);
        }
    }

    private void escolherVinculos(Psicologo psicologo) {
        List<Clinica> clinicas = Console.escolherVarios("Clínicas onde atende", clinicaDAO.listarTodos());
        List<Especialidade> especialidades = Console.escolherVarios("Especialidades", especialidadeDAO.listarTodos());
        dao.definirVinculos(psicologo.getId(), clinicas, especialidades);
    }

    private static String nomesClinicas(Psicologo p) {
        return p.getClinicas().stream().map(Clinica::getNome).collect(Collectors.joining(", "));
    }

    private static String nomesEspecialidades(Psicologo p) {
        return p.getEspecialidades().stream().map(Especialidade::getNome).collect(Collectors.joining(", "));
    }
}
