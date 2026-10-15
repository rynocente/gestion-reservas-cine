package com.mitocode.cine.booking.api;

import com.mitocode.cine.booking.api.dto.BookingRequest;
import com.mitocode.cine.booking.api.dto.BookingResponse;
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

import java.util.UUID;

@Tag(name = "Bookings", description = "Gestión de reservas de cine y orquestación SAGA.")
public interface BookingControllerDocs {

    @Operation(
            summary = "Crear reserva (Inicia SAGA)",
            description = """
                    Crea una nueva reserva de cine e inicia el flujo SAGA distribuido:
                    1. Verifica disponibilidad en movie-service.
                    2. Crea la reserva local en PENDING.
                    3. Reserva el asiento en movie-service.
                    4. Confirma la reserva (CONFIRMED) y publica evento en Kafka.
                    
                    Si no hay asientos, devuelve 422 Unprocessable Entity inmediatamente.
                    Si falla cualquier paso posterior, se ejecutan las compensaciones (SAGA inverso).
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201", description = "Reserva completada exitosamente",
                    content = @Content(schema = @Schema(implementation = BookingResponse.class)),
                    headers = @Header(
                            name = "Location", description = "URL de la reserva creada", example = "/api/v1/bookings/{id}",
                            schema = @Schema(type = "string", format = "uri")
                    )
            ),
            @ApiResponse(
                    responseCode = "422", description = "No hay asientos disponibles (Violación de Invariante)",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "400", description = "Datos de petición inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "500", description = "Fallo en el SAGA (ver logs de compensación)",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos para crear la reserva", required = true,
            content = @Content(
                    schema = @Schema(implementation = BookingRequest.class),
                    examples = @ExampleObject(
                            name = "Reserva de ejemplo",
                            value = """
                                    {
                                      "screeningId": "123e4567-e89b-12d3-a456-426614174000",
                                      "customerId": "987e6543-e21b-12d3-a456-426614174000",
                                      "totalAmount": 15.50
                                    }"""
                    )
            )
    )
    ResponseEntity<BookingResponse> createBooking(BookingRequest request);

    @Operation(
            summary = "Obtener reserva por ID",
            description = "Retorna los detalles y el estado actual de una reserva.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva encontrada", content = @Content(schema = @Schema(implementation = BookingResponse.class))),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    ResponseEntity<BookingResponse> getBooking(UUID id);

    @Operation(
            summary = "Cancelar reserva (SAGA de Cancelación)",
            description = """
                    Inicia el proceso de cancelación:
                    1. Marca la reserva local como CANCELLED.
                    2. Llama al movie-service para liberar el asiento (best-effort).
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva cancelada exitosamente", content = @Content(schema = @Schema(implementation = BookingResponse.class))),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "La reserva ya estaba cancelada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    ResponseEntity<BookingResponse> cancelBooking(UUID id);
}