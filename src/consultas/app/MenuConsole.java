package consultas.app;

import consultas.app.cadastro.MenuCadastros;
import consultas.estrategia.EstrategiaOrdenacao;
import consultas.estrategia.OrdemCronologica;
import consultas.estrategia.PreferenciaTurno;
import consultas.estrategia.Turno;
import consultas.facade.AgendamentoFacade;
import consultas.modelo.AgendamentoException;
import consultas.modelo.Clinica;
import consultas.modelo.Consulta;
import consultas.modelo.HorarioAtendimento;
import consultas.modelo.Paciente;
import consultas.modelo.Psicologo;
import consultas.notificacao.NotificadorPaciente;
import consultas.notificacao.PublicadorAgendamento;

import java.util.List;

/** Menu principal (camada de apresentação). Só conversa com a fachada e com o menu de cadastros. */
public class MenuConsole {
    private final AgendamentoFacade facade;
    private final PublicadorAgendamento publicador;
    private final NotificadorPaciente notificador;
    private final MenuCadastros cadastros;

    public MenuConsole(AgendamentoFacade facade, PublicadorAgendamento publicador,
                       NotificadorPaciente notificador) {
        this.facade = facade;
        this.publicador = publicador;
        this.notificador = notificador;
        this.cadastros = new MenuCadastros(facade);
    }

    public void iniciar() {
        int opcao;
        do {
            System.out.println();
            System.out.println("===== Agendamento de Consultas =====");
            System.out.println("1 - Agendar consulta");
            System.out.println("2 - Listar consultas");
            System.out.println("3 - Cancelar consulta");
            System.out.println("4 - Histórico de notificações de uma consulta");
            System.out.println("5 - Ligar/desligar notificação ao paciente");
            System.out.println("6 - Demonstrar validação: tentar agendar de novo o horário da última consulta");
            System.out.println("7 - Cadastros (CRUD)");
            System.out.println("0 - Sair");
            opcao = lerNumero("Escolha uma opção: ", 0, 7);

            try {
                switch (opcao) {
                    case 1 -> agendarConsulta();
                    case 2 -> listarConsultas();
                    case 3 -> cancelarConsulta();
                    case 4 -> historicoNotificacoes();
                    case 5 -> alternarNotificacao();
                    case 6 -> demonstrarValidacao();
                    case 7 -> cadastros.exibir();
                    case 0 -> System.out.println("Encerrando o sistema.");
                }
            } catch (AgendamentoException e) {
                System.out.println("Erro: " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("Erro ao acessar o banco: " + e.getMessage());
            }
        } while (opcao != 0);
    }

    // Opção 1: fluxo do RA2 — paciente, clínica, psicólogo, ordenação, horário
    private void agendarConsulta() {
        Paciente paciente = Console.escolher("Pacientes", facade.listarPacientes());
        if (paciente == null) {
            return;
        }
        Clinica clinica = Console.escolher("Clínicas", facade.listarClinicas());
        if (clinica == null) {
            return;
        }
        List<Psicologo> psicologos = facade.listarPsicologosDaClinica(clinica);
        if (psicologos.isEmpty()) {
            System.out.println("Nenhum psicólogo atende nesta clínica.");
            return;
        }
        Psicologo psicologo = Console.escolher("Psicólogos da " + clinica.getNome(), psicologos);

        EstrategiaOrdenacao estrategia = escolherEstrategia();

        List<HorarioAtendimento> horarios = facade.listarHorarios(clinica, psicologo, estrategia);
        if (horarios.isEmpty()) {
            System.out.println("Nenhum horário disponível.");
            return;
        }
        HorarioAtendimento horario = Console.escolher("Horários disponíveis", horarios);

        facade.agendar(paciente, horario);
    }

    // O menu escolhe a estratégia e entrega à fachada (Strategy)
    private EstrategiaOrdenacao escolherEstrategia() {
        System.out.println("\nOrdenar horários por:");
        System.out.println("1 - Ordem cronológica");
        System.out.println("2 - Preferência de turno");
        int tipo = lerNumero("Escolha a ordenação: ", 1, 2);

        if (tipo == 1) {
            return new OrdemCronologica();
        }

        Turno[] turnos = Turno.values();
        System.out.println("\nTurno preferido:");
        for (int i = 0; i < turnos.length; i++) {
            System.out.println((i + 1) + " - " + turnos[i]);
        }
        Turno turno = turnos[lerNumero("Escolha o turno: ", 1, turnos.length) - 1];
        return new PreferenciaTurno(turno);
    }

    // Opção 2
    private void listarConsultas() {
        List<Consulta> consultas = facade.listarConsultas();
        if (consultas.isEmpty()) {
            System.out.println("Nenhuma consulta registrada.");
            return;
        }
        System.out.println("\nConsultas registradas:");
        for (Consulta c : consultas) {
            System.out.println(c);
        }
    }

    // Opção 3: só as consultas AGENDADAS podem ser canceladas
    private void cancelarConsulta() {
        Consulta consulta = Console.escolher("Consultas agendadas", facade.listarConsultasAgendadas());
        if (consulta != null && Console.confirmar("Confirma o cancelamento?")) {
            facade.cancelar(consulta);
        }
    }

    // Opção 4: notificações gravadas pelo Observer
    private void historicoNotificacoes() {
        Consulta consulta = Console.escolher("Consultas", facade.listarConsultas());
        if (consulta != null) {
            System.out.println("\nNotificações da consulta " + consulta.getId() + ":");
            Console.imprimir(facade.listarNotificacoes(consulta));
        }
    }

    // Opção 5: demonstração do Observer (remover / inscrever)
    private void alternarNotificacao() {
        if (publicador.estaInscrito(notificador)) {
            publicador.remover(notificador);
            System.out.println("Notificação ao paciente DESLIGADA.");
        } else {
            publicador.inscrever(notificador);
            System.out.println("Notificação ao paciente LIGADA.");
        }
    }

    // Opção 6: demonstração do Chain (para no ValidadorDisponibilidade)
    private void demonstrarValidacao() {
        List<Consulta> consultas = facade.listarConsultasAgendadas();
        if (consultas.isEmpty()) {
            System.out.println("Agende uma consulta primeiro (opção 1).");
            return;
        }
        Consulta ultima = consultas.get(consultas.size() - 1);
        System.out.println("Tentando agendar de novo o horário: " + ultima.getHorario());
        facade.agendar(ultima.getPaciente(), ultima.getHorario());
    }

    private int lerNumero(String pergunta, int min, int max) {
        return Console.lerNumero(pergunta, min, max);
    }
}
