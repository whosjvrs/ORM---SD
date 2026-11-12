package consultas.app;

import consultas.agenda.Agenda;
import consultas.modelo.Clinica;
import consultas.modelo.Especialidade;
import consultas.modelo.HorarioAtendimento;
import consultas.modelo.Paciente;
import consultas.modelo.Psicologo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Dados de exemplo do RA2, já com os campos novos do RA3.
 *
 * TODO RA3 (Luiz Dias): transformar em carga inicial do banco — gravar tudo pelos DAOs
 * somente se o banco estiver vazio — e tirar a dependência da fachada nesta classe.
 */
public class DadosExemplo {
    private final List<Clinica> clinicas = new ArrayList<>();
    private final List<Psicologo> psicologos = new ArrayList<>();
    private final List<Paciente> pacientes = new ArrayList<>();
    private final List<Especialidade> especialidades = new ArrayList<>();

    public DadosExemplo(Agenda agenda) {
        Clinica centro = new Clinica("Clínica Centro", "Rua XV de Novembro, 1000 - Centro, Curitiba", "(41) 3333-1000");
        Clinica batel = new Clinica("Clínica Batel", "Av. do Batel, 1500 - Batel, Curitiba", "(41) 3333-2000");
        clinicas.add(centro);
        clinicas.add(batel);

        Especialidade tcc = new Especialidade("Terapia Cognitivo-Comportamental", "Foco em pensamentos e comportamentos");
        Especialidade infantil = new Especialidade("Psicologia Infantil", "Atendimento de crianças");
        Especialidade casal = new Especialidade("Terapia de Casal", "Atendimento de casais");
        especialidades.add(tcc);
        especialidades.add(infantil);
        especialidades.add(casal);

        Psicologo ana = new Psicologo("Dra. Ana Souza", "08/12345", "ana@clinicapsico.com", "(41) 99999-0001");
        Psicologo bruno = new Psicologo("Dr. Bruno Lima", "08/23456", "bruno@clinicapsico.com", "(41) 99999-0002");
        Psicologo carla = new Psicologo("Dra. Carla Mendes", "08/34567", "carla@clinicapsico.com", "(41) 99999-0003");
        ana.vincular(centro);
        ana.vincular(batel);   // atende nas duas clínicas
        bruno.vincular(centro);
        carla.vincular(batel);
        ana.adicionarEspecialidade(tcc);
        bruno.adicionarEspecialidade(tcc);
        bruno.adicionarEspecialidade(casal);
        carla.adicionarEspecialidade(infantil);
        psicologos.add(ana);
        psicologos.add(bruno);
        psicologos.add(carla);

        pacientes.add(new Paciente("Maria Oliveira", "111.111.111-11", "maria@email.com",
                "(41) 98888-0001", LocalDate.of(1995, 3, 10)));
        pacientes.add(new Paciente("Pedro Santos", "222.222.222-22", "pedro@email.com",
                "(41) 98888-0002", LocalDate.of(2001, 8, 25)));

        LocalDate amanha = LocalDate.now().plusDays(1);
        LocalDate depois = amanha.plusDays(1);
        // Fora de ordem de propósito, para as estratégias de ordenação mostrarem diferença.
        agenda.adicionar(new HorarioAtendimento(centro, ana, depois.atTime(19, 0)));
        agenda.adicionar(new HorarioAtendimento(centro, ana, amanha.atTime(14, 0)));
        agenda.adicionar(new HorarioAtendimento(centro, ana, depois.atTime(9, 0)));
        agenda.adicionar(new HorarioAtendimento(centro, ana, amanha.atTime(8, 0)));
        agenda.adicionar(new HorarioAtendimento(batel, ana, amanha.atTime(18, 30)));
        agenda.adicionar(new HorarioAtendimento(centro, bruno, amanha.atTime(10, 0)));
        agenda.adicionar(new HorarioAtendimento(centro, bruno, depois.atTime(15, 0)));
        agenda.adicionar(new HorarioAtendimento(batel, carla, amanha.atTime(16, 0)));
    }

    public List<Clinica> getClinicas() { return new ArrayList<>(clinicas); }
    public List<Psicologo> getPsicologos() { return new ArrayList<>(psicologos); }
    public List<Paciente> getPacientes() { return new ArrayList<>(pacientes); }
    public List<Especialidade> getEspecialidades() { return new ArrayList<>(especialidades); }
}
