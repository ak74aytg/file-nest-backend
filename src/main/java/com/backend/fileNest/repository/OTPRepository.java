package com.backend.fileNest.repository;

import com.backend.fileNest.model.OTP;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OTPRepository extends MongoRepository<OTP, String> {
    OTP findByEmail(String email);
}
