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

    /**
     * Envia um ALERTA para o tópico configurado em kafka.producer.alert-topic
     */
    public void sendAlert(String message) {
        String topic = kafkaProperties.getProducer().getAlertTopic();

        kafkaTemplate.send(topic, message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("❌ Falha ao enviar ALERTA para {}: {}", topic, ex.getMessage(), ex);
                    } else {
                        log.info("📢 ALERTA enviado para {}: {}", topic, message);
                    }
                });
    }

    /**
     * Envia estado das filas monitoradas para kafka.producer.state-topic
     */
    public void sendState(String message) {
        String topic = kafkaProperties.getProducer().getStateTopic();

        kafkaTemplate.send(topic, message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("❌ Falha ao enviar STATE para {}: {}", topic, ex.getMessage(), ex);
                    } else {
                        log.info("📊 STATUS enviado para {}: {}", topic, message);
                    }
                });
    }
}
