package io.viana.queue_alert_engine.config;
import io.viana.queue_alert_engine.domain.AlertRule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListenerConfigurer;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaTopicConfig implements KafkaListenerConfigurer {

    private final AlertsProperties alertsProperties;

    /**
     * Nenhum listener dinâmico será registrado.
     * QueueOffsetTracker fará apenas tracking via AdminClient.
     */
    @Override
    public void configureKafkaListeners(KafkaListenerEndpointRegistrar registrar) {

        List<String> topics = alertsProperties.getGroups()
                .stream()
                .filter(group -> group.getRules() != null)
                .flatMap(group -> group.getRules().stream())
                .map(AlertRule::topic)
                .distinct()
                .collect(Collectors.toList());

        if (topics.isEmpty()) {
            log.warn("⚠ Nenhum tópico configurado em alerts.groups. Nenhum listener será criado.");
        } else {
            log.info("⚡ Monitorando tópicos apenas via AdminClient (não há consumo real de mensagens): {}", topics);
        }
    }
}
