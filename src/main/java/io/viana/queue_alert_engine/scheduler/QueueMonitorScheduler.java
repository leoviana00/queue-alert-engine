package io.viana.queue_alert_engine.scheduler;

import io.viana.queue_alert_engine.config.AlertsProperties;
import io.viana.queue_alert_engine.domain.AlertGroup;
import io.viana.queue_alert_engine.service.LagCheckerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Componente que executa a verificação de lag (atraso) nas filas do Kafka
 * em intervalos de tempo fixos, usando o agendamento do Spring.
 */
@Slf4j // Para registrar mensagens (logs)
@Component // Marca a classe como um componente Spring
@RequiredArgsConstructor // Cria o construtor para injeção de dependência
public class QueueMonitorScheduler {

    // Serviço que contém a lógica para calcular o lag e disparar alertas
    private final LagCheckerService lagCheckerService;
    // Configurações dos grupos e regras de alerta a serem monitorados
    private final AlertsProperties alertsProperties;

    /**
     * Método agendado que é executado a cada 60.000 milissegundos (1 minuto).
     * Ele inicia o processo de verificação de lag para todos os grupos configurados.
     */
    @Scheduled(fixedRate = 60000)
    public void monitorQueues() {
        log.info("🚀 Iniciando monitoramento de lag das filas...");

        // Verifica se há grupos configurados para monitoramento
        if (alertsProperties.getGroups() == null || alertsProperties.getGroups().isEmpty()) {
            log.warn("⚠ Nenhum consumer group configurado em alerts.groups");
            return;
        }

        // Itera sobre cada grupo de alerta configurado
        for (AlertGroup group : alertsProperties.getGroups()) {
            log.info("📡 Monitorando consumer group '{}'", group.getGroupId());
            // Chama o serviço para verificar o lag para este grupo e suas regras
            lagCheckerService.checkLag(group.getGroupId(), group.getRules());
        }

        log.info("✅ Monitoramento concluído.");
    }
}