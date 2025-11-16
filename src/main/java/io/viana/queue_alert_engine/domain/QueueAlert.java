package io.viana.queue_alert_engine.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa um alerta publicado no tópico de alertas do Kafka.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueueAlert {

    private String groupId;
    private String topic;
    private int partition;
    private long lag;
    private String level; // WARNING, CRITICAL
}
