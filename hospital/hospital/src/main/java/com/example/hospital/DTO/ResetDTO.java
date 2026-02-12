package com.example.hospital.DTO;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ResetDTO {
    private String email;
    private String token;
    private String newPassword;
}
