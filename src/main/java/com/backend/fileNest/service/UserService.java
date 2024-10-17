package com.backend.fileNest.service;

import com.backend.fileNest.request.LoginRequest;
import com.backend.fileNest.request.RegisterRequest;
import com.backend.fileNest.response.AuthResponse;

public interface UserService {
    AuthResponse register(RegisterRequest request) throws Exception;

    AuthResponse verifyOTP(String email, String otp) throws Exception;

    String delete(String email);

    AuthResponse login(LoginRequest request);

    String getUser(String name);
}
