package com.mitocode.cine.movie.application.impl;

import com.mitocode.cine.movie.api.dto.MovieRequest;
import com.mitocode.cine.movie.api.dto.MovieResponse;
import com.mitocode.cine.movie.application.MovieService;
import com.mitocode.cine.movie.domain.Movie;
import com.mitocode.cine.movie.domain.MovieRepository;
import com.mitocode.cine.movie.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class MovieServiceImpl implements MovieService {

    private final MovieRepository repository;

    public MovieServiceImpl(MovieRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public MovieResponse create(MovieRequest request) {
        var movie = Movie.create(
                request.title(),
                request.director(),
                request.genre(),
                request.durationMin(),
                request.rating()
        );
        var saved = repository.save(movie);

        return toResponse(saved);
    }

    @Override
    public List<MovieResponse> findAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public MovieResponse findById(UUID id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", id));
    }

    @Override
    @Transactional
    public MovieResponse update(UUID id, MovieRequest request) {
        var movie = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", id));

        movie.setTitle(request.title());
        movie.setDirector(request.director());
        movie.setGenre(request.genre());
        movie.setDurationMin(request.durationMin());
        movie.setRating(request.rating());

        return toResponse(repository.save(movie));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Movie", id);
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public MovieResponse activate(UUID id) {
        var movie = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", id));
        movie.activate();
        return toResponse(repository.save(movie));
    }

    @Override
    @Transactional
    public MovieResponse deactivate(UUID id) {
        var movie = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", id));
        movie.deactivate();
        return toResponse(repository.save(movie));
    }

    // ── Mapper ────────────────────────────────────────────────────────────────

    private MovieResponse toResponse(Movie movie) {
        return new MovieResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getGenre(),
                movie.getDurationMin(),
                movie.getRating(),
                movie.isActive(),
                movie.getCreatedAt(),
                movie.getUpdatedAt()
        );
    }
}