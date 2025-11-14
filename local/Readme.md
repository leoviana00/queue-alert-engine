## 🔥 Agora, como simular diferentes cenários:


### 🎯 Cenário 1 – Lag crescente

- Pare o consumidor:

```bash
docker compose stop consumer
```

- O produtor continua enviando mensagens e o lag explode.

> !NOTE
> Essa é exatamente a situação que o queue-alert-engine deve detectar.

### 🎯 Cenário 2 – Consumidor travado

- Deixe o grupo de consumidores rodar.
- Pare e inicie rapidamente o consumidor algumas vezes:

```bash
docker compose restart consumer
```
- Isso simula:
  - consumidor instável
  - rebalanceamento de partições
  - atraso no commit

> !NOTE
> Ótimo para testar alertas.

### 🎯 Cenário 3 – Tópicos com partições desbalanceadas

- Crie tópicos com partições diferentes:

```bash
docker exec -it kafka kafka-topics \
  --create --topic test-topic-8p \
  --partitions 8 --replication-factor 1 \
  --bootstrap-server kafka:29092
```

- Isso ajuda a testar:
  - offsets por partição
  - lag individual
  - consumidores lentos

### 🎯 Cenário 4 – Tópico sem novas mensagens

- Basta parar o produtor:

```bash
docker compose stop producer
```

- O monitor deve detectar:
  - ausência de dados
  - “stalled topic”

”

### 🎯 Cenário 5 – Consumidor sem commit

- Simule um consumidor que lê, mas não faz commit:

```bash
kcat -C -b localhost:9092 -t test-topic -o beginning -q
```

- Isso deixa o lag constante mesmo consumindo, excelente para teste de detecção.

### 🎯 Cenário 6 – Simulação com Testcontainers (Testes Automáticos)

- No projeto Java, você pode adicionar:
```java
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>kafka</artifactId>
    <version>1.19.0</version>
    <scope>test</scope>
</dependency>
```
- E criar cenários automatizados como:
  - lag artificial
  - consumidor inexistente
  - topic sem partições
  - offsets inconsistentes

### 🧪 Onde você acompanha tudo?

- ✔ Kafka UI (porta 8081)
  - lag por consumer group
  - mensagens
  - offsets
  - partições

- ✔ Logs do queue-alert-engine
  - Seu serviço deve emitir logs como:

```bash
⚠️ Lag alto no tópico test-topic (group consumer-group-app): 14.232 mensagens
⚠️ Consumidor inativo há 47s
```