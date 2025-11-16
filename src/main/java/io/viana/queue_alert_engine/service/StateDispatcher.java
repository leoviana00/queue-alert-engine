package io.viana.queue_alert_engine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.viana.queue_alert_engine.config.KafkaProperties;
import io.viana.queue_alert_engine.domain.QueueStateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StateDispatcher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final KafkaProperties kafkaProperties;

    /**
     * Publica o estado da fila no tópico configurado em kafka.producer.state-topic
     */
    public void sendQueueState(QueueStateEvent stateEvent) {
        try {
            String topic = kafkaProperties.getProducer().getStateTopic();
            String payload = objectMapper.writeValueAsString(stateEvent);

            kafkaTemplate.send(topic, stateEvent.getTopic(), payload)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("❌ Falha ao enviar estado para {}: {}", topic, ex.getMessage(), ex);
                        } else {
                            log.info("📊 Estado publicado no tópico '{}': {}", topic, payload);
                        }
                    });

        } catch (JsonProcessingException e) {
            log.error("❌ Erro ao serializar QueueStateEvent", e);
        }
    }
}
