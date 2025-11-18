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
import java.util.stream.Collectors;

/**
 * Controlador REST para monitoramento manual e consulta de estado.
 * Permite listar grupos, disparar a checagem de lag sob demanda e consultar offsets.
 */
@RestController // Define que esta classe é um controlador REST
@RequestMapping("/api/monitor") // Define o caminho base
@RequiredArgsConstructor // Cria o construtor para injeção de dependência
@Tag(name = "Monitoramento", description = "Operações para verificar lag e offsets dos consumidores") // Documentação Swagger
public class MonitorController {

    private final LagCheckerService lagCheckerService; // Serviço que executa a checagem de lag
    private final QueueOffsetTracker offsetTracker; // Serviço que mantém os offsets em cache
    private final AlertsProperties alertsProperties; // Propriedades de configuração dos grupos


    // ------------------------------
    // LISTAR GROUPS CONFIGURADOS
    // ------------------------------

    /**
     * Retorna a lista dos IDs dos consumer groups configurados para monitoramento.
     */
    @Operation(
        summary = "Lista todos os consumer groups configurados",
        description = "Retorna a lista de groupIds definidos no AlertsProperties"
    )
    @ApiResponse(responseCode = "200", description = "Groups listados com sucesso")
    @GetMapping("/groups") // GET /api/monitor/groups
    public ResponseEntity<List<String>> getConfiguredGroups() {

        // Verifica se há grupos configurados. Se não, retorna 204 No Content
        if (alertsProperties.getGroups() == null || alertsProperties.getGroups().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        // Mapeia a lista de AlertGroup para uma lista de Strings contendo apenas os IDs
        List<String> groups = alertsProperties.getGroups()
                .stream()
                .map(AlertGroup::getGroupId)
                .toList();

        return ResponseEntity.ok(groups);
    }


    // ------------------------------
    // TRIGGER MANUAL EM TODOS OS GROUPS
    // ------------------------------

    /**
     * Dispara o processo de checagem de lag para todos os grupos configurados.
     */
    @Operation(
        summary = "Executa verificação manual de lag para todos os grupos",
        description = "Dispara manualmente a leitura de lag para cada grupo configurado"
    )
    @ApiResponse(responseCode = "200", description = "Monitoramento executado")
    @PostMapping("/trigger-all") // POST /api/monitor/trigger-all
    public ResponseEntity<String> triggerAllGroups() {

        // Validação: deve haver grupos configurados
        if (alertsProperties.getGroups() == null || alertsProperties.getGroups().isEmpty()) {
            return ResponseEntity.badRequest().body("Nenhum grupo configurado.");
        }

        // Itera sobre todos os grupos e chama o serviço de checagem de lag para cada um
        alertsProperties.getGroups()
                .forEach(g -> lagCheckerService.checkLag(g.getGroupId(), g.getRules()));

        return ResponseEntity.ok("Lag check executed for all groups");
    }


    // ------------------------------
    // TRIGGER MANUAL EM UM GRUPO
    // ------------------------------

    /**
     * Dispara o processo de checagem de lag para um grupo específico.
     *
     * @param groupId O ID do grupo a ser verificado.
     */
    @Operation(
        summary = "Executa verificação manual de lag para um grupo específico",
        description = "Dispara manualmente a checagem de lag apenas para o groupId informado"
    )
    @ApiResponse(responseCode = "200", description = "Monitoramento executado")
    @ApiResponse(responseCode = "404", description = "Grupo não encontrado")
    @PostMapping("/trigger/{groupId}") // POST /api/monitor/trigger/{groupId}
    public ResponseEntity<String> triggerGroup(@PathVariable String groupId) {

        // Busca o grupo específico na lista de configurações
        AlertGroup group = alertsProperties.getGroups()
                .stream()
                .filter(g -> g.getGroupId().equalsIgnoreCase(groupId))
                .findFirst()
                .orElse(null);

        // Se o grupo não for encontrado, retorna 404
        if (group == null) {
            return ResponseEntity.status(404).body("Group not found: " + groupId);
        }

        // Executa a checagem de lag apenas para o grupo encontrado
        lagCheckerService.checkLag(group.getGroupId(), group.getRules());
        return ResponseEntity.ok("Lag check executed for group " + groupId);
    }


    // ------------------------------
    // EXIBIR OFFSETS DE UM GRUPO
    // ------------------------------

    /**
     * Retorna os últimos offsets de mensagens que foram consumidas (em cache).
     *
     * @param groupId O ID do grupo para o qual consultar os offsets.
     */
    @Operation(
        summary = "Retorna os offsets consumidos por um grupo",
        description = "Mostra os offsets armazenados localmente no QueueOffsetTracker"
    )
    @ApiResponse(responseCode = "200", description = "Offsets retornados com sucesso")
    @ApiResponse(responseCode = "404", description = "Grupo não encontrado ou sem offsets registrados")
    @GetMapping("/offsets/{groupId}") // GET /api/monitor/offsets/{groupId}
    public ResponseEntity<Map<TopicPartition, Long>> getOffsets(@PathVariable String groupId) {

        // Busca os offsets consumidos para o groupId específico no cache
        Map<TopicPartition, Long> offsets =
                offsetTracker.getConsumedOffsets().get(groupId);

        // Se o mapa for nulo ou vazio (grupo sem offsets registrados), retorna 404
        if (offsets == null || offsets.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }

        // Retorna o mapa de TopicPartition para Offset com status 200 OK
        return ResponseEntity.ok(offsets);
    }
}