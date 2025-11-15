package io.viana.queue_alert_engine.service;

import io.viana.queue_alert_engine.domain.AlertRule;
import io.viana.queue_alert_engine.domain.QueueStateEvent;
import io.viana.queue_alert_engine.domain.QueueStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListOffsetsResult;
import org.apache.kafka.clients.admin.OffsetSpec;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LagCheckerService {

    private final AdminClient adminClient;
    private final QueueOffsetTracker offsetTracker;
    private final QueueStateProducer stateProducer;

    /**
     * Calcula o lag para um grupo específico.
     */
    public void checkLag(String groupId, List<AlertRule> rules) {
        if (rules == null || rules.isEmpty()) {
            log.warn("⚠ Nenhuma regra configurada para o consumer group {}", groupId);
            return;
        }

        log.info("📌 Calculando lag para consumer group: {}", groupId);

        // Atualiza os offsets consumidos do group
        offsetTracker.updateConsumedOffsets(groupId);

        rules.forEach(rule -> processRule(groupId, rule));
    }

    private void processRule(String groupId, AlertRule rule) {
        String topic = rule.topic();
        int partition = rule.partition();

        long lastConsumed = offsetTracker.getLastConsumedOffset(groupId, topic, partition);
        long lastProduced = getLastProducedOffset(topic, partition);

        if (lastProduced < 0) {
            log.warn("⚠ Não foi possível obter offset de produção para {}-{}. Pulando...", topic, partition);
            return;
        }

        long lag = Math.max(0, lastProduced - lastConsumed);
        QueueStatus status = determineStatus(lag, rule.lagWarning(), rule.lagCritical());

        QueueStateEvent event = QueueStateEvent.builder()
                .topic(topic)
                .partition(partition)
                .consumerGroup(groupId)
                .lastProducedOffset(lastProduced)
                .lastConsumedOffset(lastConsumed)
                .lag(lag)
                .status(status)
                .timestamp(Instant.now().toEpochMilli())
                .build();

        stateProducer.sendQueueState(event);
        log.info("📊 Estado publicado para {}-{} [group={}] → {}", topic, partition, groupId, event);
    }

    private long getLastProducedOffset(String topic, int partition) {
        try {
            TopicPartition tp = new TopicPartition(topic, partition);
            ListOffsetsResult result = adminClient.listOffsets(Map.of(tp, OffsetSpec.latest()));
            return result.partitionResult(tp).get().offset();
        } catch (Exception e) {
            log.error("❌ Erro ao buscar offset de produção (topic={}, partition={}): {}", topic, partition, e.getMessage(), e);
            return -1;
        }
    }

    private QueueStatus determineStatus(long lag, long warn, long critical) {
        if (lag >= critical) return QueueStatus.CRITICAL;
        if (lag >= warn) return QueueStatus.WARNING;
        return QueueStatus.OK;
    }
}
