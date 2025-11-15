package io.viana.queue_alert_engine.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import io.viana.queue_alert_engine.config.KafkaProperties;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueueListener {

    private final KafkaProperties kafkaProperties;

    /**
     * Consumidor genérico usando tópico e grupo do KafkaProperties
     */
    @KafkaListener(
            topics = "#{@kafkaProperties.producer.topic}", // pega do YAML
            groupId = "#{@kafkaProperties.consumer.groupId}"
    )
    public void consume(String message) {
        log.info("📥 Mensagem recebida do tópico '{}': {}", kafkaProperties.getProducer().getTopic(), message);
    }
}
