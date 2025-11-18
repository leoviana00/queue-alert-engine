package io.viana.queue_alert_engine.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.viana.queue_alert_engine.config.KafkaProperties;
import io.viana.queue_alert_engine.service.KafkaMessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * Endpoints simples para envio/validação de alertas via API REST.
 * Serve como um ponto de entrada para testar o fluxo de notificação.
 */
@RestController // Define que esta classe é um controlador REST
@RequestMapping("/api/alerts") // Define o caminho base para todos os endpoints
@RequiredArgsConstructor // Cria o construtor para injeção de dependência (final fields)
@Tag(name = "Alerts", description = "Endpoints relacionados a envio e testes de alertas") // Documentação Swagger/OpenAPI
public class AlertController {

    private final KafkaMessageProducer kafkaProducer; // Serviço para enviar mensagens ao Kafka
    private final KafkaProperties kafkaProperties; // Propriedades do Kafka (para obter o nome do tópico)
    private final ObjectMapper objectMapper; // Ferramenta para serializar objetos em JSON

    /**
     * Endpoint para simular o envio de um alerta de lag.
     * Recebe um payload JSON e o envia diretamente para o tópico de alertas do Kafka.
     *
     * @param payload Um mapa contendo os dados do alerta (groupId, topic, partition, lag, level).
     * @return 200 OK se a mensagem foi enviada, 400 Bad Request em caso de falha de serialização.
     */
    @Operation(summary = "Envia um alerta de teste via Kafka")
    @ApiResponse(responseCode = "200", description = "Alerta enviado com sucesso")
    @ApiResponse(responseCode = "400", description = "Payload inválido")
    @PostMapping("/test")
    public ResponseEntity<String> sendTestAlert(
            @RequestBody Map<String, Object> payload) { // Recebe o corpo da requisição como um Map

        try {
            // 1. Obtém o nome do tópico de alertas
            String topic = kafkaProperties.getProducer().getAlertTopic();

            // 2. Tenta usar o campo "topic" do payload como chave Kafka, senão usa null
            String key = (String) payload.getOrDefault("topic", null);

            // 3. Serializa o mapa recebido de volta para uma string JSON (que será o payload da mensagem Kafka)
            String json = objectMapper.writeValueAsString(payload);

            // 4. Envia a mensagem
            kafkaProducer.send(topic, key, json);
            return ResponseEntity.ok("Test alert sent");

        } catch (Exception e) {
            // Se falhar ao serializar o payload, retorna erro
            return ResponseEntity.badRequest().body("Invalid payload");
        }
    }
}