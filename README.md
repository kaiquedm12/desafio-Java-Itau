# Desafio Itaú Unibanco - API de Transações

API REST em Java 17 com Spring Boot 3 para receber transações, limpar dados em memória e calcular estatísticas dos últimos 60 segundos.

Extras: endpoints para cadastro de usuários, depósito, consulta de saldo e transferência entre usuários (tudo em memória).

## Tecnologias

- Java 17
- Spring Boot 3.3.5
- Spring Web
- Spring Actuator
- Maven Wrapper
- JUnit 5

## Requisitos atendidos

- Endpoints exatamente como especificados: `/transacao` e `/estatistica`
- Armazenamento totalmente em memória
- Apenas JSON nas entradas e respostas com corpo
- Validação de transações com retorno `422 Unprocessable Entity`
- JSON inválido com retorno `400 Bad Request`
- Estatísticas calculadas somente dentro da janela configurável
- Testes automatizados cobrindo serviço e contrato HTTP
- Health check extra em `/health/ping` e `/actuator/health`
- Containerização com Docker

## Como executar

Na raiz da aplicação:

```bash
cd transacao-api
./mvnw spring-boot:run
```

No Windows:

```powershell
cd transacao-api
.\mvnw.cmd spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

## Como testar

```bash
cd transacao-api
./mvnw test
```

No Windows:

```powershell
cd transacao-api
.\mvnw.cmd test
```

## Endpoints obrigatórios

### `POST /transacao`

Recebe uma transação no formato:

```json
{
  "valor": 123.45,
  "dataHora": "2020-08-07T12:34:56.789-03:00"
}
```

Regras:

- `valor` é obrigatório e deve ser maior ou igual a `0`
- `dataHora` é obrigatória e não pode estar no futuro
- qualquer JSON malformado retorna `400 Bad Request`
- qualquer violação de regra de negócio retorna `422 Unprocessable Entity`

Respostas:

- `201 Created` sem corpo
- `400 Bad Request` sem corpo
- `422 Unprocessable Entity` sem corpo

### `DELETE /transacao`

Remove todas as transações armazenadas.

Resposta:

- `200 OK` sem corpo

### `GET /estatistica`

Retorna estatísticas das transações ocorridas nos últimos `60` segundos:

```json
{
  "count": 10,
  "sum": 1234.56,
  "avg": 123.456,
  "min": 12.34,
  "max": 123.56
}
```

Quando não houver transações na janela, todos os campos retornam `0`.

## Endpoints adicionais (extras)

Observação: esta API retorna corpo vazio para erros `400`, `404` e `422` (apenas o status HTTP).

### `POST /usuarios`

Cria um usuário em memória (saldo inicial `0.00`).

Request:

```json
{
  "nome": "Kaique",
  "email": "kaique@example.com"
}
```

Regras:

- `nome` é obrigatório (até 120 caracteres)
- `email` é obrigatório (até 254 caracteres), normalizado para minúsculo e deve conter `@`
- não permite email duplicado (case-insensitive) -> `422 Unprocessable Entity`

Respostas:

- `201 Created` com JSON do usuário (inclui `id` e `saldo`)
- `400 Bad Request` sem corpo (JSON inválido)
- `422 Unprocessable Entity` sem corpo (validação/regra de negócio)

Exemplo de resposta:

```json
{
  "id": "00000000-0000-0000-0000-000000000000",
  "nome": "Kaique",
  "email": "kaique@example.com",
  "saldo": 0.00
}
```

### `GET /usuarios/{id}`

Busca um usuário por `id`.

Respostas:

- `200 OK` com JSON do usuário
- `404 Not Found` sem corpo (não existe)

### `GET /usuarios/{id}/saldo`

Consulta o saldo atual do usuário.

Respostas:

- `200 OK`
- `404 Not Found` sem corpo (não existe)

Exemplo de resposta:

```json
{ "saldo": 10.00 }
```

### `POST /usuarios/{id}/deposito`

Adiciona saldo ao usuário.

Request:

```json
{ "valor": 10.00 }
```

Regras:

- `valor` é obrigatório, deve ser maior que `0` e ter no máximo 2 casas decimais

Respostas:

- `200 OK` com o saldo atualizado
- `400 Bad Request` sem corpo (JSON inválido)
- `404 Not Found` sem corpo (usuário não existe)
- `422 Unprocessable Entity` sem corpo (validação/regra de negócio)

### `POST /transferencias`

Transfere saldo entre dois usuários.

Request:

```json
{
  "origemId": "00000000-0000-0000-0000-000000000000",
  "destinoId": "11111111-1111-1111-1111-111111111111",
  "valor": 7.50
}
```

Regras:

- `origemId` e `destinoId` são obrigatórios e devem ser diferentes
- `valor` é obrigatório, deve ser maior que `0` e ter no máximo 2 casas decimais
- saldo insuficiente -> `422 Unprocessable Entity`

Respostas:

- `201 Created` com os saldos pós-transferência
- `400 Bad Request` sem corpo (JSON inválido)
- `404 Not Found` sem corpo (origem/destino não existe)
- `422 Unprocessable Entity` sem corpo (validação/regra de negócio)

Exemplo de resposta:

```json
{
  "origemId": "00000000-0000-0000-0000-000000000000",
  "destinoId": "11111111-1111-1111-1111-111111111111",
  "valor": 7.50,
  "saldoOrigem": 12.50,
  "saldoDestino": 7.50
}
```

## Configuração

A janela de cálculo pode ser configurada em [transacao-api/src/main/resources/application.properties](transacao-api/src/main/resources/application.properties):

```properties
app.statistics.window-seconds=60
```

## Extras implementados

- Logs no serviço de transações
- Health check JSON em `GET /health/ping`
- Actuator health em `GET /actuator/health`
- Cadastro de usuários, depósito, consulta de saldo e transferência (em memória)
- Containerização com Docker

## Docker

Build da imagem:

```bash
docker build -t transacao-api ./transacao-api
```

Execução:

```bash
docker run --rm -p 8080:8080 transacao-api
```

## Docker Compose

Suba a aplicação com um único comando:

```bash
docker compose -f transacao-api/docker-compose.yml up --build
```

Para parar e remover os recursos criados:

```bash
docker compose -f transacao-api/docker-compose.yml down
```

## Licença

Este projeto foi desenvolvido como parte de um desafio técnico.

