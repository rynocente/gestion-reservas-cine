package com.mitocode.cine.movie.infrastructure.persistence;

import com.mitocode.cine.movie.domain.Screening;
import com.mitocode.cine.movie.domain.ScreeningRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaScreeningRepository extends JpaRepository<Screening, UUID>, ScreeningRepository {
}
