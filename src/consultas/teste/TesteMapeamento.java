package consultas.teste;

import consultas.dao.GenericDAO;
import consultas.dao.JPAUtil;
import consultas.modelo.Clinica;
import consultas.modelo.Consulta;
import consultas.modelo.Especialidade;
import consultas.modelo.HorarioAtendimento;
import consultas.modelo.Notificacao;
import consultas.modelo.Paciente;
import consultas.modelo.Pessoa;
import consultas.modelo.Psicologo;
import consultas.modelo.StatusConsulta;
import consultas.modelo.TipoNotificacao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Teste rápido do mapeamento: cria as tabelas, grava um objeto de cada entidade,
 * lê de volta, atualiza e apaga tudo. Rode esta classe depois de configurar o MySQL.
 *
 * Se aparecer "TESTE OK" no final, o mapeamento das 8 entidades está funcionando.
 * O teste apaga só o que ele mesmo criou.
 */
public class TesteMapeamento {

    public static void main(String[] args) {
        String sufixo = String.valueOf(System.currentTimeMillis() % 100000); // evita colisão com campos únicos
        try {
            // ---------- CREATE (tudo em uma transação) ----------
            Consulta consulta = JPAUtil.emTransacao(em -> {
                Clinica clinica = new Clinica("Clínica Teste " + sufixo, "Rua Teste, 1", "(41) 0000-0000");
                Especialidade esp = new Especialidade("Especialidade Teste " + sufixo, "Criada pelo teste");
                Psicologo psi = new Psicologo("Psicólogo Teste", "TESTE/" + sufixo, "psi@teste.com", "(41) 0000-0001");
                psi.vincular(clinica);
                psi.adicionarEspecialidade(esp);
                Paciente pac = new Paciente("Paciente Teste", "000.000." + sufixo, "pac@teste.com",
                        "(41) 0000-0002", LocalDate.of(2000, 1, 1));
                HorarioAtendimento hor = new HorarioAtendimento(clinica, psi, LocalDateTime.now().plusDays(30).withNano(0));
                hor.reservar();
                Consulta c = new Consulta(pac, hor);
                new Notificacao(c, TipoNotificacao.CONFIRMACAO, "Consulta de teste confirmada.");

                em.persist(clinica);
                em.persist(esp);
                em.persist(psi);
                em.persist(pac);
                em.persist(hor);
                em.persist(c); // a Notificacao vai junto (cascade)
                return c;
            });
            System.out.println("CREATE ok - consulta id " + consulta.getId());

            // ---------- READ ----------
            Consulta lida = new GenericDAO<>(Consulta.class).buscarPorId(consulta.getId());
            verificar(lida != null, "consulta não encontrada");
            verificar(lida.getPaciente().getNome().equals("Paciente Teste"), "paciente errado");
            verificar(lida.getPsicologo().getClinicas().size() == 1, "N:N psicólogo-clínica não gravou");
            verificar(lida.getPsicologo().getEspecialidades().size() == 1, "N:N psicólogo-especialidade não gravou");
            verificar(lida.getNotificacoes().size() == 1, "notificação não gravou");
            System.out.println("READ ok - " + lida);

            List<Pessoa> pessoas = new GenericDAO<>(Pessoa.class).listarTodos();
            System.out.println("READ polimórfico ok - " + pessoas.size() + " pessoa(s) no banco (pacientes + psicólogos)");

            // ---------- UPDATE ----------
            lida.setObservacoes("Atualizada pelo teste");
            lida.setStatus(StatusConsulta.REALIZADA);
            new GenericDAO<>(Consulta.class).atualizar(lida);
            Consulta atualizada = new GenericDAO<>(Consulta.class).buscarPorId(consulta.getId());
            verificar(atualizada.getStatus() == StatusConsulta.REALIZADA, "status não atualizou");
            System.out.println("UPDATE ok - status " + atualizada.getStatus());

            // ---------- DELETE (filhos antes dos pais, por causa das chaves estrangeiras) ----------
            JPAUtil.emTransacao(em -> {
                Consulta c = em.find(Consulta.class, consulta.getId());
                HorarioAtendimento h = c.getHorario();
                Paciente p = c.getPaciente();
                Psicologo psi = h.getPsicologo();
                Clinica cl = h.getClinica();
                Especialidade e = psi.getEspecialidades().get(0);
                em.remove(c);       // remove a notificação junto (orphanRemoval)
                em.remove(h);
                em.remove(p);
                psi.desvincular(cl);
                psi.removerEspecialidade(e);
                em.remove(psi);
                em.remove(cl);
                em.remove(e);
                return null;
            });
            verificar(new GenericDAO<>(Consulta.class).buscarPorId(consulta.getId()) == null, "consulta não foi apagada");
            System.out.println("DELETE ok");

            System.out.println("\nTESTE OK - mapeamento das 8 entidades funcionando.");
        } finally {
            JPAUtil.fechar();
        }
    }

    private static void verificar(boolean condicao, String erro) {
        if (!condicao) {
            throw new IllegalStateException("FALHOU: " + erro);
        }
    }
}
