package consultas.estrategia;

import consultas.modelo.HorarioAtendimento;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class PreferenciaTurno implements EstrategiaOrdenacao {

    private final Turno turnoPreferido;

    public PreferenciaTurno(Turno turnoPreferido) {
        this.turnoPreferido = Objects.requireNonNull(
            turnoPreferido,
            "O turno preferido deve ser informado."
        );
    }

    @Override
    public List<HorarioAtendimento> ordenar(
            List<HorarioAtendimento> horarios) {

        List<HorarioAtendimento> copia =
            new ArrayList<>(horarios);

        copia.sort(
            Comparator
                .comparing(
                    (HorarioAtendimento horario) ->
                        Turno.de(horario.getInicio()) != turnoPreferido
                )
                .thenComparing(HorarioAtendimento::getInicio)
        );

        return copia;
    }

    @Override
    public String getDescricao() {
        return "Preferência pelo turno " + turnoPreferido;
    }
}