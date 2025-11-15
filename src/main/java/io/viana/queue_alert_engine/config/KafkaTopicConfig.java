package io.viana.queue_alert_engine.config;

import io.viana.queue_alert_engine.service.QueueOffsetTracker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListenerConfigurer;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;
import org.springframework.kafka.config.MethodKafkaListenerEndpoint;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaTopicConfig implements KafkaListenerConfigurer {

    private final KafkaProperties kafkaProperties;
    private final QueueOffsetTracker queueOffsetTracker;
    private final BeanFactory beanFactory;

    // ------------------------------------------------------
    // MESSAGE HANDLER FACTORY PARA LISTENER DINÂMICO
    // ------------------------------------------------------
    @Bean
    public DefaultMessageHandlerMethodFactory messageHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
        factory.setBeanFactory(beanFactory);
        factory.afterPropertiesSet();
        return factory;
    }

    // ------------------------------------------------------
    // CONFIGURA LISTENER DINÂMICO
    // ------------------------------------------------------
    @Override
    public void configureKafkaListeners(KafkaListenerEndpointRegistrar registrar) {
        try {
            List<String> topics = kafkaProperties.getAlertRules()
                    .stream()
                    .map(rule -> rule.topic())
                    .distinct()
                    .toList();

            if (topics.isEmpty()) {
                log.warn("⚠ Nenhum tópico configurado em alert-rules. Listener não será criado.");
                return;
            }

            log.info("🎧 Registrando KafkaListener dinâmico para tópicos: {}", topics);

            MethodKafkaListenerEndpoint<String, String> endpoint = new MethodKafkaListenerEndpoint<>();
            endpoint.setId("dynamic-alert-listener");
            endpoint.setGroupId(kafkaProperties.getConsumer().getGroupId());
            endpoint.setAutoStartup(true);

            // Bean + método do listener
            endpoint.setBean(queueOffsetTracker);
            endpoint.setMethod(queueOffsetTracker.getClass().getMethod("onMessage", ConsumerRecord.class));

            // IMPORTANTE: set MessageHandlerMethodFactory
            endpoint.setMessageHandlerMethodFactory(messageHandlerMethodFactory());

            endpoint.setTopics(topics.toArray(new String[0]));
            registrar.registerEndpoint(endpoint);

            log.info("✅ KafkaListener dinâmico registrado com sucesso.");

        } catch (Exception e) {
            throw new RuntimeException("❌ Erro ao registrar KafkaListener dinâmico", e);
        }
    }
}
