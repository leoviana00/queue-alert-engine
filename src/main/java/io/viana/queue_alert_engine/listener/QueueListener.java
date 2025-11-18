package io.viana.queue_alert_engine.listener;

import io.viana.queue_alert_engine.config.KafkaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Componente que escuta e consome mensagens de um tópico Kafka específico.
 * Neste caso, escuta o tópico onde o estado da fila é publicado.
 */
@Slf4j // Para registrar mensagens (logs)
@Component // Marca a classe como um componente Spring
@RequiredArgsConstructor // Cria o construtor para injeção de dependência
public class QueueListener {

    // Configurações do Kafka (necessário para logs e referenciar o nome do tópico/grupo)
    private final KafkaProperties kafkaProperties;

    /**
     * Consumidor do tópico de estado gerado pelo LagCheckerService.
     *
     * Usa a anotação @KafkaListener para definir:
     * 1. topics: O nome do tópico (obtido dinamicamente das propriedades).
     * 2. groupId: O ID do grupo de consumidores (obtido dinamicamente das propriedades).
     *
     * @param message O payload (conteúdo) da mensagem recebida do Kafka (em formato String/JSON).
     */
    @KafkaListener(
            // Define o tópico a ser escutado, lendo o valor da configuração (ex: "queue.state")
            topics = "#{@kafkaProperties.producer.stateTopic}",
            // Define o ID do grupo de consumidores (ex: "queue-alert-engine-consumer")
            groupId = "#{@kafkaProperties.consumer.groupId}"
    )
    public void consume(String message) {
        // Registra a mensagem de estado recebida
        log.info("📥 Estado recebido do tópico '{}': {}",
                kafkaProperties.getProducer().getStateTopic(),
                message
        );
        // Nota: A lógica de processamento da mensagem (ex: desserialização/ação) seria adicionada aqui.
    }
}