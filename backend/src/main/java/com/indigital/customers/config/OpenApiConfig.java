package com.indigital.customers.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Customers Microservice API - InDigital XP")
                        .version("1.0.0")
                        .description("Microservicio RESTful para gestión de clientes y cálculo de indicadores demográficos. " +
                                "Desarrollado con Spring Boot y Java 21 siguiendo buenas prácticas de Clean Code y principios SOLID.")
                        .contact(new Contact()
                                .name("Jordy Montalvo Alfaro")
                                .email("jordyjosephmontalvo@gmail.com")
                                .url("https://github.com/jordyjoseph"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Entorno Local")
                ));
    }
}
