package io.viana.queue_alert_engine.service;

import io.viana.queue_alert_engine.config.AlertsProperties;
import io.viana.queue_alert_engine.domain.AlertRule;
import io.viana.queue_alert_engine.domain.QueueStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueMonitorService {

    private final KafkaMessageProducer kafkaProducer;
    private final AlertsProperties alertsProperties;

    /**
     * Agora recebemos o groupId também, porque cada alerta pertence a um consumer group.
     */
    public void evaluateLag(String groupId, AlertRule rule, long lag) {

        QueueStatus status = determineStatus(lag, rule.lagWarning(), rule.lagCritical());

        switch (status) {
            case OK ->
                log.info("✅ [{}] topic='{}' part={} OK (lag={} < warn={})",
                        groupId, rule.topic(), rule.partition(), lag, rule.lagWarning());

            case WARNING -> {
                log.warn("⚠️ WARNING [{}] topic='{}' part={} lag={} (warn={}, critical={})",
                        groupId, rule.topic(), rule.partition(), lag,
                        rule.lagWarning(), rule.lagCritical());
                sendAlert(groupId, rule, lag, "WARNING");
            }

            case CRITICAL -> {
                log.error("🚨 CRITICAL [{}] topic='{}' part={} lag={} (critical={})",
                        groupId, rule.topic(), rule.partition(), lag, rule.lagCritical());
                sendAlert(groupId, rule, lag, "CRITICAL");
            }
        }
    }

    /**
     * Inclui o groupId no alerta enviado
     */
    private void sendAlert(String groupId, AlertRule rule, long lag, String level) {
        String json = String.format("""
            {
              "groupId": "%s",
              "topic": "%s",
              "partition": %d,
              "lag": %d,
              "level": "%s"
            }
            """,
                groupId,
                rule.topic(),
                rule.partition(),
                lag,
                level
        );

        kafkaProducer.sendAlert(json);
    }

    private QueueStatus determineStatus(long lag, long warn, long critical) {
        if (lag >= critical) return QueueStatus.CRITICAL;
        if (lag >= warn) return QueueStatus.WARNING;
        return QueueStatus.OK;
    }

    /**
     * Agora lê topics diretamente de alerts.groups
     */
    public List<String> getConfiguredQueues() {

        return alertsProperties.getGroups().stream()
                .flatMap(group -> group.getRules().stream())
                .map(AlertRule::topic)
                .distinct()
                .toList();
    }
}
