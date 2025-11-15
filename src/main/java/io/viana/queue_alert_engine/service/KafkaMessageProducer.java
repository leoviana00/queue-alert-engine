package io.viana.queue_alert_engine.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import io.viana.queue_alert_engine.config.KafkaProperties;


@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaMessageProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaProperties kafkaProperties;

    public void sendMessage(String message) {
        String topic = kafkaProperties.getProducer().getTopic();
        kafkaTemplate.send(topic, message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("❌ Falha ao enviar mensagem para {}: {}", topic, ex.getMessage(), ex);
                    } else {
                        log.info("✅ Mensagem enviada para {}: {}", topic, message);
                    }
                });
    }
}
