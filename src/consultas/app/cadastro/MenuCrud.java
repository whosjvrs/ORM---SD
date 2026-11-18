package consultas.app.cadastro;

import consultas.app.Console;
import consultas.dao.GenericDAO;
import consultas.modelo.AgendamentoException;

/**
 * Base de todos os menus de cadastro (CRUD). Padrão Template Method:
 * exibir() é o método-modelo e as operações comuns (listar, buscar, atualizar, excluir)
 * já estão prontas aqui, usando o DAO. Cada subclasse implementa só o que é dela:
 *
 *   cadastrar()     - perguntar os campos e salvar
 *   detalhar(obj)   - o que mostrar no "buscar" (padrão: o toString)
 *   editar(obj)     - perguntar os campos que mudam; devolve false para não gravar
 *
 * T é a entidade e D é o DAO dela (o genérico ou um específico, com consultas e regras próprias).
 */
public abstract class MenuCrud<T, D extends GenericDAO<T>> {

    private final String titulo;
    private final String rotulo;   // ex.: "do paciente", "da clínica" (usado em "Id do paciente: ")
    protected final D dao;

    protected MenuCrud(String titulo, String rotulo, D dao) {
        this.titulo = titulo;
        this.rotulo = rotulo;
        this.dao = dao;
    }

    public String getTitulo() {
        return titulo;
    }

    public final void exibir() {
        int opcao;
        do {
            System.out.println("\n===== " + titulo + " =====");
            System.out.println("1 - Cadastrar");
            System.out.println("2 - Listar");
            System.out.println("3 - Buscar por id");
            System.out.println("4 - Atualizar");
            System.out.println("5 - Excluir");
            System.out.println("0 - Voltar");
            opcao = Console.lerNumero("Escolha uma opção: ", 0, 5);
            try {
                switch (opcao) {
                    case 1 -> cadastrar();
                    case 2 -> listar();
                    case 3 -> buscar();
                    case 4 -> atualizar();
                    case 5 -> excluir();
                    default -> { }
                }
            } catch (AgendamentoException e) {
                System.out.println("Erro: " + e.getMessage());
            } catch (RuntimeException e) {
                // Ex.: violação de campo único ou de chave estrangeira no banco.
                System.out.println("Erro ao acessar o banco: " + causaRaiz(e).getMessage());
            }
        } while (opcao != 0);
    }

    // ---------- O que cada menu implementa ----------

    protected abstract void cadastrar();

    /** Pergunta os novos valores e altera o objeto. Devolve false para cancelar (nada é gravado). */
    protected abstract boolean editar(T entidade);

    /** O que o "buscar" mostra. Os menus sobrescrevem para mostrar também os relacionamentos. */
    protected void detalhar(T entidade) {
        System.out.println(entidade);
    }

    /** Chamado depois do UPDATE, para gravar o que o merge não cobre (ex.: vínculos N:N do psicólogo). */
    protected void depoisDeAtualizar(T entidade) {
    }

    // ---------- Operações comuns (podem ser sobrescritas) ----------

    protected void listar() {
        Console.imprimir(dao.listarTodos());
    }

    protected void buscar() {
        T entidade = lerExistente();
        if (entidade != null) {
            detalhar(entidade);
        }
    }

    /** O objeto vem desanexado do DAO; depois de editado, o merge (dao.atualizar) grava o UPDATE. */
    protected void atualizar() {
        T entidade = lerExistente();
        if (entidade != null && editar(entidade)) {
            dao.atualizar(entidade);
            depoisDeAtualizar(entidade);
            System.out.println("Registro atualizado.");
        }
    }

    /** As regras que impedem a exclusão ficam no remover() do DAO específico (lança AgendamentoException). */
    protected void excluir() {
        Long id = Console.lerId("Id " + rotulo + ": ");
        if (Console.confirmar("Confirma a exclusão?")) {
            System.out.println(dao.remover(id) ? "Registro excluído." : "Nenhum registro com esse id.");
        }
    }

    /** Lê um id e busca no banco. Devolve null (e avisa) se não existir. */
    protected T lerExistente() {
        T entidade = dao.buscarPorId(Console.lerId("Id " + rotulo + ": "));
        if (entidade == null) {
            System.out.println("Nenhum registro com esse id.");
        }
        return entidade;
    }

    private static Throwable causaRaiz(Throwable e) {
        Throwable atual = e;
        while (atual.getCause() != null && atual.getCause() != atual) {
            atual = atual.getCause();
        }
        return atual;
    }
}
