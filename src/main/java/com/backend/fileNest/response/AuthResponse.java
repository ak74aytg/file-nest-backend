package com.backend.fileNest.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class AuthResponse {
    private String username;
    private String email;
    private String token;
}
