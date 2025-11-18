package io.viana.queue_alert_engine.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável por enviar mensagens genéricas para o Apache Kafka.
 */
@Slf4j // Para registrar mensagens (logs)
@Service // Marca a classe como um serviço Spring
@RequiredArgsConstructor // Cria o construtor necessário para injeção de dependência
public class KafkaMessageProducer {

    // Ferramenta do Spring para enviar mensagens ao Kafka
    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Envia uma mensagem genérica para o Kafka
     *
     * @param topic tópico de envio (onde a mensagem será publicada)
     * @param key chave da mensagem (usada para garantir a ordem em uma partição, pode ser null)
     * @param message payload da mensagem (o conteúdo real)
     */
    public void send(String topic, String key, String message) {
        // Envia a mensagem para o Kafka de forma assíncrona
        kafkaTemplate.send(topic, key, message)
                // Define uma ação para ser executada quando o envio for concluído
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        // Se falhou, registra um erro
                        log.error("❌ Falha ao enviar mensagem para {}: {}", topic, ex.getMessage(), ex);
                    } else {
                        // Se deu certo, registra o sucesso
                        log.info("📢 Mensagem enviada para {}: {}", topic, message);
                    }
                });
    }
}