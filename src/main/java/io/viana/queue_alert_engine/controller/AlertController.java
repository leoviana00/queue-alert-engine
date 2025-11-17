package io.viana.queue_alert_engine.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.viana.queue_alert_engine.config.KafkaProperties;
import io.viana.queue_alert_engine.domain.AlertRule;
import io.viana.queue_alert_engine.service.KafkaMessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * Endpoints simples para envio/validação de alertas.
 */
@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
@Tag(name = "Alerts", description = "Endpoints relacionados a envio e testes de alertas")
public class AlertController {

    private final KafkaMessageProducer kafkaProducer;
    private final KafkaProperties kafkaProperties;
    private final ObjectMapper objectMapper;

    @Operation(summary = "Envia um alerta de teste via Kafka")
    @ApiResponse(responseCode = "200", description = "Alerta enviado com sucesso")
    @ApiResponse(responseCode = "400", description = "Payload inválido")
    @PostMapping("/test")
    public ResponseEntity<String> sendTestAlert(
            @RequestBody Map<String, Object> payload) {

        try {
            String topic = kafkaProperties.getProducer().getAlertTopic();
            String key = (String) payload.getOrDefault("topic", null);
            String json = objectMapper.writeValueAsString(payload);

            kafkaProducer.send(topic, key, json);
            return ResponseEntity.ok("Test alert sent");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid payload");
        }
    }
}


    /**
     * Envia um alerta de teste para o tópico de alertas.
     * Body exemplo:
     * {
     *   "groupId": "consumer-group-app",
     *   "topic": "queue-monitor-orders",
     *   "partition": 0,
     *   "lag": 123,
     *   "level": "WARNING"
     * }
     */

    // curl -X POST http://localhost:8080/api/alerts/test \
    // -H "Content-Type: application/json" \
    // -d '{
    //     "groupId": "consumer-group-app",
    //     "topic": "queue-monitor-orders",
    //     "partition": 0,
    //     "lag": 123,
    //     "level": "WARNING"
    // }'


