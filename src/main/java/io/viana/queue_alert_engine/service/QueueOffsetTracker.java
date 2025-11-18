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

/**
 * Serviço responsável por rastrear e manter o último offset (posição)
 * consumido por cada grupo de consumidores Kafka.
 */
@Slf4j // Para logging
@Service // Marca a classe como um serviço Spring
@RequiredArgsConstructor // Cria o construtor necessário para injeção de dependência
public class QueueOffsetTracker {

    // Configurações dos alertas, incluindo quais grupos e partições monitorar
    private final AlertsProperties alertsProperties;
    // Cliente administrativo do Kafka, usado para buscar informações como offsets
    private final AdminClient adminClient;

    // Mapa para armazenar as partições que precisam ser monitoradas para cada grupo
    // Chave: GroupId | Valor: Lista de TopicPartition (tópico e partição)
    private final Map<String, List<TopicPartition>> monitoredPartitions = new ConcurrentHashMap<>();
    // Mapa para armazenar o último offset consumido.
    // Chave Externa: GroupId | Chave Interna: TopicPartition | Valor Interno: Offset (posição)
    private final Map<String, Map<TopicPartition, Long>> consumedOffsets = new ConcurrentHashMap<>();

    /**
     * Inicializa o serviço após a construção do objeto.
     * Carrega as partições a serem monitoradas a partir das configurações.
     */
    @PostConstruct
    public void init() {
        // Percorre todos os grupos configurados
        alertsProperties.getGroups().forEach(group -> {

            String groupId = group.getGroupId();

            // Mapeia as regras de alerta para objetos TopicPartition
            List<TopicPartition> partitions = group.getRules().stream()
                    .map(r -> new TopicPartition(r.topic(), r.partition()))
                    .collect(Collectors.toList());

            // Armazena as partições que serão monitoradas para este grupo
            monitoredPartitions.put(groupId, partitions);
            // Inicializa o mapa de offsets consumidos para este grupo
            consumedOffsets.put(groupId, new ConcurrentHashMap<>());

            log.info("📝 Grupo monitorado: {}", groupId);
            log.info("📝 Partições monitoradas: {}", partitions);
        });

        // Chama a atualização inicial de offsets para todos os grupos
        alertsProperties.getGroups()
                .forEach(g -> updateConsumedOffsets(g.getGroupId()));
    }

    /**
     * Busca no Kafka e atualiza os últimos offsets consumidos para um grupo específico.
     *
     * @param groupId O ID do grupo de consumidores a ser verificado.
     */
    public void updateConsumedOffsets(String groupId) {
        try {
            // Solicita ao AdminClient os offsets consumidos pelo grupo
            ListConsumerGroupOffsetsResult result = adminClient.listConsumerGroupOffsets(groupId);

            // Processa o resultado e cria um mapa simplificado (TopicPartition -> Offset)
            Map<TopicPartition, Long> offsets = result.partitionsToOffsetAndMetadata().get()
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> e.getValue().offset() // Extrai apenas o valor do offset
                    ));

            // Pega o mapa de offsets que pertence a este grupo
            Map<TopicPartition, Long> groupOffsets = consumedOffsets.get(groupId);

            // Itera sobre as partições monitoradas e armazena o offset encontrado
            monitoredPartitions.getOrDefault(groupId, List.of())
                    .forEach(tp -> groupOffsets.put(tp, offsets.getOrDefault(tp, 0L))); // Se não encontrar, usa 0

            log.debug("🔎 Offsets atualizados para group {} → {}", groupId, groupOffsets);

        } catch (Exception e) {
            log.error("❌ Erro ao atualizar offsets consumidos para group {}: {}", groupId, e.getMessage(), e);
        }
    }

    /**
     * Retorna o último offset consumido conhecido para uma dada partição e grupo.
     *
     * @param groupId O ID do grupo de consumidores.
     * @param topic O nome do tópico.
     * @param partition O número da partição.
     * @return O último offset consumido, ou 0 se não for encontrado.
     */
    public long getLastConsumedOffset(String groupId, String topic, int partition) {
        Map<TopicPartition, Long> groupOffsets = consumedOffsets.get(groupId);
        if (groupOffsets == null) return 0; // Grupo não monitorado

        // Retorna o offset da partição específica, ou 0 se não houver
        return groupOffsets.getOrDefault(new TopicPartition(topic, partition), 0L);
    }

    /**
     * Getter necessário para o MonitorController.
     *
     * @return O mapa completo de offsets consumidos por todos os grupos.
     */
    public Map<String, Map<TopicPartition, Long>> getConsumedOffsets() {
        return consumedOffsets;
    }
}