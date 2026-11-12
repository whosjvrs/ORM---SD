package consultas.estrategia;

import consultas.modelo.HorarioAtendimento;

import java.util.List;

public interface EstrategiaOrdenacao {

    List<HorarioAtendimento> ordenar(
        List<HorarioAtendimento> horarios
    );

    String getDescricao();
}