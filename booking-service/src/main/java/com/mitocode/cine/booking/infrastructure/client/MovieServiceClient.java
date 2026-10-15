package com.mitocode.cine.booking.infrastructure.client;

import com.mitocode.cine.booking.exception.NoSeatsAvailableException;
import com.mitocode.cine.booking.exception.ResourceNotFoundException;
import com.mitocode.cine.booking.infrastructure.client.dto.MovieResponseDTO;
import com.mitocode.cine.booking.infrastructure.client.dto.ScreeningResponseDTO;
import com.mitocode.cine.booking.infrastructure.config.ServiceUrlsProperties;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Slf4j
@Component
public class MovieServiceClient {

    private final RestClient restClient;

    public MovieServiceClient(RestClient.Builder builder, ServiceUrlsProperties properties) {
        log.info("######## Creando MovieServiceClient");

        this.restClient = builder
                .baseUrl(properties.getMovieService().getUrl())
                .build();
    }

    public ScreeningResponseDTO getScreening(UUID screeningId) {
        log.info("Consultando función en MovieServiceClient: {}", screeningId);
        return restClient.get()
                .uri("/screenings/{id}", screeningId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    int statusCode = res.getStatusCode().value();
                    if (statusCode == 401 || statusCode == 403) {
                        throw new RuntimeException("Error de seguridad: movie-service rechazó el token (401/403)");
                    }
                    if (statusCode == 404) {
                        throw new ResourceNotFoundException("Función", screeningId);
                    }
                    throw new RuntimeException("Error del cliente al obtener la función: " + statusCode);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new RuntimeException("Error del servidor al obtener la función: " + res.getStatusCode().value());
                })
                .body(ScreeningResponseDTO.class);
    }

    public MovieResponseDTO getMovie(UUID movieId) {
        log.info("Consultando película en MovieServiceClient: {}", movieId);
        return restClient.get()
                .uri("/movies/{id}", movieId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    int statusCode = res.getStatusCode().value();
                    if (statusCode == 401 || statusCode == 403) {
                        throw new RuntimeException("Error de seguridad: movie-service rechazó el token (401/403)");
                    }
                    if (statusCode == 404) {
                        throw new ResourceNotFoundException("Película", movieId);
                    }
                    throw new RuntimeException("Error del cliente al obtener la película: " + statusCode);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new RuntimeException("Error del servidor al obtener la película: " + res.getStatusCode().value());
                })
                .body(MovieResponseDTO.class);
    }

    /**
     * SAGA: Acción de reserva con reintentos configurados.
     * @Retry intercepta la excepción, aplica el backoff configurado y reintenta.
     * Si todos los reintentos fallan, llama a `fallbackReserveSeat`.
     */
    @Retry(name = "movieServiceRetry", fallbackMethod = "fallbackReserveSeat")
    public void reserveSeat(UUID screeningId) {
        log.info("[RETRY] Llamando a movie-service para reservar asiento — screeningId: {}", screeningId);
        restClient.patch()
                .uri("/screenings/{id}/reserve", screeningId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    int statusCode = res.getStatusCode().value();
                    if (statusCode == 401 || statusCode == 403) {
                        throw new RuntimeException("Error de seguridad: movie-service rechazó el token (401/403)");
                    }
                    if (statusCode == 422) {
                        throw new NoSeatsAvailableException("Función sin asientos disponibles");
                    }
                    if (statusCode == 404) {
                        throw new ResourceNotFoundException("Función", screeningId);
                    }
                    throw new RuntimeException("Error del cliente al reservar asiento: " + statusCode);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new RuntimeException("Error del servidor al reservar asiento: " + res.getStatusCode().value());
                })
                .toBodilessEntity();
    }

    /**
     * Fallback ejecutado si se agotan los reintentos o el servicio está caído.
     */
    private void fallbackReserveSeat(UUID screeningId, Throwable t) {
        log.error("[RETRY] Fallback ejecutado para screeningId: {} debido a: {}", screeningId, t.getMessage());

        // Si el error original fue regla de negocio (422) o no encontrado (404), propagamos la excepción real
        if (t instanceof NoSeatsAvailableException) {
            throw (NoSeatsAvailableException) t;
        }
        if (t instanceof ResourceNotFoundException) {
            throw (ResourceNotFoundException) t;
        }

        // Para errores de red o 500, lanzamos RuntimeException para que el SAGA ejecute la compensación local
        throw new RuntimeException("El movie-service no está disponible temporalmente tras varios intentos: " + t.getMessage());
    }

    /**
     * SAGA: Acción de compensación (best-effort).
     */
    public void releaseSeat(UUID screeningId) {
        log.info("Llamando a movie-service para liberar asiento (Compensación) — screeningId: {}", screeningId);
        restClient.patch()
                .uri("/screenings/{id}/release", screeningId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    int statusCode = res.getStatusCode().value();
                    if (statusCode == 401 || statusCode == 403) {
                        throw new RuntimeException("Error de seguridad: movie-service rechazó el token (401/403)");
                    }
                    if (statusCode == 404) {
                        throw new ResourceNotFoundException("Función", screeningId);
                    }
                    throw new RuntimeException("Error del cliente al liberar asiento: " + statusCode);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new RuntimeException("Error del servidor al liberar asiento: " + res.getStatusCode().value());
                })
                .toBodilessEntity();
    }
}