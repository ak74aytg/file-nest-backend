package com.backend.fileNest.controller;

import com.backend.fileNest.request.LoginRequest;
import com.backend.fileNest.request.RegisterRequest;
import com.backend.fileNest.response.AuthResponse;
import com.backend.fileNest.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/v1/")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            String response = userService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = userService.login(request);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("delete")
    public ResponseEntity<?> delete(@RequestParam String email) {
        String response = userService.delete(email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("verify-otp")
    public ResponseEntity<?> verifyOTP(@RequestParam String email, @RequestParam String otp) {
        try {
            AuthResponse user = userService.verifyOTP(email, otp);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }
}
