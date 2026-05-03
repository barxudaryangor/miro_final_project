package com.example.academic.domain.service;

import com.example.academic.api.dto.ProfessorRequest;
import com.example.academic.api.dto.ProfessorResponse;
import com.example.academic.exception.NotFoundException;
import com.example.academic.mapper.ProfessorMapper;
import com.example.academic.persistence.entity.ProfessorEntity;
import com.example.academic.persistence.repository.ProfessorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfessorServiceImpl implements ProfessorService {

    private final ProfessorRepository professorRepository;

    @Override
    public ProfessorResponse create(ProfessorRequest request) {
        ProfessorEntity entity = ProfessorMapper.toEntity(request);
        return ProfessorMapper.toResponse(professorRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProfessorResponse> getAll(Pageable pageable) {
        return professorRepository.findAll(pageable).map(ProfessorMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfessorResponse getById(UUID id) {
        return ProfessorMapper.toResponse(findOrThrow(id));
    }

    private ProfessorEntity findOrThrow(UUID id) {
        return professorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Professor not found: " + id));
    }
}
