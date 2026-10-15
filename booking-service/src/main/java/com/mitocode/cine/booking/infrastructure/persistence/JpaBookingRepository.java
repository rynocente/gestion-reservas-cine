package com.mitocode.cine.booking.infrastructure.persistence;

import com.mitocode.cine.booking.domain.Booking;
import com.mitocode.cine.booking.domain.BookingRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaBookingRepository extends JpaRepository<Booking, UUID>, BookingRepository {
}
