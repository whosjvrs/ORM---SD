package consultas.dao;

import consultas.modelo.Consulta;
import consultas.modelo.HorarioAtendimento;
import consultas.modelo.Paciente;
import consultas.modelo.StatusConsulta;

import java.time.LocalDateTime;
import java.util.List;

/** DAO específico de Consulta: registro e cancelamento em transação, e consultas por paciente e status. */
public class ConsultaDAO extends GenericDAO<Consulta> {

    public ConsultaDAO() {
        super(Consulta.class);
    }

    public List<Consulta> listarPorPaciente(Paciente paciente) {
        return JPAUtil.consultar(em -> em.createQuery(
                        "SELECT c FROM Consulta c WHERE c.paciente.id = :id ORDER BY c.horario.inicio", Consulta.class)
                .setParameter("id", paciente.getId())
                .getResultList());
    }

    /** Consultas com status AGENDADA (as que podem ser canceladas). */
    public List<Consulta> listarAtivas() {
        return JPAUtil.consultar(em -> em.createQuery(
                        "SELECT c FROM Consulta c WHERE c.status = :status ORDER BY c.horario.inicio", Consulta.class)
                .setParameter("status", StatusConsulta.AGENDADA)
                .getResultList());
    }

    /** Usado pelo ValidadorConflitoPaciente: o paciente já tem consulta AGENDADA neste mesmo início? */
    public boolean pacienteTemConsultaNoHorario(Paciente paciente, LocalDateTime inicio) {
        long total = JPAUtil.consultar(em -> em.createQuery(
                        "SELECT COUNT(c) FROM Consulta c WHERE c.paciente.id = :paciente"
                                + " AND c.horario.inicio = :inicio AND c.status = :status", Long.class)
                .setParameter("paciente", paciente.getId())
                .setParameter("inicio", inicio)
                .setParameter("status", StatusConsulta.AGENDADA)
                .getSingleResult());
        return total > 0;
    }

    /**
     * Regra 4: reserva o horário e grava a consulta em UMA transação.
     * Os objetos são buscados de novo com em.find (ficam gerenciados): o reservar() vira UPDATE
     * no commit sem chamar merge. Se qualquer passo falhar, o rollback desfaz os dois.
     */
    public Consulta registrar(Paciente paciente, HorarioAtendimento horario) {
        return JPAUtil.emTransacao(em -> {
            HorarioAtendimento h = em.find(HorarioAtendimento.class, horario.getId());
            Paciente p = em.find(Paciente.class, paciente.getId());
            h.reservar(); // lança AgendamentoException se outro processo já reservou
            Consulta consulta = new Consulta(p, h);
            em.persist(consulta);
            return consulta;
        });
    }

    /** Regra 7: cancela (status CANCELADA) e libera o horário, na mesma transação. */
    public Consulta cancelar(Long id) {
        return JPAUtil.emTransacao(em -> {
            Consulta consulta = em.find(Consulta.class, id);
            consulta.cancelar(); // lança AgendamentoException se não estiver AGENDADA
            return consulta;
        });
    }

    /** Muda para REALIZADA. O horário continua reservado, porque a consulta aconteceu. */
    public Consulta marcarRealizada(Long id) {
        return JPAUtil.emTransacao(em -> {
            Consulta consulta = em.find(Consulta.class, id);
            consulta.marcarRealizada();
            return consulta;
        });
    }

    /**
     * Exclui a consulta. As notificações vão junto (cascade + orphanRemoval em Consulta.notificacoes).
     * Se ela ainda estava AGENDADA, o horário volta a ficar livre.
     */
    @Override
    public boolean remover(Long id) {
        return JPAUtil.emTransacao(em -> {
            Consulta consulta = em.find(Consulta.class, id);
            if (consulta == null) {
                return false;
            }
            if (consulta.isAtiva()) {
                consulta.getHorario().liberar();
            }
            em.remove(consulta);
            return true;
        });
    }
}
