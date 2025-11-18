package io.viana.queue_alert_engine.config;
import io.viana.queue_alert_engine.domain.AlertRule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListenerConfigurer;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Configuração que implementa KafkaListenerConfigurer.
 * Embora esta interface seja usada tipicamente para registrar listeners de forma dinâmica,
 * aqui ela é usada primariamente para validar as configurações de tópicos
 * e confirmar que **nenhum consumidor real** será criado (apenas o monitoramento via AdminClient).
 */
@Slf4j // Para registrar mensagens (logs)
@Configuration // Marca a classe como uma fonte de definições de beans
@RequiredArgsConstructor // Cria o construtor para injeção de dependência
public class KafkaTopicConfig implements KafkaListenerConfigurer {

    // Propriedades de configuração dos alertas, incluindo a lista de grupos e regras
    private final AlertsProperties alertsProperties;

    /**
     * Este método faz parte da interface KafkaListenerConfigurer.
     * Neste projeto, ele é usado apenas para:
     * 1. Coletar a lista de tópicos configurados nas regras de alerta.
     * 2. Registrar um log informativo de quais tópicos estão sendo monitorados.
     * 3. Confirmar que **nenhum listener de consumo dinâmico real** será registrado.
     *
     * @param registrar Objeto usado para registrar listeners Kafka (não utilizado para registro neste caso).
     */
    @Override
    public void configureKafkaListeners(KafkaListenerEndpointRegistrar registrar) {

        // 1. Coleta todos os nomes de tópicos únicos a partir das regras configuradas:
        List<String> topics = alertsProperties.getGroups()
                .stream()
                // Garante que o grupo tem regras para evitar NullPointerException
                .filter(group -> group.getRules() != null)
                // Transforma a lista de grupos em um stream de todas as regras
                .flatMap(group -> group.getRules().stream())
                // Mapeia cada regra para o nome do seu tópico
                .map(AlertRule::topic)
                // Remove nomes de tópicos duplicados
                .distinct()
                .collect(Collectors.toList());

        // 2. Loga a informação
        if (topics.isEmpty()) {
            log.warn("⚠ Nenhum tópico configurado em alerts.groups. Nenhum listener será criado.");
        } else {
            // Informa que o monitoramento é feito por AdminClient (sem consumo de dados)
            log.info("⚡ Monitorando tópicos apenas via AdminClient (não há consumo real de mensagens): {}", topics);
        }
    }
}