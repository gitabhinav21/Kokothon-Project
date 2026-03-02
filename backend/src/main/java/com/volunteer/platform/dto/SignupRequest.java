package com.volunteer.platform.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
public class SignupRequest {
    @NotBlank
    private String name;

    @NotBlank
    @Email
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@gmail\\.com$", message = "Only @gmail.com addresses are allowed")
    private String email;

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[0-9]).*$", message = "Password must contain at least one uppercase letter and one number")
    private String password;
}
