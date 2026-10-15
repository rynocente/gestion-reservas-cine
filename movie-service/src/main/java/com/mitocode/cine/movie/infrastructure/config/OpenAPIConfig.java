package com.mitocode.cine.movie.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI movieServiceOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Movie Service API")
                                .description("""
                                        Servicio de gestión de películas y funciones (screenings) del sistema de cine.
                                        
                                        **Catálogo y Disponibilidad:**
                                        Actúa como el inventario del cine. Mantiene el registro de las películas y controla 
                                        la capacidad de asientos (`availableSeats`) por función.
                                        
                                        **Endpoints críticos para el SAGA (usados por booking-service):**
                                        ```
                                        PATCH /screenings/{id}/reserve  → decrementa asientos (Paso 1 SAGA)
                                        PATCH /screenings/{id}/release  → incrementa asientos (Compensación)
                                        ```
                                        """)
                                .version("1.0.0")
                                .contact(
                                        new Contact()
                                                .name("MitoCode")
                                                .email("soporte@mitocore.com")
                                )
                )
                .addServersItem(
                        new Server()
                                .url("http://localhost:51100/api/v1")
                                .description("Local")
                )
                .addServersItem(
                        new Server()
                                .url("http://dev.miempresa.com/api/v1")
                                .description("Dev")
                )
                .addServersItem(
                        new Server()
                                .url("http://qa.miempresa.com/api/v1")
                                .description("QA")
                )
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT RS256 — obtener con POST /api/v1/auth/login en auth-service")
                        )
                );
    }
}