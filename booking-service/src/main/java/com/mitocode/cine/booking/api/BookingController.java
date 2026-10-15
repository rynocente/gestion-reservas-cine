package com.mitocode.cine.booking.api;

import com.mitocode.cine.booking.api.dto.BookingRequest;
import com.mitocode.cine.booking.api.dto.BookingResponse;
import com.mitocode.cine.booking.application.BookingService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/bookings")
public class BookingController implements BookingControllerDocs {

    private static final Logger log = LoggerFactory.getLogger(BookingController.class);

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Override
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        log.info("Recibida petición para crear reserva para cliente: {}", request.customerId());

        BookingResponse response = bookingService.createBooking(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.id()).toUri();

        return ResponseEntity.created(location).body(response);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable UUID id) {
        return ResponseEntity.ok(bookingService.getBooking(id));
    }

    @Override
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable UUID id) {
        log.info("Recibida petición para cancelar reserva: {}", id);
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }
}