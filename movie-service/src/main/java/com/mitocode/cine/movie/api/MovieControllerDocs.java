package com.mitocode.cine.movie.api;

import com.mitocode.cine.movie.api.dto.MovieRequest;
import com.mitocode.cine.movie.api.dto.MovieResponse;
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

@Tag(name = "Movies", description = "Gestión del catálogo de películas del cine.")
public interface MovieControllerDocs {

    @Operation(
            summary = "Crear película",
            description = "Añade una nueva película al catálogo del cine. El estado inicial será activo.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201", description = "Película creada exitosamente",
                    content = @Content(schema = @Schema(implementation = MovieResponse.class)),
                    headers = @Header(
                            name = "Location", description = "URL de la película creada", example = "/api/v1/movies/{id}",
                            schema = @Schema(type = "string", format = "uri")
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Datos de petición inválidos", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos de la película a crear", required = true,
            content = @Content(
                    schema = @Schema(implementation = MovieRequest.class),
                    examples = @ExampleObject(
                            name = "Interstellar", summary = "Ejemplo de creación de película",
                            value = """
                                    {
                                      "title": "Interstellar",
                                      "director": "Christopher Nolan",
                                      "genre": "SCI_FI",
                                      "durationMin": 169,
                                      "rating": "PG-13"
                                    }"""
                    )
            )
    )
    ResponseEntity<MovieResponse> create(MovieRequest request);

    @Operation(
            summary = "Obtener todas las películas",
            description = "Retorna el listado completo del catálogo de películas."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido", useReturnTypeSchema = true)
    })
    ResponseEntity<List<MovieResponse>> findAll();

    @Operation(
            summary = "Obtener película por ID",
            description = "Retorna los detalles de una película específica."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Película encontrada", content = @Content(schema = @Schema(implementation = MovieResponse.class))),
            @ApiResponse(responseCode = "404", description = "Película no encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    ResponseEntity<MovieResponse> findById(UUID id);

    @Operation(
            summary = "Actualizar película",
            description = "Actualiza los datos de una película existente usando MovieRequest.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Película actualizada", content = @Content(schema = @Schema(implementation = MovieResponse.class))),
            @ApiResponse(responseCode = "404", description = "Película no encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    ResponseEntity<MovieResponse> update(UUID id, MovieRequest request);

    @Operation(summary = "Eliminar película", description = "Eliminación física de la película.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Película eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Película no encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    ResponseEntity<Void> delete(UUID id);

    @Operation(summary = "Activar película", description = "Cambia el estado de la película a activo (true).", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Película activada", content = @Content(schema = @Schema(implementation = MovieResponse.class))),
            @ApiResponse(responseCode = "404", description = "Película no encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    ResponseEntity<MovieResponse> activate(UUID id);

    @Operation(summary = "Desactivar película", description = "Cambia el estado de la película a inactivo (false).", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Película desactivada", content = @Content(schema = @Schema(implementation = MovieResponse.class))),
            @ApiResponse(responseCode = "404", description = "Película no encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    ResponseEntity<MovieResponse> deactivate(UUID id);
}