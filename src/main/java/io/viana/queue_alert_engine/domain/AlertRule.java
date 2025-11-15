package io.viana.queue_alert_engine.domain;

public record AlertRule(String topic, long pendingThreshold) {}
