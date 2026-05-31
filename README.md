# Order-Service

Microsserviço responsável pelo gerenciamento de pedidos. Recebe requisições de criação e consulta de pedidos, busca informações de produtos via Feign, persiste os dados no PostgreSQL e publica eventos no Kafka para processamento assíncrono.

## Tecnologias

- Java 25
- Spring Boot 4.0.6
- Spring Cloud 2025.1.1 (OpenFeign)
- PostgreSQL + Spring Data JPA + Flyway
- Apache Kafka
- Docker / Kubernetes

## Endpoints

| Método | Endpoint | Header | Descrição |
|--------|----------|--------|-----------|
| `POST` | `/orders` | `idAccount` | Cria um novo pedido |
| `GET` | `/orders` | `idAccount` | Lista pedidos da conta (resumo) |
| `GET` | `/orders/{id}` | — | Busca detalhes de um pedido |
| `GET` | `/orders/health-check` | — | Health check |

### POST `/orders`

Cria um pedido com os itens informados. Para cada item, busca o produto no `product-service` para calcular o preço. Após persistir, publica o pedido completo no tópico `order-events`.

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

## Kafka — Publicação de eventos

O `OrderProducer` publica o pedido serializado como JSON no tópico `order-events` após cada criação bem-sucedida. A chave da mensagem é o `id` do pedido, garantindo que todos os eventos de um mesmo pedido caiam na mesma partição.

```
order-service ──(order-events, key=orderId)──► product-service
                                                └── reduceStock()
```

O tópico é criado automaticamente com **3 partições** e fator de replicação 1.

## Comunicação entre serviços

| Tipo | Serviço | Detalhe |
|------|---------|---------|
| Feign (síncrono) | `product` | `GET http://product:8080/products/{id}` — busca preço do produto |
| Kafka (assíncrono) | `order-events` | Publica pedido completo após criação (3 partições) |

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

## Kubernetes

O manifesto `k8s/k8s.yaml` cria:
- `Deployment` `order` com 1 réplica (imagem ECR)
- `Service` `order` do tipo `LoadBalancer`

O initContainer `wait-for-kafka` aguarda o broker Kafka (`kafka-service:9092`) ficar disponível antes de iniciar a aplicação. Credenciais do banco são lidas do secret `db-credentials`.

## Dependências externas

- **`store:order:1.0.0`** — biblioteca de contratos (DTOs e Feign client)
- **`product-service`** — consulta de preços de produtos
- **PostgreSQL** — persistência de pedidos
- **Kafka** — publicação de eventos de pedido
