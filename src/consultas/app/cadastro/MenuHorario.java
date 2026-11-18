package consultas.app.cadastro;

import consultas.app.Console;
import consultas.dao.GenericDAO;
import consultas.dao.HorarioDAO;
import consultas.dao.PsicologoDAO;
import consultas.modelo.AgendamentoException;
import consultas.modelo.Clinica;
import consultas.modelo.HorarioAtendimento;
import consultas.modelo.Psicologo;
import consultas.validacao.ValidadorAgendamento;
import consultas.validacao.ValidadorDataFutura;
import consultas.validacao.ValidadorVinculo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * CRUD de HorarioAtendimento.
 *
 * Reaproveita dois elos da Chain do RA2 para validar um horário novo ou alterado:
 * o psicólogo precisa atender na clínica e o horário precisa ser futuro.
 */
public class MenuHorario extends MenuCrud {

    private final HorarioDAO dao = new HorarioDAO();
    private final GenericDAO<Clinica> clinicaDAO = new GenericDAO<>(Clinica.class);
    private final PsicologoDAO psicologoDAO = new PsicologoDAO();
    private final ValidadorAgendamento validacao;

    public MenuHorario() {
        super("Horários de atendimento");
        validacao = new ValidadorVinculo();
        validacao.setProximo(new ValidadorDataFutura());
    }

    @Override
    protected void cadastrar() {
        Clinica clinica = Console.escolher("Clínicas", clinicaDAO.listarTodos());
        if (clinica == null) {
            return;
        }
        List<Psicologo> psicologos = psicologoDAO.listarPorClinica(clinica);
        if (psicologos.isEmpty()) {
            System.out.println("Nenhum psicólogo atende nesta clínica. Vincule um no cadastro de psicólogos.");
            return;
        }
        Psicologo psicologo = Console.escolher("Psicólogos da " + clinica.getNome(), psicologos);
        LocalDateTime inicio = Console.lerDataHora("Início");
        if (dao.existe(psicologo, inicio, null)) {
            System.out.println("Este psicólogo já tem um horário começando nesse momento.");
            return;
        }
        HorarioAtendimento horario = new HorarioAtendimento(clinica, psicologo, inicio);
        validacao.validar(null, horario); // vínculo e data futura (lança AgendamentoException)
        dao.salvar(horario);
        System.out.println("Horário cadastrado com id " + horario.getId() + ".");
    }

    @Override
    protected void listar() {
        for (HorarioAtendimento h : dao.listarPorData()) {
            System.out.println(h + (h.isReservado() ? "  [RESERVADO]" : "  [livre]"));
        }
    }

    @Override
    protected void buscar() {
        HorarioAtendimento horario = dao.buscarPorId(Console.lerId("Id do horário: "));
        if (horario == null) {
            System.out.println("Horário não encontrado.");
            return;
        }
        System.out.println(horario);
        System.out.println("   Situação: " + (horario.isReservado() ? "reservado" : "livre")
                + " | Consultas no histórico: " + dao.contarConsultas(horario.getId()));
    }

    @Override
    protected void atualizar() {
        HorarioAtendimento horario = dao.buscarPorId(Console.lerId("Id do horário: "));
        if (horario == null) {
            System.out.println("Horário não encontrado.");
            return;
        }
        if (horario.isReservado()) {
            throw new AgendamentoException("Horário reservado não pode ser alterado."); // regra 8
        }
        LocalDateTime inicio = Console.lerDataHoraOpcional("Novo início", horario.getInicio());
        if (dao.existe(horario.getPsicologo(), inicio, horario.getId())) {
            System.out.println("Este psicólogo já tem um horário começando nesse momento.");
            return;
        }
        horario.setInicio(inicio);
        validacao.validar(null, horario);
        dao.atualizar(horario);
        System.out.println("Horário atualizado.");
    }

    @Override
    protected void excluir() {
        Long id = Console.lerId("Id do horário: ");
        if (!Console.confirmar("Confirma a exclusão?")) {
            return;
        }
        // HorarioDAO.remover aplica a regra 8 (reservado) e bloqueia horário com consultas no histórico.
        System.out.println(dao.remover(id) ? "Horário excluído." : "Horário não encontrado.");
    }
}
