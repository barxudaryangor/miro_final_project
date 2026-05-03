package com.example.academic.domain.service;

import com.example.academic.api.dto.ActorContext;
import com.example.academic.api.dto.GradeSubmissionRequest;
import com.example.academic.api.dto.SubmissionRequest;
import com.example.academic.api.dto.SubmissionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SubmissionService {
    SubmissionResponse create(UUID assignmentId, SubmissionRequest request, ActorContext actor);
    Page<SubmissionResponse> getByAssignmentId(UUID assignmentId, Pageable pageable);
    Page<SubmissionResponse> getByStudentId(UUID studentId, Pageable pageable);
    SubmissionResponse grade(UUID id, GradeSubmissionRequest request, ActorContext actor);
}
