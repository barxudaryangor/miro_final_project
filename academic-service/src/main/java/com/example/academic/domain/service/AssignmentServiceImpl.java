package com.example.academic.domain.service;

import com.example.academic.api.dto.ActorContext;
import com.example.academic.api.dto.AssignmentRequest;
import com.example.academic.api.dto.AssignmentResponse;
import com.example.academic.exception.NotFoundException;
import com.example.academic.kafka.AcademicEvent;
import com.example.academic.kafka.AcademicEventPublisher;
import com.example.academic.kafka.AcademicEventType;
import com.example.academic.mapper.AssignmentMapper;
import com.example.academic.metrics.AcademicMetrics;
import com.example.academic.persistence.entity.AssignmentEntity;
import com.example.academic.persistence.entity.ProfessorEntity;
import com.example.academic.persistence.repository.AssignmentRepository;
import com.example.academic.persistence.repository.CourseRepository;
import com.example.academic.persistence.repository.ProfessorRepository;
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
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final ProfessorRepository professorRepository;
    private final AcademicMetrics academicMetrics;
    private final AcademicEventPublisher eventPublisher;

    @Override
    public AssignmentResponse create(UUID courseId, AssignmentRequest request, ActorContext actor) {
        if (!courseRepository.existsById(courseId)) {
            throw new NotFoundException("Course not found: " + courseId);
        }

        ProfessorEntity professor = professorRepository.findByEmail(actor.actorEmail())
                .orElseThrow(() -> new NotFoundException("Professor not found for email: " + actor.actorEmail()));

        ActorContext enrichedActor = new ActorContext(professor.getId(), actor.actorEmail(), actor.actorRole());

        AssignmentEntity entity = AssignmentMapper.toEntity(request);
        entity.setCourseId(courseId);
        entity.setProfessorId(professor.getId());
        AssignmentEntity savedAssignment = assignmentRepository.save(entity);

        eventPublisher.publish(AcademicEvent.create(
                AcademicEventType.ASSIGNMENT_CREATED,
                enrichedActor.actorId(),
                actor.actorRole(),
                actor.actorEmail(),
                "ASSIGNMENT",
                savedAssignment.getId(),
                savedAssignment.getCourseId(),
                Map.of(
                        "assignmentId", savedAssignment.getId(),
                        "courseId", savedAssignment.getCourseId(),
                        "professorId", savedAssignment.getProfessorId(),
                        "title", savedAssignment.getTitle()
                )
        ));
        academicMetrics.incrementAssignmentsCreated();
        return AssignmentMapper.toResponse(savedAssignment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssignmentResponse> getByCourseId(UUID courseId, Pageable pageable) {
        return assignmentRepository.findByCourseId(courseId, pageable).map(AssignmentMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public AssignmentResponse getById(UUID id) {
        return AssignmentMapper.toResponse(findOrThrow(id));
    }

    @Override
    public AssignmentResponse update(UUID id, AssignmentRequest request) {
        AssignmentEntity entity = findOrThrow(id);
        AssignmentMapper.updateEntity(entity, request);
        return AssignmentMapper.toResponse(assignmentRepository.save(entity));
    }

    @Override
    public void delete(UUID id) {
        findOrThrow(id);
        assignmentRepository.deleteById(id);
    }

    private AssignmentEntity findOrThrow(UUID id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Assignment not found: " + id));
    }
}
