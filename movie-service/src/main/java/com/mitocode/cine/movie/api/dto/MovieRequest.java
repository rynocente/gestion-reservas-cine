package com.mitocode.cine.movie.api.dto;

import com.mitocode.cine.movie.domain.Genre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


public record MovieRequest(

        @NotBlank(message = "El título es requerido")
        String title,

        String director,

        @NotNull(message = "El género es requerido")
        Genre genre,

        @NotNull(message = "La duración es requerida")
        @Positive(message = "La duración debe ser mayor a cero")
        Integer durationMin,

        String rating
) {}