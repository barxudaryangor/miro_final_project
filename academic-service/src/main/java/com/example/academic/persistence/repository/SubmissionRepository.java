package com.example.academic.persistence.repository;

import com.example.academic.persistence.entity.SubmissionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubmissionRepository extends JpaRepository<SubmissionEntity, UUID> {
    Page<SubmissionEntity> findByAssignmentId(UUID assignmentId, Pageable pageable);
    Page<SubmissionEntity> findByStudentId(UUID studentId, Pageable pageable);

    boolean existsByAssignmentIdAndStudentId(UUID assignmentId, UUID studentId);
}