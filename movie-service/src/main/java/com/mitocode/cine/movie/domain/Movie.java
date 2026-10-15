package com.mitocode.cine.movie.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "movies")
@EntityListeners(AuditingEntityListener.class)
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(length = 255)
    private String director;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private Genre genre;

    @Column
    private Integer durationMin;

    @Column(length = 20)
    private String rating;

    @Column(nullable = false)
    private boolean active = true;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;

    /** Constructor requerido por JPA para instanciar via reflexión. */
    public Movie() {} //

    /**
     * Factory method: crea una película con estado inicial válido.
     */
    public static Movie create(String title, String director, Genre genre, Integer durationMin, String rating) {
        var movie = new Movie();
        movie.title = title;
        movie.director = director;
        movie.genre = genre;
        movie.durationMin = durationMin;
        movie.rating = rating;
        return movie;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getDirector() { return director; }
    public Genre getGenre() { return genre; }
    public Integer getDurationMin() { return durationMin; }
    public String getRating() { return rating; }
    public boolean isActive() { return active; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    // ── Setters para actualización de campos simples ───────────────────────────

    public void setTitle(String title) { this.title = title; }
    public void setDirector(String director) { this.director = director; }
    public void setGenre(Genre genre) { this.genre = genre; }
    public void setDurationMin(Integer durationMin) { this.durationMin = durationMin; }
    public void setRating(String rating) { this.rating = rating; }

    // ── Métodos de negocio ────────────────────────────────────────────────────

    /** Activa la película para que aparezca en el catálogo. */
    public void activate() {
        this.active = true;
    }

    /** Desactiva la película sin eliminarla del sistema. */
    public void deactivate() {
        this.active = false;
    }
}