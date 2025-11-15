package io.viana.queue_alert_engine.service;

import io.viana.queue_alert_engine.config.KafkaProperties;
import io.viana.queue_alert_engine.domain.AlertRule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueMonitorService {

    private final KafkaMessageProducer kafkaProducer;
    private final KafkaProperties kafkaProperties;

    public void checkQueueStatus(QueueOffsetTracker offsetTracker) {
        List<AlertRule> alertRules = kafkaProperties.getAlertRules();
        if (alertRules == null || alertRules.isEmpty()) {
            log.warn("⚠️ Nenhuma regra de alerta configurada!");
            return;
        }

        log.info("⏱️ Verificando filas com regras de alerta...");

        alertRules.forEach(rule -> {
            long pending = offsetTracker.getTopicPendingMessages()
                    .getOrDefault(rule.topic(), 0L);
            processQueueStatus(rule, pending);
        });
    }

    private void processQueueStatus(AlertRule rule, long pending) {
        if (pending >= rule.pendingThreshold()) {
            log.warn("⚠️ {} mensagens pendentes em {}", pending, rule.topic());
            String alertMessage = String.format(
                    "{\"msg\":\"Fila %s possui %d mensagens pendentes (limite %d)\"}",
                    rule.topic(), pending, rule.pendingThreshold()
            );
            kafkaProducer.sendMessage(alertMessage);
        } else {
            log.info("✅ Fila {} dentro do limite ({} < {})", rule.topic(), pending, rule.pendingThreshold());
        }
    }

    public List<String> getQueuesAboveThreshold(QueueOffsetTracker offsetTracker) {
        List<AlertRule> alertRules = kafkaProperties.getAlertRules();
        return alertRules.stream()
                .filter(rule -> offsetTracker.getTopicPendingMessages()
                        .getOrDefault(rule.topic(), 0L) >= rule.pendingThreshold())
                .map(AlertRule::topic)
                .collect(Collectors.toList());
    }
}
