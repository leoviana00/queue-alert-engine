package io.viana.queue_alert_engine.listener;

import io.viana.queue_alert_engine.config.KafkaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueueListener {

    private final KafkaProperties kafkaProperties;

    /**
     * Consumidor do tópico de estado gerado pelo LagCheckerService
     */
    @KafkaListener(
            topics = "#{@kafkaProperties.producer.stateTopic}",
            groupId = "#{@kafkaProperties.consumer.groupId}"
    )
    public void consume(String message) {
        log.info("📥 Estado recebido do tópico '{}': {}",
                kafkaProperties.getProducer().getStateTopic(),
                message
        );
    }
}
