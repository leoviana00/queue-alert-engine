package io.viana.queue_alert_engine.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Queue Alert Engine API")
                        .description("API para monitoramento de lags e disparo de alertas Kafka/Telegram")
                        .version("1.0.0"));
    }
}
