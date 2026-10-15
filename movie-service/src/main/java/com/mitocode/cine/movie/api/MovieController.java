package com.mitocode.cine.movie.api;

import com.mitocode.cine.movie.api.dto.MovieRequest;
import com.mitocode.cine.movie.api.dto.MovieResponse;
import com.mitocode.cine.movie.application.MovieService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/movies")
public class MovieController implements MovieControllerDocs {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @Override
    @PostMapping
    public ResponseEntity<MovieResponse> create(@Valid @RequestBody MovieRequest request) {
        MovieResponse response = movieService.create(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.id()).toUri();

        return ResponseEntity.created(location).body(response);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<MovieResponse>> findAll() {
        return ResponseEntity.ok(movieService.findAll());
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<MovieResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(movieService.findById(id));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<MovieResponse> update(@PathVariable UUID id, @Valid @RequestBody MovieRequest request) {
        return ResponseEntity.ok(movieService.update(id, request));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        movieService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PatchMapping("/{id}/activate")
    public ResponseEntity<MovieResponse> activate(@PathVariable UUID id) {
        return ResponseEntity.ok(movieService.activate(id));
    }

    @Override
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<MovieResponse> deactivate(@PathVariable UUID id) {
        return ResponseEntity.ok(movieService.deactivate(id));
    }
}