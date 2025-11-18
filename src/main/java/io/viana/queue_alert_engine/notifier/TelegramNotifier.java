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

/**
 * Serviço responsável por formatar e enviar alertas para um chat do Telegram
 * usando a API do Bot.
 */
@Slf4j // Para registrar mensagens (logs)
@Service // Marca a classe como um serviço Spring
@RequiredArgsConstructor // Cria o construtor para injeção de dependência
public class TelegramNotifier {

    // Propriedades de configuração do Telegram (Token do Bot e ID do Chat)
    private final TelegramProperties telegramProperties;
    // Cliente para fazer requisições HTTP (para a API do Telegram)
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Envia o alerta formatado para o chat configurado no Telegram.
     *
     * @param alert O objeto de alerta com todos os detalhes (lag, grupo, tópico, etc.).
     */
    public void sendAlert(QueueAlert alert) {

        // Pega as configurações necessárias
        String chatId = telegramProperties.getChatId();
        String token = telegramProperties.getBotToken();

        log.info("🔧 Telegram chatId: {}", chatId != null ? "OK" : "NULL");
        log.info("🔧 Telegram botToken: {}", token != null ? "OK" : "NULL");

        // Monta a URL da API do Telegram para envio de mensagens
        String url = "https://api.telegram.org/bot" + token + "/sendMessage";

        // Formata o texto da mensagem com os detalhes do alerta, usando Markdown para negrito
        String text = "🚨 *ALERTA DE LAG*\n\n"
                + "📌 *Group:* " + alert.getGroupId() + "\n"
                + "📄 *Topic:* " + alert.getTopic() + "\n"
                + "📦 *Partition:* " + alert.getPartition() + "\n"
                + "⏳ *Lag:* " + alert.getLag() + "\n"
                + "⚠️ *Level:* " + alert.getLevel();

        // Cria o objeto de payload (corpo da requisição)
        TelegramMessage payload = new TelegramMessage(chatId, text);

        // Define os cabeçalhos HTTP (indicando que o corpo é JSON)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Combina o payload e os cabeçalhos na requisição HTTP
        HttpEntity<TelegramMessage> request = new HttpEntity<>(payload, headers);

        try {
            // Envia a requisição POST para a API do Telegram
            restTemplate.postForEntity(url, request, String.class);
            log.info("📤 Mensagem enviada ao Telegram!");
        } catch (Exception e) {
            // Se houver um erro na comunicação, registra
            log.error("❌ Erro ao enviar alerta para Telegram: {}", e.getMessage(), e);
        }
    }
}