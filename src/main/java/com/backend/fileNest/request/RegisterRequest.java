package com.backend.fileNest.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
}
