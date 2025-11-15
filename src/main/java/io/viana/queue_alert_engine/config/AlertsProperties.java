package io.viana.queue_alert_engine.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import io.viana.queue_alert_engine.domain.AlertGroup;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "alerts")
public class AlertsProperties {

    private long repeatAlertMinutes = 5;
    private boolean recoveryAlertsEnabled = true;

    // Agora temos grupos de regras, cada um com seu groupId
    private List<AlertGroup> groups;

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
