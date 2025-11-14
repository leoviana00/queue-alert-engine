package io.viana.queue_alert_engine.scheduler;

import io.viana.queue_alert_engine.service.QueueMonitorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueueMonitorScheduler {

    private final QueueMonitorService queueMonitorService;

    /**
     * Executa o monitoramento a cada 30 segundos.
     * Pode ajustar o intervalo conforme necessário.
     */
    @Scheduled(fixedRate = 30000)
    public void runMonitor() {
        log.info("⏱️ Executando scheduler de monitoramento das filas...");
        queueMonitorService.checkQueueStatus();
    }
}
