package com.example.academic.domain.service;

import com.example.academic.api.dto.StudentRequest;
import com.example.academic.api.dto.StudentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface StudentService {
    StudentResponse create(StudentRequest request);
    Page<StudentResponse> getAll(Pageable pageable);
    StudentResponse getById(UUID id);
    StudentResponse update(UUID id, StudentRequest request);
    void delete(UUID id);
}
