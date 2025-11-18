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
 * Controlador REST para consultar os offsets consumidos (a posição de leitura)
 * de cada grupo de consumidores que está sendo monitorado.
 */
@RestController // Define que esta classe é um controlador REST
@RequestMapping("/api/offsets") // Define o caminho base
@RequiredArgsConstructor // Cria o construtor para injeção de dependência
@Tag(name = "Offsets", description = "Consulta offsets consumidos de cada consumer group") // Documentação Swagger
public class OffsetsController {

    private final AlertsProperties alertsProperties; // Propriedades de configuração dos grupos
    private final QueueOffsetTracker queueOffsetTracker; // Serviço que armazena os offsets consumidos

    // ------------------------------
    // LISTA OS GROUP IDS
    // ------------------------------

    /**
     * Retorna a lista dos IDs de todos os consumer groups que possuem regras configuradas.
     */
    @Operation(
        summary = "Lista todos os consumer groups configurados",
        description = "Retorna apenas os groupIds disponíveis no AlertsProperties"
    )
    @ApiResponse(responseCode = "200", description = "Groups retornados com sucesso")
    @GetMapping("/groups") // GET /api/offsets/groups
    public ResponseEntity<List<String>> listGroupIds() {

        // Se não houver grupos configurados, retorna uma lista vazia (200 OK)
        if (alertsProperties.getGroups() == null || alertsProperties.getGroups().isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        // Mapeia a lista de AlertGroup para uma lista contendo apenas os IDs
        List<String> ids = alertsProperties.getGroups()
                .stream()
                .map(AlertGroup::getGroupId)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ids);
    }


    // ------------------------------
    // RETORNA OS OFFSETS DE UM GROUP
    // ------------------------------

    /**
     * Retorna o último offset consumido registrado para cada tópico/partição
     * que pertence ao groupId especificado.
     *
     * @param groupId O ID do grupo de consumidores a ser consultado.
     */
    @Operation(
        summary = "Retorna os offsets atuais consumidos para um groupId",
        description = """
                Os offsets retornados são aqueles registrados internamente no QueueOffsetTracker, 
                considerando apenas as regras configuradas para o grupo.
                """
    )
    @ApiResponse(responseCode = "200", description = "Offsets retornados com sucesso")
    @ApiResponse(responseCode = "404", description = "Grupo não encontrado")
    @GetMapping("/groups/{groupId}") // GET /api/offsets/groups/{groupId}
    public ResponseEntity<?> getOffsetsForGroup(@PathVariable String groupId) {

        // Validação inicial para configurações vazias
        if (alertsProperties.getGroups() == null) {
            return ResponseEntity.status(404).body("Nenhum grupo configurado");
        }

        // Busca o grupo de alerta correspondente ao groupId fornecido
        AlertGroup group = alertsProperties.getGroups()
                .stream()
                .filter(g -> g.getGroupId().equalsIgnoreCase(groupId))
                .findFirst()
                .orElse(null);

        // Se o grupo não for encontrado, retorna 404
        if (group == null) {
            return ResponseEntity.status(404).body("Group not found: " + groupId);
        }

        // Mapa que armazenará o resultado: Chave="tópico-partição", Valor=offset
        Map<String, Long> result = new LinkedHashMap<>();

        // Itera sobre as regras do grupo para buscar os offsets monitorados
        for (AlertRule rule : group.getRules()) {
            // Busca o último offset consumido no serviço QueueOffsetTracker
            long offset = queueOffsetTracker.getLastConsumedOffset(
                    groupId,
                    rule.topic(),
                    rule.partition()
            );
            // Formata a chave como "tópico-partição" e adiciona o offset
            result.put(rule.topic() + "-" + rule.partition(), offset);
        }

        // Retorna o mapa de offsets com status 200 OK
        return ResponseEntity.ok(result);
    }
}