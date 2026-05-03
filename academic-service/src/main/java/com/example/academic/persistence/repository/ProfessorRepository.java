package com.example.academic.persistence.repository;

import com.example.academic.persistence.entity.ProfessorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProfessorRepository extends JpaRepository<ProfessorEntity, UUID> {
    Optional<ProfessorEntity> findByEmail(String email);
}
