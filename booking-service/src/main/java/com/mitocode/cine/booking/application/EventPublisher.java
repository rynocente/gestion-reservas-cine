package com.mitocode.cine.booking.application;

import com.mitocode.cine.booking.domain.event.BookingConfirmedEvent;


public interface EventPublisher {

    void publishBookingConfirmed(BookingConfirmedEvent event);
}