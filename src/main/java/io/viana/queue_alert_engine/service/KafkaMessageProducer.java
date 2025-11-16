package io.viana.queue_alert_engine.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaMessageProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Envia uma mensagem genérica para o Kafka
     * @param topic tópico de envio
     * @param key chave da mensagem (pode ser null)
     * @param message payload da mensagem
     */
    public void send(String topic, String key, String message) {
        kafkaTemplate.send(topic, key, message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("❌ Falha ao enviar mensagem para {}: {}", topic, ex.getMessage(), ex);
                    } else {
                        log.info("📢 Mensagem enviada para {}: {}", topic, message);
                    }
                });
    }
}
