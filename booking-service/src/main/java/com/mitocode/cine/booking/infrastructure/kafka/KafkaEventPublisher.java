package com.mitocode.cine.booking.infrastructure.kafka;

import com.mitocode.cine.booking.application.EventPublisher;
import com.mitocode.cine.booking.domain.event.BookingConfirmedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class KafkaEventPublisher implements EventPublisher {

    // Topic definido en los requisitos del extra opcional
    private static final String TOPIC_BOOKING_CONFIRMED = "cinema.bookings";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishBookingConfirmed(BookingConfirmedEvent event) {
        kafkaTemplate.send(TOPIC_BOOKING_CONFIRMED, event.bookingId().toString(), event);
        log.info("Evento publicado en topic {}: {}", TOPIC_BOOKING_CONFIRMED, event);
    }
}