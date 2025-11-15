package io.viana.queue_alert_engine.scheduler;

import io.viana.queue_alert_engine.service.QueueMonitorService;
import io.viana.queue_alert_engine.service.QueueOffsetTracker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueueMonitorScheduler {

    private final QueueMonitorService queueMonitorService;
    private final QueueOffsetTracker offsetTracker;

    @Scheduled(fixedRate = 60000)
    public void monitorQueues() {
        log.info("🚀 Iniciando monitoramento de filas...");
        queueMonitorService.checkQueueStatus(offsetTracker);
    }
}
