package com.mitocode.cine.movie.api;

import com.mitocode.cine.movie.api.dto.ScreeningRequest;
import com.mitocode.cine.movie.api.dto.ScreeningResponse;
import com.mitocode.cine.movie.application.ScreeningService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/screenings")
public class ScreeningController implements ScreeningControllerDocs {

    private final ScreeningService screeningService;

    public ScreeningController(ScreeningService screeningService) {
        this.screeningService = screeningService;
    }

    @Override
    @PostMapping
    public ResponseEntity<ScreeningResponse> create(@Valid @RequestBody ScreeningRequest request) {
        ScreeningResponse response = screeningService.create(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.id()).toUri();

        return ResponseEntity.created(location).body(response);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<ScreeningResponse>> findAll() {
        return ResponseEntity.ok(screeningService.findAll());
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ScreeningResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(screeningService.findById(id));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        screeningService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ── Endpoints del SAGA ───────────────────────────────────────────────────

    @Override
    @PatchMapping("/{id}/reserve")
    public ResponseEntity<Void> reserveSeat(@PathVariable UUID id) {

        // SIMULACIÓN DE FALLO PARA PROBAR EL RETRY
        /*if (Math.random() < 1.0) {
            throw new RuntimeException("Simulando caída de base de datos o red al reservar");
        }*/

        screeningService.reserveSeat(id);
        return ResponseEntity.ok().build();
    }

    @Override
    @PatchMapping("/{id}/release")
    public ResponseEntity<Void> releaseSeat(@PathVariable UUID id) {
        screeningService.releaseSeat(id);
        return ResponseEntity.ok().build();
    }
}