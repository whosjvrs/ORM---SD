package consultas.dao;

import consultas.modelo.Consulta;
import consultas.modelo.Notificacao;
import consultas.modelo.TipoNotificacao;

import java.util.List;

/** DAO específico de Notificacao. Usado pelo Observer (NotificadorPaciente) e pelo menu. */
public class NotificacaoDAO extends GenericDAO<Notificacao> {

    public NotificacaoDAO() {
        super(Notificacao.class);
    }

    public List<Notificacao> listarPorConsulta(Consulta consulta) {
        return JPAUtil.consultar(em -> em.createQuery(
                        "SELECT n FROM Notificacao n WHERE n.consulta.id = :id ORDER BY n.dataHora", Notificacao.class)
                .setParameter("id", consulta.getId())
                .getResultList());
    }

    /** Grava uma notificação ligada à consulta. A consulta é buscada de novo para ficar gerenciada. */
    public Notificacao registrar(Long consultaId, TipoNotificacao tipo, String mensagem) {
        return JPAUtil.emTransacao(em -> {
            Consulta consulta = em.find(Consulta.class, consultaId);
            Notificacao notificacao = new Notificacao(consulta, tipo, mensagem);
            em.persist(notificacao);
            return notificacao;
        });
    }

    /**
     * Remove pela consulta: tirar a notificação da lista com orphanRemoval = true já gera o DELETE.
     * Assim a lista Consulta.notificacoes e o banco ficam coerentes.
     */
    @Override
    public boolean remover(Long id) {
        return JPAUtil.emTransacao(em -> {
            Notificacao notificacao = em.find(Notificacao.class, id);
            if (notificacao == null) {
                return false;
            }
            notificacao.getConsulta().removerNotificacao(notificacao);
            em.remove(notificacao);
            return true;
        });
    }
}
