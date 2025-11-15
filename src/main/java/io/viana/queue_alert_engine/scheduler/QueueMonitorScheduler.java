package io.viana.queue_alert_engine.scheduler;

import io.viana.queue_alert_engine.config.AlertsProperties;
import io.viana.queue_alert_engine.domain.AlertGroup;
import io.viana.queue_alert_engine.service.LagCheckerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueueMonitorScheduler {

    private final LagCheckerService lagCheckerService;
    private final AlertsProperties alertsProperties;

    @Scheduled(fixedRate = 60000)
    public void monitorQueues() {
        log.info("🚀 Iniciando monitoramento de lag das filas...");

        if (alertsProperties.getGroups() == null || alertsProperties.getGroups().isEmpty()) {
            log.warn("⚠ Nenhum consumer group configurado em alerts.groups");
            return;
        }

        for (AlertGroup group : alertsProperties.getGroups()) {
            log.info("📡 Monitorando consumer group '{}'", group.getGroupId());
            lagCheckerService.checkLag(group.getGroupId(), group.getRules());
        }

        log.info("✅ Monitoramento concluído.");
    }
}
