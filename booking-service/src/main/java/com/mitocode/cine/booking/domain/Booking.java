package com.mitocode.cine.booking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "screening_id", nullable = false)
    private UUID screeningId;

    @Column(name = "movie_title", nullable = false)
    private String movieTitle;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Booking() {}

    /**
     * Factory method: crea una reserva en estado PENDING.
     */
    public static Booking create(UUID screeningId, String movieTitle, UUID customerId, BigDecimal totalAmount) {
        Booking booking = new Booking();
        booking.screeningId = screeningId;
        booking.movieTitle = movieTitle;
        booking.customerId = customerId;
        booking.totalAmount = totalAmount;
        booking.status = BookingStatus.PENDING;
        booking.createdAt = Instant.now();
        booking.updatedAt = Instant.now();
        return booking;
    }

    public void confirm() {
        this.status = BookingStatus.CONFIRMED;
        this.updatedAt = Instant.now();
    }

    public void cancel() {
        this.status = BookingStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public UUID getId() { return id; }
    public UUID getScreeningId() { return screeningId; }
    public String getMovieTitle() { return movieTitle; }
    public UUID getCustomerId() { return customerId; }
    public BookingStatus getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}