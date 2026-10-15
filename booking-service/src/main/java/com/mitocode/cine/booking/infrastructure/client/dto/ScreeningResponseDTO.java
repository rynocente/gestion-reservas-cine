package com.mitocode.cine.booking.infrastructure.client.dto;

import java.util.UUID;

public record ScreeningResponseDTO(
        UUID id,
        UUID movieId,
        Integer availableSeats
) {}