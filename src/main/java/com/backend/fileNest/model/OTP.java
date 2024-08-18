package com.backend.fileNest.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OTP {
    @Id
    private String id;
    private String username;
    private String email;
    private String password;
    private String role;
    private String otp;
    private Date expiration;
    @CreatedDate
    private Date created_at;
    @LastModifiedDate
    private Date updated_at;
}
