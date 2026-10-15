package com.mitocode.cine.booking.api.dto;

import com.mitocode.cine.booking.domain.BookingStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BookingResponse(
        UUID id,
        UUID screeningId,
        String movieTitle,
        UUID customerId,
        BookingStatus status,
        BigDecimal totalAmount,
        Instant createdAt,
        Instant updatedAt
) {}