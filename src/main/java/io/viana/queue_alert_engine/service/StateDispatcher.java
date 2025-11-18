package io.viana.queue_alert_engine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.viana.queue_alert_engine.config.KafkaProperties;
import io.viana.queue_alert_engine.domain.QueueStateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

// Classe que envia o estado da fila para o Kafka
@Slf4j // Para registrar mensagens (logs)
@Component // É um componente gerenciado pelo Spring
@RequiredArgsConstructor // Cria um construtor para injeção de dependência
public class StateDispatcher {

    // Ferramenta para enviar dados ao Kafka
    private final KafkaTemplate<String, String> kafkaTemplate;
    // Ferramenta para converter objetos em texto JSON
    private final ObjectMapper objectMapper;
    // Configurações do Kafka (como nome do tópico)
    private final KafkaProperties kafkaProperties;

    /**
     * Envia o estado da fila (QueueStateEvent) para o tópico Kafka.
     */
    public void sendQueueState(QueueStateEvent stateEvent) {
        try {
            // Pega o nome do tópico para onde vamos enviar
            String topic = kafkaProperties.getProducer().getStateTopic();
            // Transforma o objeto 'stateEvent' em texto (JSON)
            String payload = objectMapper.writeValueAsString(stateEvent);

            // Envia a mensagem para o Kafka
            kafkaTemplate.send(topic, stateEvent.getTopic(), payload)
                    // O que fazer depois que o envio terminar
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            // Se falhou, registra um erro
                            log.error("❌ Falha ao enviar estado para {}: {}", topic, ex.getMessage(), ex);
                        } else {
                            // Se deu certo, registra o sucesso
                            log.info("📊 Estado publicado no tópico '{}': {}", topic, payload);
                        }
                    });

        } catch (JsonProcessingException e) {
            // Se falhou ao converter para JSON, registra um erro
            log.error("❌ Erro ao serializar QueueStateEvent", e);
        }
    }
}