package io.viana.queue_alert_engine.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Classe de domínio que representa o estado atual de uma fila (tópico/partição)
 * e o progresso de um consumidor específico, incluindo o valor do lag.
 * Este evento é publicado no tópico de estado do Kafka.
 */
@Data // Gera Getters, Setters, toString, equals e hashCode
@Builder // Permite a criação do objeto usando o padrão Builder
@NoArgsConstructor // Gera um construtor sem argumentos (necessário para desserialização JSON)
@AllArgsConstructor // Gera um construtor com argumentos para todos os campos
public class QueueStateEvent {

    // O nome do tópico Kafka
    private String topic;
    // O número da partição dentro do tópico
    private int partition;

    // O último offset (posição) de mensagem produzida (o fim da fila)
    private long lastProducedOffset;
    // O último offset (posição) de mensagem que foi lida pelo consumidor
    private long lastConsumedOffset;
    // O atraso: lastProducedOffset - lastConsumedOffset
    private long lag;

    // O ID do grupo de consumidores que gerou este estado
    private String consumerGroup;

    // O status determinado com base no lag (OK, WARNING ou CRITICAL)
    private QueueStatus status;

    // Timestamp (data/hora em milissegundos) em que o estado foi verificado
    private long timestamp;
}