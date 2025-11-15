package io.viana.queue_alert_engine.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import io.viana.queue_alert_engine.domain.AlertRule;

@Component
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties {

    private String bootstrapServers;
    private Producer producer = new Producer();
    private Consumer consumer = new Consumer();
    private Listener listener = new Listener(); // <--- adicionado
    private List<AlertRule> alertRules; // Lista parametrizada no YAML

    // getters e setters

    public String getBootstrapServers() { return bootstrapServers; }
    public void setBootstrapServers(String bootstrapServers) { this.bootstrapServers = bootstrapServers; }

    public Producer getProducer() { return producer; }
    public void setProducer(Producer producer) { this.producer = producer; }

    public Consumer getConsumer() { return consumer; }
    public void setConsumer(Consumer consumer) { this.consumer = consumer; }

    public Listener getListener() { return listener; } // <--- adicionado
    public void setListener(Listener listener) { this.listener = listener; } // <--- adicionado

    public List<AlertRule> getAlertRules() { return alertRules; }
    public void setAlertRules(List<AlertRule> alertRules) { this.alertRules = alertRules; }

    // --------------------- Producer ---------------------
    public static class Producer {
        private String topic;
        private String keySerializer = "org.apache.kafka.common.serialization.StringSerializer";
        private String valueSerializer = "org.apache.kafka.common.serialization.StringSerializer";
        private String acks = "all";
        private int retries = 3;
        private int batchSize = 16384;
        private int lingerMs = 1;
        private long bufferMemory = 33554432;

        // getters e setters
        public String getTopic() { return topic; }
        public void setTopic(String topic) { this.topic = topic; }
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

    // --------------------- Consumer ---------------------
    public static class Consumer {
        private String groupId;
        private String autoOffsetReset = "earliest";
        private boolean enableAutoCommit = true;
        private int concurrency = 3;
        private long pollTimeout = 3000;

        // getters e setters
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

    // --------------------- Listener ---------------------
    public static class Listener {
        private String ackMode = "record"; // default baseado no YAML

        public String getAckMode() { return ackMode; }
        public void setAckMode(String ackMode) { this.ackMode = ackMode; }
    }
}
