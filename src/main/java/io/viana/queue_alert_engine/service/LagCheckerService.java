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

/**
 * Serviço principal que calcula o "lag" (atraso) de um consumidor no Kafka
 * e decide se deve gerar alertas ou eventos de estado.
 */
@Slf4j // Para registrar mensagens (logs)
@Service // Marca a classe como um serviço Spring
@RequiredArgsConstructor // Cria o construtor para injeção de dependência
public class LagCheckerService {

    // Cliente administrativo do Kafka para buscar offsets
    private final AdminClient adminClient;
    // Serviço para rastrear a posição onde o consumidor parou
    private final QueueOffsetTracker offsetTracker;
    // Serviço para enviar o estado da fila (status) para o Kafka
    private final StateDispatcher stateProducer;
    // Serviço para enviar alertas (e-mail, Slack, etc.)
    private final AlertDispatcher alertDispatcher;

    /**
     * Função principal: Calcula o lag e dispara alertas/estados para um grupo.
     */
    public void checkLag(String groupId, List<AlertRule> rules) {
        if (rules == null || rules.isEmpty()) {
            log.warn("⚠ Nenhuma regra configurada para o consumer group {}", groupId);
            return;
        }

        log.info("📌 Calculando lag para consumer group: {}", groupId);

        // Primeiro, atualiza a posição (offset) consumida do grupo
        offsetTracker.updateConsumedOffsets(groupId);

        // Processa cada regra de alerta definida
        rules.forEach(rule -> processRule(groupId, rule));
    }

    // Processa uma única regra de alerta para um tópico/partição
    private void processRule(String groupId, AlertRule rule) {
        String topic = rule.topic();
        int partition = rule.partition();

        // 1. Pega a última posição consumida pelo grupo
        long lastConsumed = offsetTracker.getLastConsumedOffset(groupId, topic, partition);
        // 2. Pega a última posição produzida no tópico/partição
        long lastProduced = getLastProducedOffset(topic, partition);

        if (lastProduced < 0) {
            log.warn("⚠ Não foi possível obter offset de produção para {}-{}. Pulando...", topic, partition);
            return;
        }

        // Calcula o Lag (atraso): Mensagens produzidas - Mensagens consumidas
        long lag = Math.max(0, lastProduced - lastConsumed);

        // 3. Determina o status (OK, WARNING ou CRITICAL) com base no Lag
        QueueStatus status = determineStatus(lag, rule.lagWarning(), rule.lagCritical());

        // 4. Cria o objeto de Evento de Estado da Fila
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

        // 5. Publica o status (estado) no tópico Kafka
        stateProducer.sendQueueState(event);

        // 6. Dispara o alerta (e-mail/slack) se o status não for OK
        alertDispatcher.dispatchAlert(groupId, rule, lag, status);

        log.info("📊 Estado publicado para {}-{} [group={}] → {}", topic, partition, groupId, event);
    }

    // Busca no AdminClient qual é o último offset (a última mensagem) em uma partição
    private long getLastProducedOffset(String topic, int partition) {
        try {
            TopicPartition tp = new TopicPartition(topic, partition);
            // Pede o offset "latest" (último)
            ListOffsetsResult result = adminClient.listOffsets(Map.of(tp, OffsetSpec.latest()));
            // Retorna o valor do offset
            return result.partitionResult(tp).get().offset();
        } catch (Exception e) {
            log.error("❌ Erro ao buscar offset de produção (topic={}, partition={}): {}", topic, partition, e.getMessage(), e);
            return -1; // Sinaliza erro
        }
    }

    // Compara o lag com os limites de WARNING e CRITICAL
    private QueueStatus determineStatus(long lag, long warn, long critical) {
        if (lag >= critical) return QueueStatus.CRITICAL; // Se for maior que o limite crítico
        if (lag >= warn) return QueueStatus.WARNING; // Se for maior que o limite de aviso
        return QueueStatus.OK; // Caso contrário, está OK
    }
}