package com.example.academic.api.controller;

import com.example.academic.api.dto.ActorContext;
import com.example.academic.api.dto.GradeSubmissionRequest;
import com.example.academic.api.dto.SubmissionRequest;
import com.example.academic.api.dto.SubmissionResponse;
import com.example.academic.domain.service.SubmissionService;
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
@RequestMapping("/api/v1/submissions")
@RequiredArgsConstructor
@Tag(name = "Submissions")
public class SubmissionController {

    private final SubmissionService submissionService;

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/assignments/{assignmentId}")
    public ResponseEntity<SubmissionResponse> create(@PathVariable UUID assignmentId,
                                                     @Valid @RequestBody SubmissionRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String actorEmail = (authentication != null) ? authentication.getName() : null;
        String actorRole = (authentication != null)
                ? authentication.getAuthorities().stream()
                        .findFirst()
                        .map(a -> a.getAuthority().replace("ROLE_", ""))
                        .orElse(null)
                : null;

        ActorContext actor = new ActorContext(null, actorEmail, actorRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(submissionService.create(assignmentId, request, actor));
    }

    @GetMapping("/assignments/{assignmentId}")
    public ResponseEntity<Page<SubmissionResponse>> getByAssignmentId(@PathVariable UUID assignmentId,
                                                                       Pageable pageable) {
        return ResponseEntity.ok(submissionService.getByAssignmentId(assignmentId, pageable));
    }

    @GetMapping("/students/{studentId}")
    public ResponseEntity<Page<SubmissionResponse>> getByStudentId(@PathVariable UUID studentId,
                                                                    Pageable pageable) {
        return ResponseEntity.ok(submissionService.getByStudentId(studentId, pageable));
    }

    @PreAuthorize("hasRole('PROFESSOR')")
    @PatchMapping("/{id}/grade")
    public ResponseEntity<SubmissionResponse> grade(@PathVariable UUID id,
                                                    @Valid @RequestBody GradeSubmissionRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String actorEmail = (authentication != null) ? authentication.getName() : null;
        String actorRole = (authentication != null)
                ? authentication.getAuthorities().stream()
                        .findFirst()
                        .map(a -> a.getAuthority().replace("ROLE_", ""))
                        .orElse(null)
                : null;

        ActorContext actor = new ActorContext(null, actorEmail, actorRole);
        return ResponseEntity.ok(submissionService.grade(id, request, actor));
    }
}
