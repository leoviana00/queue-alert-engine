## 📘 Queue Alert Engine — Roadmap do Projeto

Este documento apresenta o roadmap planejado para o desenvolvimento do Queue Alert Engine, detalhando as fases, objetivos e entregáveis principais até o deploy final.

## 🚀 Roadmap de Desenvolvimento

## 🟦 Fase 1 — Fundamentos do Projeto (Setup Inicial)

🎯 Objetivo:

- Preparar a base estrutural e de configuração da aplicação.

✅ Entregas:

Estrutura completa de pacotes:

- `config`: Configurações de Kafka e beans
- `listener`: Consumo Kafka
- `service`: Regras de negócio e monitoramento
- `alerts`: Integrações de envio
- `domain`: Entidades e modelos do motor de alerta
- `controller`: Endpoints REST futuros
- `notifier`: Enviar os alertas ao Telegram
- `scheduler`: Agendamento de verificação dos lags


## 🟩 Fase 2 — Definição do Domínio e Contratos

🎯 Objetivo:

- Definir os modelos centrais do sistema e os contratos da aplicação.

✅ Entregas:

- Modelos:
  - `AlertGroup`
  - `QueueAlert`
  - `QueueStatus`
  - `QueueStateEvent`
  - `TelegramMessage`
  - `AlertRule`
- Interface de alertas:
  - `AlertService`

## 🟨 Fase 3 — Integração com Kafka

🎯 Objetivo:

Fazer a aplicação consumir mensagens reais do tópico Kafka.

✅ Entregas:

- Listener Kafka:
  - QueueEventListener
  - TelegramAlertConsumer

## 🟧 Fase 4 — Processamento e Regras de Negócio

🎯 Objetivo:

Avaliar mensagens e decidir quando emitir alertas.

✅ Entregas:

- Serviço de monitoramento:
  - LagCheckService
  - QueueOffsetTracker
- Lógica:
  - Thresholds configuráveis
  - Aplicação de AlertRule

## 🟥 Fase 5 — Envio de Alertas

🎯 Objetivo:

- Enviar alertas reais para canais externos (Telegram, Teams).

✅ Entregas:

- Implementações:
  - AlertService
  - AlertDispatcher
  - StateDispatcher
  - TelegramNotifier

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

## 🟫 Fase 7 — Qualidade, Testes e Confiabilidade ~ TO DO

🎯 Objetivo:

- Garantir estabilidade e segurança no fluxo de negócio.

✅ Entregas:

- Testes unitários:
  - Producer, Consumer, Services e Alertas

- Testes integrados:
  - Kafka com TestContainers

- Handler global:
  - GlobalExceptionHandler via @ControllerAdvice

## ⬛ Fase 8 — Observabilidade e Monitoramento ~ TO DO

🎯 Objetivo:

- Obter visibilidade e mensurar o comportamento da aplicação.

✅ Entregas:

- Logback estruturado
- Métricas Prometheus:
  - queue_size
  - alerts_sent
  - processing_time
- Dashboard Grafana (futuro)

## ⬜ Fase 9 — Empacotamento e Deploy ~ TO DO

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

## 🟦 Fase 10 — Evoluções Futuras (Roadmap Pós-MVP) ~ TO DO

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
  - [x] 1	Estrutura + Configurações
  - [x] 2	Modelos + Contratos
  - [x] x	Kafka funcionando
  - [x] 4	Regras de negócio
  - [x] 5	Alertas reais
  - [x] 6	Scheduler + API
  - [ ] 7	Testes + Exceções
  - [ ] 8	Observabilidade
  - [ ] 9	Deploy
  - [ ] 10	Melhorias futuras