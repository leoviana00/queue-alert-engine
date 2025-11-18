package io.viana.queue_alert_engine.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Classe de configuração do Spring responsável por criar e gerenciar
 * o objeto AdminClient do Kafka.
 * O AdminClient é usado para interagir com o cluster Kafka (ex: buscar offsets de grupos).
 */
@Configuration // Marca a classe como uma fonte de definições de beans do Spring
public class KafkaAdminConfig {

    // Propriedades de configuração do Kafka (como a lista de servidores)
    private final KafkaProperties kafkaProperties;

    /**
     * Construtor para injetar as propriedades do Kafka.
     */
    public KafkaAdminConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    /**
     * Define um bean do tipo AdminClient.
     * Este objeto será gerenciado pelo Spring e estará disponível para injeção em outros serviços.
     *
     * @return Uma instância configurada do AdminClient.
     */
    @Bean
    public AdminClient adminClient() {
        // Cria o AdminClient
        return AdminClient.create(
                Map.of(
                        // Define a propriedade necessária: a lista de servidores Kafka
                        AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                        kafkaProperties.getBootstrapServers()
                )
        );
    }
}