## 📚 Lista dos Endpoints da Queue Alert Engine API

A API está dividida em 4 módulos:

1. Groups – configuração
2. Offsets – leitura de offsets consumidos
3. Monitor – gatilhos manuais de monitoramento
4. Alerts – envio de alertas de teste

## 🧭 Resumo geral dos endpoints

| Método | Endpoint                                              | Descrição                 |
| ------ | ----------------------------------------------------- | ------------------------- |
| GET    | `http://localhost:8080/api/groups`                    | Lista grupos configurados |
| GET    | `http://localhost:8080/api/groups/{groupId}`          | Detalhes de um grupo      |
| GET    | `http://localhost:8080/api/offsets/groups`            | Lista groupIds            |
| GET    | `http://localhost:8080/api/offsets/groups/{groupId}`  | Offsets consumidos        |
| GET    | `http://localhost:8080/api/monitor/groups`            | Lista grupos monitorados  |
| POST   | `http://localhost:8080/api/monitor/trigger-all`       | Trigger geral de lag      |
| POST   | `http://localhost:8080/api/monitor/trigger/{groupId}` | Trigger por grupo         |
| POST   | `http://localhost:8080/api/alerts/test`               | Envia alerta de teste     |

## 📄 Swagger / OpenAPI

✔️ Interface Swagger UI

```bash
http://localhost:8080/swagger-ui.html
```

✔️ OpenAPI JSON

```bash
http://localhost:8080/v3/api-docs
```
✔️ OpenAPI YAML

```bash
http://localhost:8080/v3/api-docs.yaml
```

## 📌 Roadmap Futuro

 - Autenticação nos endpoints
 - Painel Web de Monitoramento em tempo real
 - Suporte a múltiplos brokers Kafka
 - Exporters Prometheus