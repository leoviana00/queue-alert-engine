package io.viana.queue_alert_engine.domain;

/**
 * Enumeração (Enum) que define os possíveis estados (status) de uma fila
 * de mensagens (tópico/partição) com base no seu nível de atraso (lag).
 */
public enum QueueStatus {
    // Atraso dentro do limite aceitável.
    OK,
    // Atraso atingiu o limite de aviso (lagWarning).
    WARNING,
    // Atraso atingiu o limite crítico (lagCritical).
    CRITICAL
}