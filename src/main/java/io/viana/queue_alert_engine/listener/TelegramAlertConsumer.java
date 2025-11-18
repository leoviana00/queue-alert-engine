package io.viana.queue_alert_engine.listener;

import io.viana.queue_alert_engine.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Componente que atua como um consumidor Kafka, escutando mensagens
 * do tópico de alertas para processamento e envio ao Telegram.
 */
@Slf4j // Para registrar mensagens (logs)
@Component // Marca a classe como um componente Spring
@RequiredArgsConstructor // Cria o construtor para injeção de dependência
public class TelegramAlertConsumer {

    // Serviço que contém a lógica para formatar e enviar o alerta (ex: para o Telegram)
    private final AlertService alertService;

    /**
     * Método consumidor Kafka.
     *
     * @KafkaListener define:
     * 1. topics: O tópico a ser escutado (obtido via ${...} das configurações).
     * 2. groupId: O ID exclusivo do grupo de consumidores ("telegram-alert-consumer").
     *
     * @param rawMessage A mensagem de alerta recebida do Kafka (string JSON).
     */
    @KafkaListener(
            // Tópico onde os alertas de lag (WARNING/CRITICAL) são publicados
            topics = "${kafka.producer.alert-topic}",
            // Identificador do grupo consumidor
            groupId = "telegram-alert-consumer"
    )
    public void onAlertReceived(String rawMessage) {
        log.info("📥 Alerta recebido no tópico de alertas: {}", rawMessage);
        // Chama o serviço para desserializar (converter) e processar o alerta
        alertService.handleAlert(rawMessage);
    }
}