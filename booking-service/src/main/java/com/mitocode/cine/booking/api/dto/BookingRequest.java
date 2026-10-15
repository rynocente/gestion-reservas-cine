package com.mitocode.cine.booking.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record BookingRequest(
        @NotNull(message = "El screeningId es requerido")
        UUID screeningId,

        @NotNull(message = "El customerId es requerido")
        UUID customerId,

        @NotNull(message = "El totalAmount es requerido")
        @Positive(message = "El monto total debe ser mayor a cero")
        BigDecimal totalAmount
) {}