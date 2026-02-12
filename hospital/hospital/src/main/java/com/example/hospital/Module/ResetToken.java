package com.example.hospital.Module;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
public class ResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String email;
    private String token;
    private LocalDateTime expiry;
}
