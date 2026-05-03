package com.example.academic.domain.service;

import com.example.academic.api.dto.ProfessorRequest;
import com.example.academic.api.dto.ProfessorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProfessorService {
    ProfessorResponse create(ProfessorRequest request);
    Page<ProfessorResponse> getAll(Pageable pageable);
    ProfessorResponse getById(UUID id);
}
