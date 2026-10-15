package com.mitocode.cine.movie.application;

import com.mitocode.cine.movie.api.dto.MovieRequest;
import com.mitocode.cine.movie.api.dto.MovieResponse;
import com.mitocode.cine.movie.domain.Movie;

import java.util.List;
import java.util.UUID;

public interface MovieService {
    MovieResponse create(MovieRequest movie);

    List<MovieResponse> findAll();

    MovieResponse findById(UUID id);

    MovieResponse update(UUID id, MovieRequest movie);

    void delete(UUID id);

    MovieResponse activate(UUID id);

    MovieResponse deactivate(UUID id);
}
