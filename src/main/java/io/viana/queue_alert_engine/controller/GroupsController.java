package io.viana.queue_alert_engine.controller;

import io.viana.queue_alert_engine.config.AlertsProperties;
import io.viana.queue_alert_engine.domain.AlertGroup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@Tag(name = "Alert Groups", description = "Consulta os grupos de regras de alerta configurados")
public class GroupsController {

    private final AlertsProperties alertsProperties;

    @Operation(
        summary = "Lista todos os grupos de alerta",
        description = "Retorna todos os grupos configurados no AlertsProperties"
    )
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<AlertGroup>> listGroups() {
        List<AlertGroup> groups = alertsProperties.getGroups();

        if (groups == null || groups.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(groups);
    }

    @Operation(
        summary = "Busca um grupo específico",
        description = "Retorna um grupo de alertas pelo seu ID"
    )
    @ApiResponse(responseCode = "200", description = "Grupo encontrado")
    @ApiResponse(responseCode = "404", description = "Grupo não encontrado")
    @GetMapping("/{groupId}")
    public ResponseEntity<AlertGroup> getGroup(@PathVariable String groupId) {

        List<AlertGroup> groups = alertsProperties.getGroups();
        if (groups == null || groups.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return groups.stream()
                .filter(g -> g.getGroupId().equalsIgnoreCase(groupId))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
