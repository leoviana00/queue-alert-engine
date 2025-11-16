package io.viana.queue_alert_engine.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.viana.queue_alert_engine.domain.QueueAlert;
import io.viana.queue_alert_engine.notifier.TelegramNotifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {

    private final ObjectMapper objectMapper;
    private final TelegramNotifier telegramNotifier;

    /**
     * Processa uma mensagem crua vinda do Kafka e a transforma
     * em um alerta estruturado para envio ao Telegram.
     */
    public void handleAlert(String rawMessage) {
        try {
            log.debug("🔄 Convertendo mensagem recebida: {}", rawMessage);

            // Converte JSON -> Objeto tipado
            QueueAlert alert = objectMapper.readValue(rawMessage, QueueAlert.class);

            log.info("📦 Alerta convertido com sucesso: {}", alert);

            // Envia para o Telegram
            telegramNotifier.sendAlert(alert);

        } catch (Exception e) {
            log.error("❌ Erro ao processar alerta recebido do Kafka. Payload: {}", rawMessage, e);
        }
    }
}

