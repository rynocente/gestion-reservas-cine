package com.mitocode.cine.booking.infrastructure.config;

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
    public OpenAPI bookingServiceOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Booking Service API")
                                .description("""
                                        Servicio de reservas y orquestador SAGA del sistema de cine.
                                        
                                        **Ciclo de vida de una reserva y SAGA:**
                                        ```
                                        POST /bookings          → verifica asientos, reserva local (PENDING), descuenta remoto, confirma (CONFIRMED)
                                        PATCH /{id}/cancel      → marca CANCELLED y libera asiento remoto
                                        ```
                                        
                                        **Orquestación Distribuida:** Implementa resiliencia con `@Retry` hacia el `movie-service`. 
                                        Si algún paso de la reserva falla, se ejecutan las transacciones compensatorias para mantener 
                                        la consistencia eventual.
                                        
                                        **Eventos:** Las reservas exitosas publican un evento `BookingConfirmedEvent` al topic Kafka `cinema.bookings`.
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
                                .url("http://localhost:51200/api/v1")
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