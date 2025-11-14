## 📘 Queue Alert Engine — Roadmap do Projeto

Este documento apresenta o roadmap planejado para o desenvolvimento do Queue Alert Engine, detalhando as fases, objetivos e entregáveis principais até o deploy final.

## 🚀 Roadmap de Desenvolvimento

## 🟦 Fase 1 — Fundamentos do Projeto (Setup Inicial)

🎯 Objetivo:

- Preparar a base estrutural e de configuração da aplicação.

✅ Entregas:

Estrutura completa de pacotes:

- config/
- listener/
- service/
- alerts/
- scheduler/
- domain/
- exception/
- controller/ (futuro)


Arquivos de configuração:

- application.yaml
- application-local.yaml
- application-prod.yaml

Configurações iniciais do Kafka:

- KafkaProducerConfig.java
- KafkaConsumerConfig.java
- KafkaTopicConfig.java

Classe de propriedades:

- AppProperties.java via @ConfigurationProperties

## 🟩 Fase 2 — Definição do Domínio e Contratos

🎯 Objetivo:

- Definir os modelos centrais do sistema e os contratos da aplicação.

✅ Entregas:

- Modelos:
  - `QueueStatus`
  - `QueueMessage`
  - `AlertRule`
- Interface de alertas:
  - `AlertService`

## 🟨 Fase 3 — Integração com Kafka

🎯 Objetivo:

Fazer a aplicação consumir mensagens reais do tópico Kafka.

✅ Entregas:

- Listener Kafka:
  - QueueEventListener
- Teste manual via Kafka local (docker-compose)
- Fluxo: Kafka → Listener → Log básico

## 🟧 Fase 4 — Processamento e Regras de Negócio

🎯 Objetivo:

Avaliar mensagens e decidir quando emitir alertas.

✅ Entregas:

- Serviço:
  - MessageProcessorService
  - Validação e regras
- Serviço de monitoramento:
  - QueueMonitorService
- Lógica:
  - Thresholds configuráveis
  - Aplicação de AlertRule

## 🟥 Fase 5 — Envio de Alertas

🎯 Objetivo:

- Enviar alertas reais para canais externos (Telegram, Teams).

✅ Entregas:

- Implementações:
  - TelegramAlertService
  - TeamsAlertService

- Tratamento de exceções:
  - AlertSendException

- Logs estruturados de envio:
  - Sucesso
  - Falha

## 🟪 Fase 6 — Agendadores e Endpoints REST

🎯 Objetivo:

- Automatizar o monitoramento e preparar a API pública.

✅ Entregas:

- Scheduler:
  - QueueMonitorScheduler

- Cron configurável no application.yaml
  - Controller (futuro):
  - QueueMonitorController

- Endpoints como:
  - `/health`
  - `/alerts/test`
  - `/queues/status`

## 🟫 Fase 7 — Qualidade, Testes e Confiabilidade

🎯 Objetivo:

- Garantir estabilidade e segurança no fluxo de negócio.

✅ Entregas:

- Testes unitários:
  - Producer, Consumer, Services e Alertas

- Testes integrados:
  - Kafka com TestContainers

- Handler global:
  - GlobalExceptionHandler via @ControllerAdvice

## ⬛ Fase 8 — Observabilidade e Monitoramento

🎯 Objetivo:

- Obter visibilidade e mensurar o comportamento da aplicação.

✅ Entregas:

- Logback estruturado
- Métricas Prometheus:
  - queue_size
  - alerts_sent
  - processing_time
- Dashboard Grafana (futuro)

## ⬜ Fase 9 — Empacotamento e Deploy

🎯 Objetivo:

Entregar a aplicação para execução real.

✅ Entregas:

- Dockerfile da aplicação
- docker-compose para desenvolvimento local
- Deploy em Kubernetes (AKS, K3S ou EKS)
- CI/CD para build/test/deploy
- Configuração de Secrets:
  - Tokens do Telegram
  - Tokens do Teams

## 🟦 Fase 10 — Evoluções Futuras (Roadmap Pós-MVP)

🎯 Objetivo:

- Extender o sistema com novas funcionalidades.

🔮 Possibilidades:

- Novos canais de alerta:
  - Slack, Email, SMS
- Interface Web (Dashboard)
- Persistência das mensagens e alertas:
  - PostgreSQL ou MongoDB
- Alertas baseados em IA/Anomaly Detection
- Gestão dinâmica de regras via API

## ✔️ Resumo Geral 

- Fase	Entrega Principal
  - [ ] 1	Estrutura + Configurações
  - [ ] 2	Modelos + Contratos
  - [ ] 3	Kafka funcionando
  - [ ] 4	Regras de negócio
  - [ ] 5	Alertas reais
  - [ ] 6	Scheduler + API
  - [ ] 7	Testes + Exceções
  - [ ] 8	Observabilidade
  - [ ] 9	Deploy
  - [ ] 10	Melhorias futuras