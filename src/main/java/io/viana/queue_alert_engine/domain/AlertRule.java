package io.viana.queue_alert_engine.domain;

/**
 * Record (Registro) que representa uma regra específica de alerta
 * para uma única partição de um tópico Kafka.
 *
 * Records no Java são classes concisas para classes de dados imutáveis
 * (final fields, construtor, getters, equals(), hashCode() e toString() automáticos).
 */
public record AlertRule(
        // Nome do tópico Kafka que será monitorado
        String topic,
        // Número da partição dentro do tópico (ex: 0, 1, 2)
        int partition,
        // Limite de lag (atraso) a partir do qual o status passa a ser WARNING (Aviso)
        long lagWarning,
        // Limite de lag (atraso) a partir do qual o status passa a ser CRITICAL (Crítico)
        long lagCritical
) {}