package consultas.app;

import consultas.modelo.HorarioAtendimento;
import consultas.estrategia.EstrategiaOrdenacao;
import consultas.estrategia.OrdemCronologica;
import consultas.estrategia.PreferenciaTurno;
import consultas.estrategia.Turno;
import consultas.facade.AgendamentoFacade;
import consultas.modelo.AgendamentoException;
import consultas.modelo.Clinica;
import consultas.modelo.Consulta;
import consultas.modelo.Paciente;
import consultas.modelo.Psicologo;
import consultas.notificacao.NotificadorPaciente;
import consultas.notificacao.PublicadorAgendamento;

import consultas.app.cadastro.MenuCadastros;

import java.util.List;

public class MenuConsole {
    private final AgendamentoFacade facade;
    private final PublicadorAgendamento publicador;
    private final NotificadorPaciente notificador;
    private final MenuCadastros cadastros = new MenuCadastros();

    public MenuConsole(AgendamentoFacade facade, PublicadorAgendamento publicador,
                       NotificadorPaciente notificador) {
        this.facade = facade;
        this.publicador = publicador;
        this.notificador = notificador;
    }

    public void iniciar() {
        int opcao;
        do {
            System.out.println();
            System.out.println("===== Agendamento de Consultas =====");
            System.out.println("1 - Agendar consulta");
            System.out.println("2 - Listar consultas");
            System.out.println("3 - Ligar/desligar notificação ao paciente");
            System.out.println("4 - Demonstrar validação: tentar agendar de novo o horário da última consulta");
            System.out.println("5 - Cadastros (CRUD)");
            System.out.println("0 - Sair");
            opcao = lerNumero("Escolha uma opção: ", 0, 5);

            try {
                switch (opcao) {
                    case 1 -> agendarConsulta();
                    case 2 -> listarConsultas();
                    case 3 -> alternarNotificacao();
                    case 4 -> demonstrarValidacao();
                    case 5 -> cadastros.exibir();
                    case 0 -> System.out.println("Encerrando o sistema.");
                }
            } catch (AgendamentoException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        } while (opcao != 0);
    }

    // Opção 1: segue o fluxo da seção 2 do guia
    private void agendarConsulta() {
        List<Paciente> pacientes = facade.listarPacientes();
        System.out.println("\nPacientes:");
        for (int i = 0; i < pacientes.size(); i++) {
            System.out.println((i + 1) + " - " + pacientes.get(i).getNome());
        }
        Paciente paciente = pacientes.get(lerNumero("Escolha o paciente: ", 1, pacientes.size()) - 1);

        List<Clinica> clinicas = facade.listarClinicas();
        System.out.println("\nClínicas:");
        for (int i = 0; i < clinicas.size(); i++) {
            System.out.println((i + 1) + " - " + clinicas.get(i).getNome());
        }
        Clinica clinica = clinicas.get(lerNumero("Escolha a clínica: ", 1, clinicas.size()) - 1);

        List<Psicologo> psicologos = facade.listarPsicologosDaClinica(clinica);
        if (psicologos.isEmpty()) {
            System.out.println("Nenhum psicólogo atende nesta clínica.");
            return;
        }
        System.out.println("\nPsicólogos da " + clinica.getNome() + ":");
        for (int i = 0; i < psicologos.size(); i++) {
            System.out.println((i + 1) + " - " + psicologos.get(i).getNome());
        }
        Psicologo psicologo = psicologos.get(lerNumero("Escolha o psicólogo: ", 1, psicologos.size()) - 1);

        EstrategiaOrdenacao estrategia = escolherEstrategia();

        List<HorarioAtendimento> horarios = facade.listarHorarios(clinica, psicologo, estrategia);
        if (horarios.isEmpty()) {
            System.out.println("Nenhum horário disponível.");
            return;
        }
        System.out.println("\nHorários disponíveis:");
        for (int i = 0; i < horarios.size(); i++) {
            System.out.println((i + 1) + " - " + horarios.get(i));
        }
        HorarioAtendimento horario = horarios.get(lerNumero("Escolha o horário: ", 1, horarios.size()) - 1);

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

    // Opção 3: demonstração do Observer (remover / inscrever)
    private void alternarNotificacao() {
        if (publicador.estaInscrito(notificador)) {
            publicador.remover(notificador);
            System.out.println("Notificação ao paciente DESLIGADA.");
        } else {
            publicador.inscrever(notificador);
            System.out.println("Notificação ao paciente LIGADA.");
        }
    }

    // Opção 4: demonstração do Chain (para no ValidadorDisponibilidade)
    private void demonstrarValidacao() {
        List<Consulta> consultas = facade.listarConsultas();
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
