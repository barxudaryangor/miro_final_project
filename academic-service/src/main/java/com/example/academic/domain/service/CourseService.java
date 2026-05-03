package com.example.academic.domain.service;

import com.example.academic.api.dto.ActorContext;
import com.example.academic.api.dto.CourseRequest;
import com.example.academic.api.dto.CourseResponse;
import com.example.academic.api.dto.StudentCourseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CourseService {
    CourseResponse create(CourseRequest request);
    Page<CourseResponse> getAll(UUID professorId, Pageable pageable);
    CourseResponse getById(UUID id);
    CourseResponse update(UUID id, CourseRequest request);
    void delete(UUID id);
    void registerStudent(UUID courseId, UUID studentId, ActorContext actor);
    void removeStudent(UUID courseId, UUID studentId);
    Page<StudentCourseResponse> getCourseStudents(UUID courseId, Pageable pageable);
    Page<StudentCourseResponse> getStudentCourses(UUID studentId, Pageable pageable);
}
