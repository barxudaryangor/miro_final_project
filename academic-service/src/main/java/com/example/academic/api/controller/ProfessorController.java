package com.example.academic.api.controller;

import com.example.academic.api.dto.ProfessorRequest;
import com.example.academic.api.dto.ProfessorResponse;
import com.example.academic.domain.service.ProfessorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/professors")
@RequiredArgsConstructor
@Tag(name = "Professors")
public class ProfessorController {

    private final ProfessorService professorService;

    @PostMapping
    public ResponseEntity<ProfessorResponse> create(@Valid @RequestBody ProfessorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(professorService.create(request));
    }

    @GetMapping
    public ResponseEntity<Page<ProfessorResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(professorService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfessorResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(professorService.getById(id));
    }
}
