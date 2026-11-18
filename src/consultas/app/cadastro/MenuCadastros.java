package consultas.app.cadastro;

import consultas.app.Console;
import consultas.facade.AgendamentoFacade;

import java.util.List;

/** Submenu com o CRUD de cada entidade mapeada. */
public class MenuCadastros {

    private final List<MenuCrud> menus;

    /** A fachada é usada pelo CRUD de Consulta, para que agendar e cancelar passem pela Chain e pelo Observer. */
    public MenuCadastros(AgendamentoFacade facade) {
        MenuPaciente pacientes = new MenuPaciente();
        MenuPsicologo psicologos = new MenuPsicologo();
        menus = List.of(
                pacientes,
                psicologos,
                new MenuPessoa(pacientes, psicologos),
                new MenuClinica(),
                new MenuEspecialidade(),
                new MenuHorario(),
                new MenuConsulta(facade),
                new MenuNotificacao()
        );
    }

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
