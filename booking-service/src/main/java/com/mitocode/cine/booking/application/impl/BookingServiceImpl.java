package com.mitocode.cine.booking.application.impl;

import com.mitocode.cine.booking.api.dto.BookingRequest;
import com.mitocode.cine.booking.api.dto.BookingResponse;
import com.mitocode.cine.booking.application.BookingService;
import com.mitocode.cine.booking.application.EventPublisher;
import com.mitocode.cine.booking.domain.Booking;
import com.mitocode.cine.booking.domain.BookingRepository;
import com.mitocode.cine.booking.domain.BookingStatus;
import com.mitocode.cine.booking.domain.event.BookingConfirmedEvent;
import com.mitocode.cine.booking.exception.InvalidBookingStateException;
import com.mitocode.cine.booking.exception.NoSeatsAvailableException;
import com.mitocode.cine.booking.exception.ResourceNotFoundException;
import com.mitocode.cine.booking.infrastructure.client.MovieServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class BookingServiceImpl implements BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final BookingRepository bookingRepository;
    private final MovieServiceClient movieClient;
    private final EventPublisher eventPublisher;

    public BookingServiceImpl(BookingRepository bookingRepository, MovieServiceClient movieClient, EventPublisher eventPublisher) {
        this.bookingRepository = bookingRepository;
        this.movieClient = movieClient;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public BookingResponse createBooking(BookingRequest request) {

        // Variables de estado para determinar qué compensaciones ejecutar si algo falla
        String currentStep = "unknown";
        UUID bookingId = null;
        boolean seatReserved = false;

        try {
            log.info("SAGA iniciado — customerId: {}, screeningId: {}", request.customerId(), request.screeningId());

            // ── Paso 1: Verificar función disponible ──────────────────────────────────
            currentStep = "verify_screening";
            var screening = movieClient.getScreening(request.screeningId());
            if (screening.availableSeats() <= 0) {
                // Lanza nuestra excepción custom que devolverá 422 Unprocessable Entity
                throw new NoSeatsAvailableException("Función sin asientos disponibles");
            }
            var movie = movieClient.getMovie(screening.movieId());
            log.info("SAGA [1/4] función verificada — availableSeats: {}", screening.availableSeats());

            // ── Paso 2: Crear booking en estado PENDING ───────────────────────────────
            currentStep = "create_booking";
            Booking booking = Booking.create(request.screeningId(), movie.title(), request.customerId(), request.totalAmount());
            booking = bookingRepository.save(booking);
            bookingId = booking.getId();
            log.info("SAGA [2/4] reserva local creada — bookingId: {}, status: {}", booking.getId(), booking.getStatus());

            // ── Paso 3: Reservar asiento en movie-service ─────────────────────────────
            currentStep = "reserve_seat";
            movieClient.reserveSeat(request.screeningId());
            seatReserved = true; // Se actualiza DESPUÉS del paso exitoso
            log.info("SAGA [3/4] asiento reservado remotamente");

            // ── Paso 4: Confirmar booking ─────────────────────────────────────────────
            currentStep = "confirm_booking";
            booking.confirm();
            booking = bookingRepository.save(booking);
            log.info("SAGA [4/4] reserva confirmada — status: {}", booking.getStatus());

            // ── Evento Kafka ──────────────────────────────────────────────────────────
            currentStep = "publish_event";
            eventPublisher.publishBookingConfirmed(new BookingConfirmedEvent(
                    booking.getId(), booking.getCustomerId(), booking.getScreeningId()
            ));

            log.info("SAGA completado exitosamente — bookingId: {}", booking.getId());

            return toResponse(booking);

        } catch (Exception ex) {

            log.error("SAGA fallida en paso '{}' — customerId: {}, motivo: {}",
                    currentStep, request.customerId(), ex.getMessage());

            try {
                // ── Compensaciones (orden inverso al flujo) ───────────────────────────
                // Cada compensación es best-effort

                if (seatReserved) {
                    movieClient.releaseSeat(request.screeningId());
                }

                if (bookingId != null) {
                    // Cancelación local
                    Booking failedBooking = bookingRepository.findById(bookingId).orElse(null);
                    if (failedBooking != null) {
                        failedBooking.cancel();
                        bookingRepository.save(failedBooking);
                    }
                }

            } catch (Exception compensationEx) {
                log.error("Compensación fallida — motivo: {}", compensationEx.getMessage());
            }

            // Relanza la excepción original para que el GlobalExceptionHandler devuelva 422 o 500
            throw ex;
        }
    }

    @Override
    public BookingResponse cancelBooking(UUID id) {
        log.info("Iniciando SAGA Inverso de Cancelación para la reserva: {}", id);

        // 1. Verificar que booking existe
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", id));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new InvalidBookingStateException("La reserva ya se encuentra cancelada");
        }

        // 2. Actualizar booking a CANCELLED
        booking.cancel();
        booking = bookingRepository.save(booking);

        // 3. Liberar asiento en movie-service (best-effort)
        try {
            movieClient.releaseSeat(booking.getScreeningId());
        } catch (Exception e) {
            log.error("Fallo al liberar asiento durante cancelación. El booking queda CANCELLED. Error: {}", e.getMessage());
        }

        return toResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBooking(UUID id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", id));
        return toResponse(booking);
    }

    // ── Mapper ────────────────────────────────────────────────────────────────

    private BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getScreeningId(),
                booking.getMovieTitle(),
                booking.getCustomerId(),
                booking.getStatus(),
                booking.getTotalAmount(),
                booking.getCreatedAt(),
                booking.getUpdatedAt()
        );
    }
}