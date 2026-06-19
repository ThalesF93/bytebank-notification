# ByteBank Notification Service

Microsserviço responsável pelo envio de notificações por e-mail no ecossistema ByteBank. Consome eventos de transação via RabbitMQ e notifica o cliente sempre que uma transferência é concluída com sucesso.

---

## Funcionalidades

**Notificação de transferência**
Consome o evento `TransactionCompletedEvent` da fila `transaction.completed` e envia um e-mail ao cliente com os detalhes da operação: nome, tipo e valor da transferência.

**Retry automático**
Em caso de falha no envio, o RabbitMQ reprocessa automaticamente com backoff exponencial — até 5 tentativas, com intervalo inicial de 5 segundos e multiplicador de 3.

---

## Stack

| Camada | Tecnologia |
|--------|------------|
| Framework | Spring Boot 3.x |
| Mensageria | RabbitMQ |
| E-mail | JavaMailSender (SMTP Gmail) |
| Observabilidade | Prometheus, Zipkin, Spring Boot Actuator |
| Testes | JUnit 5, Mockito |

---

## Arquitetura

```
src/main/java/br/com/bytebank/notification/
├── application/
│   ├── service/        # Interface EmailService
│   └── impl/           # EmailServiceImpl
└── infrastructure/
    ├── config/         # RabbitMQ, OpenAPI
    └── messaging/      # TransactionEventListener, eventos
```

O serviço é intencionalmente simples — sem controller HTTP, sem banco de dados. Só consome eventos e envia e-mails.

---

## Fluxo

```
bytebank-transactions publica TransactionCompletedEvent
        ↓
RabbitMQ → fila transaction.completed
        ↓
TransactionEventListener consome o evento
        ↓
EmailServiceImpl envia e-mail via SMTP
        ↓
Cliente recebe notificação com detalhes da transferência
```

---

## Evento Consumido

**`TransactionCompletedEvent`** — fila `transaction.completed`

```java
record TransactionCompletedEvent(
    UUID transactionId,
    UUID accountId,
    String customerEmail,
    String customerName,
    BigDecimal amount,
    String operationType,
    LocalDateTime occurredAt
)
```

Publicado pelo `bytebank-transactions` após uma transferência bem-sucedida.

---

## Como Executar

### Pré-requisitos

- Docker e Docker Compose instalados
- Rede Docker `bytebank-net` criada
- Conta Gmail com senha de app configurada

### Variáveis de Ambiente

```env
SPRING_RABBITMQ_HOST=rabbitmq
MAIL_USERNAME=seu-email@gmail.com
MAIL_PASSWORD=sua-senha-de-app
EUREKA_DEFAULT_ZONE=http://eureka-server:8761/eureka/
ZIPKIN_ENDPOINT=http://zipkin:9411/api/v2/spans
```

> A senha de app é gerada nas configurações de segurança do Google — não é a senha normal da conta.

### Subindo o serviço

```bash
docker compose -p bytebank-notification up -d --build
```

---

## Testes

Teste unitário do `EmailServiceImpl` verificando:

- Envio do e-mail com remetente, destinatário, assunto e corpo corretos
- Formatação correta da mensagem com nome, tipo e valor da transação

```bash
./gradlew test
```

---

## Autor

**Thales Fernandes**

[![GitHub](https://img.shields.io/badge/GitHub-ThalesF93-181717?style=flat&logo=github)](https://github.com/ThalesF93)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Thales_Fernandes-0A66C2?style=flat&logo=linkedin)](https://www.linkedin.com/in/thales-fernandes-24418126a/)
