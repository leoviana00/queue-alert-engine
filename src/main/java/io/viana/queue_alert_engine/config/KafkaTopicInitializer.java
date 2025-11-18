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

/**
 * Componente responsável por garantir que todos os tópicos necessários (de alerta, estado e monitorados)
 * existam no cluster Kafka quando a aplicação inicia.
 */
@Slf4j // Para registrar mensagens (logs)
@Component // Marca a classe como um componente Spring
@RequiredArgsConstructor // Cria o construtor para injeção de dependência
public class KafkaTopicInitializer {

    // Cliente administrativo do Kafka para criar tópicos
    private final AdminClient adminClient;
    // Propriedades gerais do Kafka (para obter tópicos de produtor)
    private final KafkaProperties kafkaProperties;
    // Propriedades dos alertas (para obter tópicos monitorados)
    private final AlertsProperties alertsProperties;

    /**
     * Garante que todos os tópicos do projeto existam ao iniciar a aplicação.
     * Este método é chamado automaticamente pelo Spring após a construção do objeto.
     */
    @PostConstruct
    public void ensureTopicsExist() {

        // Conjunto para armazenar nomes de tópicos únicos que precisam ser criados
        Set<String> topics = new HashSet<>();

        // 1️⃣ Adiciona os tópicos de saída (produção) definidos nas propriedades
        if (kafkaProperties.getProducer().getStateTopic() != null) {
            topics.add(kafkaProperties.getProducer().getStateTopic()); // Tópico para eventos de estado
        }
        if (kafkaProperties.getProducer().getAlertTopic() != null) {
            topics.add(kafkaProperties.getProducer().getAlertTopic()); // Tópico para alertas (WARNING/CRITICAL)
        }

        // 2️⃣ Adiciona os tópicos definidos nas regras de monitoramento
        if (alertsProperties.getGroups() != null) {
            for (AlertGroup group : alertsProperties.getGroups()) {
                if (group.getRules() != null) {
                    for (AlertRule rule : group.getRules()) {
                        if (rule.topic() != null) {
                            topics.add(rule.topic()); // Tópico que está sendo monitorado
                        }
                    }
                }
            }
        }

        // Se nenhum tópico foi encontrado nas configurações, apenas loga e sai
        if (topics.isEmpty()) {
            log.warn("⚠ Nenhum tópico encontrado para criação automática.");
            return;
        }

        // 3️⃣ Itera e tenta criar cada tópico
        for (String topicName : topics) {
            try {
                // Define um novo tópico com 1 partição e Fator de Replicação (RF) 1
                NewTopic topic = new NewTopic(topicName, 1, (short) 1);
                // Tenta criar o tópico de forma assíncrona e espera pelo resultado
                adminClient.createTopics(Collections.singletonList(topic)).all().get();
                log.info("✅ Tópico '{}' criado ou já existente.", topicName);

            } catch (ExecutionException e) {
                // Se a exceção for porque o tópico já existe, loga como aviso e continua
                if (e.getCause() != null && e.getCause().getMessage().contains("already exists")) {
                    log.info("⚠ Tópico '{}' já existe.", topicName);
                } else {
                    // Se for qualquer outro erro, loga e lança exceção (falha na inicialização)
                    log.error("❌ Erro ao criar tópico '{}': {}", topicName, e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            } catch (InterruptedException e) {
                // Se o thread for interrompido, restaura o status de interrupção e loga
                Thread.currentThread().interrupt();
                log.error("❌ Inicialização interrompida ao criar tópico '{}'", topicName);
            }
        }
    }
}