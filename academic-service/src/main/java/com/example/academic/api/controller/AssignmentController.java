package com.example.academic.api.controller;

import com.example.academic.api.dto.ActorContext;
import com.example.academic.api.dto.AssignmentRequest;
import com.example.academic.api.dto.AssignmentResponse;
import com.example.academic.domain.service.AssignmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/assignments")
@RequiredArgsConstructor
@Tag(name = "Assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    @PreAuthorize("hasRole('PROFESSOR')")
    @PostMapping("/courses/{courseId}")
    public ResponseEntity<AssignmentResponse> create(@PathVariable UUID courseId,
                                                     @Valid @RequestBody AssignmentRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String actorEmail = (authentication != null) ? authentication.getName() : null;
        String actorRole = (authentication != null)
                ? authentication.getAuthorities().stream()
                        .findFirst()
                        .map(a -> a.getAuthority().replace("ROLE_", ""))
                        .orElse(null)
                : null;

        ActorContext actor = new ActorContext(null, actorEmail, actorRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(assignmentService.create(courseId, request, actor));
    }


    @GetMapping("/courses/{courseId}")
    public ResponseEntity<Page<AssignmentResponse>> getByCourseId(@PathVariable UUID courseId,
                                                                   Pageable pageable) {
        return ResponseEntity.ok(assignmentService.getByCourseId(courseId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssignmentResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(assignmentService.getById(id));
    }

    @PreAuthorize("hasRole('PROFESSOR')")
    @PatchMapping("/{id}")
    public ResponseEntity<AssignmentResponse> update(@PathVariable UUID id,
                                                     @Valid @RequestBody AssignmentRequest request) {
        return ResponseEntity.ok(assignmentService.update(id, request));
    }

    @PreAuthorize("hasRole('PROFESSOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        assignmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
