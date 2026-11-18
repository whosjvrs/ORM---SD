package consultas.notificacao;

import consultas.dao.NotificacaoDAO;
import consultas.modelo.Clinica;
import consultas.modelo.Consulta;
import consultas.modelo.HorarioAtendimento;
import consultas.modelo.Psicologo;
import consultas.modelo.TipoNotificacao;

import java.time.format.DateTimeFormatter;

/**
 * Observer — observador concreto.
 * RA3: além de mostrar o aviso no console, grava uma Notificacao no banco (histórico por consulta).
 * Se o observador for desligado (opção do menu), nenhuma notificação é gravada.
 */
public class NotificadorPaciente implements ObservadorAgendamento {
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final NotificacaoDAO notificacaoDAO = new NotificacaoDAO();

    @Override
    public void aoAgendar(Consulta consulta) {
        String detalhes = detalhes(consulta);
        System.out.println("[Notificação] Olá, " + consulta.getPaciente().getNome()
                + "! Sua consulta está confirmada.");
        System.out.println("   " + detalhes);
        notificacaoDAO.registrar(consulta.getId(), TipoNotificacao.CONFIRMACAO, "Consulta confirmada: " + detalhes);
    }

    @Override
    public void aoCancelar(Consulta consulta) {
        String detalhes = detalhes(consulta);
        System.out.println("[Notificação] Olá, " + consulta.getPaciente().getNome()
                + "! Sua consulta foi cancelada.");
        System.out.println("   " + detalhes);
        notificacaoDAO.registrar(consulta.getId(), TipoNotificacao.CANCELAMENTO, "Consulta cancelada: " + detalhes);
    }

    private String detalhes(Consulta consulta) {
        HorarioAtendimento horario = consulta.getHorario();
        Clinica clinica = consulta.getClinica();
        Psicologo psicologo = consulta.getPsicologo();
        return clinica.getNome() + " | " + psicologo.getNome() + " (CRP " + psicologo.getCrp() + ") | "
                + horario.getInicio().format(DATA) + " às " + horario.getInicio().format(HORA)
                + " (" + HorarioAtendimento.DURACAO_MINUTOS + " min)";
    }
}
