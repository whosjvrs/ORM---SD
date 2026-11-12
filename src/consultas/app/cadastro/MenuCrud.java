package consultas.app.cadastro;

import consultas.app.Console;
import consultas.modelo.AgendamentoException;

/**
 * Esqueleto comum dos menus de cadastro (CRUD) de cada entidade.
 *
 * O laço do menu é sempre o mesmo; cada subclasse só implementa as 5 operações.
 * (É o padrão Template Method: exibir() é o método-modelo.)
 */
public abstract class MenuCrud {

    private final String titulo;

    protected MenuCrud(String titulo) {
        this.titulo = titulo;
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

    protected abstract void cadastrar();

    protected abstract void listar();

    protected abstract void buscar();

    protected abstract void atualizar();

    protected abstract void excluir();

    /** Mensagem usada enquanto uma operação não foi implementada. */
    protected void emConstrucao() {
        System.out.println("(em construção)");
    }

    private static Throwable causaRaiz(Throwable e) {
        Throwable atual = e;
        while (atual.getCause() != null && atual.getCause() != atual) {
            atual = atual.getCause();
        }
        return atual;
    }
}
