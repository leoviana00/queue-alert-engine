## 📦 queue-alert-engine

`queue-alert-engine` é um sistema criado em Spring Boot para realizar monitoramento de filas Kafka, detectar anomalias e possibilitar o disparo de alertas automáticos (via Telegram, Teams, e-mail etc).
É um projeto simples, didático e extensível — ideal para estudos, PoCs e práticas modernas de DevOps/Observability.

## ✨ Funcionalidades

✔️ Monitoramento periódico das filas (via Scheduler)

🔜 Estrutura básica de integração com Kafka

🔜 Serviço central de monitoramento (QueueMonitorService)

🔜 Logs estruturados

🔜 Arquitetura limpa por camadas

🔜 Alertas (Telegram, Teams)

🔜 Métricas Prometheus/Grafana

🔜 Cálculo de Lag e Dead Letter Queue

## 📂 Estrutura do Projeto

```console
src/
 └── main/
     ├── java/
     │   └── io.viana.queue_alert_engine/
     │        ├── config/          → Configurações Kafka
     │        ├── scheduler/       → Rotinas agendadas
     │        ├── service/         → Lógica de monitoramento
     │        ├── listener/        → Consumo de tópicos Kafka
     │        └── controller/      → Endpoints REST (futuro)
     └── resources/
         ├── application.yaml      → Configurações da aplicação
         └── logback.xml           → Logs (opcional)
```

## ⚙️ Tecnologias Utilizadas

- Java 17
- Spring Boot 3.x
- Spring for Apache Kafka
- Lombok
- Scheduler (Spring Scheduling)

## 🚀 Como executar o projeto

1️⃣ Clonar repositório
```bash
git clone https://github.com/seu-usuario/queue-alert-engine.git
cd queue-alert-engine
```

2️⃣ Gerar build
```bash
mvn clean install
```

3️⃣ Subir a aplicação
```bash
mvn spring-boot:run
```

- A API sobe em:
```bash
http://localhost:8080
```

## 🕒 Monitoramento Automático

A aplicação executa automaticamente o monitoramento a cada 30 segundos.

Log esperado:
```bash
⏱️ Executando scheduler de monitoramento das filas...
Executando monitoramento das filas...
⚠️ Problema detectado na fila!
```

## 🧪 Testar com Kafka Local 

Se quiser rodar Kafka localmente para testar cenários: [Laboraório local](./local/Readme.md)
