package io.viana.queue_alert_engine.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa um alerta publicado no tópico de alertas do Kafka.
 * Esta classe é usada para desserializar (converter) a mensagem JSON do Kafka.
 */
@Data // Gera Getters, Setters, toString, equals e hashCode
@NoArgsConstructor // Gera um construtor sem argumentos (necessário para desserialização JSON)
@AllArgsConstructor // Gera um construtor com argumentos para todos os campos
public class QueueAlert {

    // O ID do grupo de consumidores que está atrasado
    private String groupId;
    // O nome do tópico onde o lag ocorreu
    private String topic;
    // O número da partição onde o lag ocorreu
    private int partition;
    // O valor do atraso (lag) no momento da geração do alerta
    private long lag;
    // O nível de severidade do alerta (WARNING ou CRITICAL)
    private String level; // WARNING, CRITICAL
}