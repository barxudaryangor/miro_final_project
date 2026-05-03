package com.example.academic.persistence.repository;

import com.example.academic.persistence.entity.CourseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CourseRepository extends JpaRepository<CourseEntity, UUID> {
    Page<CourseEntity> findByProfessorId(UUID professorId, Pageable pageable);
}
