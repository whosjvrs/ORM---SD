package consultas.app;

import consultas.agenda.Agenda;
import consultas.dao.JPAUtil;
import consultas.facade.AgendamentoFacade;
import consultas.notificacao.NotificadorPaciente;
import consultas.notificacao.PublicadorAgendamento;
import consultas.validacao.ValidadorAgendamento;
import consultas.validacao.ValidadorConflitoPaciente;
import consultas.validacao.ValidadorDataFutura;
import consultas.validacao.ValidadorDisponibilidade;
import consultas.validacao.ValidadorVinculo;

public class Main {
    public static void main(String[] args) {
        try {
            // Abre a conexão (cria banco e tabelas, se preciso) e insere os dados de exemplo se estiver vazio.
            DadosExemplo.carregarSeVazio();
        } catch (RuntimeException e) {
            System.out.println("Não foi possível conectar ao MySQL. Confira se o serviço está rodando e se"
                    + " usuário/senha no src/META-INF/persistence.xml estão corretos.");
            System.out.println("Detalhe: " + e.getMessage());
            JPAUtil.fechar();
            return;
        }

        Agenda agenda = new Agenda();

        // Chain of Responsibility: vínculo -> data futura -> disponibilidade -> conflito do paciente (novo no RA3)
        ValidadorAgendamento cadeia = new ValidadorVinculo();
        cadeia.setProximo(new ValidadorDataFutura())
                .setProximo(new ValidadorDisponibilidade())
                .setProximo(new ValidadorConflitoPaciente());

        // Observer: cria o publicador e inscreve o notificador
        PublicadorAgendamento publicador = new PublicadorAgendamento();
        NotificadorPaciente notificador = new NotificadorPaciente();
        publicador.inscrever(notificador);

        // Facade: recebe as peças pelo construtor; os dados agora vêm do banco pelos DAOs
        AgendamentoFacade facade = new AgendamentoFacade(agenda, cadeia, publicador);

        try {
            new MenuConsole(facade, publicador, notificador).iniciar();
        } finally {
            JPAUtil.fechar(); // fecha a conexão com o banco ao sair
        }
    }
}
