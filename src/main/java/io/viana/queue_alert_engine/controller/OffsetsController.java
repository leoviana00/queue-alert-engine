package io.viana.queue_alert_engine.controller;

import io.viana.queue_alert_engine.config.AlertsProperties;
import io.viana.queue_alert_engine.domain.AlertGroup;
import io.viana.queue_alert_engine.domain.AlertRule;
import io.viana.queue_alert_engine.service.QueueOffsetTracker;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Exposição dos offsets consumidos conhecidos pelo QueueOffsetTracker.
 */
@RestController
@RequestMapping("/api/offsets")
@RequiredArgsConstructor
@Tag(name = "Offsets", description = "Consulta offsets consumidos de cada consumer group")
public class OffsetsController {

    private final AlertsProperties alertsProperties;
    private final QueueOffsetTracker queueOffsetTracker;

    // ------------------------------
    // LISTA OS GROUP IDS
    // ------------------------------

    @Operation(
        summary = "Lista todos os consumer groups configurados",
        description = "Retorna apenas os groupIds disponíveis no AlertsProperties"
    )
    @ApiResponse(responseCode = "200", description = "Groups retornados com sucesso")
    @GetMapping("/groups")
    public ResponseEntity<List<String>> listGroupIds() {

        if (alertsProperties.getGroups() == null || alertsProperties.getGroups().isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        List<String> ids = alertsProperties.getGroups()
                .stream()
                .map(AlertGroup::getGroupId)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ids);
    }


    // ------------------------------
    // RETORNA OS OFFSETS DE UM GROUP
    // ------------------------------

    @Operation(
        summary = "Retorna os offsets atuais consumidos para um groupId",
        description = """
                Os offsets retornados são aqueles registrados internamente no QueueOffsetTracker, 
                considerando apenas as regras configuradas para o grupo.
                """
    )
    @ApiResponse(responseCode = "200", description = "Offsets retornados com sucesso")
    @ApiResponse(responseCode = "404", description = "Grupo não encontrado")
    @GetMapping("/groups/{groupId}")
    public ResponseEntity<?> getOffsetsForGroup(@PathVariable String groupId) {

        if (alertsProperties.getGroups() == null) {
            return ResponseEntity.status(404).body("Nenhum grupo configurado");
        }

        AlertGroup group = alertsProperties.getGroups()
                .stream()
                .filter(g -> g.getGroupId().equalsIgnoreCase(groupId))
                .findFirst()
                .orElse(null);

        if (group == null) {
            return ResponseEntity.status(404).body("Group not found: " + groupId);
        }

        // Ex: { "orders-0": 1234 }
        Map<String, Long> result = new LinkedHashMap<>();

        for (AlertRule rule : group.getRules()) {
            long offset = queueOffsetTracker.getLastConsumedOffset(
                    groupId,
                    rule.topic(),
                    rule.partition()
            );
            result.put(rule.topic() + "-" + rule.partition(), offset);
        }

        return ResponseEntity.ok(result);
    }
}
