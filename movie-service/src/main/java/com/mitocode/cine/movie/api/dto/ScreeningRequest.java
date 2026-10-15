package com.mitocode.cine.movie.api.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.UUID;


public record ScreeningRequest(

        @NotNull(message = "El ID de la película es requerido")
        UUID movieId,

        @NotBlank(message = "La sala es requerida")
        String room,

        @NotNull(message = "La fecha y hora es requerida")
        @Future(message = "La función debe programarse para una fecha futura")
        LocalDateTime showAt,

        @NotNull(message = "La capacidad total es requerida")
        @Positive(message = "La capacidad debe ser mayor a cero")
        Integer totalSeats
) {}