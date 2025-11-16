package io.viana.queue_alert_engine.notifier;

import io.viana.queue_alert_engine.config.TelegramProperties;
import io.viana.queue_alert_engine.domain.QueueAlert;
import io.viana.queue_alert_engine.domain.TelegramMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramNotifier {

    private final TelegramProperties telegramProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    public void sendAlert(QueueAlert alert) {

        String chatId = telegramProperties.getChatId();
        String token = telegramProperties.getBotToken();

        log.info("🔧 Telegram chatId utilizado: {}", chatId);
        log.info("🔧 Telegram botToken: {}", token != null ? "OK" : "NULL");

        String url = "https://api.telegram.org/bot" + token + "/sendMessage";

        String text = "🚨 *ALERTA DE LAG*\n\n"
                + "📌 *Group:* " + alert.getGroupId() + "\n"
                + "📄 *Topic:* " + alert.getTopic() + "\n"
                + "📦 *Partition:* " + alert.getPartition() + "\n"
                + "⏳ *Lag:* " + alert.getLag() + "\n"
                + "⚠️ *Level:* " + alert.getLevel();

        TelegramMessage payload = new TelegramMessage(chatId, text);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TelegramMessage> request = new HttpEntity<>(payload, headers);

        try {
            restTemplate.postForEntity(url, request, String.class);
            log.info("📤 Mensagem enviada ao Telegram!");
        } catch (Exception e) {
            log.error("❌ Erro ao enviar alerta para Telegram: {}", e.getMessage(), e);
        }
    }
}
