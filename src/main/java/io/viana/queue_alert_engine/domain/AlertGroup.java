package io.viana.queue_alert_engine.domain;

import java.util.List;

/**
 * Classe de domínio que representa um grupo de consumidores Kafka
 * e as regras de alerta associadas a ele.
 * É usada principalmente para mapear configurações.
 */
public class AlertGroup {

    // O ID do grupo de consumidores Kafka (ex: "meu-consumidor-app")
    private String groupId;
    // A lista de regras específicas que devem ser aplicadas a este grupo
    private List<AlertRule> rules;

    // --- Métodos Getters e Setters ---

    /**
     * Retorna o ID do grupo de consumidores.
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * Define o ID do grupo de consumidores.
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * Retorna a lista de regras de alerta para este grupo.
     */
    public List<AlertRule> getRules() {
        return rules;
    }

    /**
     * Define a lista de regras de alerta para este grupo.
     */
    public void setRules(List<AlertRule> rules) {
        this.rules = rules;
    }
}