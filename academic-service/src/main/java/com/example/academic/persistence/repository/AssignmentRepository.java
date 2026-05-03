package com.example.academic.persistence.repository;

import com.example.academic.persistence.entity.AssignmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AssignmentRepository extends JpaRepository<AssignmentEntity, UUID> {
    Page<AssignmentEntity> findByCourseId(UUID courseId, Pageable pageable);
}
