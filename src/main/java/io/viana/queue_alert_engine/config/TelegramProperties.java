package io.viana.queue_alert_engine.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Classe de configuração que mapeia as propriedades relacionadas ao Telegram
 * definidas no arquivo de configuração (ex: application.yml/properties) sob o prefixo 'telegram'.
 */
@Getter // Gera todos os métodos getters
@Setter // Gera todos os métodos setters
@Configuration // Marca a classe como uma fonte de definições de beans do Spring
@ConfigurationProperties(prefix = "telegram") // Mapeia as propriedades que começam com 'telegram'
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

    /**
     * Retorna a URL completa da API do Telegram para envio de mensagens.
     * Se 'apiUrl' foi definido nas configurações, usa ele; caso contrário,
     * monta a URL padrão usando o 'botToken'.
     *
     * @return A URL completa da API de envio de mensagens do Telegram.
     */
    public String getBotUrl() {
        if (apiUrl != null && !apiUrl.isBlank()) {
            return apiUrl;
        }
        // Monta a URL padrão: https://api.telegram.org/bot<TOKEN>/sendMessage
        return "https://api.telegram.org/bot" + botToken + "/sendMessage";
    }
}