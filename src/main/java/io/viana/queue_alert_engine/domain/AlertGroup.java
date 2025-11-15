package io.viana.queue_alert_engine.domain;

import java.util.List;

public class AlertGroup {

    private String groupId;
    private List<AlertRule> rules;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public List<AlertRule> getRules() {
        return rules;
    }

    public void setRules(List<AlertRule> rules) {
        this.rules = rules;
    }
}
