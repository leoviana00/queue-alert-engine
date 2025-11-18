## Planejamento

## 🚀 Queue Alert Engine — Descrição do Projeto

O Queue Alert Engine é um serviço de monitoramento de filas baseado em Kafka, desenvolvido para identificar atrasos, lentidões ou comportamentos anormais no processamento de mensagens. Ele permite gerar alertas inteligentes e integrados com ferramentas externas, como Telegram e Microsoft Teams, garantindo visibilidade em tempo real sobre o fluxo de eventos dentro do ecossistema distribuído da empresa.

## 🎯 Objetivo Principal

Monitorar tópicos Kafka em tempo real, avaliar o volume e o padrão de consumo das mensagens e emitir alertas automáticos quando uma fila apresenta problemas — tudo de forma configurável, extensível e fácil de operar.

## 📦 Funcionalidades 

🔹 1. Consumo de Tópicos Kafka

- Listener configurado para consumir mensagens do tópico principal.
- Processamento por registro (ack-mode record).
- Possibilidade de adicionar múltiplos listeners no futuro.

🔹 2. Análise e Processamento de Mensagens

- Serviço dedicado (MessageProcessorService) para analisar dados recebidos.
- Extração das informações relevantes para avaliação de saúde da fila.
- Identificação de mensagens problemáticas ou fora do formato esperado.

🔹 3. Monitoramento Periódico da Fila

- Scheduler (QueueMonitorScheduler) executa análises cíclicas.
- Mede volume, taxa e comportamento das mensagens.
- Integra-se com regras personalizadas definidas pela camada de domínio (AlertRule).

🔹 4. Envio de Alertas

- Camada de Alert Services com implementação plugável:

✔️ Telegram

- Envia alertas diretamente para um chat ou grupo via Bot API.

✔️ Fácil expansão para outros canais:

- Slack
- Email
- Discord

> NOTE!
> A adição de novos canais exige apenas criar uma nova implementação de AlertService.

🔹 5. Estrutura Modular e Extensível

Arquitetura limpa dividida em camadas:

  - `config`: Configurações de Kafka e beans
  - `listener`: Consumo Kafka
  - `service`: Regras de negócio e monitoramento
  - `alerts`: Integrações de envio
  - `domain`: Entidades e modelos do motor de alerta
  - `controller`: Endpoints REST futuros
  - `notifier`: Enviar os alertas ao Telegram
  - `scheduler`: Agendamento de verificação dos lags

## 🛠️ Funcionalidades Planejadas inicialmente / Roadmap

🟦 1. API REST de Observabilidade

- Endpoints como:
  - GET /queues/status
  - GET /queues/{topic}/metrics
  - POST /alerts/test


🟦 2. Configuração Dinâmica de Regras

- Uso de banco ou arquivo externo para definir, por exemplo:
  - limite de mensagens pendentes,
  - tempo máximo parado,
  - padrão de mensagens,
  - quantidade mínima de consumo por minuto.

🟦 3. Armazenamento de Métricas

- Suporte para:
  - Prometheus
  - Grafana
  - OpenTelemetry

🟦 4. Múltiplos Brokers Kafka

- Permitir monitorar vários clusters simultaneamente:
  - Produção
  - Homologação
  - Dev

🟦 5. Mecanismo de Anomalia (Machine Learning Light)

- Exemplo:
  - detectar picos atípicos,
  - variações abruptas de chegada/consumo,
  - "fila travada" por comportamento histórico.

## 🔧 Tecnologias Utilizadas

- Java 17+
- Spring Boot 3+
- Spring Kafka
- Kafka / Zookeeper
- Docker Compose
- Telegram Bot API
- Teams Webhook
- Lombok
- SLF4J / Logback

## 🌐 Fluxo Geral do Sistema - Pensado inicialmete

```console
Kafka Topic -> Listener -> Processor -> Monitor -> AlertService -> Telegram/Teams
```

> NOTE!
> O projeto funciona como um engine independente, que consome mensagens, monitora o comportamento das filas e envia alertas proativamente.

## ✅ Estrutura pensada iicialmente

```console
src/
 └── main/
     ├── java/
     │   └── io.viana.queue_alert_engine/
     │        ├── config/                 → Configurações gerais e do Kafka
     │        │     ├── KafkaProducerConfig.java
     │        │     ├── KafkaConsumerConfig.java
     │        │     ├── KafkaTopicConfig.java
     │        │     └── AppProperties.java (@ConfigurationProperties)
     │        │
     │        ├── alerts/                 → Serviços de envio de alertas
     │        │     ├── AlertService.java
     │        │     ├── TelegramAlertService.java
     │        │     └── TeamsAlertService.java
     │        │
     │        ├── listener/               → Consumidores Kafka
     │        │     └── QueueListener.java
     │        │
     │        ├── service/                → Regras de negócio e monitoramento
     │        │     ├── QueueMonitorService.java
     │        │     └── MessageProcessorService.java
     │        │
     │        ├── scheduler/              → Tarefas agendadas
     │        │     └── QueueMonitorScheduler.java
     │        │
     │        ├── controller/             → Endpoints REST (futuro)
     │        │     └── QueueMonitorController.java
     │        │
     │        ├── domain/                 → Objetos de domínio (modelos)
     │        │     ├── QueueStatus.java
     │        │     ├── QueueMessage.java
     │        │     └── AlertRule.java
     │        │
     │        └── exception/              → Exceções + Handler
     │              ├── QueueNotFoundException.java
     │              ├── AlertSendException.java
     │              └── GlobalExceptionHandler.java (@ControllerAdvice)
     │
     └── resources/
         ├── application.yaml
         ├── application-local.yaml        → configurações locais (pode incluir Kafka)
         ├── application-prod.yaml
         └── logback.xml
```