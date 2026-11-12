package consultas.app.cadastro;

/**
 * CRUD de HorarioAtendimento.
 *
 * Responsável: João Reis
 * Siga a estrutura de MenuClinica (modelo de referência).
 * - Cadastrar: escolher clínica e psicólogo; só permitir se o psicólogo atende na clínica
 *   (reaproveitar ValidadorVinculo). Psicólogo + início é único.
 * - Atualizar e excluir: proibido se o horário estiver reservado (regra 8).
 */
public class MenuHorario extends MenuCrud {

    public MenuHorario() {
        super("Horários de atendimento");
    }

    @Override
    protected void cadastrar() {
        emConstrucao(); // TODO João Reis
    }

    @Override
    protected void listar() {
        emConstrucao(); // TODO João Reis
    }

    @Override
    protected void buscar() {
        emConstrucao(); // TODO João Reis
    }

    @Override
    protected void atualizar() {
        emConstrucao(); // TODO João Reis
    }

    @Override
    protected void excluir() {
        emConstrucao(); // TODO João Reis
    }
}
