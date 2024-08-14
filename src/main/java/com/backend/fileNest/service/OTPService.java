package com.backend.fileNest.service;

import com.backend.fileNest.model.OTP;
import com.backend.fileNest.repository.OTPRepository;
import com.backend.fileNest.request.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.UUID;

@Service
public class OTPService {

    @Autowired
    private OTPRepository otpRepository;
    @Autowired
    private EmailService emailService;

    private static final long OTP_VALIDITY_DURATION = 5 * 60 * 1000; // 5 minutes


    public String registerTemporaryUser(RegisterRequest request){
        OTP isTempUserPresent = otpRepository.findByEmail(request.getEmail());
        if (isTempUserPresent != null){
            isTempUserPresent.setPassword(request.getPassword());
            generateAndSendOTP(isTempUserPresent);
            return "OTP generated successfully";
        }
        OTP tempUser = OTP.builder()
                .id(UUID.randomUUID().toString())
                .role("ROLE_USER")
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        generateAndSendOTP(tempUser);
        return "OTP generated successfully";
    }


    public void generateAndSendOTP(OTP tempUser) {
        String otp = generateOTP();
        Date expiration = new Date(System.currentTimeMillis() + OTP_VALIDITY_DURATION);
        tempUser.setOtp(otp);
        tempUser.setExpiration(expiration);

        String subject = "OTP Verification";
        String body = "Hi,\nPlease use the following One Time Password (OTP) to register: " + otp + ". Do not share this OTP with anyone.\nThank you!";
        emailService.sendEmail(tempUser.getEmail(), subject, body);

        otpRepository.save(tempUser);
    }

    public OTP verifyOTP(String email, String otp) {
        OTP otpEntity = otpRepository.findByEmail(email);
        if (otpEntity != null && otpEntity.getExpiration().after(new Date()) && otpEntity.getOtp().equals(otp)) {
            otpRepository.delete(otpEntity);
            return otpEntity;
        }
        return null;
    }

    private String generateOTP() {
        String digits = "0123456789";
        SecureRandom random = new SecureRandom();

        StringBuilder otp = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(digits.length());
            otp.append(digits.charAt(index));
        }
        return otp.toString();
    }

    public String delete(String email) {
        OTP tempUser = otpRepository.findByEmail(email);
        if(tempUser==null) throw new RuntimeException("User does not exist!");
        otpRepository.delete(tempUser);
        return "user deleted successfully";
    }
}
