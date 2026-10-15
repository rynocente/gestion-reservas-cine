package com.mitocode.cine.booking.infrastructure.client.dto;

import java.util.UUID;

public record MovieResponseDTO(
        UUID id,
        String title
) {}