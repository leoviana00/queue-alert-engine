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

@Configuration
public class KafkaProducerConfig {

    private final KafkaProperties kafkaProperties;

    public KafkaProducerConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();

        KafkaProperties.Producer producer = kafkaProperties.getProducer();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                producer.getKeySerializer() != null ? producer.getKeySerializer() : StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                producer.getValueSerializer() != null ? producer.getValueSerializer() : StringSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, producer.getAcks() != null ? producer.getAcks() : "all");
        props.put(ProducerConfig.RETRIES_CONFIG, producer.getRetries());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, producer.getBatchSize());
        props.put(ProducerConfig.LINGER_MS_CONFIG, producer.getLingerMs());
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, producer.getBufferMemory());

        return props;
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
