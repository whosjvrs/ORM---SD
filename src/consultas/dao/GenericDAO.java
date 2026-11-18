package consultas.dao;

import consultas.modelo.AgendamentoException;

import java.util.List;

/**
 * CRUD comum a todas as entidades.
 *
 * Uso direto:      new GenericDAO<>(Clinica.class).listarTodos();
 * Ou por herança:  class ConsultaDAO extends GenericDAO<Consulta> { ...consultas JPQL próprias... }
 */
public class GenericDAO<T> {

    protected final Class<T> classe;

    public GenericDAO(Class<T> classe) {
        this.classe = classe;
    }

    /** CREATE: grava um objeto novo (INSERT). O id é preenchido pelo banco. */
    public T salvar(T entidade) {
        return JPAUtil.emTransacao(em -> {
            em.persist(entidade);
            return entidade;
        });
    }

    /** READ: busca pela chave primária. Devolve null se não existir. */
    public T buscarPorId(Long id) {
        return JPAUtil.consultar(em -> em.find(classe, id));
    }

    /** READ: todos os registros. Para Pessoa, traz pacientes e psicólogos (consulta polimórfica). */
    public List<T> listarTodos() {
        String jpql = "SELECT e FROM " + classe.getSimpleName() + " e ORDER BY e.id";
        return JPAUtil.consultar(em -> em.createQuery(jpql, classe).getResultList());
    }

    /** UPDATE: grava as alterações de um objeto que já existe (UPDATE) e devolve a versão gerenciada. */
    public T atualizar(T entidade) {
        return JPAUtil.emTransacao(em -> em.merge(entidade));
    }

    /** DELETE: remove pelo id. Devolve false se o registro não existir. */
    public boolean remover(Long id) {
        return JPAUtil.emTransacao(em -> {
            T entidade = em.find(classe, id);
            if (entidade == null) {
                return false;
            }
            em.remove(entidade);
            return true;
        });
    }

    /** Quantidade de registros (usado para saber se o banco está vazio). */
    public long contar() {
        String jpql = "SELECT COUNT(e) FROM " + classe.getSimpleName() + " e";
        return JPAUtil.consultar(em -> em.createQuery(jpql, Long.class).getSingleResult());
    }

    // ---------- Auxiliares usados pelos DAOs específicos ----------

    /**
     * Regra de campo único (regra 10): existe OUTRO registro com atributo = valor?
     * ignorarId é o próprio registro no "atualizar" (no "cadastrar", passe null).
     * Ex.: existeOutro("cpf", "111.111.111-11", null)
     */
    protected boolean existeOutro(String atributo, Object valor, Long ignorarId) {
        String jpql = "SELECT COUNT(e) FROM " + classe.getSimpleName() + " e WHERE e." + atributo + " = :valor"
                + " AND (:ignorar IS NULL OR e.id <> :ignorar)";
        return JPAUtil.consultar(em -> em.createQuery(jpql, Long.class)
                .setParameter("valor", valor)
                .setParameter("ignorar", ignorarId)
                .getSingleResult()) > 0;
    }

    /** Executa uma contagem JPQL que tem um único parâmetro, chamado :id. */
    protected static long contar(String jpql, Long id) {
        return JPAUtil.consultar(em -> em.createQuery(jpql, Long.class)
                .setParameter("id", id)
                .getSingleResult());
    }

    /**
     * Regra de exclusão (regra 9): se a contagem for maior que zero, impede a operação.
     * A mensagem recebe a quantidade no lugar de %d.
     * Ex.: impedirSeExistir("SELECT COUNT(c) FROM Consulta c WHERE c.paciente.id = :id", id,
     *                       "O paciente tem %d consulta(s) e não pode ser excluído.")
     */
    protected static void impedirSeExistir(String jpqlContagem, Long id, String mensagem) {
        long quantidade = contar(jpqlContagem, id);
        if (quantidade > 0) {
            throw new AgendamentoException(String.format(mensagem, quantidade));
        }
    }
}
