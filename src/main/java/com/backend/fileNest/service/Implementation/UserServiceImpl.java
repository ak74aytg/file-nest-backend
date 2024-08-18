package com.backend.fileNest.service.Implementation;

import com.backend.fileNest.model.OTP;
import com.backend.fileNest.model.User;
import com.backend.fileNest.repository.UserRepository;
import com.backend.fileNest.request.LoginRequest;
import com.backend.fileNest.request.RegisterRequest;
import com.backend.fileNest.response.AuthResponse;
import com.backend.fileNest.service.CustomUserDetailsService;
import com.backend.fileNest.service.JwtService;
import com.backend.fileNest.service.OTPService;
import com.backend.fileNest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OTPService otpService;
    private final BCryptPasswordEncoder encoder =  new BCryptPasswordEncoder(10);
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;



    @Override
    public AuthResponse register(RegisterRequest request) throws Exception {
        User isUserPresent = userRepository.findByEmail(request.getEmail());
        if(isUserPresent!=null) throw new RuntimeException("User already exist!");
        return otpService.registerTemporaryUser(request);
    }

    @Override
    public AuthResponse verifyOTP(String email, String otp) throws Exception {
        OTP tempUser = otpService.verifyOTP(email, otp);
        if(tempUser==null) throw new RuntimeException("Invalid OTP!");
        User user = User.builder()
                .id(tempUser.getId())
                .email(tempUser.getEmail())
                .username(tempUser.getUsername())
                .role(tempUser.getRole())
                .password(encoder.encode(tempUser.getPassword()))
                .build();
        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser.getEmail());
        return AuthResponse.builder()
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .token(token)
                .build();
    }

    @Override
    public String delete(String email) {
        User isUserPresent = userRepository.findByEmail(email);
        if(isUserPresent==null){
            return otpService.delete(email);
        }
        userRepository.delete(isUserPresent);
        return "user deleted successfully!";
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            String token = jwtService.generateToken(request.getEmail());
            User savedUser = userRepository.findByEmail(request.getEmail());
            return AuthResponse.builder()
                    .email(savedUser.getEmail())
                    .username(savedUser.getUsername())
                    .token(token)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("wrong email/password!");
        }
    }
}
