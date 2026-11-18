package consultas.app;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Leitura do teclado usada por todos os menus. Um único Scanner para o programa todo.
 * Todos os métodos repetem a pergunta enquanto a resposta for inválida.
 */
public final class Console {

    private static final Scanner SCANNER = new Scanner(System.in); // nunca fechado
    public static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Console() {
    }

    /** Número inteiro entre min e max. */
    public static int lerNumero(String pergunta, int min, int max) {
        while (true) {
            System.out.print(pergunta);
            String linha = SCANNER.nextLine().trim();
            try {
                int numero = Integer.parseInt(linha);
                if (numero >= min && numero <= max) {
                    return numero;
                }
            } catch (NumberFormatException e) {
                // não é número: cai na mensagem abaixo
            }
            System.out.println("Número inválido. Digite um valor entre " + min + " e " + max + ".");
        }
    }

    /** Um id (número positivo). */
    public static Long lerId(String pergunta) {
        return (long) lerNumero(pergunta, 1, Integer.MAX_VALUE);
    }

    /** Texto obrigatório (não aceita vazio). */
    public static String lerTexto(String pergunta) {
        while (true) {
            System.out.print(pergunta);
            String linha = SCANNER.nextLine().trim();
            if (!linha.isEmpty()) {
                return linha;
            }
            System.out.println("Campo obrigatório.");
        }
    }

    /** Texto opcional. Se o usuário só apertar Enter, devolve o valor atual (útil no "atualizar"). */
    public static String lerTextoOpcional(String pergunta, String valorAtual) {
        System.out.print(pergunta + " [" + (valorAtual == null ? "" : valorAtual) + "]: ");
        String linha = SCANNER.nextLine().trim();
        return linha.isEmpty() ? valorAtual : linha;
    }

    /** Data no formato dd/MM/yyyy. */
    public static LocalDate lerData(String pergunta) {
        while (true) {
            System.out.print(pergunta + " (dd/MM/aaaa): ");
            try {
                return LocalDate.parse(SCANNER.nextLine().trim(), DATA);
            } catch (DateTimeParseException e) {
                System.out.println("Data inválida.");
            }
        }
    }

    /** Data e hora no formato dd/MM/yyyy HH:mm. */
    public static LocalDateTime lerDataHora(String pergunta) {
        while (true) {
            System.out.print(pergunta + " (dd/MM/aaaa HH:mm): ");
            try {
                return LocalDateTime.parse(SCANNER.nextLine().trim(), DATA_HORA);
            } catch (DateTimeParseException e) {
                System.out.println("Data/hora inválida.");
            }
        }
    }

    /** Data opcional (dd/MM/yyyy). Enter mantém o valor atual (usado no "atualizar"). */
    public static LocalDate lerDataOpcional(String pergunta, LocalDate valorAtual) {
        while (true) {
            System.out.print(pergunta + " (dd/MM/aaaa) [" + (valorAtual == null ? "" : valorAtual.format(DATA)) + "]: ");
            String linha = SCANNER.nextLine().trim();
            if (linha.isEmpty()) {
                return valorAtual;
            }
            try {
                return LocalDate.parse(linha, DATA);
            } catch (DateTimeParseException e) {
                System.out.println("Data inválida.");
            }
        }
    }

    /** Data e hora opcionais (dd/MM/yyyy HH:mm). Enter mantém o valor atual. */
    public static LocalDateTime lerDataHoraOpcional(String pergunta, LocalDateTime valorAtual) {
        while (true) {
            System.out.print(pergunta + " (dd/MM/aaaa HH:mm) [" + valorAtual.format(DATA_HORA) + "]: ");
            String linha = SCANNER.nextLine().trim();
            if (linha.isEmpty()) {
                return valorAtual;
            }
            try {
                return LocalDateTime.parse(linha, DATA_HORA);
            } catch (DateTimeParseException e) {
                System.out.println("Data/hora inválida.");
            }
        }
    }

    /**
     * Escolha de vários itens de uma lista, digitando os números separados por vírgula (ex.: 1,3).
     * Enter sem nada devolve uma lista vazia.
     */
    public static <T> List<T> escolherVarios(String titulo, List<T> itens) {
        if (itens.isEmpty()) {
            System.out.println("Nenhum item cadastrado em " + titulo + ".");
            return new ArrayList<>();
        }
        System.out.println("\n" + titulo + ":");
        for (int i = 0; i < itens.size(); i++) {
            System.out.println((i + 1) + " - " + itens.get(i));
        }
        while (true) {
            System.out.print("Números separados por vírgula (Enter para nenhum): ");
            String linha = SCANNER.nextLine().trim();
            List<T> escolhidos = new ArrayList<>();
            if (linha.isEmpty()) {
                return escolhidos;
            }
            try {
                for (String parte : linha.split(",")) {
                    int n = Integer.parseInt(parte.trim());
                    if (n < 1 || n > itens.size()) {
                        throw new NumberFormatException();
                    }
                    if (!escolhidos.contains(itens.get(n - 1))) {
                        escolhidos.add(itens.get(n - 1));
                    }
                }
                return escolhidos;
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Use números entre 1 e " + itens.size() + ", separados por vírgula.");
            }
        }
    }

    /** Pergunta de sim ou não. */
    public static boolean confirmar(String pergunta) {
        while (true) {
            System.out.print(pergunta + " (s/n): ");
            String linha = SCANNER.nextLine().trim().toLowerCase();
            if (linha.equals("s")) return true;
            if (linha.equals("n")) return false;
        }
    }

    /** Mostra uma lista numerada e devolve o item escolhido (ou null se a lista estiver vazia). */
    public static <T> T escolher(String titulo, List<T> itens) {
        if (itens.isEmpty()) {
            System.out.println("Nenhum item cadastrado.");
            return null;
        }
        System.out.println("\n" + titulo + ":");
        for (int i = 0; i < itens.size(); i++) {
            System.out.println((i + 1) + " - " + itens.get(i));
        }
        return itens.get(lerNumero("Escolha: ", 1, itens.size()) - 1);
    }

    /** Imprime uma lista, ou um aviso se estiver vazia. */
    public static void imprimir(List<?> itens) {
        if (itens.isEmpty()) {
            System.out.println("Nenhum registro.");
            return;
        }
        itens.forEach(System.out::println);
    }
}
