package consultas.app;

import consultas.agenda.Agenda;
import consultas.dao.JPAUtil;
import consultas.facade.AgendamentoFacade;
import consultas.notificacao.NotificadorPaciente;
import consultas.notificacao.PublicadorAgendamento;
import consultas.validacao.ValidadorAgendamento;
import consultas.validacao.ValidadorDataFutura;
import consultas.validacao.ValidadorDisponibilidade;
import consultas.validacao.ValidadorVinculo;

public class Main {
    public static void main(String[] args) {
        Agenda agenda = new Agenda();
        DadosExemplo dados = new DadosExemplo(agenda);

        // Chain of Responsibility: monta a corrente vínculo -> data futura -> disponibilidade
        ValidadorAgendamento cadeia = new ValidadorVinculo();
        cadeia.setProximo(new ValidadorDataFutura())
                .setProximo(new ValidadorDisponibilidade());

        // Observer: cria o publicador e inscreve o notificador
        PublicadorAgendamento publicador = new PublicadorAgendamento();
        NotificadorPaciente notificador = new NotificadorPaciente();
        publicador.inscrever(notificador);

        // Facade: recebe todas as peças pelo construtor
        AgendamentoFacade facade = new AgendamentoFacade(dados, agenda, cadeia, publicador);

        try {
            new MenuConsole(facade, publicador, notificador).iniciar();
        } finally {
            JPAUtil.fechar(); // fecha a conexão com o banco ao sair
        }
    }
}