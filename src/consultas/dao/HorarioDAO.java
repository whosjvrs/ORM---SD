package consultas.dao;

import consultas.modelo.AgendamentoException;
import consultas.modelo.Clinica;
import consultas.modelo.HorarioAtendimento;
import consultas.modelo.Psicologo;

import java.time.LocalDateTime;
import java.util.List;

/** DAO específico de HorarioAtendimento: consultas JPQL usadas pela Agenda e pelo menu de horários. */
public class HorarioDAO extends GenericDAO<HorarioAtendimento> {

    public HorarioDAO() {
        super(HorarioAtendimento.class);
    }

    /**
     * Horários livres e futuros de um psicólogo em uma clínica.
     * Substitui o filtro em memória do RA2. Compara pelo id, não pela referência do objeto.
     */
    public List<HorarioAtendimento> listarDisponiveis(Clinica clinica, Psicologo psicologo) {
        return JPAUtil.consultar(em -> em.createQuery(
                        "SELECT h FROM HorarioAtendimento h"
                                + " WHERE h.clinica.id = :clinica AND h.psicologo.id = :psicologo"
                                + " AND h.reservado = false AND h.inicio > :agora", HorarioAtendimento.class)
                .setParameter("clinica", clinica.getId())
                .setParameter("psicologo", psicologo.getId())
                .setParameter("agora", LocalDateTime.now())
                .getResultList());
    }

    /** Todos os horários livres e futuros, de qualquer clínica (usado no cadastro de consultas). */
    public List<HorarioAtendimento> listarTodosDisponiveis() {
        return JPAUtil.consultar(em -> em.createQuery(
                        "SELECT h FROM HorarioAtendimento h WHERE h.reservado = false AND h.inicio > :agora"
                                + " ORDER BY h.inicio", HorarioAtendimento.class)
                .setParameter("agora", LocalDateTime.now())
                .getResultList());
    }

    /** Todos os horários em ordem cronológica (o listarTodos do GenericDAO ordena por id). */
    public List<HorarioAtendimento> listarPorData() {
        return JPAUtil.consultar(em -> em.createQuery(
                "SELECT h FROM HorarioAtendimento h ORDER BY h.inicio", HorarioAtendimento.class)
                .getResultList());
    }

    /** Regra de unicidade: um psicólogo não tem dois horários começando no mesmo instante. */
    public boolean existe(Psicologo psicologo, LocalDateTime inicio, Long ignorarId) {
        long total = JPAUtil.consultar(em -> em.createQuery(
                        "SELECT COUNT(h) FROM HorarioAtendimento h"
                                + " WHERE h.psicologo.id = :psicologo AND h.inicio = :inicio"
                                + " AND (:ignorar IS NULL OR h.id <> :ignorar)", Long.class)
                .setParameter("psicologo", psicologo.getId())
                .setParameter("inicio", inicio)
                .setParameter("ignorar", ignorarId)
                .getSingleResult());
        return total > 0;
    }

    /** Quantas consultas (de qualquer status) já usaram este horário. */
    public long contarConsultas(Long horarioId) {
        return JPAUtil.consultar(em -> em.createQuery(
                        "SELECT COUNT(c) FROM Consulta c WHERE c.horario.id = :id", Long.class)
                .setParameter("id", horarioId)
                .getSingleResult());
    }

    /**
     * Regra 8: horário reservado não pode ser excluído. Também bloqueia horário com consultas
     * no histórico (canceladas), porque a chave estrangeira consulta.horario_id impediria.
     */
    @Override
    public boolean remover(Long id) {
        HorarioAtendimento horario = buscarPorId(id);
        if (horario == null) {
            return false;
        }
        if (horario.isReservado()) {
            throw new AgendamentoException("Horário reservado não pode ser excluído. Cancele a consulta antes.");
        }
        long consultas = contarConsultas(id);
        if (consultas > 0) {
            throw new AgendamentoException("O horário tem " + consultas
                    + " consulta(s) no histórico e não pode ser excluído.");
        }
        return super.remover(id);
    }
}
