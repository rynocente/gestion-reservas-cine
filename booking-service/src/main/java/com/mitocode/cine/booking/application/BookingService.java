package com.mitocode.cine.booking.application;

import com.mitocode.cine.booking.api.dto.BookingRequest;
import com.mitocode.cine.booking.api.dto.BookingResponse;

import java.util.UUID;

public interface BookingService {

    BookingResponse createBooking(BookingRequest request);

    BookingResponse cancelBooking(UUID id);

    BookingResponse getBooking(UUID id);
}