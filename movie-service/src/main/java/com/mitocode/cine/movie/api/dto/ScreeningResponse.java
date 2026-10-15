package com.mitocode.cine.movie.api.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;


public record ScreeningResponse(
        UUID id,
        UUID movieId,
        String room,
        LocalDateTime showAt,
        Integer totalSeats,
        Integer availableSeats,
        boolean active
) {}