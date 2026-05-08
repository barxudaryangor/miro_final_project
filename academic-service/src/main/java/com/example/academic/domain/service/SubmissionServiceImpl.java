package com.example.academic.domain.service;

import com.example.academic.api.dto.ActorContext;
import com.example.academic.api.dto.GradeSubmissionRequest;
import com.example.academic.api.dto.SubmissionRequest;
import com.example.academic.api.dto.SubmissionResponse;
import com.example.academic.exception.ConflictException;
import com.example.academic.exception.NotFoundException;
import com.example.academic.kafka.AcademicEvent;
import com.example.academic.kafka.AcademicEventPublisher;
import com.example.academic.kafka.AcademicEventType;
import com.example.academic.mapper.SubmissionMapper;
import com.example.academic.metrics.AcademicMetrics;
import com.example.academic.persistence.entity.AssignmentEntity;
import com.example.academic.persistence.entity.ProfessorEntity;
import com.example.academic.persistence.entity.StudentEntity;
import com.example.academic.persistence.entity.SubmissionEntity;
import com.example.academic.persistence.entity.SubmissionStatus;
import com.example.academic.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final StudentCourseRepository studentCourseRepository;
    private final AcademicMetrics academicMetrics;
    private final AcademicEventPublisher eventPublisher;

    @Override
    public SubmissionResponse create(UUID assignmentId, SubmissionRequest request, ActorContext actor) {

        if (!"STUDENT".equals(actor.actorRole())) {
            throw new AccessDeniedException("Only students can submit assignments");
        }

        AssignmentEntity assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment not found: " + assignmentId));

        StudentEntity student = studentRepository.findByEmail(actor.actorEmail())
                .orElseThrow(() -> new NotFoundException("Student not found for email: " + actor.actorEmail()));

        if (!studentCourseRepository.existsByStudentIdAndCourseId(student.getId(), assignment.getCourseId())) {
            throw new ConflictException(
                    "Student " + student.getId() + " is not enrolled in course " + assignment.getCourseId()
            );
        }

        if (assignment.getDueDate() != null && assignment.getDueDate().isBefore(LocalDateTime.now())) {
            throw new ConflictException("Assignment deadline has passed");
        }

        if (submissionRepository.existsByAssignmentIdAndStudentId(assignmentId, student.getId())) {
            throw new ConflictException(
                    "Student " + student.getId() + " has already submitted assignment " + assignmentId
            );
        }

        ActorContext enrichedActor = new ActorContext(student.getId(), actor.actorEmail(), actor.actorRole());

        ProfessorEntity assignmentProfessor = professorRepository.findById(assignment.getProfessorId())
                .orElseThrow(() -> new NotFoundException("Professor not found: " + assignment.getProfessorId()));
        SubmissionEntity entity = SubmissionMapper.toEntity(request);
        entity.setAssignmentId(assignmentId);
        entity.setStudentId(student.getId());
        SubmissionEntity savedSubmission = submissionRepository.save(entity);

        eventPublisher.publish(AcademicEvent.create(
                AcademicEventType.SUBMISSION_CREATED,
                enrichedActor.actorId(),
                actor.actorRole(),
                actor.actorEmail(),
                "SUBMISSION",
                savedSubmission.getId(),
                assignment.getCourseId(),
                Map.of(
                        "submissionId", savedSubmission.getId(),
                        "assignmentId", savedSubmission.getAssignmentId(),
                        "studentId", savedSubmission.getStudentId(),
                        "teacherEmail", assignmentProfessor.getEmail()
                )
        ));
        academicMetrics.incrementSubmissionsCreated();
        return SubmissionMapper.toResponse(savedSubmission);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubmissionResponse> getByAssignmentId(UUID assignmentId, Pageable pageable) {
        return submissionRepository.findByAssignmentId(assignmentId, pageable).map(SubmissionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubmissionResponse> getByStudentId(UUID studentId, Pageable pageable) {
        return submissionRepository.findByStudentId(studentId, pageable).map(SubmissionMapper::toResponse);
    }

    @Override
    public SubmissionResponse grade(UUID id, GradeSubmissionRequest request, ActorContext actor) {
        SubmissionEntity entity = findOrThrow(id);
        entity.setGrade(request.grade());
        entity.setStatus(SubmissionStatus.GRADED);

        SubmissionEntity savedSubmission = submissionRepository.save(entity);

        AssignmentEntity assignment = assignmentRepository.findById(savedSubmission.getAssignmentId())
                .orElseThrow(() -> new NotFoundException("Assignment not found: " + savedSubmission.getAssignmentId()));

        ProfessorEntity professor = professorRepository.findByEmail(actor.actorEmail())
                .orElseThrow(() -> new NotFoundException("Professor not found for email: " + actor.actorEmail()));

        StudentEntity submissionStudent = studentRepository.findById(savedSubmission.getStudentId())
                .orElseThrow(() -> new NotFoundException("Student not found: " + savedSubmission.getStudentId()));

        ActorContext enrichedActor = new ActorContext(professor.getId(), actor.actorEmail(), actor.actorRole());

        eventPublisher.publish(AcademicEvent.create(
                AcademicEventType.SUBMISSION_GRADED,
                enrichedActor.actorId(),
                actor.actorRole(),
                actor.actorEmail(),
                "SUBMISSION",
                savedSubmission.getId(),
                assignment.getCourseId(),
                Map.of(
                        "submissionId", savedSubmission.getId(),
                        "grade", savedSubmission.getGrade(),
                        "studentId", savedSubmission.getStudentId(),
                        "studentEmail", submissionStudent.getEmail()
                )
        ));
        return SubmissionMapper.toResponse(savedSubmission);
    }

    private SubmissionEntity findOrThrow(UUID id) {
        return submissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Submission not found: " + id));
    }
}
