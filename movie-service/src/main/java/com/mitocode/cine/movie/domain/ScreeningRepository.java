package com.mitocode.cine.movie.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScreeningRepository {
    Screening save(Screening screening);

    Optional<Screening> findById(UUID id);

    List<Screening> findAll();

    void deleteById(UUID id);

    boolean existsById(UUID id);
}
