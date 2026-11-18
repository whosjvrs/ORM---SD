package consultas.app;

import consultas.dao.GenericDAO;
import consultas.dao.JPAUtil;
import consultas.modelo.Clinica;
import consultas.modelo.Especialidade;
import consultas.modelo.HorarioAtendimento;
import consultas.modelo.Paciente;
import consultas.modelo.Pessoa;
import consultas.modelo.Psicologo;

import java.time.LocalDate;

/**
 * Carga inicial: os dados de exemplo do RA2 (com os campos novos do RA3) gravados no banco.
 *
 * Só roda quando ainda não há nenhuma pessoa cadastrada. Nas execuções seguintes o banco já tem
 * dados e nada é inserido de novo. Para recomeçar do zero: DROP DATABASE clinica_psico;
 */
public final class DadosExemplo {

    private DadosExemplo() {
    }

    public static void carregarSeVazio() {
        if (new GenericDAO<>(Pessoa.class).contar() > 0) {
            return;
        }
        System.out.println("[Carga inicial] Banco vazio: inserindo dados de exemplo...");

        // Tudo em uma transação: ou grava o conjunto inteiro, ou nada.
        JPAUtil.emTransacao(em -> {
            Clinica centro = new Clinica("Clínica Centro", "Rua XV de Novembro, 1000 - Centro, Curitiba", "(41) 3333-1000");
            Clinica batel = new Clinica("Clínica Batel", "Av. do Batel, 1500 - Batel, Curitiba", "(41) 3333-2000");
            em.persist(centro);
            em.persist(batel);

            Especialidade tcc = new Especialidade("Terapia Cognitivo-Comportamental", "Foco em pensamentos e comportamentos");
            Especialidade infantil = new Especialidade("Psicologia Infantil", "Atendimento de crianças");
            Especialidade casal = new Especialidade("Terapia de Casal", "Atendimento de casais");
            em.persist(tcc);
            em.persist(infantil);
            em.persist(casal);

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
            em.persist(ana);
            em.persist(bruno);
            em.persist(carla);

            em.persist(new Paciente("Maria Oliveira", "111.111.111-11", "maria@email.com",
                    "(41) 98888-0001", LocalDate.of(1995, 3, 10)));
            em.persist(new Paciente("Pedro Santos", "222.222.222-22", "pedro@email.com",
                    "(41) 98888-0002", LocalDate.of(2001, 8, 25)));

            // Horários a partir de 2 dias depois da carga, fora de ordem de propósito,
            // para as estratégias de ordenação (Strategy) mostrarem diferença.
            LocalDate dia1 = LocalDate.now().plusDays(2);
            LocalDate dia2 = dia1.plusDays(1);
            em.persist(new HorarioAtendimento(centro, ana, dia2.atTime(19, 0)));
            em.persist(new HorarioAtendimento(centro, ana, dia1.atTime(14, 0)));
            em.persist(new HorarioAtendimento(centro, ana, dia2.atTime(9, 0)));
            em.persist(new HorarioAtendimento(centro, ana, dia1.atTime(8, 0)));
            em.persist(new HorarioAtendimento(batel, ana, dia1.atTime(18, 30)));
            em.persist(new HorarioAtendimento(centro, bruno, dia1.atTime(10, 0)));
            em.persist(new HorarioAtendimento(centro, bruno, dia2.atTime(15, 0)));
            em.persist(new HorarioAtendimento(batel, carla, dia1.atTime(16, 0)));
            return null;
        });
        System.out.println("[Carga inicial] 2 clínicas, 3 especialidades, 3 psicólogos, 2 pacientes e 8 horários gravados.");
    }
}
