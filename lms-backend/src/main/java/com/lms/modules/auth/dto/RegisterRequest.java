package com.lms.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    @NotBlank(message = "Email cannot be blank!")
    @Email(message = "Email format is invalid!")
    private String email;
    @NotBlank(message = "Pass word cannot be blank!")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;
    @NotBlank(message = "Full name cannot be blank!")
    private String fullName;
    private String phoneNumber;
}
