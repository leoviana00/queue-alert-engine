package io.viana.queue_alert_engine.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Classe de configuração para o Swagger/OpenAPI 3.
 * Define informações básicas sobre a API para serem exibidas na documentação gerada.
 */
@Configuration // Marca a classe como uma fonte de definições de beans do Spring
public class SwaggerConfig {

    /**
     * Define um bean para customizar a configuração da documentação OpenAPI.
     *
     * @return O objeto OpenAPI com informações customizadas.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // Define as informações gerais sobre a API (título, descrição, versão)
                .info(new Info()
                        .title("Queue Alert Engine API") // Título da API
                        .description("API para monitoramento de lags e disparo de alertas Kafka/Telegram") // Descrição da API
                        .version("1.0.0")); // Versão da API
    }
}