package io.viana.queue_alert_engine.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Classe de domínio que representa o formato esperado pelo Telegram
 * para o corpo (payload) de envio de uma mensagem de texto via API.
 */
@Data // Gera Getters, Setters, toString, equals e hashCode
@NoArgsConstructor // Gera um construtor sem argumentos
@AllArgsConstructor // Gera um construtor com argumentos para todos os campos
public class TelegramMessage {

    /**
     * O ID do chat (conversa ou canal) para onde a mensagem será enviada.
     * @JsonProperty é usado para mapear este campo para "chat_id" no JSON,
     * que é o nome exigido pela API do Telegram.
     */
    @JsonProperty("chat_id")
    private String chatId;

    // O conteúdo textual da mensagem a ser enviada.
    private String text;
}