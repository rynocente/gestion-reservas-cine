package com.mitocode.cine.movie.application;

import com.mitocode.cine.movie.api.dto.ScreeningRequest;
import com.mitocode.cine.movie.api.dto.ScreeningResponse;

import java.util.List;
import java.util.UUID;

public interface ScreeningService {

    ScreeningResponse create(ScreeningRequest request);

    List<ScreeningResponse> findAll();

    ScreeningResponse findById(UUID id);

    void delete(UUID id);

    void reserveSeat(UUID id);

    void releaseSeat(UUID id);
}