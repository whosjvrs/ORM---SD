package consultas.app.cadastro;

import consultas.app.Console;

import java.util.List;

/** Submenu com o CRUD de cada entidade mapeada. */
public class MenuCadastros {

    private final List<MenuCrud> menus = List.of(
            new MenuPaciente(),
            new MenuPsicologo(),
            new MenuPessoa(),
            new MenuClinica(),
            new MenuEspecialidade(),
            new MenuHorario(),
            new MenuConsulta(),
            new MenuNotificacao()
    );

    public void exibir() {
        int opcao;
        do {
            System.out.println("\n===== Cadastros =====");
            for (int i = 0; i < menus.size(); i++) {
                System.out.println((i + 1) + " - " + menus.get(i).getTitulo());
            }
            System.out.println("0 - Voltar");
            opcao = Console.lerNumero("Escolha uma opção: ", 0, menus.size());
            if (opcao > 0) {
                menus.get(opcao - 1).exibir();
            }
        } while (opcao != 0);
    }
}
