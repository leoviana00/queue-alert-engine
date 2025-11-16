<h1 align="center"> Queue Alert Engine </h1>

<p align="center">
  <img alt="Kafka" src="./image/notifier.png">
</p>

## 📦 queue-alert-engine

`queue-alert-engine` é um sistema criado em Spring Boot para realizar monitoramento de filas Kafka, detectar anomalias e possibilitar o disparo de alertas automáticos (via Telegram, Teams, e-mail etc).
É um projeto simples, didático e extensível, ideal para estudos, PoCs e práticas modernas de DevOps/Observability.

## 📌 Planejamento

- [Planejamento inicial do projeto](./docs/Planejamento.md)

## 🚀 Roadmap

- [Roadmap de execução do prjeto](./docs/Rodmap.md)

## 🧪 Testar com Kafka Local 

Se quiser rodar Kafka localmente para testar cenários: [Laboratório local](./local/Readme.md)

## Fluxo resumido

```console
   ┌────────────────────────────┐
   │    QueueMonitorScheduler   │
   │  (executa periodicamente)  │
   └───────────┬────────────────┘
               │
               ▼
     ┌───────────────────┐
     │ LagCheckerService │
     │  - calcula lag    │
     │  - cria eventos   │
     │  - dispara alertas│
     └─────────┬─────────┘
               │
       ┌───────┴──────────────────┬─────────────────────────┐
       │                          │                         │
       ▼                          ▼                         ▼   
┌────────────────────┐   ┌────────────────────┐   ┌─────────────────────┐
│ QueueOffsetTracker │   │ AlertDispatcher    │   │  StateDispatcher    │   
└────────────────────┘   │  - envia alertas   │   │  - publica estados  │    
                         └────────────────────┘   └─────────────────────┘
                                  │                         │
                                  └────────────┬────────────┘
                                               │
                                               ▼
                                    ┌──────────────────────┐                ┌─────────────────────────┐  
                                    │ KafkaMessageProducer │                │ Telegram Alert Consumer │ 
                                    │  - envia mensagens   │                │ - consome Alerts Queue  │ 
                                    └──────────┬───────────┘                └─────────────┬───────────┘
                                               │                                          │
                                  ┌──────────────────────────┐     ┌──────────────────────┘
                                  │                          │     │                      │ 
                                  ▼                          ▼     ▼                      ▼
                        ┌──────────────────────┐     ┌─────────────────────┐      ┌────────────────────────────┐      
                        │    State Queue       │     │   Alerts Queue      │      │   Alerts Service           │
                        │- armazena os estados │     │- armazena os alertas│      │  - recebe msg do consumer  │                      
                        └──────────────────────┘     └─────────────────────┘      │  - converte a msg em alerta│
                                                                                  │  - chama o notifier        │
                                                                                  └─────────────┬──────────────┘                                                         │                      
                                                                  ┌─────────────────────────────┘
                                                                  ▼
                                                      ┌──────────────────────┐ 
                                                      │   Telegram Notifier  │ 
                                                      │- Notifica no Telegram│
                                                      └──────────────────────┘
```                


1. `QueueMonitorScheduler` dispara a execução a cada minuto.
2. `LagCheckerService` recebe os grupos e regras do AlertsProperties.
3. Para cada tópico/partição:
  - Consulta último offset consumido (`QueueOffsetTracker`).
  - Consulta último offset produzido (AdminClient).
  - Calcula lag e determina QueueStatus.
  - Cria QueueStateEvent.
  - Publica estado via `StateDispatcher`.
  - Dispara alerta se status >= WARNING via `AlertDispatcher`.
4. `AlertDispatcher` e `StateDispatcher` usam `KafkaMessageProducer` para enviar mensagens ao Kafka.
5. Modulo de notificação
  - `TelegramAlertConsumer` consme o topico de alertas no kafka e chama o `AlertService`.
  - `AlertService` Processa a mensagem ea transforma em um alerta estruturado para envio ao Telegram chamando o `TelegramNotifier`.
  - `TelegramNotifier` envia o alerta para o canal no telegram.