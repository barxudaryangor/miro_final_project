package com.example.academic.api.controller;

import com.example.academic.api.dto.ActorContext;
import com.example.academic.api.dto.CourseRequest;
import com.example.academic.api.dto.CourseResponse;
import com.example.academic.api.dto.StudentCourseResponse;
import com.example.academic.domain.service.CourseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Tag(name = "Courses")
public class CourseController {

    private final CourseService courseService;

    @PreAuthorize("hasRole('PROFESSOR')")
    @PostMapping
    public ResponseEntity<CourseResponse> create(@Valid @RequestBody CourseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.create(request));
    }

    @GetMapping
    public ResponseEntity<Page<CourseResponse>> getAll(
            @RequestParam(required = false) UUID professorId,
            Pageable pageable) {
        return ResponseEntity.ok(courseService.getAll(professorId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(courseService.getById(id));
    }

    @PreAuthorize("hasRole('PROFESSOR')")
    @PatchMapping("/{id}")
    public ResponseEntity<CourseResponse> update(@PathVariable UUID id,
                                                 @Valid @RequestBody CourseRequest request) {
        return ResponseEntity.ok(courseService.update(id, request));
    }

    @PreAuthorize("hasRole('PROFESSOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('PROFESSOR')")
    @PostMapping("/{courseId}/students/{studentId}")
    public ResponseEntity<Void> registerStudent(@PathVariable UUID courseId,
                                                @PathVariable UUID studentId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth != null ? (String) auth.getPrincipal() : null;
        String role = auth != null ? auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(a -> a.startsWith("ROLE_") ? a.substring(5) : a)
                .findFirst().orElse(null) : null;
        ActorContext actor = new ActorContext(null, email, role);
        courseService.registerStudent(courseId, studentId, actor);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('PROFESSOR')")
    @DeleteMapping("/{courseId}/students/{studentId}")
    public ResponseEntity<Void> removeStudent(@PathVariable UUID courseId,
                                              @PathVariable UUID studentId) {
        courseService.removeStudent(courseId, studentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{courseId}/students")
    public ResponseEntity<Page<StudentCourseResponse>> getCourseStudents(@PathVariable UUID courseId,
                                                                         Pageable pageable) {
        return ResponseEntity.ok(courseService.getCourseStudents(courseId, pageable));
    }

    @GetMapping("/students/{studentId}")
    public ResponseEntity<Page<StudentCourseResponse>> getStudentCourses(@PathVariable UUID studentId,
                                                                          Pageable pageable) {
        return ResponseEntity.ok(courseService.getStudentCourses(studentId, pageable));
    }
}
