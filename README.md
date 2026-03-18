# Desafio Itau Unibanco - API de Transacoes e Usuarios (Spring Boot)

API REST em Java 17 com Spring Boot 3 evoluida para um cenario mais proximo de producao:

- Clean Architecture (controller, usecase/service, domain, infrastructure)
- Persistencia real com PostgreSQL + Spring Data JPA + Flyway
- Autenticacao e autorizacao com JWT + BCrypt
- Documentacao OpenAPI/Swagger
- Tratamento global de erros com JSON estruturado
- Testes de integracao com PostgreSQL via Testcontainers

## Como rodar (Docker Compose)

```bash
docker compose -f transacao-api/docker-compose.yml up --build
```

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Health: `http://localhost:8080/actuator/health`

## Como rodar (local)

Requer Docker rodando (para o Spring Boot iniciar o Postgres via `docker-compose.dev.yml`) ou, alternativamente, PostgreSQL local em `localhost:5432`.

```powershell
cd transacao-api
.\mvnw.cmd spring-boot:run
```

## Autenticacao (JWT)

No primeiro start (fora do profile `test`), a aplicacao cria um usuario seed:

- email: `admin@example.com`
- senha: `admin123`

Login:

```bash
curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","senha":"admin123"}'
```

Use o token retornado em `Authorization: Bearer <token>`.

Endpoints que exigem autenticacao:

- `GET /usuarios/{id}/saldo`
- `POST /usuarios/{id}/deposito`
- `POST /transferencias`
- `GET /usuarios/{id}/transacoes`

## Endpoints do desafio (mantidos)

### `POST /transacao`

```json
{ "valor": 123.45, "dataHora": "2026-03-16T14:59:30Z" }
```

### `DELETE /transacao`

### `GET /estatistica`

Retorna estatisticas da janela configuravel (`app.statistics.window-seconds`).

## Usuarios e operacoes

### `POST /usuarios`

Cria usuario.

```json
{ "nome": "Kaique", "email": "kaique@example.com", "senha": "minha-senha" }
```

Compatibilidade: se `senha` nao for enviada e `app.user.registration.allow-temporary-password=true`, a API gera uma senha temporaria e retorna em `senhaTemporaria` apenas na resposta de criacao.

### `GET /usuarios/{id}`

### `GET /usuarios/{id}/transacoes?dataInicio=...&dataFim=...`

Historico (depositos e transferencias, origem e destino), com filtro opcional por periodo (ISO-8601).

## Testes

```powershell
cd transacao-api
.\mvnw.cmd test
```
