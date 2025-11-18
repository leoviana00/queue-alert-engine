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

/**
 * Controlador REST para consultar os grupos de consumidores e suas regras de alerta
 * configuradas na aplicação.
 */
@RestController // Define que esta classe é um controlador REST
@RequestMapping("/api/groups") // Define o caminho base para todos os endpoints
@RequiredArgsConstructor // Cria o construtor para injeção de dependência (final fields)
@Tag(name = "Alert Groups", description = "Consulta os grupos de regras de alerta configurados") // Documentação Swagger/OpenAPI
public class GroupsController {

    // Propriedades de configuração dos alertas, onde os grupos são armazenados
    private final AlertsProperties alertsProperties;

    /**
     * Endpoint para listar todos os grupos de alerta configurados.
     *
     * @return Uma lista de AlertGroup ou 204 No Content se a lista estiver vazia.
     */
    @Operation(
        summary = "Lista todos os grupos de alerta",
        description = "Retorna todos os grupos configurados no AlertsProperties"
    )
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping // Mapeia para requisições GET em /api/groups
    public ResponseEntity<List<AlertGroup>> listGroups() {
        // Obtém a lista de grupos das propriedades
        List<AlertGroup> groups = alertsProperties.getGroups();

        // Se a lista for nula ou vazia, retorna 204 No Content
        if (groups == null || groups.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        // Retorna a lista de grupos com status 200 OK
        return ResponseEntity.ok(groups);
    }

    /**
     * Endpoint para buscar um grupo de alerta específico pelo seu ID.
     *
     * @param groupId O ID do grupo de consumidores a ser procurado (vem da URL).
     * @return O AlertGroup encontrado ou 404 Not Found.
     */
    @Operation(
        summary = "Busca um grupo específico",
        description = "Retorna um grupo de alertas pelo seu ID"
    )
    @ApiResponse(responseCode = "200", description = "Grupo encontrado")
    @ApiResponse(responseCode = "404", description = "Grupo não encontrado")
    @GetMapping("/{groupId}") // Mapeia para requisições GET em /api/groups/{groupId}
    public ResponseEntity<AlertGroup> getGroup(@PathVariable String groupId) {

        List<AlertGroup> groups = alertsProperties.getGroups();

        // Se a lista de grupos estiver vazia, não há o que buscar (404)
        if (groups == null || groups.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Usa Stream para buscar o primeiro grupo cujo ID case com o ID da URL (ignorando case)
        return groups.stream()
                .filter(g -> g.getGroupId().equalsIgnoreCase(groupId))
                .findFirst() // Tenta encontrar o primeiro resultado
                .map(ResponseEntity::ok) // Se encontrar, retorna 200 OK com o objeto
                .orElseGet(() -> ResponseEntity.notFound().build()); // Se não encontrar, retorna 404 Not Found
    }
}