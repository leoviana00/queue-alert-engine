package io.viana.queue_alert_engine.service;

import io.viana.queue_alert_engine.config.AlertsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListConsumerGroupOffsetsResult;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueOffsetTracker {

    private final AlertsProperties alertsProperties;
    private final AdminClient adminClient;

    private final Map<String, List<TopicPartition>> monitoredPartitions = new ConcurrentHashMap<>();
    private final Map<String, Map<TopicPartition, Long>> consumedOffsets = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        alertsProperties.getGroups().forEach(group -> {

            String groupId = group.getGroupId();

            List<TopicPartition> partitions = group.getRules().stream()
                    .map(r -> new TopicPartition(r.topic(), r.partition()))
                    .collect(Collectors.toList());

            monitoredPartitions.put(groupId, partitions);
            consumedOffsets.put(groupId, new ConcurrentHashMap<>());

            log.info("📝 Grupo monitorado: {}", groupId);
            log.info("📝 Partições monitoradas: {}", partitions);
        });

        alertsProperties.getGroups()
                .forEach(g -> updateConsumedOffsets(g.getGroupId()));
    }

    public void updateConsumedOffsets(String groupId) {
        try {
            ListConsumerGroupOffsetsResult result = adminClient.listConsumerGroupOffsets(groupId);

            Map<TopicPartition, Long> offsets = result.partitionsToOffsetAndMetadata().get()
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> e.getValue().offset()
                    ));

            Map<TopicPartition, Long> groupOffsets = consumedOffsets.get(groupId);

            monitoredPartitions.getOrDefault(groupId, List.of())
                    .forEach(tp -> groupOffsets.put(tp, offsets.getOrDefault(tp, 0L)));

            log.debug("🔎 Offsets atualizados para group {} → {}", groupId, groupOffsets);

        } catch (Exception e) {
            log.error("❌ Erro ao atualizar offsets consumidos para group {}: {}", groupId, e.getMessage(), e);
        }
    }

    public long getLastConsumedOffset(String groupId, String topic, int partition) {
        Map<TopicPartition, Long> groupOffsets = consumedOffsets.get(groupId);
        if (groupOffsets == null) return 0;

        return groupOffsets.getOrDefault(new TopicPartition(topic, partition), 0L);
    }

    /**
     * Getter necessário para o MonitorController.
     */
    public Map<String, Map<TopicPartition, Long>> getConsumedOffsets() {
        return consumedOffsets;
    }
}
