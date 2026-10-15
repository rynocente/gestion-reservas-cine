package com.mitocode.cine.booking.domain.event;

import java.util.UUID;


public record BookingConfirmedEvent(
        UUID bookingId,
        UUID customerId,
        UUID screeningId
) {}