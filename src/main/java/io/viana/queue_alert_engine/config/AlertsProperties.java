package io.viana.queue_alert_engine.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import io.viana.queue_alert_engine.domain.AlertGroup;

import java.util.List;

/**
 * Classe de configuração que mapeia as propriedades definidas no application.yml/properties
 * sob o prefixo 'alerts'.
 * Esta classe controla o comportamento geral dos alertas (repetição, recuperação e grupos).
 */
@Component // Marca a classe como um componente Spring
@ConfigurationProperties(prefix = "alerts") // Mapeia as propriedades que começam com 'alerts'
public class AlertsProperties {

    // Tempo em minutos para repetir um alerta se o lag persistir (padrão: 5 minutos)
    private long repeatAlertMinutes = 5;
    // Habilita ou desabilita o envio de alertas de recuperação (quando o status volta a ser OK)
    private boolean recoveryAlertsEnabled = true;

    // Lista de grupos de consumidores a serem monitorados, cada um com suas regras
    private List<AlertGroup> groups;

    // --- Métodos Getters e Setters ---

    public long getRepeatAlertMinutes() {
        return repeatAlertMinutes;
    }

    public void setRepeatAlertMinutes(long repeatAlertMinutes) {
        this.repeatAlertMinutes = repeatAlertMinutes;
    }

    public boolean isRecoveryAlertsEnabled() {
        return recoveryAlertsEnabled;
    }

    public void setRecoveryAlertsEnabled(boolean recoveryAlertsEnabled) {
        this.recoveryAlertsEnabled = recoveryAlertsEnabled;
    }

    public List<AlertGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<AlertGroup> groups) {
        this.groups = groups;
    }
}