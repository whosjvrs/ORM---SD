# Clínica Psico — Persistência com JPA + EclipseLink (RA3)

Sistema de console em Java para agendar consultas com psicólogos. É o projeto do RA2
(padrões **Facade**, **Chain of Responsibility**, **Strategy** e **Observer**) evoluído para
guardar os dados em **MySQL** com **JPA + EclipseLink**, com CRUD de 8 entidades.

Design de Software — Engenharia de Software, PUCPR.
Equipe (Grupo 4): Geovana Vincensi, João Reis, Luiz Dias e Tabatha Aita.

## Requisitos

- JDK 17 ou superior
- MySQL 8 rodando em `localhost:3306`, usuário `root`, senha `12345`
  (mesma configuração do exemplo da professora; se a sua for outra, ajuste no
  `src/META-INF/persistence.xml` **e não faça commit da sua senha**)
- Os 3 jars já estão na pasta `lib/` (não precisa baixar nada):
  `eclipselink.jar` (4.0.4), `jakarta.persistence-api.jar` (3.1) e `mysql-connector-java-8.0.26.jar`

O banco `clinica_psico` e as tabelas são criados sozinhos na primeira execução.

### Subir o MySQL

- **Fedora:** `sudo dnf install community-mysql-server && sudo systemctl start mysqld`
- **Ubuntu/WSL:** `sudo apt install mysql-server && sudo service mysql start`
- **Docker/Podman (qualquer sistema):**
  ```bash
  podman run -d --name mysql-clinica -e MYSQL_ROOT_PASSWORD=12345 -p 3306:3306 mysql:8
  ```
- **Windows:** MySQL Installer, definindo a senha do root como `12345`.

Se o root foi criado sem senha: `ALTER USER 'root'@'localhost' IDENTIFIED BY '12345';`

## Como executar

### IntelliJ
1. **File → Open** e escolher a pasta do projeto.
2. Botão direito em `src` → **Mark Directory as → Sources Root**.
3. **File → Project Structure → Libraries → + → Java** e selecionar os 3 jars da pasta `lib/`.
4. Rodar `consultas.teste.TesteMapeamento`. Deve terminar com **TESTE OK**.
5. Rodar `consultas.app.Main`.

### Eclipse (como no tutorial da professora)
1. **File → Import → Existing Projects** (ou criar um Java Project apontando para a pasta).
2. **Properties → Java Build Path → Libraries → Add JARs** e selecionar os 3 jars de `lib/`.
3. Rodar `TesteMapeamento` e depois `Main`.

### Terminal
```bash
javac -encoding UTF-8 -cp "lib/*" -d out $(find src -name "*.java")
cp -r src/META-INF out/
java -cp "out:lib/*" consultas.teste.TesteMapeamento
java -cp "out:lib/*" consultas.app.Main
```
No Windows, troque `:` por `;` no `-cp`.

### Ver o SQL gerado
No `persistence.xml`, troque `SEVERE` por `FINE` em `eclipselink.logging.level.sql`.

## Estrutura (3 camadas)

| Camada | Pacote | Conteúdo |
|---|---|---|
| Apresentação | `consultas.app` | `Main`, `MenuConsole`, `Console` (leitura do teclado), `DadosExemplo` |
| Apresentação | `consultas.app.cadastro` | `MenuCadastros` e um menu de CRUD por entidade (`MenuCrud` é a base) |
| Negócio | `consultas.facade` | `AgendamentoFacade` (Facade) |
| Negócio | `consultas.agenda` | `Agenda` |
| Negócio | `consultas.validacao` | Chain of Responsibility |
| Negócio | `consultas.estrategia` | Strategy |
| Negócio | `consultas.notificacao` | Observer |
| Dados | `consultas.dao` | `JPAUtil`, `GenericDAO<T>` e DAOs específicos |
| Modelo | `consultas.modelo` | 8 entidades, enums e `AgendamentoException` |
| Teste | `consultas.teste` | `TesteMapeamento` |
| Configuração | `src/META-INF` | `persistence.xml` |

## Entidades mapeadas

| Entidade | Tabela | Relacionamentos |
|---|---|---|
| `Pessoa` (abstrata) | `pessoa` | herança JOINED |
| `Paciente` | `paciente` | estende Pessoa; 1:N Consulta |
| `Psicologo` | `psicologo` | estende Pessoa; N:N Clinica; N:N Especialidade; 1:N HorarioAtendimento |
| `Clinica` | `clinica` | N:N Psicologo; 1:N HorarioAtendimento |
| `Especialidade` | `especialidade` | N:N Psicologo |
| `HorarioAtendimento` | `horario_atendimento` | N:1 Clinica; N:1 Psicologo |
| `Consulta` | `consulta` | N:1 Paciente; N:1 HorarioAtendimento; 1:N Notificacao (composição) |
| `Notificacao` | `notificacao` | N:1 Consulta |

Tabelas de junção: `psicologo_clinica` e `psicologo_especialidade`.

## Menu

```
1 - Agendar consulta
2 - Listar consultas
3 - Ligar/desligar notificação ao paciente
4 - Demonstrar validação
5 - Cadastros (CRUD)
0 - Sair
```

## Como a equipe trabalha

Plano de trabalho e tarefas de cada um: documento "RA3 Clínica Psico: plano de trabalho da equipe".

- Cada pessoa trabalha na própria branch (`dias`, `geovana`, `tabatha`, `joao`) e abre Pull Request para a `main`.
- Antes de abrir o PR: rodar `TesteMapeamento` e o `Main`, e fazer `git pull origin main`.
- Commits no formato `area: o que foi feito` (ex.: `crud: implementa MenuPaciente`).
