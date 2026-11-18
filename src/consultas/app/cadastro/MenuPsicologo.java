package consultas.app.cadastro;

import consultas.app.Console;
import consultas.dao.GenericDAO;
import consultas.dao.PsicologoDAO;
import consultas.modelo.Clinica;
import consultas.modelo.Especialidade;
import consultas.modelo.Psicologo;

import java.util.List;

/**
 * CRUD de Psicologo, incluindo os dois relacionamentos N:N (clínicas e especialidades).
 * Segue a estrutura de MenuClinica (modelo de referência).
 */
public class MenuPsicologo extends MenuCrud {

    private final PsicologoDAO dao = new PsicologoDAO();
    private final GenericDAO<Clinica> clinicaDAO = new GenericDAO<>(Clinica.class);
    private final GenericDAO<Especialidade> especialidadeDAO = new GenericDAO<>(Especialidade.class);

    public MenuPsicologo() {
        super("Psicólogos");
    }

    @Override
    protected void cadastrar() {
        String nome = Console.lerTexto("Nome: ");
        String crp = Console.lerTexto("CRP: ");
        if (dao.existeCrp(crp, null)) {           // regra 10
            System.out.println("Já existe um psicólogo com o CRP " + crp + ".");
            return;
        }
        String email = Console.lerTexto("E-mail: ");
        String telefone = Console.lerTexto("Telefone: ");
        Psicologo psicologo = dao.salvar(new Psicologo(nome, crp, email, telefone));
        escolherVinculos(psicologo);
        System.out.println("Psicólogo cadastrado com id " + psicologo.getId() + ".");
    }

    @Override
    protected void listar() {
        for (Psicologo p : dao.listarTodos()) {
            System.out.println(p + " | clínicas: " + nomes(p.getClinicas())
                    + " | especialidades: " + nomes(p.getEspecialidades()));
        }
    }

    @Override
    protected void buscar() {
        Psicologo psicologo = dao.buscarPorId(Console.lerId("Id do psicólogo: "));
        if (psicologo == null) {
            System.out.println("Psicólogo não encontrado.");
            return;
        }
        // As listas são LAZY: cada uma é buscada no banco quando usada aqui.
        System.out.println(psicologo);
        System.out.println("   E-mail: " + psicologo.getEmail() + " | Telefone: " + psicologo.getTelefone());
        System.out.println("   Clínicas: " + nomes(psicologo.getClinicas()));
        System.out.println("   Especialidades: " + nomes(psicologo.getEspecialidades()));
        System.out.println("   Horários cadastrados: " + psicologo.getHorarios().size());
    }

    @Override
    protected void atualizar() {
        Psicologo psicologo = dao.buscarPorId(Console.lerId("Id do psicólogo: "));
        if (psicologo == null) {
            System.out.println("Psicólogo não encontrado.");
            return;
        }
        System.out.println("Enter mantém o valor atual.");
        psicologo.setNome(Console.lerTextoOpcional("Nome", psicologo.getNome()));
        String crp = Console.lerTextoOpcional("CRP", psicologo.getCrp());
        if (dao.existeCrp(crp, psicologo.getId())) {
            System.out.println("Já existe outro psicólogo com o CRP " + crp + ".");
            return;
        }
        psicologo.setCrp(crp);
        psicologo.setEmail(Console.lerTextoOpcional("E-mail", psicologo.getEmail()));
        psicologo.setTelefone(Console.lerTextoOpcional("Telefone", psicologo.getTelefone()));
        dao.atualizar(psicologo);
        System.out.println("Atuais: clínicas " + nomes(psicologo.getClinicas())
                + " | especialidades " + nomes(psicologo.getEspecialidades()));
        if (Console.confirmar("Alterar clínicas e especialidades?")) {
            escolherVinculos(psicologo);
        }
        System.out.println("Psicólogo atualizado.");
    }

    @Override
    protected void excluir() {
        Long id = Console.lerId("Id do psicólogo: ");
        if (!Console.confirmar("Confirma a exclusão?")) {
            return;
        }
        // PsicologoDAO.remover aplica a regra 9 e desfaz os vínculos N:N antes de apagar.
        System.out.println(dao.remover(id) ? "Psicólogo excluído." : "Psicólogo não encontrado.");
    }

    /** Escolhe clínicas e especialidades e grava os N:N em uma transação (PsicologoDAO.definirVinculos). */
    private void escolherVinculos(Psicologo psicologo) {
        List<Clinica> clinicas = Console.escolherVarios("Clínicas onde atende", clinicaDAO.listarTodos());
        List<Especialidade> especialidades = Console.escolherVarios("Especialidades", especialidadeDAO.listarTodos());
        dao.definirVinculos(psicologo.getId(), clinicas, especialidades);
    }

    private static String nomes(List<?> itens) {
        if (itens.isEmpty()) {
            return "-";
        }
        StringBuilder sb = new StringBuilder();
        for (Object item : itens) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(item instanceof Clinica c ? c.getNome() : ((Especialidade) item).getNome());
        }
        return sb.toString();
    }
}
