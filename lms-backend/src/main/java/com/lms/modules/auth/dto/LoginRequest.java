package com.lms.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    @NotBlank(message = "Email cannot be blank!")
    private String email;
    @NotBlank(message = "Password cannot be blank!")
    private String password;
}
