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
 * Reaproveita dois elos da Chain do RA2 para validar um horário novo ou alterado:
 * o psicólogo precisa atender na clínica e o horário precisa ser futuro.
 */
public class MenuHorario extends MenuCrud<HorarioAtendimento, HorarioDAO> {

    private final GenericDAO<Clinica> clinicaDAO = new GenericDAO<>(Clinica.class);
    private final PsicologoDAO psicologoDAO = new PsicologoDAO();
    private final ValidadorAgendamento validacao = new ValidadorVinculo();

    public MenuHorario() {
        super("Horários de atendimento", "do horário", new HorarioDAO());
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
    protected void detalhar(HorarioAtendimento horario) {
        System.out.println(horario);
        System.out.println("   Situação: " + (horario.isReservado() ? "reservado" : "livre")
                + " | Consultas no histórico: " + dao.contarConsultas(horario.getId()));
    }

    @Override
    protected boolean editar(HorarioAtendimento horario) {
        if (horario.isReservado()) {
            throw new AgendamentoException("Horário reservado não pode ser alterado."); // regra 8
        }
        LocalDateTime inicio = Console.lerDataHoraOpcional("Novo início", horario.getInicio());
        if (dao.existe(horario.getPsicologo(), inicio, horario.getId())) {
            System.out.println("Este psicólogo já tem um horário começando nesse momento.");
            return false;
        }
        horario.setInicio(inicio);
        validacao.validar(null, horario);
        return true;
    }
}
