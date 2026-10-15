package com.mitocode.cine.movie.api;

import com.mitocode.cine.movie.api.dto.ScreeningRequest;
import com.mitocode.cine.movie.api.dto.ScreeningResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "Screenings", description = "Gestión de funciones de cine y control de inventario de asientos (Integración SAGA).")
public interface ScreeningControllerDocs {

    @Operation(
            summary = "Programar función",
            description = "Crea una nueva función asociándola a una película existente e inicializando la disponibilidad de asientos.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201", description = "Función programada exitosamente",
                    content = @Content(schema = @Schema(implementation = ScreeningResponse.class)),
                    headers = @Header(
                            name = "Location", description = "URL de la función creada", example = "/api/v1/screenings/{id}",
                            schema = @Schema(type = "string", format = "uri")
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Datos de petición inválidos", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "La película asociada no existe", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos de la función a programar", required = true,
            content = @Content(
                    schema = @Schema(implementation = ScreeningRequest.class),
                    examples = @ExampleObject(
                            name = "Función en Sala IMAX", summary = "Ejemplo de programación de función",
                            value = """
                                    {
                                      "movieId": "123e4567-e89b-12d3-a456-426614174000",
                                      "room": "Sala 4 IMAX",
                                      "showAt": "2026-11-20T19:30:00",
                                      "totalSeats": 150
                                    }"""
                    )
            )
    )
    ResponseEntity<ScreeningResponse> create(ScreeningRequest request);

    @Operation(summary = "Obtener todas las funciones", description = "Retorna el listado completo de las funciones programadas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido", useReturnTypeSchema = true)
    })
    ResponseEntity<List<ScreeningResponse>> findAll();

    @Operation(summary = "Obtener función por ID", description = "Retorna los detalles y disponibilidad actual de una función.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Función encontrada", content = @Content(schema = @Schema(implementation = ScreeningResponse.class))),
            @ApiResponse(responseCode = "404", description = "Función no encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    ResponseEntity<ScreeningResponse> findById(UUID id);

    @Operation(summary = "Eliminar función", description = "Eliminación física de la programación.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Función eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Función no encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    ResponseEntity<Void> delete(UUID id);

    // ── Endpoints del SAGA ───────────────────────────────────────────────────

    @Operation(
            summary = "Reservar un asiento (SAGA)",
            description = "Resta un asiento disponible. Llamado síncronamente por el booking-service durante la creación de una reserva.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Asiento reservado con éxito"),
            @ApiResponse(responseCode = "404", description = "Función no encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(
                    responseCode = "422", description = "No hay asientos disponibles (Lanza compensación en el SAGA)",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    ResponseEntity<Void> reserveSeat(UUID id);

    @Operation(
            summary = "Liberar un asiento (SAGA - Compensación)",
            description = "Suma un asiento a la disponibilidad. Llamado por el booking-service si el SAGA falla en pasos posteriores (ej. pago rechazado).",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Asiento liberado con éxito (Best-effort)"),
            @ApiResponse(responseCode = "404", description = "Función no encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    ResponseEntity<Void> releaseSeat(UUID id);
}