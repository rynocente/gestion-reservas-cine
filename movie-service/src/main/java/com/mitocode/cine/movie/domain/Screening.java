package com.mitocode.cine.movie.domain;

import com.mitocode.cine.movie.exception.NoSeatsAvailableException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Agregado del dominio: Función de cine (Screening).
 * Mantiene el invariante de que los asientos disponibles nunca sean negativos.
 */
@Entity
@Table(name = "screenings")
@EntityListeners(AuditingEntityListener.class)
public class Screening {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID movieId;

    @Column(nullable = false, length = 100)
    private String room;

    @Column(nullable = false)
    private LocalDateTime showAt;

    @Column(nullable = false, updatable = false)
    private Integer totalSeats;

    @Column(nullable = false)
    private Integer availableSeats;

    @Column(nullable = false)
    private boolean active = true;


    /** Constructor requerido por JPA para instanciar via reflexión. */
    protected Screening() {}

    /**
     * Factory method: crea una función con estado inicial válido.
     * Los asientos disponibles siempre inician igual a la capacidad total.
     */
    public static Screening create(UUID movieId, String room, LocalDateTime showAt, Integer totalSeats) {
        var screening = new Screening();
        screening.movieId = movieId;
        screening.room = room;
        screening.showAt = showAt;
        screening.totalSeats = totalSeats;
        screening.availableSeats = totalSeats;
        return screening;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public UUID getId() { return id; }
    public UUID getMovieId() { return movieId; }
    public String getRoom() { return room; }
    public LocalDateTime getShowAt() { return showAt; }
    public Integer getTotalSeats() { return totalSeats; }
    public Integer getAvailableSeats() { return availableSeats; }
    public boolean isActive() { return active; }


    // ── Setters para actualización de campos simples ───────────────────────────

    public void setRoom(String room) { this.room = room; }
    public void setShowAt(LocalDateTime showAt) { this.showAt = showAt; }

    // ── Métodos de negocio (Invariantes) ──────────────────────────────────────

    /**
     * Reserva un asiento para esta función (Paso del SAGA).
     * @throws NoSeatsAvailableException si no quedan asientos.
     */
    public void reserve() {
        if (this.availableSeats <= 0) {
            throw new NoSeatsAvailableException("Función sin asientos disponibles");
        }
        this.availableSeats--;
    }

    /**
     * Libera un asiento previamente reservado (Compensación del SAGA).
     * Best-effort: Evita sobrepasar la capacidad total si hay inconsistencias.
     */
    public void release() {
        if (this.availableSeats < this.totalSeats) {
            this.availableSeats++;
        }
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}