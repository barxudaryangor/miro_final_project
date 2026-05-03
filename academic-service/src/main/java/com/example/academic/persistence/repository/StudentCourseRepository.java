package com.example.academic.persistence.repository;

import com.example.academic.persistence.entity.StudentCourseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StudentCourseRepository extends JpaRepository<StudentCourseEntity, UUID> {
    boolean existsByStudentIdAndCourseId(UUID studentId, UUID courseId);
    Optional<StudentCourseEntity> findByStudentIdAndCourseId(UUID studentId, UUID courseId);
    Page<StudentCourseEntity> findByCourseId(UUID courseId, Pageable pageable);
    Page<StudentCourseEntity> findByStudentId(UUID studentId, Pageable pageable);
}
