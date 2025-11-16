package io.viana.queue_alert_engine.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "telegram")
public class TelegramProperties {

    /**
     * Token do bot do Telegram.
     * Exemplo: 123456789:ABCdefGhijkLMNOPqrsTUVwxYZ
     */
    private String botToken;

    /**
     * ID do chat onde os alertas serão enviados.
     * Pode ser um chat privado ou grupo.
     */
    private String chatId;

    /**
     * URL completa para envio de mensagens via Bot API.
     * É montada automaticamente se não for definida no YAML.
     */
    private String apiUrl;

    public String getBotUrl() {
        if (apiUrl != null && !apiUrl.isBlank()) {
            return apiUrl;
        }
        return "https://api.telegram.org/bot" + botToken + "/sendMessage";
    }
}
