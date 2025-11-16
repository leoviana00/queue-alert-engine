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
                                    ┌──────────────────────┐
                                    │ KafkaMessageProducer │
                                    │  - envia mensagens   │
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