package io.viana.queue_alert_engine.listener;

import io.viana.queue_alert_engine.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramAlertConsumer {

    private final AlertService alertService;

    @KafkaListener(
            topics = "${kafka.producer.alert-topic}",
            groupId = "telegram-alert-consumer"
    )
    public void onAlertReceived(String rawMessage) {
        log.info("📥 Alerta recebido no tópico de alertas: {}", rawMessage);
        alertService.handleAlert(rawMessage);
    }
}
