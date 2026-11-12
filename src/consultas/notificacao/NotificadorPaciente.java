package consultas.notificacao;

import consultas.modelo.HorarioAtendimento;
import consultas.modelo.Clinica;
import consultas.modelo.Consulta;
import consultas.modelo.Psicologo;

import java.time.format.DateTimeFormatter;

public class NotificadorPaciente implements ObservadorAgendamento {
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public void aoAgendar(Consulta consulta) {
        HorarioAtendimento horario = consulta.getHorario();
        Clinica clinica = consulta.getClinica();
        Psicologo psicologo = consulta.getPsicologo();

        System.out.println("[Notificação] Olá, " + consulta.getPaciente().getNome()
                + "! Sua consulta está confirmada.");
        System.out.println("   Clínica: " + clinica.getNome()
                + " | Psicólogo: " + psicologo.getNome()
                + " (CRP " + psicologo.getCrp() + ")");
        System.out.println("   Data: " + horario.getInicio().format(DATA)
                + " às " + horario.getInicio().format(HORA)
                + " (" + HorarioAtendimento.DURACAO_MINUTOS + " min)");
    }
}