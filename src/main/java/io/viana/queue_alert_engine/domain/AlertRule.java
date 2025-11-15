package io.viana.queue_alert_engine.domain;

public record AlertRule(
        String topic,
        int partition,
        long lagWarning,
        long lagCritical
) {}
