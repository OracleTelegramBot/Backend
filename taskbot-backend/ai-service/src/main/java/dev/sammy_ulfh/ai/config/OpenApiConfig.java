package dev.sammy_ulfh.ai.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Server prodServer = new Server()
                .url("https://sammy-ulfh.dev")
                .description("Producción");

        Server localServer = new Server()
                .url("http://localhost:8083")
                .description("Local");

        return new OpenAPI()
                .servers(List.of(prodServer, localServer))
                .info(new Info()
                        .title("AI Service API")
                        .version("1.0.0")
                        .description("Microservicio de Inteligencia Artificial"));
    }
}
