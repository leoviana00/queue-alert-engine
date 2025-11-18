package io.viana.queue_alert_engine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.viana.queue_alert_engine.config.KafkaProperties;
import io.viana.queue_alert_engine.domain.AlertRule;
import io.viana.queue_alert_engine.domain.QueueStateEvent;
import io.viana.queue_alert_engine.domain.QueueStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável por despachar (enviar) alertas e eventos de estado
 * para os tópicos Kafka configurados.
 */
@Slf4j // Para registrar mensagens (logs)
@Service // Marca a classe como um serviço Spring
@RequiredArgsConstructor // Cria o construtor para injeção de dependência
public class AlertDispatcher {

    // Produtor genérico para enviar mensagens ao Kafka
    private final KafkaMessageProducer kafkaProducer;
    // Ferramenta para converter objetos Java em texto JSON
    private final ObjectMapper objectMapper;
    // Configurações do Kafka (incluindo nomes dos tópicos de alerta e estado)
    private final KafkaProperties kafkaProperties;

    /**
     * Envia um alerta se o status for WARNING ou CRITICAL para um tópico específico.
     *
     * @param groupId O ID do grupo de consumidores.
     * @param rule A regra de alerta que foi violada.
     * @param lag O atraso (lag) atual.
     * @param status O status atual da fila (OK, WARNING, CRITICAL).
     */
    public void dispatchAlert(String groupId, AlertRule rule, long lag, QueueStatus status) {
        // Verifica se o status exige a emissão de um alerta
        if (status == QueueStatus.WARNING || status == QueueStatus.CRITICAL) {
            // Cria a mensagem do alerta no formato JSON
            String json = String.format("""
                {
                  "groupId": "%s",
                  "topic": "%s",
                  "partition": %d,
                  "lag": %d,
                  "level": "%s"
                }
                """, groupId, rule.topic(), rule.partition(), lag, status.name());

            // Envia o JSON para o tópico de alerta configurado
            kafkaProducer.send(kafkaProperties.getProducer().getAlertTopic(), rule.topic(), json);
        }
    }

    /**
     * Envia o estado completo da fila (QueueStateEvent) para o tópico de estado configurado.
     *
     * @param stateEvent O objeto contendo o estado atual da fila (lag, offsets, status, etc.).
     */
    public void dispatchState(QueueStateEvent stateEvent) {
        try {
            // Transforma o objeto de estado em uma string JSON (o payload)
            String payload = objectMapper.writeValueAsString(stateEvent);
            // Envia o payload para o tópico de estado
            kafkaProducer.send(kafkaProperties.getProducer().getStateTopic(), stateEvent.getTopic(), payload);
            // Registra o sucesso do envio
            log.info("📤 Estado publicado no tópico '{}': {}", kafkaProperties.getProducer().getStateTopic(), payload);
        } catch (JsonProcessingException e) {
            // Se falhar ao converter o objeto para JSON, registra um erro
            log.error("❌ Erro ao serializar QueueStateEvent", e);
        }
    }
}