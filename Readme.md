<h1 align="center"> Queue Alert Engine </h1>

<p align="center">
  <img alt="LOgs" src="./image/notifier.png">
</p>

## 📦 queue-alert-engine

`queue-alert-engine` é um sistema criado em Spring Boot para realizar monitoramento de filas Kafka, detectar anomalias e possibilitar o disparo de alertas automáticos (via Telegram, Teams, e-mail etc).
Fornece também endpoints REST para consulta, debug, gatilho manual de monitoramentos e inspeção de offsets — todos documentados automaticamente com Swagger / OpenAPI.
É um projeto simples, didático e extensível, ideal para estudos, PoCs e práticas modernas de DevOps/Observability.

## 📌 Planejamento

- [Planejamento inicial do projeto](./docs/Planejamento.md)

## 🚀 Roadmap

- [Roadmap de execução do prjeto](./docs/Rodmap.md)

## 🧪 Testar com Kafka Local 

Se quiser rodar Kafka localmente para testar cenários: [Laboratório local](./local/Readme.md)

## 🌐 Fluxo geral do sistema

- [Desenho do Fluxo do serviço](./docs/fluxo.md)

## 📚 Lista dos Endpoints

- [Documentação dos edpoints](./docs/Controllers.md)