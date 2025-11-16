package io.viana.queue_alert_engine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.viana.queue_alert_engine.config.KafkaProperties;
import io.viana.queue_alert_engine.domain.AlertRule;
import io.viana.queue_alert_engine.domain.QueueStateEvent;
import io.viana.queue_alert_engine.domain.QueueStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertDispatcher {

    private final KafkaMessageProducer kafkaProducer;
    private final ObjectMapper objectMapper;
    private final KafkaProperties kafkaProperties;

    /**
     * Envia alerta se status for WARNING ou CRITICAL
     */
    public void dispatchAlert(String groupId, AlertRule rule, long lag, QueueStatus status) {
        if (status == QueueStatus.WARNING || status == QueueStatus.CRITICAL) {
            String json = String.format("""
                {
                  "groupId": "%s",
                  "topic": "%s",
                  "partition": %d,
                  "lag": %d,
                  "level": "%s"
                }
                """, groupId, rule.topic(), rule.partition(), lag, status.name());

            kafkaProducer.send(kafkaProperties.getProducer().getAlertTopic(), rule.topic(), json);
        }
    }

    /**
     * Envia estado da fila
     */
    public void dispatchState(QueueStateEvent stateEvent) {
        try {
            String payload = objectMapper.writeValueAsString(stateEvent);
            kafkaProducer.send(kafkaProperties.getProducer().getStateTopic(), stateEvent.getTopic(), payload);
            log.info("📤 Estado publicado no tópico '{}': {}", kafkaProperties.getProducer().getStateTopic(), payload);
        } catch (JsonProcessingException e) {
            log.error("❌ Erro ao serializar QueueStateEvent", e);
        }
    }
}
