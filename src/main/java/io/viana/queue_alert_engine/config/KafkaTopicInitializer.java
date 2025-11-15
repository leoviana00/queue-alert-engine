package io.viana.queue_alert_engine.config;

import io.viana.queue_alert_engine.domain.AlertGroup;
import io.viana.queue_alert_engine.domain.AlertRule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaTopicInitializer {

    private final AdminClient adminClient;
    private final KafkaProperties kafkaProperties;
    private final AlertsProperties alertsProperties;

    /**
     * Garante que todos os tópicos do projeto existam ao iniciar a aplicação.
     */
    @PostConstruct
    public void ensureTopicsExist() {

        Set<String> topics = new HashSet<>();

        // 1️⃣ Tópicos de producer
        if (kafkaProperties.getProducer().getStateTopic() != null) {
            topics.add(kafkaProperties.getProducer().getStateTopic());
        }
        if (kafkaProperties.getProducer().getAlertTopic() != null) {
            topics.add(kafkaProperties.getProducer().getAlertTopic());
        }

        // 2️⃣ Tópicos definidos nas regras de alerta de todos os grupos
        if (alertsProperties.getGroups() != null) {
            for (AlertGroup group : alertsProperties.getGroups()) {
                if (group.getRules() != null) {
                    for (AlertRule rule : group.getRules()) {
                        if (rule.topic() != null) {
                            topics.add(rule.topic());
                        }
                    }
                }
            }
        }

        if (topics.isEmpty()) {
            log.warn("⚠ Nenhum tópico encontrado para criação automática.");
            return;
        }

        // 3️⃣ Criação dos tópicos
        for (String topicName : topics) {
            try {
                NewTopic topic = new NewTopic(topicName, 1, (short) 1); // 1 partição, RF=1
                adminClient.createTopics(Collections.singletonList(topic)).all().get();
                log.info("✅ Tópico '{}' criado ou já existente.", topicName);
            } catch (ExecutionException e) {
                if (e.getCause() != null && e.getCause().getMessage().contains("already exists")) {
                    log.info("⚠ Tópico '{}' já existe.", topicName);
                } else {
                    log.error("❌ Erro ao criar tópico '{}': {}", topicName, e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("❌ Inicialização interrompida ao criar tópico '{}'", topicName);
            }
        }
    }
}
