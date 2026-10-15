package com.mitocode.cine.movie.application.impl;

import com.mitocode.cine.movie.api.dto.ScreeningRequest;
import com.mitocode.cine.movie.api.dto.ScreeningResponse;
import com.mitocode.cine.movie.application.ScreeningService;
import com.mitocode.cine.movie.domain.MovieRepository;
import com.mitocode.cine.movie.domain.Screening;
import com.mitocode.cine.movie.domain.ScreeningRepository;
import com.mitocode.cine.movie.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Implementación del caso de uso de funciones (Screenings).
 *
 * Gestiona el ciclo de vida de las proyecciones y el inventario
 * de asientos que será orquestado por el SAGA.
 */
@Service
@Transactional(readOnly = true)
public class ScreeningServiceImpl implements ScreeningService {

    private final ScreeningRepository screeningRepository;
    private final MovieRepository movieRepository;

    public ScreeningServiceImpl(ScreeningRepository screeningRepository, MovieRepository movieRepository) {
        this.screeningRepository = screeningRepository;
        this.movieRepository = movieRepository;
    }

    @Override
    @Transactional
    public ScreeningResponse create(ScreeningRequest request) {
        // 1. Validar que la película exista
        if (!movieRepository.existsById(request.movieId())) {
            throw new ResourceNotFoundException("Movie", request.movieId());
        }

        // 2. Crear la entidad a través del Factory Method
        var screening = Screening.create(
                request.movieId(),
                request.room(),
                request.showAt(),
                request.totalSeats()
        );

        // 3. Persistir y retornar DTO
        var saved = screeningRepository.save(screening);
        return toResponse(saved);
    }

    @Override
    public List<ScreeningResponse> findAll() {
        return screeningRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ScreeningResponse findById(UUID id) {
        return screeningRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Screening", id));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!screeningRepository.existsById(id)) {
            throw new ResourceNotFoundException("Screening", id);
        }
        screeningRepository.deleteById(id);
    }

    // ── Métodos de negocio para el SAGA ───────────────────────────────────────

    @Override
    @Transactional
    public void reserveSeat(UUID id) {
        var screening = screeningRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Screening", id));

        screening.reserve();

        screeningRepository.save(screening);
    }

    @Override
    @Transactional
    public void releaseSeat(UUID id) {
        var screening = screeningRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Screening", id));

        screening.release();

        screeningRepository.save(screening);
    }

    // ── Mapper ────────────────────────────────────────────────────────────────

    private ScreeningResponse toResponse(Screening screening) {
        return new ScreeningResponse(
                screening.getId(),
                screening.getMovieId(),
                screening.getRoom(),
                screening.getShowAt(),
                screening.getTotalSeats(),
                screening.getAvailableSeats(),
                screening.isActive()
        );
    }
}