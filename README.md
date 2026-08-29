# Sistema de Controle de Chamados Técnicos

API REST desenvolvida em **Java com Spring Boot**, referente à **SA 01 — Sistema de Controle de Chamados Técnicos**, do Projeto Final da disciplina de Programação de Aplicativos.

---

## 📋 Sumário

- [Descrição do problema](#-descrição-do-problema)
- [Objetivo do sistema](#-objetivo-do-sistema)
- [Tecnologias utilizadas](#-tecnologias-utilizadas)
- [Estrutura do projeto](#-estrutura-do-projeto)
- [Modelo de dados](#-modelo-de-dados)
- [Enums](#-enums)
- [Regras de negócio](#-regras-de-negócio)
- [Segurança / Autenticação](#-segurança--autenticação)
- [Tratamento de erros](#-tratamento-de-erros)
- [Como executar o projeto](#-como-executar-o-projeto)
- [Endpoints da API](#-endpoints-da-api)
- [DTOs (objetos de entrada e saída)](#-dtos-objetos-de-entrada-e-saída)
- [Exemplos de requisição e resposta](#-exemplos-de-requisição-e-resposta)
- [Exemplos de erro](#-exemplos-de-erro)
- [Códigos HTTP utilizados](#-códigos-http-utilizados)
- [Testes](#-testes)

---

## 📖 Descrição do problema

Uma escola técnica precisa organizar melhor os chamados internos relacionados a problemas em computadores, projetores, internet, impressoras e equipamentos de laboratório. Antes desta API, os chamados eram anotados manualmente, o que dificultava o acompanhamento do status de cada chamado e a identificação dos setores/categorias com mais problemas recorrentes.

## 🎯 Objetivo do sistema

Fornecer uma API REST que permita:
- Registrar, consultar, atualizar e finalizar chamados técnicos;
- Classificar cada chamado em uma categoria;
- Vincular um ou mais técnicos responsáveis a cada chamado;
- Controlar o ciclo de vida do chamado através de status (aberto, em andamento, finalizado, cancelado);
- Impedir operações inválidas através de regras de negócio (ex.: excluir chamado finalizado, vincular técnico inativo).

## 🚀 Tecnologias utilizadas

| Tecnologia | Uso |
|---|---|
| Java 21 | Linguagem principal |
| Spring Boot 4.1.0 | Framework da aplicação |
| Spring Web (starter-webmvc) | Criação da API REST |
| Spring Data JPA | Persistência e mapeamento objeto-relacional |
| Hibernate | Implementação JPA |
| Spring Security | Autenticação HTTP Basic |
| PostgreSQL | Banco de dados relacional |
| Maven | Gerenciador de dependências e build |
| Spring Boot DevTools | Hot reload em desenvolvimento |

## 🗂️ Estrutura do projeto

```
br.com.senai.mateus.controlechamados
│
├── ControleChamadosApplication.java   -> classe principal (main)
│
├── config/
│   └── SecurityConfig.java            -> configuração de autenticação e autorização
│
├── controller/
│   ├── ChamadoController.java         -> endpoints de /chamados
│   ├── CategoriaController.java       -> endpoints de /categorias
│   └── TecnicoController.java         -> endpoints de /tecnicos
│
├── service/
│   ├── ChamadoService.java            -> regras de negócio dos chamados
│   ├── CategoriaService.java          -> regras de negócio das categorias
│   └── TecnicoService.java            -> regras de negócio dos técnicos
│
├── repository/
│   ├── ChamadoRepository.java
│   ├── CategoriaRepository.java
│   └── TecnicoRepository.java
│
├── entity/
│   ├── Chamado.java
│   ├── Categoria.java
│   └── Tecnico.java
│
├── enums/
│   ├── StatusChamado.java
│   ├── Prioridade.java
│   └── Ativo.java
│
├── dto/
│   ├── ChamadoRequestDTO.java / ChamadoResponseDTO.java
│   ├── CategoriaRequestDTO.java / CategoriaResponseDTO.java
│   ├── TecnicoRequestDTO.java / TecnicoResponseDTO.java
│   ├── AtualizarStatusDTO.java
│   └── AlterarTecnicoDTO.java
│
└── exception/
    ├── GlobalExceptionHandler.java        -> @RestControllerAdvice (tratamento global)
    ├── RecursoNaoEncontradoException.java -> 404
    ├── RegraDeNegocioException.java       -> 400
    ├── ConflitoException.java             -> 409
    └── ErroResponse.java                  -> corpo padrão de erro
```

## 🧩 Modelo de dados

### Entidade `Chamado`

| Campo | Tipo | Observações |
|---|---|---|
| `id` | `Long` | Chave primária, auto-incremento |
| `titulo` | `String` | Obrigatório |
| `descricao` | `String` | Obrigatório |
| `solicitante` | `String` | Obrigatório |
| `local` | `String` | Obrigatório |
| `prioridade` | `Prioridade` (enum) | Obrigatório — `BAIXA`, `MEDIA`, `ALTA` |
| `status` | `StatusChamado` (enum) | Default `ABERTO` na criação |
| `dataAbertura` | `LocalDate` | Preenchida automaticamente na criação |
| `dataFinalizacao` | `LocalDate` | Preenchida automaticamente ao mudar status para `FINALIZADO`; volta a `null` caso contrário |
| `categoria` | `Categoria` | Relacionamento `@ManyToOne` (obrigatório) |
| `tecnicos` | `List<Tecnico>` | Relacionamento `@ManyToMany` (opcional na criação) |

### Entidade `Categoria`

| Campo | Tipo | Observações |
|---|---|---|
| `id` | `Long` | Chave primária, auto-incremento |
| `nome` | `String` | Obrigatório, único |
| `descricao` | `String` | Obrigatório |
| `chamados` | `List<Chamado>` | Relacionamento `@OneToMany` (lado inverso, `mappedBy = "categoria"`) |

### Entidade `Tecnico`

| Campo | Tipo | Observações |
|---|---|---|
| `id` | `Long` | Chave primária, auto-incremento |
| `nome` | `String` | Obrigatório |
| `email` | `String` | Obrigatório, único |
| `especialidade` | `String` | Opcional |
| `ativo` | `Ativo` (enum) | Default `ATIVO` na criação |
| `chamados` | `List<Chamado>` | Relacionamento `@ManyToMany` (lado inverso, `mappedBy = "tecnicos"`) |

### Relacionamentos

- **`Chamado` `@ManyToOne` → `Categoria`**: um chamado pertence a exatamente uma categoria; uma categoria pode ter vários chamados.
- **`Chamado` `@ManyToMany` ↔ `Tecnico`**: um chamado pode ter vários técnicos responsáveis, e um técnico pode atender vários chamados. Tabela de junção: `chamados_tecnico` (`chamado_id`, `tecnico_id`).

## 🏷️ Enums

**`StatusChamado`**
| Valor | Descrição |
|---|---|
| `ABERTO` | Status inicial de todo chamado |
| `EM_ANDAMENTO` | Chamado sendo atendido (exige ao menos 1 técnico vinculado) |
| `FINALIZADO` | Chamado concluído (bloqueia edição/exclusão/novos técnicos) |
| `CANCELADO` | Chamado cancelado |

**`Prioridade`**
| Valor | Descrição |
|---|---|
| `BAIXA` | Prioridade baixa |
| `MEDIA` | Prioridade média |
| `ALTA` | Prioridade alta |

**`Ativo`** (usado em `Tecnico`)
| Valor | Descrição |
|---|---|
| `ATIVO` | Técnico disponível para ser vinculado a chamados |
| `INATIVO` | Técnico não pode ser vinculado a novos chamados |

## ⚙️ Regras de negócio

- Todo chamado é criado com status `ABERTO` e `dataAbertura` preenchida automaticamente.
- Um chamado só pode ser alterado para `EM_ANDAMENTO` se possuir **pelo menos um técnico vinculado**.
- Um chamado com status `FINALIZADO`:
  - não pode ser editado (`PUT /chamados/{id}`);
  - não pode ser excluído (`DELETE /chamados/{id}`);
  - não pode receber novos técnicos (`PATCH /chamados/{id}/tecnicos`);
  - não pode ter técnicos desvinculados (`PATCH /chamados/{id}/tecnicos/desvincular`).
- Ao mudar o status para `FINALIZADO`, `dataFinalizacao` é preenchida com a data atual; ao mudar para qualquer outro status, `dataFinalizacao` volta a `null`.
- Um técnico com status `INATIVO` não pode ser vinculado a um chamado (nem na criação, nem via `PATCH /tecnicos`).
- Não é permitido informar o mesmo ID de técnico duplicado em uma lista de vinculação/desvinculação.
- Um técnico já vinculado a um chamado não pode ser vinculado novamente ao mesmo chamado (retorna conflito).
- Não é permitido cadastrar categoria com nome já existente.
- Não é permitido cadastrar técnico com e-mail já existente.
- Uma categoria não pode ser excluída se estiver vinculada a algum chamado.
- Um técnico não pode ser excluído se estiver vinculado a algum chamado.
- Campos obrigatórios de `Chamado` (`titulo`, `descricao`, `solicitante`, `local`, `prioridade`, `categoriaId`), `Categoria` (`nome`, `descricao`) e `Tecnico` (`nome`, `email`) são validados no service antes de qualquer persistência.

## 🔐 Segurança / Autenticação

A API utiliza **HTTP Basic Authentication** via Spring Security.

- **Rotas públicas** (sem autenticação): todos os métodos `GET` de `/chamados/**`, `/categorias/**` e `/tecnicos/**`.
- **Rotas protegidas** (exigem usuário e senha): `POST`, `PUT`, `PATCH` e `DELETE` em `/chamados/**`, `/categorias/**` e `/tecnicos/**`.
- Qualquer outra rota não mapeada exige autenticação por padrão (`anyRequest().authenticated()`).
- Se a requisição não estiver autenticada em uma rota protegida, a API retorna `401 Unauthorized`.
- CSRF está desabilitado (`csrf.disable()`), adequado para uma API stateless consumida por Postman/Insomnia/front-end.
- A senha do usuário é armazenada com hash `BCrypt` (`BCryptPasswordEncoder`).
- O usuário/senha da API **não estão fixos no código** — são lidos de variáveis de ambiente (`SECURITY_USERNAME` e `SECURITY_PASSWORD`).

**Usuário e senha de teste (definidos via variáveis de ambiente ao subir a aplicação):**
```
Usuário: admin
Senha: admin123
```

## ❌ Tratamento de erros

O tratamento é centralizado em `GlobalExceptionHandler`, anotado com `@RestControllerAdvice`, cobrindo:

| Exceção | Status HTTP | Quando ocorre |
|---|---|---|
| `RecursoNaoEncontradoException` | 404 Not Found | Chamado, categoria ou técnico não encontrado por ID |
| `RegraDeNegocioException` | 400 Bad Request | Violação de regra de negócio (campo obrigatório, status inválido, técnico inativo, etc.) |
| `ConflitoException` | 409 Conflict | Tentativa de excluir recurso vinculado, e-mail/nome duplicado, técnico já vinculado |
| `Exception` (genérica) | 500 Internal Server Error | Qualquer erro não tratado explicitamente |

Todas as respostas de erro seguem o mesmo formato (`ErroResponse`):
```json
{
  "status": 400,
  "erro": "Regra de negócio violada",
  "mensagem": "descrição específica do erro"
}
```

## 🛠️ Como executar o projeto

### Pré-requisitos
- JDK 21+
- Maven 3.9+ (ou utilize o wrapper `./mvnw` incluso)
- PostgreSQL 14+ em execução

### 1. Criar o banco de dados

```sql
CREATE DATABASE controle_chamados;
```

### 2. Definir as variáveis de ambiente

O arquivo `src/main/resources/application.properties` depende das variáveis abaixo:

| Variável | Descrição | Exemplo |
|---|---|---|
| `DB_URL` | URL JDBC do PostgreSQL | `jdbc:postgresql://localhost:5432/controle_chamados` |
| `DB_USER` | Usuário do banco | `postgres` |
| `DB_PASSWORD` | Senha do banco | `postgres` |
| `SECURITY_USERNAME` | Usuário para autenticação básica da API | `admin` |
| `SECURITY_PASSWORD` | Senha para autenticação básica da API | `admin123` |

**Linux/macOS:**
```bash
export DB_URL=jdbc:postgresql://localhost:5432/controle_chamados
export DB_USER=postgres
export DB_PASSWORD=postgres
export SECURITY_USERNAME=admin
export SECURITY_PASSWORD=admin123
```

**Windows (PowerShell):**
```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/controle_chamados"
$env:DB_USER="postgres"
$env:DB_PASSWORD="postgres"
$env:SECURITY_USERNAME="admin"
$env:SECURITY_PASSWORD="admin123"
```

> ⚠️ Se essas variáveis não forem definidas, a aplicação falha ao subir.

### 3. Executar a aplicação

```bash
./mvnw spring-boot:run
```

Ou gerando o `.jar`:
```bash
./mvnw clean package
java -jar target/controle-chamados-0.0.1-SNAPSHOT.jar
```

A API sobe em `http://localhost:8080`. O schema do banco é criado/atualizado automaticamente (`spring.jpa.hibernate.ddl-auto=update`).

## 📡 Endpoints da API

### Chamados (`/chamados`)

| Método | Rota | Auth | Corpo | Descrição |
|---|---|---|---|---|
| GET | `/chamados` | Não | — | Lista todos os chamados |
| GET | `/chamados/{id}` | Não | — | Busca um chamado por ID |
| POST | `/chamados` | Sim | `ChamadoRequestDTO` | Cria um novo chamado (status inicial `ABERTO`) |
| PUT | `/chamados/{id}` | Sim | `ChamadoRequestDTO` | Atualiza um chamado (bloqueado se `FINALIZADO`) |
| DELETE | `/chamados/{id}` | Sim | — | Remove um chamado (bloqueado se `FINALIZADO`) |
| PATCH | `/chamados/{id}/status` | Sim | `AtualizarStatusDTO` | Atualiza apenas o status do chamado |
| PATCH | `/chamados/{id}/tecnicos` | Sim | `AlterarTecnicoDTO` | Vincula um ou mais técnicos ao chamado |
| PATCH | `/chamados/{id}/tecnicos/desvincular` | Sim | `AlterarTecnicoDTO` | Desvincula um ou mais técnicos do chamado |

### Categorias (`/categorias`)

| Método | Rota | Auth | Corpo | Descrição |
|---|---|---|---|---|
| GET | `/categorias` | Não | — | Lista todas as categorias |
| GET | `/categorias/{id}` | Não | — | Busca uma categoria por ID |
| POST | `/categorias` | Sim | `CategoriaRequestDTO` | Cria uma nova categoria |
| PUT | `/categorias/{id}` | Sim | `CategoriaRequestDTO` | Atualiza uma categoria |
| DELETE | `/categorias/{id}` | Sim | — | Remove uma categoria (bloqueado se houver chamados vinculados) |

### Técnicos (`/tecnicos`)

| Método | Rota | Auth | Corpo | Descrição |
|---|---|---|---|---|
| GET | `/tecnicos` | Não | — | Lista todos os técnicos |
| GET | `/tecnicos/{id}` | Não | — | Busca um técnico por ID |
| POST | `/tecnicos` | Sim | `TecnicoRequestDTO` | Cria um novo técnico (status inicial `ATIVO`) |
| PUT | `/tecnicos/{id}` | Sim | `TecnicoRequestDTO` | Atualiza um técnico |
| DELETE | `/tecnicos/{id}` | Sim | — | Remove um técnico (bloqueado se houver chamados vinculados) |

## 📦 DTOs (objetos de entrada e saída)

**`ChamadoRequestDTO`** — entrada de criação/atualização de chamado
```json
{
  "titulo": "string",
  "descricao": "string",
  "solicitante": "string",
  "local": "string",
  "prioridade": "BAIXA | MEDIA | ALTA",
  "categoriaId": 1,
  "tecnicosIds": [1, 2]
}
```

**`ChamadoResponseDTO`** — saída de chamado
```json
{
  "id": 1,
  "titulo": "string",
  "descricao": "string",
  "solicitante": "string",
  "local": "string",
  "prioridade": "ALTA",
  "status": "ABERTO",
  "dataAbertura": "2026-08-28",
  "dataFinalizacao": null,
  "categoria": { "id": 1, "nome": "string", "descricao": "string" },
  "tecnicos": [ { "id": 1, "nome": "string", "email": "string", "especialidade": "string", "ativo": "ATIVO" } ]
}
```

**`CategoriaRequestDTO` / `CategoriaResponseDTO`**
```json
{ "nome": "string", "descricao": "string" }
```

**`TecnicoRequestDTO` / `TecnicoResponseDTO`**
```json
{ "nome": "string", "email": "string", "especialidade": "string", "ativo": "ATIVO | INATIVO" }
```

**`AtualizarStatusDTO`** — usado em `PATCH /chamados/{id}/status`
```json
{ "statusChamado": "ABERTO | EM_ANDAMENTO | FINALIZADO | CANCELADO" }
```

**`AlterarTecnicoDTO`** — usado em `PATCH /chamados/{id}/tecnicos` e `.../desvincular`
```json
{ "tecnicosIds": [1, 2] }
```

## 🧪 Exemplos de requisição e resposta

### Criar categoria — `POST /categorias`
**Request:**
```json
{
  "nome": "Rede",
  "descricao": "Problemas relacionados a internet e conectividade"
}
```
**Response (201 Created):**
```json
{
  "id": 1,
  "nome": "Rede",
  "descricao": "Problemas relacionados a internet e conectividade"
}
```

### Criar técnico — `POST /tecnicos`
**Request:**
```json
{
  "nome": "João Silva",
  "email": "joao.silva@escola.com",
  "especialidade": "Redes",
  "ativo": "ATIVO"
}
```
**Response (201 Created):**
```json
{
  "id": 1,
  "nome": "João Silva",
  "email": "joao.silva@escola.com",
  "especialidade": "Redes",
  "ativo": "ATIVO"
}
```

### Criar chamado — `POST /chamados`
**Request:**
```json
{
  "titulo": "Sem conexão com a internet no laboratório 2",
  "descricao": "Os computadores do laboratório 2 não conseguem acessar a internet",
  "solicitante": "Maria Souza",
  "local": "Laboratório 2",
  "prioridade": "ALTA",
  "categoriaId": 1,
  "tecnicosIds": [1]
}
```
**Response (201 Created):**
```json
{
  "id": 1,
  "titulo": "Sem conexão com a internet no laboratório 2",
  "descricao": "Os computadores do laboratório 2 não conseguem acessar a internet",
  "solicitante": "Maria Souza",
  "local": "Laboratório 2",
  "prioridade": "ALTA",
  "status": "ABERTO",
  "dataAbertura": "2026-08-28",
  "dataFinalizacao": null,
  "categoria": { "id": 1, "nome": "Rede", "descricao": "Problemas relacionados a internet e conectividade" },
  "tecnicos": [ { "id": 1, "nome": "João Silva", "email": "joao.silva@escola.com", "especialidade": "Redes", "ativo": "ATIVO" } ]
}
```

### Atualizar status — `PATCH /chamados/1/status`
**Request:**
```json
{ "statusChamado": "EM_ANDAMENTO" }
```
**Response (200 OK):** mesmo formato acima, com `"status": "EM_ANDAMENTO"`.

### Vincular técnicos — `PATCH /chamados/1/tecnicos`
**Request:**
```json
{ "tecnicosIds": [2, 3] }
```

### Desvincular técnicos — `PATCH /chamados/1/tecnicos/desvincular`
**Request:**
```json
{ "tecnicosIds": [2] }
```

## ❌ Exemplos de erro

```json
{
  "status": 400,
  "erro": "Regra de negócio violada",
  "mensagem": "Um chamado só poderá ser alterado para EM_ANDAMENTO se possuir pelo menos um técnico vinculado."
}
```

| Situação | Status | Mensagem |
|---|---|---|
| Chamado não encontrado | 404 | `Chamado com o ID 99 não foi encontrado.` |
| Categoria não encontrada | 404 | `Categoria com ID 5 não encontrada.` |
| Técnico não encontrado | 404 | `Técnico com ID 3 não encontrado.` |
| Técnico inativo vinculado | 400 | `O técnico João Silva está inativo e não pode ser vinculado.` |
| Falta técnico para EM_ANDAMENTO | 400 | `Um chamado só poderá ser alterado para EM_ANDAMENTO se possuir pelo menos um técnico vinculado.` |
| Excluir chamado finalizado | 400 | `Não é possível excluir um chamado Finalizado.` |
| Alterar chamado finalizado | 400 | `Não é possível alterar um chamado finalizado.` |
| Excluir categoria vinculada | 409 | `Não é possível excluir um categoria vinculada a um chamado.` |
| Excluir técnico vinculado | 409 | `Não é possível excluir técnicos vinculados a chamados.` |
| E-mail de técnico duplicado | 409 | `Já existe um técnico cadastrado com esse e-mail.` |
| Sem autenticação em rota protegida | 401 | `Unauthorized` |

## 📊 Códigos HTTP utilizados

| Código | Situação |
|---|---|
| 200 OK | Consulta ou atualização bem-sucedida |
| 201 Created | Recurso criado com sucesso |
| 204 No Content | Recurso removido com sucesso |
| 400 Bad Request | Dados inválidos ou regra de negócio violada |
| 401 Unauthorized | Falta de autenticação em rota protegida |
| 404 Not Found | Recurso não encontrado |
| 409 Conflict | Conflito (duplicidade ou exclusão de recurso vinculado) |
| 500 Internal Server Error | Erro inesperado não tratado |

## 🧪 Testes

O projeto contém a classe de teste padrão gerada pelo Spring Initializr (`ControleChamadosApplicationTests`), que apenas valida a subida do contexto Spring (`contextLoads`). Não há testes unitários ou de integração cobrindo services/controllers.

---

## 👥 Autor

Projeto desenvolvido por **Mateus Sena** — SENAI — Projeto Final da disciplina de Programação de Aplicativos (SA 01 — Sistema de Controle de Chamados Técnicos).
