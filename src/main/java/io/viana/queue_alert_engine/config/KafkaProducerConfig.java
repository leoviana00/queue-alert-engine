package io.viana.queue_alert_engine.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe de configuração do Spring responsável por configurar
 * e criar os beans necessários para enviar mensagens ao Kafka (produtor).
 */
@Configuration // Marca a classe como uma fonte de definições de beans
public class KafkaProducerConfig {

    // Propriedades de configuração do Kafka injetadas
    private final KafkaProperties kafkaProperties;

    /**
     * Construtor para injetar as propriedades do Kafka.
     */
    public KafkaProducerConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    /**
     * Define um bean que contém o mapa de configurações do Produtor Kafka.
     * As configurações são lidas da classe KafkaProperties.
     *
     * @return Um mapa de String para Object com as configurações do produtor.
     */
    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();

        // Obtém o sub-objeto de propriedades específicas do produtor
        KafkaProperties.Producer producer = kafkaProperties.getProducer();

        // 1. Configurações básicas de conexão
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());

        // 2. Configuração dos Serializadores (como converter a chave e o valor para bytes)
        // Usa StringSerializer como padrão se não estiver configurado
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                producer.getKeySerializer() != null ? producer.getKeySerializer() : StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                producer.getValueSerializer() != null ? producer.getValueSerializer() : StringSerializer.class);

        // 3. Configurações de performance e confiabilidade
        // Nível de confirmação de recebimento (acks=all garante maior durabilidade)
        props.put(ProducerConfig.ACKS_CONFIG, producer.getAcks() != null ? producer.getAcks() : "all");
        // Número de tentativas de reenvio em caso de falha
        props.put(ProducerConfig.RETRIES_CONFIG, producer.getRetries());
        // Tamanho máximo do lote de mensagens enviado de uma vez
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, producer.getBatchSize());
        // Tempo que o produtor aguarda para enviar um lote (mesmo que não esteja cheio)
        props.put(ProducerConfig.LINGER_MS_CONFIG, producer.getLingerMs());
        // Tamanho do buffer de memória que o produtor usa para armazenar mensagens não enviadas
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, producer.getBufferMemory());

        return props;
    }

    /**
     * Define um bean que cria a Fábrica de Produtores (ProducerFactory).
     * Esta fábrica usa as configurações definidas acima.
     *
     * @return Uma instância de ProducerFactory.
     */
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    /**
     * Define um bean do tipo KafkaTemplate.
     * Este é o objeto principal que os serviços usam para enviar mensagens ao Kafka.
     *
     * @return Uma instância de KafkaTemplate.
     */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}