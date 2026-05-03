package com.example.academic.domain.service;

import com.example.academic.api.dto.ActorContext;
import com.example.academic.api.dto.CourseRequest;
import com.example.academic.api.dto.CourseResponse;
import com.example.academic.api.dto.StudentCourseRequest;
import com.example.academic.api.dto.StudentCourseResponse;
import com.example.academic.exception.ConflictException;
import com.example.academic.exception.NotFoundException;
import com.example.academic.kafka.AcademicEvent;
import com.example.academic.kafka.AcademicEventPublisher;
import com.example.academic.kafka.AcademicEventType;
import com.example.academic.mapper.CourseMapper;
import com.example.academic.mapper.StudentCourseMapper;
import com.example.academic.metrics.AcademicMetrics;
import com.example.academic.persistence.entity.CourseEntity;
import com.example.academic.persistence.entity.ProfessorEntity;
import com.example.academic.persistence.entity.StudentCourseEntity;
import com.example.academic.persistence.entity.StudentEntity;
import com.example.academic.persistence.repository.CourseRepository;
import com.example.academic.persistence.repository.ProfessorRepository;
import com.example.academic.persistence.repository.StudentCourseRepository;
import com.example.academic.persistence.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final StudentCourseRepository studentCourseRepository;
    private final ProfessorRepository professorRepository;
    private final AcademicMetrics academicMetrics;
    private final AcademicEventPublisher eventPublisher;

    @Override
    public CourseResponse create(CourseRequest request) {
        CourseEntity entity = CourseMapper.toEntity(request);
        CourseResponse response = CourseMapper.toResponse(courseRepository.save(entity));
        academicMetrics.incrementCoursesCreated();
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponse> getAll(UUID professorId, Pageable pageable) {
        if (professorId != null) {
            return courseRepository.findByProfessorId(professorId, pageable).map(CourseMapper::toResponse);
        }
        return courseRepository.findAll(pageable).map(CourseMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse getById(UUID id) {
        return CourseMapper.toResponse(findOrThrow(id));
    }

    @Override
    public CourseResponse update(UUID id, CourseRequest request) {
        CourseEntity entity = findOrThrow(id);
        CourseMapper.updateEntity(entity, request);
        return CourseMapper.toResponse(courseRepository.save(entity));
    }

    @Override
    public void delete(UUID id) {
        findOrThrow(id);
        courseRepository.deleteById(id);
    }

    @Override
    public void registerStudent(UUID courseId, UUID studentId, ActorContext actor) {
        if (!courseRepository.existsById(courseId)) {
            throw new NotFoundException("Course not found: " + courseId);
        }
        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found: " + studentId));
        if (studentCourseRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new ConflictException("Student " + studentId + " is already enrolled in course " + courseId);
        }
        StudentCourseEntity saved = studentCourseRepository.save(
                StudentCourseMapper.toEntity(new StudentCourseRequest(studentId, courseId)));
        academicMetrics.incrementStudentsRegistered();
        ProfessorEntity professor = professorRepository.findByEmail(actor.actorEmail())
                .orElseThrow(() -> new NotFoundException("Professor not found for email: " + actor.actorEmail()));
        ActorContext enrichedActor = new ActorContext(professor.getId(), actor.actorEmail(), actor.actorRole());
        eventPublisher.publish(AcademicEvent.create(
                AcademicEventType.STUDENT_REGISTERED_TO_COURSE,
                enrichedActor.actorId(),
                actor.actorRole(),
                actor.actorEmail(),
                "STUDENT_COURSE",
                saved.getId(),
                courseId,
                Map.of("studentId", studentId, "courseId", courseId, "studentEmail", student.getEmail())
        ));
    }

    @Override
    public void removeStudent(UUID courseId, UUID studentId) {
        studentCourseRepository.delete(
                studentCourseRepository.findByStudentIdAndCourseId(studentId, courseId)
                        .orElseThrow(() -> new RuntimeException(
                                "Enrollment not found for student " + studentId + " in course " + courseId)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentCourseResponse> getCourseStudents(UUID courseId, Pageable pageable) {
        findOrThrow(courseId);
        return studentCourseRepository.findByCourseId(courseId, pageable).map(StudentCourseMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentCourseResponse> getStudentCourses(UUID studentId, Pageable pageable) {
        return studentCourseRepository.findByStudentId(studentId, pageable).map(StudentCourseMapper::toResponse);
    }

    private CourseEntity findOrThrow(UUID id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found: " + id));
    }
}
