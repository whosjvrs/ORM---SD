package consultas.estrategia;

import consultas.modelo.HorarioAtendimento;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OrdemCronologica implements EstrategiaOrdenacao {

    @Override
    public List<HorarioAtendimento> ordenar(
            List<HorarioAtendimento> horarios) {

        List<HorarioAtendimento> copia =
            new ArrayList<>(horarios);

        copia.sort(
            Comparator.comparing(HorarioAtendimento::getInicio)
        );

        return copia;
    }

    @Override
    public String getDescricao() {
        return "Ordem cronológica";
    }
}