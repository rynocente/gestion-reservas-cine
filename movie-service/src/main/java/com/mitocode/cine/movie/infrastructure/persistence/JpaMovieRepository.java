package com.mitocode.cine.movie.infrastructure.persistence;

import com.mitocode.cine.movie.domain.Movie;
import com.mitocode.cine.movie.domain.MovieRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaMovieRepository extends JpaRepository<Movie, UUID>, MovieRepository {
}
