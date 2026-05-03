package com.example.auth.domain.service;

import com.example.auth.api.dto.*;

public interface AuthService {

    AuthResponse registerStudent(RegisterStudentRequest request);

    AuthResponse registerProfessor(RegisterProfessorRequest request);

    AuthResponse login(LoginRequest request);

    UserInfoResponse getCurrentUser(String email);
}
