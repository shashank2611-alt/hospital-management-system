package com.example.hospital.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDTO {

    private Integer id;
    private String name;

    private String email;

    @NotBlank(message = "Mobile is required")
    private String mobile;

    @NotBlank(message = "Password is required")
    private String password;

    private String role;

    // Doctor-only fields
    private String department;
    private String drCode;
}
