package io.viana.queue_alert_engine.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;

import io.viana.queue_alert_engine.config.KafkaProperties;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class QueueOffsetTracker {

    @Getter
    private final Map<String, Long> topicPendingMessages = new ConcurrentHashMap<>();

    @Getter
    private final List<String> monitoredTopics;

    private final String consumerGroupId;

    public QueueOffsetTracker(KafkaProperties kafkaProperties) {

        this.monitoredTopics = kafkaProperties.getAlertRules()
                .stream()
                .map(rule -> rule.topic())
                .distinct() // evita duplicados
                .toList();

        this.consumerGroupId = kafkaProperties.getConsumer().getGroupId();

        monitoredTopics.forEach(topic -> topicPendingMessages.put(topic, 0L));

        log.info("📝 Tópicos monitorados inicializados ({}): {}", monitoredTopics.size(), monitoredTopics);
        log.info("🔐 Consumer Group: {}", consumerGroupId);
    }

    /**
     * Recebido pelo listener dinâmico via MethodKafkaListenerEndpoint.
     */
    public void onMessage(ConsumerRecord<String, String> record) {

        // Só processa tópicos monitorados (segurança extra)
        if (!topicPendingMessages.containsKey(record.topic())) {
            log.warn("⚠️ Mensagem recebida de tópico NÃO monitorado: {}", record.topic());
            return;
        }

        topicPendingMessages.merge(record.topic(), 1L, Long::sum);

        if (log.isDebugEnabled()) {
            log.debug("📥 [{}] +1 mensagem pendente | Novo total={}", record.topic(),
                    topicPendingMessages.get(record.topic()));
        }
    }

    /**
     * Decrementa o número de mensagens pendentes de um tópico monitorado.
     * Nunca deixa o valor negativo.
     */
    public void decrementPending(String topic, long count) {

        if (!topicPendingMessages.containsKey(topic)) {
            log.warn("⚠️ Tentativa de decrementar tópico não monitorado: {}", topic);
            return;
        }

        topicPendingMessages.compute(topic, (t, oldVal) -> {
            if (oldVal == null) return 0L;

            long updated = oldVal - count;
            return Math.max(0, updated);
        });

        log.debug("📉 [{}] -{} mensagens | Total={}", topic, count, topicPendingMessages.get(topic));
    }
}
