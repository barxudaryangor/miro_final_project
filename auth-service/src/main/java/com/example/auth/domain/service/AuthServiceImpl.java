package com.example.auth.domain.service;

import com.example.auth.api.dto.*;
import com.example.auth.domain.model.Role;
import com.example.auth.exception.DuplicateEmailException;
import com.example.auth.exception.InvalidCredentialsException;
import com.example.auth.exception.UserNotFoundException;
import com.example.auth.persistence.entity.UserEntity;
import com.example.auth.persistence.repository.UserRepository;
import com.example.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional
    public AuthResponse registerStudent(RegisterStudentRequest request) {
        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException();
        }

        UserEntity user = UserEntity.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(Role.STUDENT)
                .isActive(true)
                .build();

        userRepository.save(user);

        log.info("User registered: email={}, role={}", email, Role.STUDENT);

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getEmail(), user.getRole().name());
    }

    @Override
    @Transactional
    public AuthResponse registerProfessor(RegisterProfessorRequest request) {
        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException();
        }

        UserEntity user = UserEntity.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(Role.PROFESSOR)
                .isActive(true)
                .build();

        userRepository.save(user);

        log.info("User registered: email={}, role={}", email, Role.PROFESSOR);

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getEmail(), user.getRole().name());
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Failed login attempt: email={}", email);
                    return new InvalidCredentialsException();
                });

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            log.warn("Failed login attempt: email={}", email);
            throw new InvalidCredentialsException();
        }

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            log.warn("Failed login attempt: email={}", email);
            throw new InvalidCredentialsException();
        }

        log.info("User logged in: email={}", email);

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getEmail(), user.getRole().name());
    }

    @Override
    @Transactional(readOnly = true)
    public UserInfoResponse getCurrentUser(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new InvalidCredentialsException();
        }

        return new UserInfoResponse(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                user.getIsActive(),
                user.getCreatedAt()
        );
    }
}
