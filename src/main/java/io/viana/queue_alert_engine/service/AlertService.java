package io.viana.queue_alert_engine.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.viana.queue_alert_engine.domain.QueueAlert;
import io.viana.queue_alert_engine.notifier.TelegramNotifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável por receber e processar mensagens de alerta
 * (geralmente vindas de um tópico Kafka) e encaminhá-las para notificação.
 */
@Slf4j // Para registrar mensagens (logs)
@Service // Marca a classe como um serviço Spring
@RequiredArgsConstructor // Cria o construtor para injeção de dependência
public class AlertService {

    // Ferramenta para converter texto JSON em objetos Java
    private final ObjectMapper objectMapper;
    // Serviço para enviar a notificação final via Telegram
    private final TelegramNotifier telegramNotifier;

    /**
     * Processa uma mensagem crua (JSON) vinda do Kafka, transforma em um objeto
     * de alerta estruturado e o envia para notificação no Telegram.
     *
     * @param rawMessage A string JSON recebida do Kafka.
     */
    public void handleAlert(String rawMessage) {
        try {
            log.debug("🔄 Convertendo mensagem recebida: {}", rawMessage);

            // Converte a string JSON para o objeto QueueAlert
            QueueAlert alert = objectMapper.readValue(rawMessage, QueueAlert.class);

            log.info("📦 Alerta convertido com sucesso: {}", alert);

            // Envia o alerta formatado usando o notificador do Telegram
            telegramNotifier.sendAlert(alert);

        } catch (Exception e) {
            // Captura qualquer erro (ex: falha na conversão do JSON) e registra
            log.error("❌ Erro ao processar alerta recebido do Kafka. Payload: {}", rawMessage, e);
        }
    }
}