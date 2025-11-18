package io.viana.queue_alert_engine.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;



/**
 * Classe de configuração que mapeia todas as propriedades relacionadas ao Kafka
 * definidas no arquivo de configuração (ex: application.yml/properties) sob o prefixo 'kafka'.
 */
@Component // Marca a classe como um componente Spring
@ConfigurationProperties(prefix = "kafka") // Mapeia as propriedades que começam com 'kafka'
public class KafkaProperties {

    // Lista de servidores iniciais do Kafka (ex: "localhost:9092,kafka2:9092")
    private String bootstrapServers;

    // Sub-classes para agrupar propriedades de Produtor, Consumidor e Listener
    private Producer producer = new Producer();
    private Consumer consumer = new Consumer();
    private Listener listener = new Listener();

    // --------------------- Getters e Setters ---------------------

    public String getBootstrapServers() { return bootstrapServers; }
    public void setBootstrapServers(String bootstrapServers) { this.bootstrapServers = bootstrapServers; }

    public Producer getProducer() { return producer; }
    public void setProducer(Producer producer) { this.producer = producer; }

    public Consumer getConsumer() { return consumer; }
    public void setConsumer(Consumer consumer) { this.consumer = consumer; }

    public Listener getListener() { return listener; }
    public void setListener(Listener listener) { this.listener = listener; }

    // =====================================================================
    //                          PRODUCER PROPERTIES
    // =====================================================================
    /**
     * Propriedades específicas para o Produtor Kafka (envio de mensagens).
     */
    public static class Producer {

        /** Tópico para alertas (ex: fila estourou threshold) */
        private String alertTopic;

        /** Tópico para estado das filas monitoradas */
        private String stateTopic;

        // Serializers: Classes usadas para converter chave e valor em bytes antes do envio
        private String keySerializer = "org.apache.kafka.common.serialization.StringSerializer";
        private String valueSerializer = "org.apache.kafka.common.serialization.StringSerializer";

        // Config do produtor: Parâmetros de performance e confiabilidade
        private String acks = "all"; // Nível de confirmação de recebimento (all = mais seguro)
        private int retries = 3; // Número de tentativas de reenvio
        private int batchSize = 16384; // Tamanho máximo do lote de mensagens em bytes
        private int lingerMs = 1; // Tempo máximo de espera antes de enviar um lote (em ms)
        private long bufferMemory = 33554432; // Tamanho total da memória disponível para o buffer do produtor

        // --------------------- Getters e Setters ---------------------

        public String getAlertTopic() { return alertTopic; }
        public void setAlertTopic(String alertTopic) { this.alertTopic = alertTopic; }

        public String getStateTopic() { return stateTopic; }
        public void setStateTopic(String stateTopic) { this.stateTopic = stateTopic; }

        public String getKeySerializer() { return keySerializer; }
        public void setKeySerializer(String keySerializer) { this.keySerializer = keySerializer; }

        public String getValueSerializer() { return valueSerializer; }
        public void setValueSerializer(String valueSerializer) { this.valueSerializer = valueSerializer; }

        public String getAcks() { return acks; }
        public void setAcks(String acks) { this.acks = acks; }

        public int getRetries() { return retries; }
        public void setRetries(int retries) { this.retries = retries; }

        public int getBatchSize() { return batchSize; }
        public void setBatchSize(int batchSize) { this.batchSize = batchSize; }

        public int getLingerMs() { return lingerMs; }
        public void setLingerMs(int lingerMs) { this.lingerMs = lingerMs; }

        public long getBufferMemory() { return bufferMemory; }
        public void setBufferMemory(long bufferMemory) { this.bufferMemory = bufferMemory; }
    }

    // =====================================================================
    //                          CONSUMER PROPERTIES
    // =====================================================================
    /**
     * Propriedades específicas para o Consumidor Kafka (recebimento de mensagens).
     */
    public static class Consumer {

        private String groupId; // O ID do grupo de consumidores ao qual esta instância pertence
        private String autoOffsetReset = "earliest"; // Onde começar a ler se não houver offset salvo (earliest/latest)
        private boolean enableAutoCommit = true; // Se o offset deve ser salvo automaticamente
        private int concurrency = 3; // Número de threads/instâncias paralelas para processar mensagens
        private long pollTimeout = 3000; // Tempo máximo de espera por novas mensagens no tópico (em ms)

        // --------------------- Getters e Setters ---------------------

        public String getGroupId() { return groupId; }
        public void setGroupId(String groupId) { this.groupId = groupId; }

        public String getAutoOffsetReset() { return autoOffsetReset; }
        public void setAutoOffsetReset(String autoOffsetReset) { this.autoOffsetReset = autoOffsetReset; }

        public boolean isEnableAutoCommit() { return enableAutoCommit; }
        public void setEnableAutoCommit(boolean enableAutoCommit) { this.enableAutoCommit = enableAutoCommit; }

        public int getConcurrency() { return concurrency; }
        public void setConcurrency(int concurrency) { this.concurrency = concurrency; }

        public long getPollTimeout() { return pollTimeout; }
        public void setPollTimeout(long pollTimeout) { this.pollTimeout = pollTimeout; }
    }

    // =====================================================================
    //                           LISTENER PROPERTIES
    // =====================================================================
    /**
     * Propriedades específicas para o Listener do Spring Kafka.
     */
    public static class Listener {
        private String ackMode = "record"; // Modo de confirmação de recebimento (pode ser record, batch, manual, etc.)

        public String getAckMode() { return ackMode; }
        public void setAckMode(String ackMode) { this.ackMode = ackMode; }
    }
}