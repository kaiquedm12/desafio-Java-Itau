# 💳 Desafio Java Itaú - API de Transações

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.2-green?style=for-the-badge&logo=springboot)
![Maven](https://img.shields.io/badge/Maven-3.8+-blue?style=for-the-badge&logo=apachemaven)

API REST para gerenciamento de transações financeiras e cálculo de estatísticas em tempo real, desenvolvida como parte do desafio técnico do Itaú.

## 📋 Descrição

Este projeto implementa uma API que permite:
- Registrar transações financeiras com valor e data/hora
- Calcular estatísticas das transações dos últimos 60 segundos
- Limpar todas as transações registradas
- Health check para monitoramento da aplicação

## 🚀 Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 4.0.2**
- **Spring Web** - Para criação dos endpoints REST
- **Spring Actuator** - Para monitoramento e métricas
- **Lombok** - Para redução de código boilerplate
- **Maven** - Gerenciamento de dependências

## 📁 Estrutura do Projeto

```
transacao-api/
├── src/main/java/com/kaique/transacao_api/
│   ├── TransacaoApiApplication.java      # Classe principal
│   ├── business/
│   │   └── services/
│   │       └── TransacaoService.java     # Lógica de negócio
│   ├── controller/
│   │   ├── PingController.java           # Health check
│   │   ├── TransacaoController.java      # Endpoints principais
│   │   └── dtos/
│   │       ├── EstatisticaResponseDTO.java
│   │       └── TransacaoRequestDTO.java
│   └── infrastructure/
│       └── exceptions/
│           └── UnprocessableEntity.java  # Exceção customizada
└── pom.xml
```

## 🔧 Instalação e Execução

### Pré-requisitos
- Java 17 ou superior
- Maven 3.8 ou superior

### Passos para execução

1. **Clone o repositório:**
```bash
git clone https://github.com/kaiquedm12/desafio-Java-Itau.git
cd desafio-Java-Itau/transacao-api
```

2. **Compile o projeto:**
```bash
./mvnw clean install
```

3. **Execute a aplicação:**
```bash
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`

## 📡 Endpoints da API

### Transações

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/transacao` | Registra uma nova transação |
| `DELETE` | `/transacao` | Remove todas as transações |
| `GET` | `/estatistica` | Retorna estatísticas dos últimos 60 segundos |

### Health Check

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/health/ping` | Verifica se a API está ativa |

## 📝 Exemplos de Uso

### Registrar uma transação

```bash
curl -X POST http://localhost:8080/transacao \
  -H "Content-Type: application/json" \
  -d '{
    "valor": 150.50,
    "dataHora": "2026-02-02T10:30:00.000-03:00"
  }'
```

**Resposta:** `201 Created`

### Obter estatísticas

```bash
curl -X GET http://localhost:8080/estatistica
```

**Resposta:**
```json
{
  "count": 5,
  "sum": 750.25,
  "avg": 150.05,
  "min": 50.00,
  "max": 300.00
}
```

### Limpar transações

```bash
curl -X DELETE http://localhost:8080/transacao
```

**Resposta:** `204 No Content`

## 📊 Modelo de Dados

### TransacaoRequestDTO
| Campo | Tipo | Descrição |
|-------|------|-----------|
| `valor` | `Double` | Valor da transação (deve ser >= 0) |
| `dataHora` | `OffsetDateTime` | Data e hora da transação (não pode ser no futuro) |

### EstatisticaResponseDTO
| Campo | Tipo | Descrição |
|-------|------|-----------|
| `count` | `long` | Quantidade de transações nos últimos 60s |
| `sum` | `double` | Soma dos valores |
| `avg` | `double` | Média dos valores |
| `min` | `double` | Menor valor |
| `max` | `double` | Maior valor |

## ⚠️ Regras de Negócio

- **Valor negativo:** Transações com valor negativo são rejeitadas (HTTP 422)
- **Data futura:** Transações com data/hora no futuro são rejeitadas (HTTP 422)
- **Estatísticas:** São calculadas apenas com base nas transações dos últimos 60 segundos
- **Armazenamento:** As transações são armazenadas em memória (não persistidas)

## 🧪 Testes

Para executar os testes:

```bash
./mvnw test
```

## 📄 Licença

Este projeto foi desenvolvido como parte de um desafio técnico.
