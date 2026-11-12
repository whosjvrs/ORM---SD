package consultas.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.function.Function;

/**
 * Cria uma única vez o EntityManagerFactory da unidade "clinicaPU"
 * (configurada em src/META-INF/persistence.xml) e entrega EntityManagers.
 */
public final class JPAUtil {

    private static final String UNIDADE = "clinicaPU";
    private static EntityManagerFactory fabrica;

    private JPAUtil() {
    }

    public static synchronized EntityManager getEntityManager() {
        if (fabrica == null) {
            fabrica = Persistence.createEntityManagerFactory(UNIDADE);
        }
        return fabrica.createEntityManager();
    }

    /**
     * Executa um trecho dentro de uma transação: begin, commit e, se der erro, rollback.
     * O EntityManager é sempre fechado no final.
     *
     * Exemplo: JPAUtil.emTransacao(em -> { em.persist(clinica); return clinica; });
     */
    public static <R> R emTransacao(Function<EntityManager, R> trabalho) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            R resultado = trabalho.apply(em);
            tx.commit();
            return resultado;
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /** Executa uma consulta (sem transação) e fecha o EntityManager no final. */
    public static <R> R consultar(Function<EntityManager, R> consulta) {
        EntityManager em = getEntityManager();
        try {
            return consulta.apply(em);
        } finally {
            em.close();
        }
    }

    /** Chamado no fim do programa (Main). */
    public static synchronized void fechar() {
        if (fabrica != null && fabrica.isOpen()) {
            fabrica.close();
        }
    }
}
