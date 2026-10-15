package com.mitocode.cine.movie.api.dto;

import com.mitocode.cine.movie.domain.Genre;

import java.time.Instant;
import java.util.UUID;

public record MovieResponse(
        UUID id,
        String title,
        String director,
        Genre genre,
        Integer durationMin,
        String rating,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {}