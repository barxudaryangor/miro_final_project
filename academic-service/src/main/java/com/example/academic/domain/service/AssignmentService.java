package com.example.academic.domain.service;

import com.example.academic.api.dto.ActorContext;
import com.example.academic.api.dto.AssignmentRequest;
import com.example.academic.api.dto.AssignmentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AssignmentService {
    AssignmentResponse create(UUID courseId, AssignmentRequest request, ActorContext actor);
    Page<AssignmentResponse> getByCourseId(UUID courseId, Pageable pageable);
    AssignmentResponse getById(UUID id);
    AssignmentResponse update(UUID id, AssignmentRequest request);
    void delete(UUID id);
}
