package io.viana.queue_alert_engine.controller;

import io.viana.queue_alert_engine.config.AlertsProperties;
import io.viana.queue_alert_engine.domain.AlertGroup;
import io.viana.queue_alert_engine.service.LagCheckerService;
import io.viana.queue_alert_engine.service.QueueOffsetTracker;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.TopicPartition;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitor")
@RequiredArgsConstructor
@Tag(name = "Monitoramento", description = "Operações para verificar lag e offsets dos consumidores")
public class MonitorController {

    private final LagCheckerService lagCheckerService;
    private final QueueOffsetTracker offsetTracker;
    private final AlertsProperties alertsProperties;


    // ------------------------------
    // LISTAR GROUPS CONFIGURADOS
    // ------------------------------

    @Operation(
        summary = "Lista todos os consumer groups configurados",
        description = "Retorna a lista de groupIds definidos no AlertsProperties"
    )
    @ApiResponse(responseCode = "200", description = "Groups listados com sucesso")
    @GetMapping("/groups")
    public ResponseEntity<List<String>> getConfiguredGroups() {

        if (alertsProperties.getGroups() == null || alertsProperties.getGroups().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<String> groups = alertsProperties.getGroups()
                .stream()
                .map(AlertGroup::getGroupId)
                .toList();

        return ResponseEntity.ok(groups);
    }


    // ------------------------------
    // TRIGGER MANUAL EM TODOS OS GROUPS
    // ------------------------------

    @Operation(
        summary = "Executa verificação manual de lag para todos os grupos",
        description = "Dispara manualmente a leitura de lag para cada grupo configurado"
    )
    @ApiResponse(responseCode = "200", description = "Monitoramento executado")
    @PostMapping("/trigger-all")
    public ResponseEntity<String> triggerAllGroups() {

        if (alertsProperties.getGroups() == null || alertsProperties.getGroups().isEmpty()) {
            return ResponseEntity.badRequest().body("Nenhum grupo configurado.");
        }

        alertsProperties.getGroups()
                .forEach(g -> lagCheckerService.checkLag(g.getGroupId(), g.getRules()));

        return ResponseEntity.ok("Lag check executed for all groups");
    }


    // ------------------------------
    // TRIGGER MANUAL EM UM GRUPO
    // ------------------------------

    @Operation(
        summary = "Executa verificação manual de lag para um grupo específico",
        description = "Dispara manualmente a checagem de lag apenas para o groupId informado"
    )
    @ApiResponse(responseCode = "200", description = "Monitoramento executado")
    @ApiResponse(responseCode = "404", description = "Grupo não encontrado")
    @PostMapping("/trigger/{groupId}")
    public ResponseEntity<String> triggerGroup(@PathVariable String groupId) {

        AlertGroup group = alertsProperties.getGroups()
                .stream()
                .filter(g -> g.getGroupId().equalsIgnoreCase(groupId))
                .findFirst()
                .orElse(null);

        if (group == null) {
            return ResponseEntity.status(404).body("Group not found: " + groupId);
        }

        lagCheckerService.checkLag(group.getGroupId(), group.getRules());
        return ResponseEntity.ok("Lag check executed for group " + groupId);
    }


    // ------------------------------
    // EXIBIR OFFSETS DE UM GRUPO
    // ------------------------------

    @Operation(
        summary = "Retorna os offsets consumidos por um grupo",
        description = "Mostra os offsets armazenados localmente no QueueOffsetTracker"
    )
    @ApiResponse(responseCode = "200", description = "Offsets retornados com sucesso")
    @ApiResponse(responseCode = "404", description = "Grupo não encontrado ou sem offsets registrados")
    @GetMapping("/offsets/{groupId}")
    public ResponseEntity<Map<TopicPartition, Long>> getOffsets(@PathVariable String groupId) {

        Map<TopicPartition, Long> offsets =
                offsetTracker.getConsumedOffsets().get(groupId);

        if (offsets == null || offsets.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }

        return ResponseEntity.ok(offsets);
    }
}
