package io.viana.queue_alert_engine.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueStateEvent {

    private String topic;
    private int partition;

    private long lastProducedOffset;
    private long lastConsumedOffset;
    private long lag;

    private String consumerGroup;

    private QueueStatus status;

    private long timestamp;
}
