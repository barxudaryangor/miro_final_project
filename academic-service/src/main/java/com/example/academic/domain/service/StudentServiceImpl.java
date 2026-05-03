package com.example.academic.domain.service;

import com.example.academic.api.dto.StudentRequest;
import com.example.academic.api.dto.StudentResponse;
import com.example.academic.exception.NotFoundException;
import com.example.academic.mapper.StudentMapper;
import com.example.academic.persistence.entity.StudentEntity;
import com.example.academic.persistence.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    @Override
    public StudentResponse create(StudentRequest request) {
        StudentEntity entity = StudentMapper.toEntity(request);
        return StudentMapper.toResponse(studentRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentResponse> getAll(Pageable pageable) {
        return studentRepository.findAll(pageable).map(StudentMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponse getById(UUID id) {
        return StudentMapper.toResponse(findOrThrow(id));
    }

    @Override
    public StudentResponse update(UUID id, StudentRequest request) {
        StudentEntity entity = findOrThrow(id);
        StudentMapper.updateEntity(entity, request);
        return StudentMapper.toResponse(studentRepository.save(entity));
    }

    @Override
    public void delete(UUID id) {
        findOrThrow(id);
        studentRepository.deleteById(id);
    }

    private StudentEntity findOrThrow(UUID id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Student not found: " + id));
    }
}
