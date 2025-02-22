package br.com.techchallenge.safedeliver.core.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("SafeDeliver Gateway API")
                .version("1.0")
                .description("Gateway centralizando todas as APIs do sistema SafeDeliver"));
    }

    @Bean
    public GroupedOpenApi clientesApi() {
        return GroupedOpenApi.builder()
                .group("clientes")
                .pathsToMatch("/clientes/**")
                .build();
    }

    @Bean
    public GroupedOpenApi pedidosApi() {
        return GroupedOpenApi.builder()
                .group("pedidos")
                .pathsToMatch("/pedidos/**")
                .build();
    }

    @Bean
    public GroupedOpenApi produtosApi() {
        return GroupedOpenApi.builder()
                .group("produtos")
                .pathsToMatch("/produtos/**")
                .build();
    }

    @Bean
    public GroupedOpenApi LogisticaApi() {
        return GroupedOpenApi.builder()
                .group("Logistica")
                .pathsToMatch("/rastreamento/**")
                .build();
    }
}
