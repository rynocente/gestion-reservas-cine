package com.mitocode.cine.booking.domain;

import java.util.Optional;
import java.util.UUID;

public interface BookingRepository {
    Booking save(Booking booking);

    Optional<Booking> findById(UUID id);
}
