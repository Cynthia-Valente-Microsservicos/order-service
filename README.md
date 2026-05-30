# order-service

Microsserviço responsável pelo gerenciamento de pedidos. Recebe requisições de criação e consulta de pedidos, busca informações de produtos via Feign, persiste os dados no PostgreSQL e publica eventos no Kafka.

## Tecnologias

- Java 25
- Spring Boot 4.0.6
- Spring Cloud 2025.1.1 (OpenFeign, Eureka Client)
- PostgreSQL + Spring Data JPA + Flyway
- Apache Kafka
- Docker

## Endpoints

| Método | Endpoint | Header | Descrição |
|--------|----------|--------|-----------|
| `POST` | `/orders` | `idAccount` | Cria um novo pedido |
| `GET` | `/orders` | `idAccount` | Lista pedidos da conta (resumo) |
| `GET` | `/orders/{id}` | — | Busca detalhes de um pedido |
| `GET` | `/orders/health-check` | — | Health check |

### POST `/orders`

Cria um pedido com os itens informados. Para cada item, busca o produto no `product-service` para calcular o preço.

**Request body:**
```json
{
  "items": [
    { "idProduct": "abc123", "quantity": 2 }
  ]
}
```

**Response:**
```json
{
  "id": "uuid",
  "date": "2026-05-30T10:00:00",
  "items": [
    {
      "id": "uuid",
      "product": { "id": "abc123" },
      "quantity": 2,
      "total": 49.90
    }
  ],
  "total": 49.90
}
```

## Banco de Dados

Schema PostgreSQL: `orders`

| Tabela | Descrição |
|--------|-----------|
| `orders.orders` | Pedidos (id, idAccount, date, total) |
| `orders.order_items` | Itens de cada pedido (id, idProduct, quantity, total) |

## Comunicação entre serviços

| Tipo | Serviço | Detalhe |
|------|---------|---------|
| Feign (síncrono) | `product` | `GET http://product:8080/products/{id}` — busca preço do produto |
| Kafka (assíncrono) | — | Publica em `order-events` (3 partições) após criação do pedido |
| Eureka | `eureka-container:8761` | Registro para service discovery |

## Variáveis de ambiente

| Variável | Descrição |
|----------|-----------|
| `DATABASE_HOST` | Host do PostgreSQL |
| `DATABASE_PORT` | Porta do PostgreSQL |
| `DATABASE_DB` | Nome do banco de dados |
| `DATABASE_USERNAME` | Usuário do banco |
| `DATABASE_PASSWORD` | Senha do banco |
| `KAFKA_BOOTSTRAP_SERVERS` | Endereço do broker Kafka (padrão: `localhost:9092`) |

## Executando localmente

### Com Docker (recomendado)

O serviço faz parte do `compose.yaml` na raiz de `api/`. Execute todos os serviços com:

```bash
docker compose up
```

### Build manual

```bash
mvn clean package -DskipTests
java -jar target/order-service-1.0.0.jar
```

O serviço sobe na porta `8080`.

## Dependências externas

- **`store:order:1.0.0`** — biblioteca de contratos (DTOs e Feign client)
- **`product-service`** — consulta de preços de produtos
- **PostgreSQL** — persistência de pedidos
- **Kafka** — publicação de eventos de pedido
- **Eureka Server** — service discovery
